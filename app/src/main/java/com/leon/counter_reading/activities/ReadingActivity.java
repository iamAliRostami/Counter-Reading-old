package com.leon.counter_reading.activities;

import static com.leon.counter_reading.helpers.Constants.CAMERA;
import static com.leon.counter_reading.helpers.Constants.COUNTER_LOCATION;
import static com.leon.counter_reading.helpers.Constants.DESCRIPTION;
import static com.leon.counter_reading.helpers.Constants.FOCUS_ON_EDIT_TEXT;
import static com.leon.counter_reading.helpers.Constants.NAVIGATION;
import static com.leon.counter_reading.helpers.Constants.REPORT;
import static com.leon.counter_reading.helpers.MyApplication.getLocationTracker;
import static com.leon.counter_reading.utils.MakeNotification.makeRing;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Debug;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.Gson;
import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.ViewPagerAdapterReading2;
import com.leon.counter_reading.base_items.BaseActivity;
import com.leon.counter_reading.databinding.ActivityReadingBinding;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.DialogType;
import com.leon.counter_reading.enums.NotificationType;
import com.leon.counter_reading.enums.OffloadStateEnum;
import com.leon.counter_reading.enums.SearchTypeEnum;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.fragments.PossibleFragment;
import com.leon.counter_reading.fragments.SearchFragment;
import com.leon.counter_reading.fragments.SerialFragment;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.infrastructure.IFlashLightManager;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.tables.CounterStateDto;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.tables.ReadingData;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.DepthPageTransformer2;
import com.leon.counter_reading.utils.login.TwoStepVerification;
import com.leon.counter_reading.utils.reading.ChangeSortType;
import com.leon.counter_reading.utils.reading.GetBundle;
import com.leon.counter_reading.utils.reading.GetReadingDBData;
import com.leon.counter_reading.utils.reading.PrepareToSend;
import com.leon.counter_reading.utils.reading.ReadingUtils;
import com.leon.counter_reading.utils.reading.Result;
import com.leon.counter_reading.utils.reading.Search;
import com.leon.counter_reading.utils.reading.Update;

import java.util.ArrayList;

public class ReadingActivity extends BaseActivity {
    private final ReadingData readingData = new ReadingData(), readingDataTemp = new ReadingData();
    private int[] imageSrc = new int[15];
    private ActivityReadingBinding binding;
    private Activity activity;
    private IFlashLightManager flashLightManager;
    private ViewPagerAdapterReading2 viewPagerAdapterReading;
    private ISharedPreferenceManager sharedPreferenceManager;
    private int readStatus = 0, highLow = 1;
    private boolean isReading = false, isShowing = false;

    @Override
    protected void initialize() {
        binding = ActivityReadingBinding.inflate(getLayoutInflater());
        View childLayout = binding.getRoot();
        ConstraintLayout parentLayout = findViewById(R.id.base_Content);
        parentLayout.addView(childLayout);
        activity = this;
        sharedPreferenceManager = MyApplication.getApplicationComponent().SharedPreferenceModel();
        imageSrc = ReadingUtils.setAboveIcons();
        getBundle();
        setOnImageViewsClickListener();
        new GetReadingDBData(activity, readStatus, highLow, sharedPreferenceManager.
                getBoolData(SharedReferenceKeys.SORT_TYPE.getValue())).execute(activity);
    }

    private void getBundle() {
        if (getIntent().getExtras() != null) {
            readStatus = getIntent().getIntExtra(BundleEnum.READ_STATUS.getValue(), 0);
            highLow = getIntent().getIntExtra(BundleEnum.TYPE.getValue(), 1);
            ArrayList<String> json = getIntent().getExtras().getStringArrayList(
                    BundleEnum.IS_MANE.getValue());
            getIntent().getExtras().clear();
            new GetBundle(json).execute();
        }
    }

