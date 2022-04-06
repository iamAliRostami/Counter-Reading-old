package com.leon.counter_reading.activities;

import static com.leon.counter_reading.enums.BundleEnum.IS_MANE;
import static com.leon.counter_reading.enums.BundleEnum.POSITION;
import static com.leon.counter_reading.enums.BundleEnum.READ_STATUS;
import static com.leon.counter_reading.enums.BundleEnum.TYPE;
import static com.leon.counter_reading.enums.DialogType.Red;
import static com.leon.counter_reading.enums.DialogType.Yellow;
import static com.leon.counter_reading.enums.NotificationType.LIGHT_OFF;
import static com.leon.counter_reading.enums.NotificationType.LIGHT_ON;
import static com.leon.counter_reading.enums.SearchTypeEnum.All;
import static com.leon.counter_reading.enums.SearchTypeEnum.PAGE_NUMBER;
import static com.leon.counter_reading.enums.SharedReferenceKeys.ACCOUNT;
import static com.leon.counter_reading.enums.SharedReferenceKeys.ADDRESS;
import static com.leon.counter_reading.enums.SharedReferenceKeys.AHAD_1;
import static com.leon.counter_reading.enums.SharedReferenceKeys.AHAD_2;
import static com.leon.counter_reading.enums.SharedReferenceKeys.AHAD_EMPTY;
import static com.leon.counter_reading.enums.SharedReferenceKeys.AHAD_TOTAL;
import static com.leon.counter_reading.enums.SharedReferenceKeys.IMAGE;
import static com.leon.counter_reading.enums.SharedReferenceKeys.KARBARI;
import static com.leon.counter_reading.enums.SharedReferenceKeys.MOBILE;
import static com.leon.counter_reading.enums.SharedReferenceKeys.READING_REPORT;
import static com.leon.counter_reading.enums.SharedReferenceKeys.SERIAL;
import static com.leon.counter_reading.enums.SharedReferenceKeys.SORT_TYPE;
import static com.leon.counter_reading.fragments.dialog.ShowFragmentDialog.ShowFragmentDialogOnce;
import static com.leon.counter_reading.helpers.Constants.CAMERA;
import static com.leon.counter_reading.helpers.Constants.COUNTER_LOCATION;
import static com.leon.counter_reading.helpers.Constants.DESCRIPTION;
import static com.leon.counter_reading.helpers.Constants.NAVIGATION;
import static com.leon.counter_reading.helpers.Constants.OFFLINE_ATTEMPT;
import static com.leon.counter_reading.helpers.Constants.REPORT;
import static com.leon.counter_reading.helpers.Constants.readingData;
import static com.leon.counter_reading.helpers.Constants.readingDataTemp;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.getContext;
import static com.leon.counter_reading.helpers.MyApplication.getLocationTracker;
import static com.leon.counter_reading.utils.MakeNotification.makeRing;
import static com.leon.counter_reading.utils.login.TwoStepVerification.showPersonalCode;
import static com.leon.counter_reading.utils.reading.ReadingUtils.setAboveIcons;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Debug;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.ViewPagerStateAdapter2;
import com.leon.counter_reading.base_items.BaseActivity;
import com.leon.counter_reading.databinding.ActivityReadingBinding;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.enums.NotificationType;
import com.leon.counter_reading.enums.OffloadStateEnum;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.fragments.ReadingFragment;
import com.leon.counter_reading.fragments.dialog.CounterPlaceFragment;
import com.leon.counter_reading.fragments.dialog.NavigationFragment;
import com.leon.counter_reading.fragments.dialog.PossibleFragment;
import com.leon.counter_reading.fragments.dialog.ReadingReportFragment;
import com.leon.counter_reading.fragments.dialog.SearchFragment;
import com.leon.counter_reading.fragments.dialog.SerialFragment;
import com.leon.counter_reading.fragments.dialog.TakePhotoFragment;
import com.leon.counter_reading.infrastructure.IFlashLightManager;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.tables.CounterStateDto;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.OnSwipeTouchListener;
import com.leon.counter_reading.utils.reading.ChangeSortType;
import com.leon.counter_reading.utils.reading.GetBundle;
import com.leon.counter_reading.utils.reading.GetReadingDBData;
import com.leon.counter_reading.utils.reading.PrepareToSend;
import com.leon.counter_reading.utils.reading.ReadingUtils;
import com.leon.counter_reading.utils.reading.Result;
import com.leon.counter_reading.utils.reading.ResultOld;
import com.leon.counter_reading.utils.reading.Search;
import com.leon.counter_reading.utils.reading.Update;
import com.leon.counter_reading.utils.reading.UpdateOnOffLoadByAttemptNumber;
import com.leon.counter_reading.utils.reading.UpdateOnOffLoadByIsShown;
import com.leon.counter_reading.utils.reading.UpdateOnOffLoadDtoByLock;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;

