package com.leon.counter_reading.activities;

import static com.leon.counter_reading.enums.BundleEnum.BILL_ID;
import static com.leon.counter_reading.enums.BundleEnum.IS_MANE;
import static com.leon.counter_reading.enums.BundleEnum.POSITION;
import static com.leon.counter_reading.enums.BundleEnum.READ_STATUS;
import static com.leon.counter_reading.enums.BundleEnum.TRACKING;
import static com.leon.counter_reading.enums.BundleEnum.TYPE;
import static com.leon.counter_reading.enums.DialogType.Red;
import static com.leon.counter_reading.enums.DialogType.Yellow;
import static com.leon.counter_reading.enums.FragmentTags.REPORT_FORBID;
import static com.leon.counter_reading.enums.FragmentTags.SEARCH;
import static com.leon.counter_reading.enums.FragmentTags.TAKE_PHOTO;
import static com.leon.counter_reading.enums.NotificationType.LIGHT_OFF;
import static com.leon.counter_reading.enums.NotificationType.LIGHT_ON;
import static com.leon.counter_reading.enums.NotificationType.SAVE;
import static com.leon.counter_reading.enums.OffloadStateEnum.INSERTED;
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
import static com.leon.counter_reading.enums.SharedReferenceKeys.RTL_PAGING;
import static com.leon.counter_reading.enums.SharedReferenceKeys.SERIAL;
import static com.leon.counter_reading.enums.SharedReferenceKeys.SORT_TYPE;
import static com.leon.counter_reading.enums.SharedReferenceKeys.THEME_TEMPORARY;
import static com.leon.counter_reading.enums.SharedReferenceKeys.TOKEN;
import static com.leon.counter_reading.fragments.dialog.ShowFragmentDialog.ShowDialogOnce;
import static com.leon.counter_reading.helpers.Constants.CAMERA;
import static com.leon.counter_reading.helpers.Constants.COUNTER_PLACE;
import static com.leon.counter_reading.helpers.Constants.DESCRIPTION;
import static com.leon.counter_reading.helpers.Constants.NAVIGATION;
import static com.leon.counter_reading.helpers.Constants.OFFLINE_ATTEMPT;
import static com.leon.counter_reading.helpers.Constants.REPORT;
import static com.leon.counter_reading.helpers.Constants.onOffLoadDtos;
import static com.leon.counter_reading.helpers.Constants.readingData;
import static com.leon.counter_reading.helpers.Constants.readingDataTemp;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.getContext;
import static com.leon.counter_reading.helpers.MyApplication.getLocationTracker;
import static com.leon.counter_reading.utils.MakeNotification.makeRing;
import static com.leon.counter_reading.utils.login.TwoStepVerification.showPersonalCode;
import static com.leon.counter_reading.utils.reading.ReadingUtils.setAboveIcons;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Debug;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.ViewPagerStateAdapter2;
import com.leon.counter_reading.base_items.BaseActivity;
import com.leon.counter_reading.databinding.ActivityReadingBinding;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.FragmentTags;
import com.leon.counter_reading.fragments.dialog.CounterPlaceFragment;
import com.leon.counter_reading.fragments.dialog.NavigationFragment;
import com.leon.counter_reading.fragments.dialog.PossibleFragment;
import com.leon.counter_reading.fragments.dialog.ReadingReportFragment;
import com.leon.counter_reading.fragments.dialog.ReportForbidFragment;
import com.leon.counter_reading.fragments.dialog.SearchFragment;
import com.leon.counter_reading.fragments.dialog.SerialFragment;
import com.leon.counter_reading.fragments.dialog.TakePhotoFragment;
import com.leon.counter_reading.infrastructure.IFlashLightManager;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.tables.CounterStateDto;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.DepthPageTransformer2;
import com.leon.counter_reading.utils.reading.ChangeSortType;
import com.leon.counter_reading.utils.reading.DataResult;
import com.leon.counter_reading.utils.reading.GetBundle;
import com.leon.counter_reading.utils.reading.GetReadingDBData;
import com.leon.counter_reading.utils.reading.PrepareToSend;
import com.leon.counter_reading.utils.reading.ReadingUtils;
import com.leon.counter_reading.utils.reading.Result;
import com.leon.counter_reading.utils.reading.Search;
import com.leon.counter_reading.utils.reading.Update;
import com.leon.counter_reading.utils.reading.UpdateOnOffLoadByAttemptNumber;
import com.leon.counter_reading.utils.reading.UpdateOnOffLoadByIsShown;
import com.leon.counter_reading.utils.reading.UpdateOnOffLoadDtoByLock;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;