    private void setOnImageViewsClickListener() {
        flashLightManager = MyApplication.getApplicationComponent().FlashViewModel();
        ImageView imageViewFlash = findViewById(R.id.image_view_flash);
        imageViewFlash.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),
                R.drawable.img_flash_off));
        imageViewFlash.setOnClickListener(v -> {
            boolean isOn = flashLightManager.toggleFlash();
            makeRing(activity, isOn ? NotificationType.LIGHT_ON : NotificationType.LIGHT_OFF);
            imageViewFlash.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),
                    isOn ? R.drawable.img_flash_on : R.drawable.img_flash_off));
        });

        ImageView imageViewReverse = findViewById(R.id.image_view_reverse);
        imageViewReverse.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),
                R.drawable.img_inverse));
        imageViewReverse.setOnClickListener(v ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode() < 2 ?
                        AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO));

        ImageView imageViewCamera = findViewById(R.id.image_view_camera);
        imageViewCamera.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),
                R.drawable.img_camera));
        imageViewCamera.setOnClickListener(v -> {
            if (readingDataTemp.onOffLoadDtos.isEmpty()) {
                showNoEshterakFound();
            } else {
                Intent intent = new Intent(activity, TakePhotoActivity.class);
                intent.putExtra(BundleEnum.SENT.getValue(),
                        readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).offLoadStateId > 0);
                intent.putExtra(BundleEnum.BILL_ID.getValue(),
                        readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).id);
                intent.putExtra(BundleEnum.TRACKING.getValue(),
                        readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).trackNumber);
                startActivity(intent);
            }
        });

        ImageView imageViewCheck = findViewById(R.id.image_view_reading_report);
        imageViewCheck.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),
                R.drawable.img_checked));
        imageViewCheck.setOnClickListener(v -> {
            if (readingDataTemp.onOffLoadDtos.isEmpty()) {
                showNoEshterakFound();
            } else {
                Intent intent = new Intent(activity, ReadingReportActivity.class);
                intent.putExtra(BundleEnum.BILL_ID.getValue(),
                        readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).id);
                intent.putExtra(BundleEnum.TRACKING.getValue(),
                        readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).trackNumber);
                intent.putExtra(BundleEnum.POSITION.getValue(), binding.viewPager.getCurrentItem());
                intent.putExtra(BundleEnum.ZONE_ID.getValue(),
                        readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).zoneId);
                startActivityForResult(intent, REPORT);
            }
        });

        ImageView imageViewSearch = findViewById(R.id.image_view_search);
        imageViewSearch.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),
                R.drawable.img_search));
        imageViewSearch.setOnClickListener(v -> {
            if (readingDataTemp.onOffLoadDtos.isEmpty()) {
                showNoEshterakFound();
            } else {
                FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                SearchFragment searchFragment = new SearchFragment();
                searchFragment.show(fragmentTransaction, "");
            }
        });
    }

    private void updateOnOffLoad(int position, int counterStateCode, int counterStatePosition) {
        readingData.onOffLoadDtos.get(position).isBazdid = true;
        readingData.onOffLoadDtos.get(position).offLoadStateId = OffloadStateEnum.INSERTED.getValue();
        readingData.onOffLoadDtos.get(position).counterStatePosition = counterStatePosition;
        readingData.onOffLoadDtos.get(position).counterStateId = counterStateCode;
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
                if (pageNumber < readingData.onOffLoadDtos.size())
                    binding.viewPager.setCurrentItem(pageNumber);
                else {
                    new CustomToast().success(getString(R.string.all_masir_bazdid));
                }
            });
        } catch (Exception e) {
            activity.runOnUiThread(() -> new CustomDialogModel(DialogType.Red, activity,
                    e.getMessage(),
                    getString(R.string.dear_user),
                    getString(R.string.take_screen_shot),
                    getString(R.string.accepted)));
        }
    }

    public void search(int type, String key, boolean goToPage) {
        if (type == SearchTypeEnum.PAGE_NUMBER.getValue()) {
            runOnUiThread(() -> binding.viewPager.setCurrentItem(Integer.parseInt(key) - 1));
        } else if (type == SearchTypeEnum.All.getValue()) {
            readingData.onOffLoadDtos.clear();
            readingData.onOffLoadDtos.addAll(readingDataTemp.onOffLoadDtos);
            runOnUiThread(this::setupViewPager);
        } else {
            new Search(type, key, goToPage).execute(activity);
        }
    }

    public void setupViewPager(ReadingData readingData, ReadingData readingDataTemp) {
        setReadingData(readingData, readingDataTemp);
        setupViewPager();
    }

    public void setupViewPager() {
        runOnUiThread(() -> {
            binding.textViewNotFound.setVisibility(readingData.onOffLoadDtos.size() > 0 ?
                    View.GONE : View.VISIBLE);
            binding.linearLayoutAbove.setVisibility(readingData.onOffLoadDtos.size() > 0 ?
                    View.VISIBLE : View.GONE);
            binding.viewPager.setVisibility(readingData.onOffLoadDtos.size() > 0 ?
                    View.VISIBLE : View.GONE);
            binding.viewPager.setPageTransformer(new DepthPageTransformer2());
            setOnPageChangeListener();
        });
        setupViewPagerAdapter(0);
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (FOCUS_ON_EDIT_TEXT)
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        isReading = true;
    }

    private void setReadingDataOnOffload(ArrayList<OnOffLoadDto> onOffLoadDtos,
                                         ArrayList<OnOffLoadDto> onOffLoadDtosTemp) {
        this.readingData.onOffLoadDtos.clear();
        this.readingData.onOffLoadDtos.addAll(onOffLoadDtos);
        this.readingDataTemp.onOffLoadDtos.clear();
        this.readingDataTemp.onOffLoadDtos.addAll(onOffLoadDtosTemp);
    }

    private void setReadingData(ReadingData readingData, ReadingData readingDataTemp) {
        setReadingDataOnOffload(readingData.onOffLoadDtos, readingDataTemp.onOffLoadDtos);
        this.readingData.qotrDictionary.clear();
        this.readingData.qotrDictionary.addAll(readingData.qotrDictionary);
        this.readingData.karbariDtos.clear();
        this.readingData.karbariDtos.addAll(readingData.karbariDtos);
        this.readingData.trackingDtos.clear();
        this.readingData.trackingDtos.addAll(readingData.trackingDtos);
        this.readingData.readingConfigDefaultDtos.clear();
        this.readingData.readingConfigDefaultDtos.addAll(readingData.readingConfigDefaultDtos);
        this.readingData.counterReportDtos.clear();
        this.readingData.counterReportDtos.addAll(readingData.counterReportDtos);
        this.readingData.counterStateDtos.clear();
        this.readingData.counterStateDtos.addAll(readingData.counterStateDtos);

        this.readingDataTemp.qotrDictionary.clear();
        this.readingDataTemp.qotrDictionary.addAll(readingDataTemp.qotrDictionary);
        this.readingDataTemp.karbariDtos.clear();
        this.readingDataTemp.karbariDtos.addAll(readingDataTemp.karbariDtos);
        this.readingDataTemp.trackingDtos.clear();
        this.readingDataTemp.trackingDtos.addAll(readingDataTemp.trackingDtos);
        this.readingDataTemp.readingConfigDefaultDtos.clear();
        this.readingDataTemp.readingConfigDefaultDtos.addAll(readingDataTemp.readingConfigDefaultDtos);
        this.readingDataTemp.counterReportDtos.clear();
        this.readingDataTemp.counterReportDtos.addAll(readingDataTemp.counterReportDtos);
        this.readingDataTemp.counterStateDtos.clear();
        this.readingDataTemp.counterStateDtos.addAll(readingDataTemp.counterStateDtos);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void dataChanged(ArrayList<OnOffLoadDto> onOffLoadDtos, ArrayList<OnOffLoadDto> onOffLoadDtosTemp) {
        setReadingDataOnOffload(onOffLoadDtos, onOffLoadDtosTemp);
        setupViewPagerAdapter(0);
    }

    public void setupViewPagerAdapter(int currentItem) {
        runOnUiThread(() -> {
            viewPagerAdapterReading = new ViewPagerAdapterReading2(readingData,
                    readingDataTemp.onOffLoadDtos ,activity);
            try {
                binding.viewPager.setOffscreenPageLimit(1);
                binding.viewPager.setAdapter(viewPagerAdapterReading);
                if (currentItem > 0)
                    binding.viewPager.setCurrentItem(currentItem);
            } catch (Exception e) {
                new CustomToast().error(MyApplication.getContext().getString(R.string.error_download_data), Toast.LENGTH_LONG);
            }
        });
    }

    private void setOnPageChangeListener() {
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                final String number = (position + 1) + "/" + readingData.onOffLoadDtos.size();
                runOnUiThread(() -> binding.textViewPageNumber.setText(number));
                setAboveIconsSrc(position);
            }
        });
    }

    private void showImage(int position) {
        Intent intent = new Intent(activity, TakePhotoActivity.class);
        intent.putExtra(BundleEnum.BILL_ID.getValue(),
                readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).id);
        intent.putExtra(BundleEnum.TRACKING.getValue(),
                readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).trackNumber);
        intent.putExtra(BundleEnum.POSITION.getValue(), position);
        intent.putExtra(BundleEnum.IMAGE.getValue(), true);
        intent.putExtra(BundleEnum.SENT.getValue(),
                readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).offLoadStateId > 0);
        startActivityForResult(intent, CAMERA);
    }

    private void attemptSend(int position, boolean isForm, boolean isImage) {
        if (isForm
                && (sharedPreferenceManager.getBoolData(SharedReferenceKeys.SERIAL.getValue())
                || sharedPreferenceManager.getBoolData(SharedReferenceKeys.AHAD_2.getValue())
                || sharedPreferenceManager.getBoolData(SharedReferenceKeys.AHAD_1.getValue())
                || sharedPreferenceManager.getBoolData(SharedReferenceKeys.AHAD_TOTAL.getValue())
                || sharedPreferenceManager.getBoolData(SharedReferenceKeys.AHAD_EMPTY.getValue())
                || sharedPreferenceManager.getBoolData(SharedReferenceKeys.KARBARI.getValue())
                || sharedPreferenceManager.getBoolData(SharedReferenceKeys.ADDRESS.getValue())
                || sharedPreferenceManager.getBoolData(SharedReferenceKeys.ACCOUNT.getValue())
                || sharedPreferenceManager.getBoolData(SharedReferenceKeys.READING_REPORT.getValue())
                || sharedPreferenceManager.getBoolData(SharedReferenceKeys.MOBILE.getValue()))) {
            showPossible(position);
        } else if (isImage && sharedPreferenceManager.getBoolData(SharedReferenceKeys.IMAGE.getValue())) {
            showImage(position);
        } else {
            if (!isShowing) {
                CounterStateDto counterStateDto = readingData.counterStateDtos.get(readingData.onOffLoadDtos.get(position).counterStatePosition);
                if ((counterStateDto.isTavizi || counterStateDto.isXarab) &&
                        counterStateDto.moshtarakinId != readingData.onOffLoadDtos.get(position).preCounterStateCode) {
                    SerialFragment serialFragment = SerialFragment.newInstance(position,
                            counterStateDto.id, readingData.onOffLoadDtos.get(position).counterStatePosition);
                    serialFragment.show(getSupportFragmentManager(), getString(R.string.counter_serial));
                } else isShowing = true;
            }
            if (isShowing) {
                isShowing = false;
                makeRing(activity, NotificationType.SAVE);
                new Update(position, getLocationTracker(activity).getCurrentLocation())
                        .execute(activity);
                new PrepareToSend(sharedPreferenceManager
                        .getStringData(SharedReferenceKeys.TOKEN.getValue())).execute(activity);
                changePage(binding.viewPager.getCurrentItem() + 1);
            }
        }
    }

    private void showPossible(int position) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        PossibleFragment possibleFragment = PossibleFragment.newInstance(
                readingData.onOffLoadDtos.get(position), position, false);
        possibleFragment.show(fragmentTransaction, getString(R.string.dynamic_navigation));
    }

    private void setAboveIconsSrc(int position) {
        if (readingData.onOffLoadDtos != null) {
            runOnUiThread(() -> {
                setHighLowImage(position);
                setReadStatusImage(position);
                setExceptionImage(position);
                setIsBazdidImage(position);
            });
        }
    }

    private void setExceptionImage(int position) {
        int src = ReadingUtils.setExceptionImage(readingData, position);
        binding.imageViewExceptionState.setVisibility(View.GONE);
        if (src > -1) {
            binding.imageViewExceptionState.setVisibility(View.VISIBLE);
            binding.imageViewExceptionState.setImageResource(imageSrc[src]);
        }
    }

    private void setIsBazdidImage(int position) {
        if (readingData.onOffLoadDtos.get(position).isBazdid)
            binding.imageViewReadingType.setImageResource(imageSrc[6]);
        else binding.imageViewReadingType.setImageResource(imageSrc[7]);
    }

    private void setReadStatusImage(int position) {
        binding.imageViewOffLoadState.setImageResource(imageSrc[readingData.onOffLoadDtos
                .get(position).offLoadStateId]);
        if (readingData.onOffLoadDtos.get(position).offLoadStateId == 0)
            binding.imageViewOffLoadState.setImageResource(imageSrc[8]);
    }

    private void setHighLowImage(int position) {
        binding.imageViewHighLowState.setImageResource(imageSrc[readingData.onOffLoadDtos
                .get(position).highLowStateId]);
    }

    private void showNoEshterakFound() {
        new CustomDialogModel(DialogType.Yellow, activity, getString(R.string.no_eshterak_found),
                getString(R.string.dear_user), getString(R.string.eshterak),
                getString(R.string.accepted));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reading_menu, menu);
        menu.getItem(5).setChecked(FOCUS_ON_EDIT_TEXT);
        menu.getItem(6).setChecked(MyApplication.getApplicationComponent()
                .SharedPreferenceModel().getBoolData(SharedReferenceKeys.SORT_TYPE.getValue()));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        if (id == R.id.menu_sort) {
            item.setChecked(!item.isChecked());
            sharedPreferenceManager.putData(SharedReferenceKeys.SORT_TYPE.getValue(), item.isChecked());
            new ChangeSortType(activity, item.isChecked(), readingData.onOffLoadDtos,
                    readingDataTemp.onOffLoadDtos).execute(activity);
        } else if (id == R.id.menu_navigation) {
            if (readingDataTemp.onOffLoadDtos.isEmpty()) {
                showNoEshterakFound();
            } else {
                intent = new Intent(activity, NavigationActivity.class);
                intent.putExtra(BundleEnum.POSITION.getValue(), binding.viewPager.getCurrentItem());

                Gson gson = new Gson();
                String json1 = gson.toJson(readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()));
                intent.putExtra(BundleEnum.ON_OFF_LOAD.getValue(), json1);
                startActivityForResult(intent, NAVIGATION);
            }
        } else if (id == R.id.menu_report_forbid) {
            intent = new Intent(activity, ReportForbidActivity.class);
            if (readingData.onOffLoadDtos.size() > 0)
                intent.putExtra(BundleEnum.ZONE_ID.getValue(), readingData.onOffLoadDtos.
                        get(binding.viewPager.getCurrentItem()).zoneId);
            startActivity(intent);
        } else if (id == R.id.menu_description) {
            if (readingDataTemp.onOffLoadDtos.isEmpty()) {
                showNoEshterakFound();
            } else {
                intent = new Intent(activity, DescriptionActivity.class);
                intent.putExtra(BundleEnum.BILL_ID.getValue(),
                        readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).id);
                intent.putExtra(BundleEnum.TRACKING.getValue(),
                        readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).trackNumber);
                intent.putExtra(BundleEnum.DESCRIPTION.getValue(),
                        readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).description);
                intent.putExtra(BundleEnum.POSITION.getValue(), binding.viewPager.getCurrentItem());
                startActivityForResult(intent, DESCRIPTION);
            }
        }
        if (id == R.id.menu_location) {
            if (readingDataTemp.onOffLoadDtos.isEmpty()) {
                showNoEshterakFound();
            } else {
                intent = new Intent(activity, CounterPlaceActivity.class);
                intent.putExtra(BundleEnum.BILL_ID.getValue(),
                        readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).id);
                intent.putExtra(BundleEnum.POSITION.getValue(), binding.viewPager.getCurrentItem());
                startActivityForResult(intent, COUNTER_LOCATION);
            }
        } else if (id == R.id.menu_keyboard) {
            if (readingData.onOffLoadDtos.isEmpty()) {
                showNoEshterakFound();
            } else {
                item.setChecked(!item.isChecked());
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (FOCUS_ON_EDIT_TEXT) {
                    try {
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (!inputMethodManager.isAcceptingText()) {
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
                FOCUS_ON_EDIT_TEXT = !FOCUS_ON_EDIT_TEXT;
            }
        } else if (id == R.id.menu_last) {
            if (readingData.onOffLoadDtos.isEmpty()) {
                showNoEshterakFound();
            } else {
                int currentItem = 0, i = 0;
                for (OnOffLoadDto onOffLoadDto : readingData.onOffLoadDtos) {
                    if (!onOffLoadDto.isBazdid) {
                        currentItem = i;
                        break;
                    }
                    i++;
                }
                binding.viewPager.setCurrentItem(currentItem);
            }
        } else if (id == R.id.menu_verification) {
            TwoStepVerification.showPersonalCode(activity);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REPORT || requestCode == NAVIGATION ||
                requestCode == DESCRIPTION ||
                requestCode == COUNTER_LOCATION) && resultCode == RESULT_OK) {
            new Result(data).execute(activity);

        } else if (requestCode == CAMERA && resultCode == RESULT_OK) {
            int position = data.getExtras().getInt(BundleEnum.POSITION.getValue());
            attemptSend(position, false, false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isReading && !readingData.onOffLoadDtos.isEmpty() && FOCUS_ON_EDIT_TEXT) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        try {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            ImageView imageViewFlash = findViewById(R.id.image_view_flash);
            imageViewFlash.setImageDrawable(
                    AppCompatResources.getDrawable(activity, R.drawable.img_flash_off));
            flashLightManager.turnOff();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Debug.getNativeHeapAllocatedSize();
        System.runFinalization();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Runtime.getRuntime().gc();
        System.gc();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewPagerAdapterReading = null;
        ImageView imageViewFlash = findViewById(R.id.image_view_flash);
        imageViewFlash.setImageDrawable(null);
        ImageView imageViewReverse = findViewById(R.id.image_view_reverse);
        imageViewReverse.setImageDrawable(null);
        ImageView imageViewCamera = findViewById(R.id.image_view_camera);
        imageViewCamera.setImageDrawable(null);
        ImageView imageViewSearch = findViewById(R.id.image_view_search);
        imageViewSearch.setImageDrawable(null);
        ImageView imageViewCheck = findViewById(R.id.image_view_reading_report);
        imageViewCheck.setImageDrawable(null);
        binding.imageViewHighLowState.setImageDrawable(null);
        binding.imageViewOffLoadState.setImageDrawable(null);
        binding.imageViewReadingType.setImageDrawable(null);
        binding.imageViewExceptionState.setImageDrawable(null);
        this.readingData.onOffLoadDtos.clear();
        this.readingData.qotrDictionary.clear();
        this.readingData.karbariDtos.clear();
        this.readingData.trackingDtos.clear();
        this.readingData.readingConfigDefaultDtos.clear();
        this.readingData.counterReportDtos.clear();
        this.readingData.counterStateDtos.clear();
        this.readingDataTemp.onOffLoadDtos.clear();
        this.readingDataTemp.qotrDictionary.clear();
        this.readingDataTemp.karbariDtos.clear();
        this.readingDataTemp.trackingDtos.clear();
        this.readingDataTemp.readingConfigDefaultDtos.clear();
        this.readingDataTemp.counterReportDtos.clear();
        this.readingDataTemp.counterStateDtos.clear();
        Debug.getNativeHeapAllocatedSize();
        System.runFinalization();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Runtime.getRuntime().gc();
        System.gc();
    }
}