public class ReadingActivity extends BaseActivity implements TakePhotoFragment.Callback,
        ReadingReportFragment.Callback, CounterPlaceFragment.Callback, NavigationFragment.Callback {
    public static int offlineAttempts = 0;
    private int[] imageSrc;
    private ActivityReadingBinding binding;
    private Activity activity;
    private IFlashLightManager flashLightManager;
    private ISharedPreferenceManager sharedPreferenceManager;
    private int readStatus = 0, highLow = 1;
    private boolean isShowing = false;
    private ViewPagerStateAdapter2 viewPagerAdapterReading;
    private int position = 0;

    @Override
    protected void initialize() {
        binding = ActivityReadingBinding.inflate(getLayoutInflater());
        final View childLayout = binding.getRoot();
        final ConstraintLayout parentLayout = findViewById(R.id.base_Content);
        parentLayout.addView(childLayout);
        activity = this;
        sharedPreferenceManager = getApplicationComponent().SharedPreferenceModel();
        imageSrc = ArrayUtils.clone(setAboveIcons());
        getBundle();
        setOnImageViewsClickListener();
        new GetReadingDBData(activity, readStatus, highLow, sharedPreferenceManager.
                getBoolData(SORT_TYPE.getValue())).execute(activity);
    }

    private void updateOnOffLoad(int position, int counterStateCode, int counterStatePosition) {
        readingData.onOffLoadDtos.get(position).isBazdid = true;
        readingData.onOffLoadDtos.get(position).offLoadStateId = OffloadStateEnum.INSERTED.getValue();
        readingData.onOffLoadDtos.get(position).counterStatePosition = counterStatePosition;
        readingData.onOffLoadDtos.get(position).counterStateId = counterStateCode;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateAdapter(int position) {
        try {
            runOnUiThread(() -> viewPagerAdapterReading.notifyDataSetChanged());
            int i = 0;
            boolean found = false;
            while (!found && i < readingDataTemp.onOffLoadDtos.size()) {
                if (readingDataTemp.onOffLoadDtos.get(i).id.equals(readingData.onOffLoadDtos.get(position).id)) {
                    readingDataTemp.onOffLoadDtos.set(i, readingData.onOffLoadDtos.get(position));
                    found = true;
                }
                i++;
            }
        } catch (Exception e) {
            runOnUiThread(() -> new CustomDialogModel(Red, activity, e.getMessage(),
                    getString(R.string.dear_user), getString(R.string.take_screen_shot),
                    getString(R.string.accepted)));
        }
    }

    public void updateOnOffLoadByPreNumber(int position) {
        readingData.onOffLoadDtos.get(position).counterNumberShown = true;
        readingData.onOffLoadDtos.get(position).isBazdid = true;
        updateAdapter(position);
        new UpdateOnOffLoadByIsShown(readingData.onOffLoadDtos.get(position)).execute();
    }

    public void updateOnOffLoadByAttempt(int position) {
        updateAdapter(position);
        new UpdateOnOffLoadByAttemptNumber(readingData.onOffLoadDtos.get(position)).execute();
    }

    public void updateOnOffLoadByLock(int position) {
        readingData.onOffLoadDtos.get(position).isLocked = true;
        updateAdapter(position);
        new UpdateOnOffLoadDtoByLock(readingData.onOffLoadDtos.get(position)).execute();
    }

    public void updateOnOffLoadWithoutCounterNumber(int position, int counterStateCode,
                                                    int counterStatePosition) {
        readingData.onOffLoadDtos.get(position).counterNumber = null;
        updateOnOffLoad(position, counterStateCode, counterStatePosition);
        attemptSend(position, true, true);
    }

    public void updateOnOffLoadByCounterSerial(int position, int counterStatePosition,
                                               int counterStateCode, String counterSerial) {
        updateOnOffLoad(position, counterStateCode, counterStatePosition);
        readingData.onOffLoadDtos.get(position).possibleCounterSerial = counterSerial;
        isShowing = true;
        attemptSend(position, false, false);
    }

    public void updateOnOffLoadByCounterNumber(int position, int number, int counterStateCode,
                                               int counterStatePosition) {
        updateOnOffLoad(position, counterStateCode, counterStatePosition);
        readingData.onOffLoadDtos.get(position).counterNumber = number;
        attemptSend(position, true, true);
    }

    public void updateOnOffLoadByCounterNumber(int position, int number, int counterStateCode,
                                               int counterStatePosition, int type) {
        readingData.onOffLoadDtos.get(position).highLowStateId = type;
        updateOnOffLoadByCounterNumber(position, number, counterStateCode, counterStatePosition);
    }

    public void updateOnOffLoadByNavigation(int position, OnOffLoadDto onOffLoadDto, boolean justMobile) {
        readingData.onOffLoadDtos.get(position).possibleMobile = onOffLoadDto.possibleMobile;
        if (justMobile) return;
        readingData.onOffLoadDtos.get(position).possibleCounterSerial = onOffLoadDto.possibleCounterSerial;
        readingData.onOffLoadDtos.get(position).description = onOffLoadDto.description;
        readingData.onOffLoadDtos.get(position).possibleKarbariCode = onOffLoadDto.possibleKarbariCode;
        readingData.onOffLoadDtos.get(position).possibleAhadTejariOrFari = onOffLoadDto.possibleAhadTejariOrFari;
        readingData.onOffLoadDtos.get(position).possibleAhadMaskooniOrAsli = onOffLoadDto.possibleAhadMaskooniOrAsli;
        readingData.onOffLoadDtos.get(position).possibleAhadSaierOrAbBaha = onOffLoadDto.possibleAhadSaierOrAbBaha;
        readingData.onOffLoadDtos.get(position).possibleEmpty = onOffLoadDto.possibleEmpty;
        readingData.onOffLoadDtos.get(position).possibleAddress = onOffLoadDto.possibleAddress;
        readingData.onOffLoadDtos.get(position).possibleEshterak = onOffLoadDto.possibleEshterak;
        attemptSend(position, false, true);
    }

    public void changePage(int pageNumber) {
        try {
            runOnUiThread(() -> {
                if (pageNumber < readingData.onOffLoadDtos.size()) {
                    displayView(pageNumber);
                } else {
                    new CustomToast().success(getString(R.string.all_masir_bazdid));
                }
            });
        } catch (Exception e) {
            activity.runOnUiThread(() -> new CustomDialogModel(Red, activity, e.getMessage(),
                    getString(R.string.dear_user), getString(R.string.take_screen_shot),
                    getString(R.string.accepted)));
        }
    }

    public void search(int type, String key, boolean goToPage) {
        if (type == PAGE_NUMBER.getValue()) {
            displayView(Integer.parseInt(key) - 1);
        } else if (type == All.getValue()) {
            readingData.onOffLoadDtos.clear();
            readingData.onOffLoadDtos.addAll(readingDataTemp.onOffLoadDtos);
            runOnUiThread(this::setupViewPager);
        } else {
            new Search(type, key, goToPage).execute(activity);
        }
    }

    public void setupViewPager() {
        runOnUiThread(() -> {
            binding.textViewNotFound.setVisibility(readingData.onOffLoadDtos.size() > 0 ?
                    View.GONE : View.VISIBLE);
            binding.linearLayoutAbove.setVisibility(readingData.onOffLoadDtos.size() > 0 ?
                    View.VISIBLE : View.GONE);
            binding.containerBody.setVisibility(readingData.onOffLoadDtos.size() > 0 ?
                    View.VISIBLE : View.GONE);
            setOnPageChangeListener();
        });
        setupViewPagerAdapter();
    }

    private void setupViewPagerAdapter() {
        runOnUiThread(() -> {
            viewPagerAdapterReading = new ViewPagerStateAdapter2(this, readingData);
            try {
                displayView(0);
            } catch (Exception e) {
                new CustomToast().error(getContext().getString(R.string.error_download_data), Toast.LENGTH_LONG);
            }
        });
    }

    private void displayView(int position) {
        final String tag = Integer.toString(position);

        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.enter, R.animator.exit,
                R.animator.pop_enter, R.animator.pop_exit);
        fragmentTransaction.replace(binding.containerBody.getId(), getFragment(position), tag);
        // Home fragment is not added to the stack
        if (position != 0) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commitAllowingStateLoss();
        runOnUiThread(() -> {
                    getFragmentManager().executePendingTransactions();

                    final String number = (position + 1) + "/" + readingData.onOffLoadDtos.size();
                    binding.textViewPageNumber.setText(number);
                }
        );
//        getFragmentManager().executePendingTransactions();
        setAboveIconsSrc(position);
    }

    private Fragment getFragment(int position) {
        return ReadingFragment.newInstance(position);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setOnPageChangeListener() {
//        binding.linearLayoutTest.setOnTouchListener((view, motionEvent) -> {
//            Log.e("here", "onTouch");
//            return false;
//        });
//        binding.linearLayoutTest.setOnClickListener(view -> Log.e("here", "onClick"));
        binding.linearLayoutTest.setOnTouchListener(new OnSwipeTouchListener(ReadingActivity.this) {
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                position++;
                displayView(position);
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                position--;
                displayView(position);
            }
        });
    }

    private void showImage(int position) {
        //TODO
//        ShowFragmentDialogOnce(activity, "TAKE_PHOTO", TakePhotoFragment
//                .newInstance(readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).offLoadStateId > 0,
//                        readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).id,
//                        readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).trackNumber,
//                        position, true));
    }

    private void attemptSend(int position, boolean isForm, boolean isImage) {
        if (isForm && (sharedPreferenceManager.getBoolData(SERIAL.getValue())
                || sharedPreferenceManager.getBoolData(AHAD_2.getValue())
                || sharedPreferenceManager.getBoolData(AHAD_1.getValue())
                || sharedPreferenceManager.getBoolData(AHAD_TOTAL.getValue())
                || sharedPreferenceManager.getBoolData(AHAD_EMPTY.getValue())
                || sharedPreferenceManager.getBoolData(KARBARI.getValue())
                || sharedPreferenceManager.getBoolData(ADDRESS.getValue())
                || sharedPreferenceManager.getBoolData(ACCOUNT.getValue())
                || sharedPreferenceManager.getBoolData(READING_REPORT.getValue())
                || sharedPreferenceManager.getBoolData(MOBILE.getValue()))) {
            showPossible(position);
        } else if (isImage && sharedPreferenceManager.getBoolData(IMAGE.getValue())) {
            showImage(position);
        } else {
            if (!isShowing) {
                final CounterStateDto counterStateDto = readingData.counterStateDtos.get(readingData
                        .onOffLoadDtos.get(position).counterStatePosition);
                if ((counterStateDto.isTavizi || counterStateDto.isXarab) &&
                        counterStateDto.moshtarakinId != readingData.onOffLoadDtos.get(position).preCounterStateCode) {
                    ShowFragmentDialogOnce(activity, "SERIAL_DIALOG",
                            SerialFragment.newInstance(position, counterStateDto.id,
                                    readingData.onOffLoadDtos.get(position).counterStatePosition));
                } else isShowing = true;
            }
            if (isShowing) {
                isShowing = false;
                makeRing(activity, NotificationType.SAVE);
                Location location = null;
                try {
                    location = getLocationTracker(activity).getCurrentLocation();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new Update(readingData.onOffLoadDtos.get(position), location).execute(activity);
                if (offlineAttempts < OFFLINE_ATTEMPT)
                    new PrepareToSend(sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue()))
                            .execute(activity);
                position++;
                changePage(position);
            }
        }
    }

    private void showPossible(int position) {
        ShowFragmentDialogOnce(activity, "SHOW_POSSIBLE_DIALOG",
                PossibleFragment.newInstance(readingData.onOffLoadDtos.get(position), position, false));
    }

    private void showNoEshterakFound() {
        new CustomDialogModel(Yellow, activity, getString(R.string.no_eshterak_found),
                getString(R.string.dear_user), getString(R.string.eshterak),
                getString(R.string.accepted));
    }

    //TODO
    private void setAboveIconsSrc(int position) {
        runOnUiThread(() -> {
            setHighLowImage(position);
            setReadStatusImage(position);
            setExceptionImage(position);
            setIsBazdidImage(position);
        });
    }

    private void setExceptionImage(int position) {
        int src = ReadingUtils.setExceptionImage(readingData, position);
        try {
            binding.imageViewExceptionState.setVisibility(View.GONE);
            if (src > -1) {
                binding.imageViewExceptionState.setVisibility(View.VISIBLE);
                binding.imageViewExceptionState.setImageResource(imageSrc[src]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setIsBazdidImage(int position) {
        try {
            if (readingData.onOffLoadDtos.get(position).isBazdid)
                binding.imageViewReadingType.setImageResource(imageSrc[6]);
            else binding.imageViewReadingType.setImageResource(imageSrc[7]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setReadStatusImage(int position) {
        try {
            binding.imageViewOffLoadState.setImageResource(imageSrc[readingData.onOffLoadDtos
                    .get(position).offLoadStateId]);
            if (readingData.onOffLoadDtos.get(position).offLoadStateId == 0)
                binding.imageViewOffLoadState.setImageResource(imageSrc[8]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setHighLowImage(int position) {
        try {
            binding.imageViewHighLowState.setImageResource(imageSrc[readingData.onOffLoadDtos
                    .get(position).highLowStateId]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getBundle() {
        if (getIntent().getExtras() != null) {
            readStatus = getIntent().getIntExtra(READ_STATUS.getValue(), 0);
            highLow = getIntent().getIntExtra(TYPE.getValue(), 1);
            final ArrayList<String> json = getIntent().getExtras().getStringArrayList(IS_MANE.getValue());
            getIntent().getExtras().clear();
            new GetBundle(json).execute();
        }
    }

    private void setOnImageViewsClickListener() {
        flashLightManager = getApplicationComponent().FlashViewModel();
        final ImageView imageViewFlash = findViewById(R.id.image_view_flash);
        if (imageViewFlash != null) {
            imageViewFlash.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),
                    R.drawable.img_flash_off));
            imageViewFlash.setOnClickListener(v -> {
                boolean isOn = flashLightManager.toggleFlash();
                makeRing(activity, isOn ? LIGHT_ON : LIGHT_OFF);
                imageViewFlash.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),
                        isOn ? R.drawable.img_flash_on : R.drawable.img_flash_off));
            });

            final ImageView imageViewReverse = findViewById(R.id.image_view_reverse);
            imageViewReverse.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),
                    R.drawable.img_inverse));
            imageViewReverse.setOnClickListener(v ->
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode() < 2 ?
                            AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO));

            final ImageView imageViewCamera = findViewById(R.id.image_view_camera);
            imageViewCamera.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),
                    R.drawable.img_camera));
            imageViewCamera.setOnClickListener(v -> {
                if (readingData.onOffLoadDtos.isEmpty()) {
                    showNoEshterakFound();
                } else {
                    //TODO
//                    ShowFragmentDialogOnce(activity, "TAKE_PHOTO", TakePhotoFragment
//                            .newInstance(readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).offLoadStateId > 0,
//                                    readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).id,
//                                    readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).trackNumber));
                }
            });

            final ImageView imageViewCheck = findViewById(R.id.image_view_reading_report);
            imageViewCheck.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),
                    R.drawable.img_checked));
            imageViewCheck.setOnClickListener(v -> {
                if (readingData.onOffLoadDtos.isEmpty()) {
                    showNoEshterakFound();
                } else {
                    //TODO
//                    ShowFragmentDialogOnce(activity, "READING_REPORT", ReadingReportFragment.newInstance(readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).id,
//                            readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).trackNumber,
//                            binding.viewPager.getCurrentItem(),
//                            readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).zoneId));
                }
            });

            final ImageView imageViewSearch = findViewById(R.id.image_view_search);
            imageViewSearch.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),
                    R.drawable.img_search));
            imageViewSearch.setOnClickListener(v -> {
                if (readingDataTemp.onOffLoadDtos.isEmpty()) {
                    showNoEshterakFound();
                } else {
                    ShowFragmentDialogOnce(activity, "SEARCH_DIALOG", new SearchFragment());
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reading_menu, menu);
        menu.getItem(6).setChecked(getApplicationComponent().SharedPreferenceModel()
                .getBoolData(SORT_TYPE.getValue()));
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        if (id == R.id.menu_sort) {
            item.setChecked(!item.isChecked());
            sharedPreferenceManager.putData(SORT_TYPE.getValue(), item.isChecked());
            new ChangeSortType(activity, item.isChecked()).execute(activity);
        } else if (id == R.id.menu_navigation) {
            if (readingData.onOffLoadDtos.isEmpty()) {
                showNoEshterakFound();
            } else {
                //TODO
//                ShowFragmentDialogOnce(activity, "NAVIGATION", NavigationFragment
//                        .newInstance(binding.viewPager.getCurrentItem()));
            }
        } else if (id == R.id.menu_report_forbid) {
            //TODO
//            ShowFragmentDialogOnce(activity, "REPORT_FORBID", ReportForbidFragment.newInstance(
//                    readingData.onOffLoadDtos.size() > 0 ? readingData.onOffLoadDtos
//                            .get(binding.viewPager.getCurrentItem()).zoneId : 0));
        } else if (id == R.id.menu_description) {
            if (readingData.onOffLoadDtos.isEmpty()) {
                showNoEshterakFound();
            } else {
                //TODO
//                intent = new Intent(activity, DescriptionActivity.class);
//                intent.putExtra(BILL_ID.getValue(),
//                        readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).id);
//                intent.putExtra(TRACKING.getValue(),
//                        readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).trackNumber);
//                intent.putExtra(BundleEnum.DESCRIPTION.getValue(),
//                        readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).description);
//                intent.putExtra(POSITION.getValue(), binding.viewPager.getCurrentItem());
//                startActivityForResult(intent, DESCRIPTION);
            }
        }
        if (id == R.id.menu_location) {
            if (readingData.onOffLoadDtos.isEmpty()) {
                showNoEshterakFound();
            } else {
                //TODO
//                ShowFragmentDialogOnce(activity, "COUNTER_PLACE", CounterPlaceFragment
//                        .newInstance(readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).id,
//                                binding.viewPager.getCurrentItem()));
            }
        } else if (id == R.id.menu_last) {
            if (readingData.onOffLoadDtos.isEmpty()) {
                showNoEshterakFound();
            } else {
                position = 0;
                int i = 0;
                for (OnOffLoadDto onOffLoadDto : readingData.onOffLoadDtos) {
                    if (!onOffLoadDto.isBazdid) {
                        position = i;
                        break;
                    }
                    i++;
                }
                displayView(position);
            }
        } else if (id == R.id.menu_verification) {
            showPersonalCode(activity);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REPORT || requestCode == NAVIGATION ||
                requestCode == DESCRIPTION ||
                requestCode == COUNTER_LOCATION) && resultCode == RESULT_OK) {
            new ResultOld(data).execute(activity);
        } else if (requestCode == CAMERA && resultCode == RESULT_OK) {
            int position = data.getExtras().getInt(POSITION.getValue());
            attemptSend(position, false, false);
        }
    }

    @Override
    public void setResult(int position, String uuid) {
        new Result(position, uuid).execute(activity);

    }

    @Override
    public void setPhotoResult(int position) {
        attemptSend(position, false, false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            final ImageView imageViewFlash = findViewById(R.id.image_view_flash);
            imageViewFlash.setImageDrawable(AppCompatResources.getDrawable(activity,
                    R.drawable.img_flash_off));
            flashLightManager.turnOff();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        offlineAttempts = 0;
        Debug.getNativeHeapAllocatedSize();
        System.runFinalization();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Runtime.getRuntime().gc();
        System.gc();
    }
}