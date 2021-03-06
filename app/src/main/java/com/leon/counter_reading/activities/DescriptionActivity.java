package com.leon.counter_reading.activities;

import static com.leon.counter_reading.enums.BundleEnum.BILL_ID;
import static com.leon.counter_reading.enums.BundleEnum.DESCRIPTION;
import static com.leon.counter_reading.enums.BundleEnum.POSITION;
import static com.leon.counter_reading.enums.BundleEnum.TRACKING;
import static com.leon.counter_reading.helpers.Constants.RECORD_AUDIO_PERMISSIONS;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.onActivitySetTheme;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getCompanyName;
import static com.leon.counter_reading.utils.PermissionManager.checkRecorderPermission;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.ActivityDescriptionBinding;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.tables.Voice;
import com.leon.counter_reading.utils.CustomFile;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.DifferentCompanyManager;
import com.leon.counter_reading.utils.PermissionManager;
import com.leon.counter_reading.utils.voice.PrepareMultimedia;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class DescriptionActivity extends AppCompatActivity {
    private ActivityDescriptionBinding binding;
    private Activity activity;
    private Voice voice;
    private MediaPlayer mediaPlayer;
    private MediaRecorder mediaRecorder;
    private String uuid, description;
    private boolean play = false;
    private int position, startTime = 0, finalTime = 0, trackNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        onActivitySetTheme(this, getApplicationComponent().SharedPreferenceModel()
                        .getIntData(SharedReferenceKeys.THEME_STABLE.getValue()),
                true);
        super.onCreate(savedInstanceState);
        binding = ActivityDescriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        final TextView textViewCompanyName = findViewById(R.id.text_view_company_name);
        textViewCompanyName.setText(getCompanyName(DifferentCompanyManager.getActiveCompanyName()));
        activity = this;
        if (checkRecorderPermission(getApplicationContext()))
            initialize();
        else askRecorderPermission();
    }

    private void initialize() {
        if (getIntent().getExtras() != null) {
            uuid = getIntent().getExtras().getString(BILL_ID.getValue());
            position = getIntent().getExtras().getInt(POSITION.getValue());
            trackNumber = getIntent().getExtras().getInt(TRACKING.getValue());
            description = getIntent().getExtras().getString(DESCRIPTION.getValue());
            getIntent().getExtras().clear();
        }
        binding.imageViewRecord.setImageDrawable(AppCompatResources.getDrawable(activity,
                R.drawable.img_record));
        checkMultimediaAndToggle();
        setImageViewRecordClickListener();
        setImageViewPausePlayClickListener();
        setOnButtonSendClickListener();
        setSeekBarChangeListener();
    }

    @SuppressLint({"ClickableViewAccessibility"})
    private void setImageViewRecordClickListener() {
        binding.imageViewRecord.setLongClickable(true);
        binding.imageViewRecord.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                new CustomToast().info(getString(R.string.recording), Toast.LENGTH_LONG);
                binding.imageViewPlay.setEnabled(false);
                voice.address = CustomFile.createAudioFile(activity);
                setupMediaRecorder();
                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();//TODO I don't know why sometimes crashes!
                } catch (IOException e) {
                    e.printStackTrace();
                    new CustomToast().warning(getString(R.string.error_in_record_voice));
                    mediaRecorder.stop();
                }
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mediaRecorder.stop();
                try {
                    mediaPlayer = new MediaPlayer();
                    voice.size = new File(getExternalFilesDir(null).getAbsolutePath() +
                            getString(R.string.audio_folder) + voice.address).length();
                    mediaPlayer.setDataSource(getExternalFilesDir(null).getAbsolutePath() +
                            getString(R.string.audio_folder) + voice.address);
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mediaPlayer.getDuration() > 2000) {
                    binding.imageViewPlay.setEnabled(true);
                    binding.imageViewPlay.setImageResource(R.drawable.img_play);
                } else {
                    binding.imageViewPlay.setImageResource(R.drawable.img_play_pause);
                    binding.imageViewPlay.setEnabled(false);
                }
                if (mediaPlayer != null) {
                    try {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return false;
        });
    }

    private void stopPlaying() {
        play = false;
        if (voice.id == 0)
            binding.imageViewRecord.setEnabled(true);
        binding.linearLayoutSeek.setVisibility(View.GONE);
        binding.imageViewPlay.setImageResource(R.drawable.img_play);
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(getExternalFilesDir(null).getAbsolutePath() +
                getString(R.string.audio_folder) + voice.address);
    }

    private void setSeekBarChangeListener() {
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                if (progressChangedValue / 100 == finalTime / 100) {
                    stopPlaying();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (play) {
                    mediaPlayer.seekTo(progressChangedValue);
                    startTime = progressChangedValue;
                }
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void setImageViewPausePlayClickListener() {
        binding.imageViewPlay.setOnClickListener(v -> {
            if (!play) {
                try {
                    binding.linearLayoutSeek.setVisibility(View.VISIBLE);
                    play = true;
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(getExternalFilesDir(null).getAbsolutePath() +
                            getString(R.string.audio_folder) + voice.address);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    finalTime = mediaPlayer.getDuration();
                    startTime = mediaPlayer.getCurrentPosition();
                    binding.seekBar.setMax(finalTime);
                    final Handler myHandler = new Handler();
                    binding.seekBar.setProgress(startTime);
                    Runnable UpdateSongTime = new Runnable() {
                        public void run() {
                            if (play) {
                                startTime = mediaPlayer.getCurrentPosition();
                                binding.textViewCurrent.setText(String.format("%d ???????????? %d ??????????",
                                        TimeUnit.MILLISECONDS.toMinutes(startTime),
                                        TimeUnit.MILLISECONDS.toSeconds(startTime) -
                                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                                        toMinutes(startTime)))
                                );
                                binding.seekBar.setProgress(startTime);
                                myHandler.postDelayed(this, 1);
                                if (startTime == finalTime) {
                                    stopPlaying();
                                }
                            }
                        }
                    };
                    binding.textViewTotal.setText(String.format("%d ???????????? %d ??????????",
                            TimeUnit.MILLISECONDS.toMinutes(finalTime),
                            TimeUnit.MILLISECONDS.toSeconds(finalTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime)))
                    );
                    myHandler.postDelayed(UpdateSongTime, 1);
                    binding.imageViewPlay.setImageResource(R.drawable.img_pause);
                    binding.imageViewRecord.setEnabled(false);
                } catch (IOException e) {
                    e.printStackTrace();
                    new CustomToast().warning(getString(R.string.error_in_play_voice));
                }
            } else {
                stopPlaying();
            }
        });
    }

    private void checkMultimediaAndToggle() {
        voice = getApplicationComponent().MyDatabase().voiceDao().
                getVoicesByOnOffLoadId(uuid);
        if (voice == null) {
            voice = new Voice();
            binding.editTextMessage.setText(description);
            binding.buttonSend.setEnabled(true);
            binding.imageViewRecord.setEnabled(true);
            binding.imageViewPlay.setEnabled(false);
            binding.imageViewPlay.setImageResource(R.drawable.img_play_pause);
        } else {
            binding.buttonSend.setEnabled(!voice.isSent);
            binding.editTextMessage.setEnabled(false);
            binding.imageViewRecord.setEnabled(false);
            binding.editTextMessage.setText(voice.Description);
            binding.imageViewPlay.setEnabled(true);
            binding.imageViewPlay.setImageResource(R.drawable.img_play);
        }
    }

    private void setOnButtonSendClickListener() {
        binding.buttonSend.setOnClickListener(v -> {
            binding.buttonSend.setEnabled(false);
            voice.OnOffLoadId = uuid;
            voice.trackNumber = trackNumber;
            String message = binding.editTextMessage.getText().toString();
            if (voice.address != null && voice.address.length() > 0)
                new PrepareMultimedia(activity, voice, binding.editTextMessage.getText().toString()
                        , uuid, position).execute(activity);
            else if (message.length() > 0) {
                finishDescription(message);
            } else {
                new CustomToast().warning(getString(R.string.insert_message));
                binding.buttonSend.setEnabled(true);
            }
        });
    }

    private void finishDescription(String message) {
        getApplicationComponent().MyDatabase().onOffLoadDao().updateOnOffLoadDescription(uuid, message);
        Intent intent = new Intent();
        intent.putExtra(POSITION.getValue(), position);
        intent.putExtra(BILL_ID.getValue(), uuid);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void askRecorderPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                new CustomToast().info(getString(R.string.access_granted));
                initialize();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                PermissionManager.forceClose(activity);
            }
        };
        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage(getString(R.string.confirm_permission))
                .setRationaleConfirmText(getString(R.string.allow_permission))
                .setDeniedMessage(getString(R.string.if_reject_permission))
                .setDeniedCloseButtonText(getString(R.string.close))
                .setGotoSettingButtonText(getString(R.string.allow_permission))
                .setPermissions(RECORD_AUDIO_PERMISSIONS).check();
    }


    @Override
    public void onBackPressed() {
        stopPlaying();
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        Debug.getNativeHeapAllocatedSize();
        System.runFinalization();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Runtime.getRuntime().gc();
        System.gc();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        binding.imageViewPlay.setImageDrawable(null);
        binding.imageViewRecord.setImageDrawable(null);
        super.onDestroy();
    }
}