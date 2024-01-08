package com.leon.counter_reading.activities;

import static com.leon.counter_reading.enums.BundleEnum.BILL_ID;
import static com.leon.counter_reading.enums.BundleEnum.DESCRIPTION;
import static com.leon.counter_reading.enums.BundleEnum.POSITION;
import static com.leon.counter_reading.enums.BundleEnum.TRACKING;
import static com.leon.counter_reading.enums.SharedReferenceKeys.THEME_STABLE;
import static com.leon.counter_reading.helpers.Constants.RECORD_AUDIO_PERMISSIONS;
import static com.leon.counter_reading.helpers.DifferentCompanyManager.getCompanyName;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.onActivitySetTheme;
import static com.leon.counter_reading.utils.PermissionManager.checkRecorderPermission;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.SystemClock;
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
import com.leon.counter_reading.tables.Voice;
import com.leon.counter_reading.utils.CustomFile;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.PermissionManager;
import com.leon.counter_reading.utils.voice.PrepareMultimedia;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DescriptionActivity extends AppCompatActivity implements View.OnClickListener,
        View.OnTouchListener, SeekBar.OnSeekBarChangeListener {
    private ActivityDescriptionBinding binding;
    private Activity activity;
    private Voice voice;
    private MediaPlayer mediaPlayer;
    private MediaRecorder mediaRecorder;
    private String uuid, description, dir;
    private long lastClickTime = 0;
    private boolean play = false;
    private int progressChangedValue;
    private int position, startTime = 0, finalTime = 0, trackNumber;
    private final Handler myHandler = new Handler();
    private final Runnable UpdateSongTime = new Runnable() {
        public void run() {
            if (play) {
                startTime = mediaPlayer.getCurrentPosition();
                binding.textViewCurrent.setText(String.format(Locale.US, "%d دقیقه، %d ثانیه",
                        MILLISECONDS.toMinutes(startTime), MILLISECONDS.toSeconds(startTime) -
                                TimeUnit.MINUTES.toSeconds(MILLISECONDS.toMinutes(startTime))));
                binding.seekBar.setProgress(startTime);
                myHandler.postDelayed(this, 1);
                if (startTime == finalTime) {
                    stopPlaying();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        onActivitySetTheme(this, getApplicationComponent().SharedPreferenceModel()
                .getIntData(THEME_STABLE.getValue()), true);
        super.onCreate(savedInstanceState);
        binding = ActivityDescriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        final TextView textViewCompanyName = findViewById(R.id.text_view_company_name);
        textViewCompanyName.setText(getCompanyName());
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
        dir = getExternalFilesDir(null).getAbsolutePath().concat(getString(R.string.audio_folder));
        binding.imageViewRecord.setImageDrawable(AppCompatResources.getDrawable(activity,
                R.drawable.img_record));
        checkMultimediaAndToggle();
        setListeners();
    }

    @SuppressLint({"ClickableViewAccessibility"})
    private void setListeners() {
        binding.imageViewRecord.setLongClickable(true);
        binding.imageViewRecord.setOnTouchListener(this);
        binding.imageViewPlay.setOnClickListener(this);
        binding.buttonSend.setOnClickListener(this);
        binding.seekBar.setOnSeekBarChangeListener(this);
    }

    private void startPlaying() {
        try {
            binding.linearLayoutSeek.setVisibility(View.VISIBLE);
            play = true;
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(dir.concat(voice.address));
            mediaPlayer.prepare();
            mediaPlayer.start();
            finalTime = mediaPlayer.getDuration();
            startTime = mediaPlayer.getCurrentPosition();
            binding.seekBar.setMax(finalTime);
            binding.seekBar.setProgress(startTime);
            binding.textViewTotal.setText(String.format(Locale.US, "%d دقیقه، %d ثانیه",
                    MILLISECONDS.toMinutes(finalTime), MILLISECONDS.toSeconds(finalTime) -
                            TimeUnit.MINUTES.toSeconds(MILLISECONDS.toMinutes(finalTime))));
            myHandler.postDelayed(UpdateSongTime, 1);
            binding.imageViewPlay.setImageResource(R.drawable.img_pause);
            binding.imageViewRecord.setEnabled(false);
        } catch (IOException e) {
            e.printStackTrace();
            new CustomToast().warning(getString(R.string.error_in_play_voice));
        }
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
        mediaRecorder.setOutputFile(dir.concat(voice.address));
    }

    private void checkMultimediaAndToggle() {
        voice = getApplicationComponent().MyDatabase().voiceDao().getVoicesByOnOffLoadId(uuid);
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        progressChangedValue = i;
        if (progressChangedValue / 100 == finalTime / 100) {
            stopPlaying();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        progressChangedValue = 0;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (play) {
            mediaPlayer.seekTo(progressChangedValue);
            startTime = progressChangedValue;
        }
    }

    @Override
    public void onClick(View view) {
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000) return;
        lastClickTime = SystemClock.elapsedRealtime();
        int id = view.getId();
        if (id == R.id.button_send) {
            sendDescription();
        } else if (id == R.id.image_view_play) {
            if (!play) {
                startPlaying();
            } else {
                stopPlaying();
            }
        }
    }

    private void sendDescription() {
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
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            startRecording();
        }
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            stopRecording();
        }
        return false;
    }

    private void startRecording() {
        new CustomToast().info(getString(R.string.recording), Toast.LENGTH_LONG);
        binding.imageViewPlay.setEnabled(false);
        voice.address = CustomFile.createAudioFile(activity);
        setupMediaRecorder();
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
            new CustomToast().warning(getString(R.string.error_in_record_voice));
            mediaRecorder.stop();
        }
    }

    private void stopRecording() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mediaRecorder.stop();
        try {
            mediaPlayer = new MediaPlayer();
            voice.size = new File(dir.concat(voice.address)).length();
            mediaPlayer.setDataSource(dir.concat(voice.address));
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

    private void finishDescription(String message) {
        getApplicationComponent().MyDatabase().onOffLoadDao().updateOnOffLoadDescription(uuid, message);
        final Intent intent = new Intent();
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
    protected void onDestroy() {
        binding.imageViewPlay.setImageDrawable(null);
        binding.imageViewRecord.setImageDrawable(null);
        Debug.getNativeHeapAllocatedSize();
        System.runFinalization();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Runtime.getRuntime().gc();
        System.gc();
        super.onDestroy();
    }
}