public class ReadingActivity extends BaseActivity implements ReadingReportFragment.Callback,
        CounterPlaceFragment.Callback, NavigationFragment.Callback {
    public static int offlineAttempts = 0;

    private ISharedPreferenceManager sharedPreferenceManager;
    private ViewPagerStateAdapter2 viewPagerAdapterReading;
    private IFlashLightManager flashLightManager;
    private ActivityReadingBinding binding;
    private int readStatus = 0, highLow = 1;
    private boolean isShowing = false;
    private int[] imageSrc;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }

    @Override
    protected void initialize() {
        binding = ActivityReadingBinding.inflate(getLayoutInflater());
        final View childLayout = binding.getRoot();
        final ConstraintLayout parentLayout = findViewById(R.id.base_Content);
        parentLayout.addView(childLayout);
        //TODO
//        AppCompatDelegate.setDefaultNightMode(getApplicationComponent().SharedPreferenceModel()
//                .getBoolData(THEME_TEMPORARY.getValue()) ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        getDelegate().setLocalNightMode(getApplicationComponent().SharedPreferenceModel()
                .getBoolData(THEME_TEMPORARY.getValue()) ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        sharedPreferenceManager = getApplicationComponent().SharedPreferenceModel();
        imageSrc = ArrayUtils.clone(setAboveIcons());
        getBundle();
        setOnImageViewsClickListener();
        new GetReadingDBData(this, readStatus, highLow, sharedPreferenceManager.
                getBoolData(SORT_TYPE.getValue())).execute(this);
    }

    private void updateOnOffLoad(int position, int counterStateCode, int counterStatePosition) {
        readingData.onOffLoadDtos.get(position).isBazdid = true;
        readingData.onOffLoadDtos.get(position).offLoadStateId = INSERTED.getValue();
        readingData.onOffLoadDtos.get(position).counterStatePosition = counterStatePosition;
        readingData.onOffLoadDtos.get(position).counterStateId = counterStateCode;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateAdapter(int position) {
        try {
            int i = 0;
            boolean found = false;
            while (!found && i < readingDataTemp.onOffLoadDtos.size()) {
                if (readingDataTemp.onOffLoadDtos.get(i).id.equals(readingData.onOffLoadDtos.get(position).id)) {
                    readingDataTemp.onOffLoadDtos.set(i, readingData.onOffLoadDtos.get(position));
                    found = true;
                }
                i++;
            }
            onOffLoadDtos.set(position, readingData.onOffLoadDtos.get(position));
            runOnUiThread(() -> viewPagerAdapterReading.notifyDataSetChanged());
        } catch (Exception e) {
            runOnUiThread(() -> new CustomDialogModel(Red, this, e.getMessage(),
                    getString(R.string.dear_user), getString(R.string.take_screen_shot),
                    getString(R.string.accepted)));
        }
    }

    public void updateOnOffLoadByPreNumber(int position) {
        readingData.onOffLoadDtos.get(position).counterNumberShown = true;
        readingData.onOffLoadDtos.get(position).isBazdid = true;
        new UpdateOnOffLoadByIsShown(readingData.onOffLoadDtos.get(position)).execute();
        updateAdapter(position);
    }

    public void updateOnOffLoadByAttempt(int position, boolean... booleans) {
        if (booleans != null && booleans.length > 0)
            readingData.onOffLoadDtos.get(position).attemptCount--;
        new UpdateOnOffLoadByAttemptNumber(readingData.onOffLoadDtos.get(position)).execute();
        updateAdapter(position);
    }

    public void updateOnOffLoadByLock(int position) {
        readingData.onOffLoadDtos.get(position).isLocked = true;
        new UpdateOnOffLoadDtoByLock(readingData.onOffLoadDtos.get(position)).execute();
        updateAdapter(position);
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

    public void updateOnOffLoadByNumber(int position, int number, int counterStateCode,
                                        int counterStatePosition) {
        updateOnOffLoad(position, counterStateCode, counterStatePosition);
        readingData.onOffLoadDtos.get(position).counterNumber = number;
        attemptSend(position, true, true);
    }

    public void updateOnOffLoadByNumber(int position, int number, int counterStateCode,
                                        int counterStatePosition, int type) {
        readingData.onOffLoadDtos.get(position).highLowStateId = type;
        updateOnOffLoadByNumber(position, number, counterStateCode, counterStatePosition);
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
            runOnUiThread(() -> new CustomDialogModel(Red, this, e.getMessage(),
                    getString(R.string.dear_user), getString(R.string.take_screen_shot),
                    getString(R.string.accepted)));
        }
    }

    public boolean searchResult;

    public boolean search(int type, String key, boolean goToPage) {
        if (type == PAGE_NUMBER.getValue()) {
            runOnUiThread(() -> binding.viewPager.setCurrentItem(Integer.parseInt(key) - 1));
        } else if (type == All.getValue()) {
            readingData.onOffLoadDtos.clear();
            readingData.onOffLoadDtos.addAll(readingDataTemp.onOffLoadDtos);
            runOnUiThread(this::setupViewPager);
        } else {
            new Search(type, key, goToPage).execute(this);
            return searchResult;
        }
        return true;
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
        setupViewPagerAdapter();
    }

    private void setupViewPagerAdapter() {
        runOnUiThread(() -> {
            viewPagerAdapterReading = new ViewPagerStateAdapter2(this, readingData);
            try {
                binding.viewPager.setOffscreenPageLimit(1);
                binding.viewPager.setAdapter(viewPagerAdapterReading);
                final RecyclerView recyclerView = ((RecyclerView) (binding.viewPager.getChildAt(0)));
                if (recyclerView != null) {
                    recyclerView.setItemViewCacheSize(0);
                }
                if (getApplicationComponent().SharedPreferenceModel().getBoolData(RTL_PAGING.getValue()))
                    binding.viewPager.setRotationY(180);
            } catch (Exception e) {
                new CustomToast().error(getContext().getString(R.string.error_download_data), Toast.LENGTH_LONG);
            }
        });
    }

    private void setOnPageChangeListener() {
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (getIntent().getExtras() != null)
                    getIntent().getExtras().clear();
                final String number = (binding.viewPager.getCurrentItem() + 1) + "/" + readingData.onOffLoadDtos.size();
                runOnUiThread(() -> binding.textViewPageNumber.setText(number));
                setAboveIconsSrc(position);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                try {
                    final FragmentManager manager = getSupportFragmentManager();
                    manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (readingData.onOffLoadDtos.get(position).isLocked)
                    new CustomToast().error(getString(R.string.by_mistakes)
                            .concat(readingData.onOffLoadDtos.get(position).eshterak)
                            .concat(getString(R.string.is_locked)), Toast.LENGTH_SHORT);
            }
        });
    }

    private void showImage(int position) {
        ShowDialogOnce(this, TAKE_PHOTO.getValue().concat(readingData.onOffLoadDtos
                .get(binding.viewPager.getCurrentItem()).id), TakePhotoFragment
                .newInstance(readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).offLoadStateId > 0,
                        readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).id,
                        readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).trackNumber,
                        position, true));
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
                    ShowDialogOnce(this, "SERIAL_DIALOG_".concat(readingData.onOffLoadDtos.get(position).eshterak),
                            SerialFragment.newInstance(position, counterStateDto.id,
                                    readingData.onOffLoadDtos.get(position).counterStatePosition));
                } else
                    isShowing = true;
            }
            if (isShowing) {
                updateAdapter(position);
                isShowing = false;
                makeRing(this, SAVE);
                Location location = null;
                try {
                    location = getLocationTracker(this).getCurrentLocation();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new Update(readingData.onOffLoadDtos.get(position), location).execute(this);
                if (offlineAttempts < OFFLINE_ATTEMPT)
                    new PrepareToSend(sharedPreferenceManager.getStringData(TOKEN.getValue()))
                            .execute(this);
                changePage(binding.viewPager.getCurrentItem() + 1);
            }
        }
    }

    private void showPossible(int position) {
        ShowDialogOnce(this, "SHOW_POSSIBLE_DIALOG_".concat(readingData.onOffLoadDtos.get(position).eshterak),
                PossibleFragment.newInstance(readingData.onOffLoadDtos.get(position), position, false));
    }

    private void showNoEshterakFound() {
        new CustomDialogModel(Yellow, this, getString(R.string.no_eshterak_found),
                getString(R.string.dear_user), getString(R.string.eshterak),
                getString(R.string.accepted));
    }

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
                makeRing(this, isOn ? LIGHT_ON : LIGHT_OFF);
                imageViewFlash.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),
                        isOn ? R.drawable.img_flash_on : R.drawable.img_flash_off));
            });

            final ImageView imageViewReverse = findViewById(R.id.image_view_reverse);
            imageViewReverse.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),
                    R.drawable.img_inverse));
            imageViewReverse.setOnClickListener(v -> {
                new CustomToast().warning("به دلیل تست، تغییر تم میسر نیست.");
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode() < 2 ?
//                        AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
//                getDelegate().setLocalNightMode(getDelegate().getLocalNightMode() < 2 ?
//                        AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            });

            final ImageView imageViewCamera = findViewById(R.id.image_view_camera);
            imageViewCamera.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),
                    R.drawable.img_camera));
            imageViewCamera.setOnClickListener(v -> {
                if (readingData.onOffLoadDtos.isEmpty()) {
                    showNoEshterakFound();
                } else {
                    final OnOffLoadDto onOffLoadDtoTemp = readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem());
                    ShowDialogOnce(this, TAKE_PHOTO.getValue().concat(onOffLoadDtoTemp.id),
                            TakePhotoFragment.newInstance(onOffLoadDtoTemp.offLoadStateId > 0,
                                    onOffLoadDtoTemp.id, onOffLoadDtoTemp.trackNumber));
                }
            });

            final ImageView imageViewCheck = findViewById(R.id.image_view_reading_report);
            imageViewCheck.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),
                    R.drawable.img_checked));
            imageViewCheck.setOnClickListener(v -> {
                if (readingData.onOffLoadDtos.isEmpty()) {
                    showNoEshterakFound();
                } else {
                    final OnOffLoadDto onOffLoadDtoTemp = readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem());
                    ShowDialogOnce(this, READING_REPORT.getValue(), ReadingReportFragment
                            .newInstance(onOffLoadDtoTemp.id, onOffLoadDtoTemp.trackNumber,
                                    binding.viewPager.getCurrentItem(), onOffLoadDtoTemp.zoneId));
                }
            });

            final ImageView imageViewSearch = findViewById(R.id.image_view_search);
            imageViewSearch.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),
                    R.drawable.img_search));
            imageViewSearch.setOnClickListener(v -> {
                if (readingDataTemp.onOffLoadDtos.isEmpty()) {
                    showNoEshterakFound();
                } else {
                    ShowDialogOnce(this, SEARCH.getValue(), new SearchFragment());
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
        final int id = item.getItemId();
        Intent intent;
        if (id == R.id.menu_sort) {
            item.setChecked(!item.isChecked());
            sharedPreferenceManager.putData(SORT_TYPE.getValue(), item.isChecked());
            new ChangeSortType(this, item.isChecked()).execute(this);
        } else if (id == R.id.menu_navigation) {
            if (readingData.onOffLoadDtos.isEmpty()) {
                showNoEshterakFound();
            } else {
                ShowDialogOnce(this, FragmentTags.NAVIGATION.getValue(), NavigationFragment
                        .newInstance(binding.viewPager.getCurrentItem()));
            }
        } else if (id == R.id.menu_report_forbid) {
            ShowDialogOnce(this, REPORT_FORBID.getValue(),
                    ReportForbidFragment.newInstance(readingData.onOffLoadDtos.size() > 0 ?
                            readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).zoneId : 0));
        } else if (id == R.id.menu_description) {
            if (readingData.onOffLoadDtos.isEmpty()) {
                showNoEshterakFound();
            } else {
                intent = new Intent(this, DescriptionActivity.class);
                final OnOffLoadDto onOffLoadDtoTemp = readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem());
                intent.putExtra(BILL_ID.getValue(), onOffLoadDtoTemp.id);
                intent.putExtra(TRACKING.getValue(), onOffLoadDtoTemp.trackNumber);
                intent.putExtra(BundleEnum.DESCRIPTION.getValue(), onOffLoadDtoTemp.description);
                intent.putExtra(POSITION.getValue(), binding.viewPager.getCurrentItem());
                startActivityForResult(intent, DESCRIPTION);
            }
        }
        if (id == R.id.menu_location) {
            if (readingData.onOffLoadDtos.isEmpty()) {
                showNoEshterakFound();
            } else {
                ShowDialogOnce(this, FragmentTags.COUNTER_PLACE.getValue(), CounterPlaceFragment
                        .newInstance(readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).id,
                                binding.viewPager.getCurrentItem()));
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
            showPersonalCode(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REPORT || requestCode == NAVIGATION || requestCode == DESCRIPTION ||
                requestCode == COUNTER_PLACE) && resultCode == RESULT_OK) {
            new DataResult(data).execute(this);
        } else if (requestCode == CAMERA && resultCode == RESULT_OK) {
            int position = data.getExtras().getInt(POSITION.getValue());
            attemptSend(position, false, false);
        }
    }

    @Override
    public void setResult(int position, String uuid) {
        new Result(position, uuid).execute(this);
    }

    public void setPhotoResult(int position) {
        attemptSend(position, false, false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            final ImageView imageViewFlash = findViewById(R.id.image_view_flash);
            imageViewFlash.setImageDrawable(AppCompatResources.getDrawable(this,
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