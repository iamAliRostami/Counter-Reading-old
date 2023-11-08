package com.leon.counter_reading.activities;

import static com.leon.counter_reading.enums.BundleEnum.BILL_ID;
import static com.leon.counter_reading.enums.BundleEnum.DESCRIPTION;
import static com.leon.counter_reading.enums.BundleEnum.IS_MANE;
import static com.leon.counter_reading.enums.BundleEnum.POSITION;
import static com.leon.counter_reading.enums.BundleEnum.READ_STATUS;
import static com.leon.counter_reading.enums.BundleEnum.TRACKING;
import static com.leon.counter_reading.enums.BundleEnum.TYPE;
import static com.leon.counter_reading.enums.DialogType.Red;
import static com.leon.counter_reading.enums.DialogType.Yellow;
import static com.leon.counter_reading.enums.FragmentTags.AHAD;
import static com.leon.counter_reading.enums.FragmentTags.LAST_READ;
import static com.leon.counter_reading.enums.FragmentTags.NAVIGATION;
import static com.leon.counter_reading.enums.FragmentTags.POSSIBLE_DIALOG;
import static com.leon.counter_reading.enums.FragmentTags.REPORT_FORBID;
import static com.leon.counter_reading.enums.FragmentTags.SEARCH;
import static com.leon.counter_reading.enums.FragmentTags.SERIAL_DIALOG;
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
import static com.leon.counter_reading.enums.SharedReferenceKeys.GUILD;
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
import static com.leon.counter_reading.helpers.Constants.MAX_OFFLINE_ATTEMPT;
import static com.leon.counter_reading.helpers.Constants.currentOfflineAttempts;
import static com.leon.counter_reading.helpers.Constants.karbariDtos;
import static com.leon.counter_reading.helpers.Constants.onOffLoadDtos;
import static com.leon.counter_reading.helpers.Constants.readingData;
import static com.leon.counter_reading.helpers.Constants.readingDataTemp;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.getContext;
import static com.leon.counter_reading.utils.MakeNotification.makeRing;
import static com.leon.counter_reading.utils.MakeNotification.makeVibrate;
import static com.leon.counter_reading.utils.login.TwoStepVerification.showPersonalCode;
import static com.leon.counter_reading.utils.reading.ReadingUtils.setAboveIcons;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.leon.counter_reading.enums.FragmentTags;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.fragments.ReadingFragment;
import com.leon.counter_reading.fragments.dialog.AhadFragment;
import com.leon.counter_reading.fragments.dialog.AreYouSureFragment;
import com.leon.counter_reading.fragments.dialog.CounterPlaceFragment;
import com.leon.counter_reading.fragments.dialog.KarbariFragment;
import com.leon.counter_reading.fragments.dialog.LastReadFragment;
import com.leon.counter_reading.fragments.dialog.NavigationFragment;
import com.leon.counter_reading.fragments.dialog.PossibleFragment;
import com.leon.counter_reading.fragments.dialog.ReadingReportFragment;
import com.leon.counter_reading.fragments.dialog.ReportForbidFragment;
import com.leon.counter_reading.fragments.dialog.SearchFragment;
import com.leon.counter_reading.fragments.dialog.SerialFragment;
import com.leon.counter_reading.fragments.dialog.TakePhotoFragment;
import com.leon.counter_reading.helpers.Constants;
import com.leon.counter_reading.infrastructure.IFlashLightManager;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.tables.CounterStateDto;
import com.leon.counter_reading.tables.OffLoadReport;
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

import java.util.ArrayList;

public class ReadingActivity extends BaseActivity implements View.OnClickListener,
        ReadingReportFragment.Callback, CounterPlaceFragment.Callback, NavigationFragment.Callback,
        ReadingFragment.Callback, TakePhotoFragment.Callback, SerialFragment.Callback,
        AreYouSureFragment.Callback, PossibleFragment.Callback, LastReadFragment.ICallback {
    private ActivityReadingBinding binding;
    private ISharedPreferenceManager sharedPreferenceManager;
    private ViewPagerStateAdapter2 adapter;
    private IFlashLightManager flashLightManager;
    private int readStatus = 0, highLow = 1;
    private boolean isShowing = false;
    private int[] imageSrc;
    public boolean searchResult;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }

    @Override
    protected void initialize() {
        getDelegate().setLocalNightMode(getApplicationComponent().SharedPreferenceModel()
                .getBoolData(THEME_TEMPORARY.getValue()) ? AppCompatDelegate.MODE_NIGHT_YES :
                AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityReadingBinding.inflate(getLayoutInflater());
        View childLayout = binding.getRoot();
        ConstraintLayout parentLayout = findViewById(R.id.base_Content);
        parentLayout.addView(childLayout);
        sharedPreferenceManager = getApplicationComponent().SharedPreferenceModel();
        imageSrc = setAboveIcons();
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
            runOnUiThread(() -> adapter.notifyDataSetChanged());
        } catch (Exception e) {
            runOnUiThread(() -> new CustomDialogModel(Red, this, e.getMessage(),
                    getString(R.string.dear_user), getString(R.string.take_screen_shot),
                    getString(R.string.accepted)));
        }
    }

    @Override
    public void updateOnOffLoadByPreNumber(int position) {
        readingData.onOffLoadDtos.get(position).counterNumberShown = true;
        readingData.onOffLoadDtos.get(position).isBazdid = true;
        new UpdateOnOffLoadByIsShown(readingData.onOffLoadDtos.get(position)).execute();
        updateAdapter(position);
    }

    @Override
    public void updateOnOffLoadByAttempt(int position, boolean... booleans) {
        if (booleans != null && booleans.length > 0)
            readingData.onOffLoadDtos.get(position).attemptCount--;
        new UpdateOnOffLoadByAttemptNumber(readingData.onOffLoadDtos.get(position)).execute();
        updateAdapter(position);
    }

    @Override
    public void updateOnOffLoadByLock(int position) {
        readingData.onOffLoadDtos.get(position).isLocked = true;
        new UpdateOnOffLoadDtoByLock(readingData.onOffLoadDtos.get(position)).execute();
        updateAdapter(position);
    }

    @Override
    public void updateOnOffLoadWithoutNumber(int position, int counterStateCode,
                                             int counterStatePosition) {
        readingData.onOffLoadDtos.get(position).counterNumber = null;
        updateOnOffLoad(position, counterStateCode, counterStatePosition);
        attemptSend(position, true, true);
    }

    @Override
    public void updateOnOffLoadByCounterSerial(int position, int counterStatePosition,
                                               int counterStateCode, String counterSerial) {
        updateOnOffLoad(position, counterStateCode, counterStatePosition);
        readingData.onOffLoadDtos.get(position).possibleCounterSerial = counterSerial;
        isShowing = true;
        attemptSend(position, false, false);
    }

    @Override
    public void updateOnOffLoadByNumber(int position, int number, int counterStateCode,
                                        int counterStatePosition) {
        updateOnOffLoad(position, counterStateCode, counterStatePosition);
        readingData.onOffLoadDtos.get(position).counterNumber = number;
        attemptSend(position, true, true);
    }

    @Override
    public void updateOnOffLoadByNumber(int position, int number, int counterStateCode,
                                        int counterStatePosition, int type) {
        readingData.onOffLoadDtos.get(position).highLowStateId = type;
        updateOnOffLoadByNumber(position, number, counterStateCode, counterStatePosition);
    }

    @Override
    public void updateOnOffLoadByNavigation(boolean justMobile, int position, OnOffLoadDto onOffLoadDto) {
        readingData.onOffLoadDtos.get(position).possibleMobile = onOffLoadDto.possibleMobile;
        readingData.onOffLoadDtos.get(position).guildId = onOffLoadDto.guildId;
        if (justMobile) return;
        readingData.onOffLoadDtos.get(position).possibleCounterSerial = onOffLoadDto.possibleCounterSerial;
        readingData.onOffLoadDtos.get(position).possibleKarbariCode = onOffLoadDto.possibleKarbariCode;
        readingData.onOffLoadDtos.get(position).possibleAhadTejariOrFari = onOffLoadDto.possibleAhadTejariOrFari;
        readingData.onOffLoadDtos.get(position).possibleAhadMaskooniOrAsli = onOffLoadDto.possibleAhadMaskooniOrAsli;
        readingData.onOffLoadDtos.get(position).possibleAhadSaierOrAbBaha = onOffLoadDto.possibleAhadSaierOrAbBaha;
        readingData.onOffLoadDtos.get(position).possibleEmpty = onOffLoadDto.possibleEmpty;
        readingData.onOffLoadDtos.get(position).description = onOffLoadDto.description;
        readingData.onOffLoadDtos.get(position).possibleAddress = onOffLoadDto.possibleAddress;
        readingData.onOffLoadDtos.get(position).possibleEshterak = onOffLoadDto.possibleEshterak;
        attemptSend(position, false, true);
    }

    public void changePage(int pageNumber) {
        try {
            runOnUiThread(() -> {
                if (pageNumber < readingData.onOffLoadDtos.size())
                    binding.viewPager.setCurrentItem(pageNumber, false);
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

    public boolean search(int type, String key, boolean goToPage) {
        if (type == PAGE_NUMBER.getValue()) {
            runOnUiThread(() -> binding.viewPager.setCurrentItem(Integer.parseInt(key) - 1, false));
        } else if (type == All.getValue()) {
            readingData.onOffLoadDtos.clear();
            readingData.onOffLoadDtos.addAll(readingDataTemp.onOffLoadDtos);
            runOnUiThread(() -> setupViewPager(false));
        } else {
            new Search(type, key, goToPage).execute(this);
            return searchResult;
        }
        return true;
    }

    public void setupViewPager(boolean lastRead) {
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
        setupViewPagerAdapter(lastRead);
    }

    private void setupViewPagerAdapter(boolean lastRead) {
        adapter = new ViewPagerStateAdapter2(this, readingData);
        runOnUiThread(() -> {
            try {
                binding.viewPager.setOffscreenPageLimit(1);
                if (lastRead)
                    ShowDialogOnce(this, LAST_READ.getValue(), LastReadFragment.newInstance());
                else {
                    binding.viewPager.setAdapter(adapter);
                    if (sharedPreferenceManager.getBoolData(SharedReferenceKeys.LAST_READ.getValue()))
                        goLastRead();
                }
                RecyclerView recyclerView = ((RecyclerView) (binding.viewPager.getChildAt(0)));
                if (recyclerView != null) recyclerView.setItemViewCacheSize(0);
                if (getApplicationComponent().SharedPreferenceModel().getBoolData(RTL_PAGING.getValue()))
                    binding.viewPager.setRotationY(180);
            } catch (Exception e) {
                e.printStackTrace();
                new CustomToast().error(getContext().getString(R.string.error_download_data), Toast.LENGTH_LONG);
            }
        });
    }
    @Override
    public void goLastRead() {
        setAdapter();
        int page = 0;
        for (int i = 1; i < readingData.onOffLoadDtos.size(); i++) {
//            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy MM dd HH:mm:ss:SSS");
            if (readingData.onOffLoadDtos.get(page).phoneDateTime == null &&
                    readingData.onOffLoadDtos.get(i).phoneDateTime != null) {
                page = i;
            } else if (readingData.onOffLoadDtos.get(page).phoneDateTime != null &&
                    readingData.onOffLoadDtos.get(i).phoneDateTime != null &&
                    readingData.onOffLoadDtos.get(i).phoneDateTime.compareTo(readingData.onOffLoadDtos.get(page).phoneDateTime) > 0
//                    readingData.onOffLoadDtos.get(i).phoneDateTime > readingData.onOffLoadDtos.get(page).phoneDateTime
            ) {
                Log.e("first day", readingData.onOffLoadDtos.get(page).phoneDateTime);
                Log.e("second day", readingData.onOffLoadDtos.get(i).phoneDateTime);
                page = i;
            }
        }
        binding.viewPager.setCurrentItem(page, false);
    }

    @Override
    public void setAdapter() {
        binding.viewPager.setAdapter(adapter);
    }
    private void setOnPageChangeListener() {
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (getIntent().getExtras() != null)
                    getIntent().getExtras().clear();
                String number = (binding.viewPager.getCurrentItem() + 1) + "/" + readingData.onOffLoadDtos.size();
                runOnUiThread(() -> binding.textViewPageNumber.setText(number));
                setAboveIconsSrc(position);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (karbariDtos.get(binding.viewPager.getCurrentItem()).hasReadingVibrate)
                    makeVibrate(getApplicationContext());
                try {
                    FragmentManager manager = getSupportFragmentManager();
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

    private void showImage(int position, boolean counterHasImage, boolean reportHasImage) {
        ShowDialogOnce(this, TAKE_PHOTO.getValue().concat(readingData.onOffLoadDtos
                .get(binding.viewPager.getCurrentItem()).id), TakePhotoFragment
                .newInstance(readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).offLoadStateId > 0,
                        readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).id,
                        readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).trackNumber,
                        position, true, counterHasImage, reportHasImage));
    }

    private boolean shouldShowPossible() {
        return sharedPreferenceManager.getBoolData(SERIAL.getValue())
                || sharedPreferenceManager.getBoolData(AHAD_2.getValue())
                || sharedPreferenceManager.getBoolData(AHAD_1.getValue())
                || sharedPreferenceManager.getBoolData(AHAD_TOTAL.getValue())
                || sharedPreferenceManager.getBoolData(AHAD_EMPTY.getValue())
                || sharedPreferenceManager.getBoolData(KARBARI.getValue())
                || sharedPreferenceManager.getBoolData(ADDRESS.getValue())
                || sharedPreferenceManager.getBoolData(ACCOUNT.getValue())
                || sharedPreferenceManager.getBoolData(READING_REPORT.getValue())
                || sharedPreferenceManager.getBoolData(MOBILE.getValue())
                || (sharedPreferenceManager.getBoolData(GUILD.getValue())
                && Constants.karbariDtos.get(binding.viewPager.getCurrentItem()).isTejari);
    }

    private void attemptSend(int position, boolean isForm, boolean isImage) {
        CounterStateDto counterState = readingData.counterStateDtos.get(readingData
                .onOffLoadDtos.get(position).counterStatePosition);
        boolean hasImage = false;
        boolean reportHasImage = false;
        if (isImage) {
            ArrayList<OffLoadReport> offLoadReports = new ArrayList<>(getApplicationComponent()
                    .MyDatabase().offLoadReportDao().getAllOffLoadReportById(
                            readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).id,
                            readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).trackNumber));
            while (!hasImage && !offLoadReports.isEmpty()) {
                reportHasImage = hasImage = offLoadReports.get(0).hasImage;
                offLoadReports.remove(0);
            }
            if (sharedPreferenceManager.getBoolData(IMAGE.getValue()))
                hasImage = true;
            else if (counterState.hasImage)
                hasImage = true;
        }
        if (isForm && shouldShowPossible()) {
            showPossible(position);
        } else if (hasImage) {
            showImage(position, counterState.hasImage, reportHasImage);
        } else {
            if (!isShowing) {
                if ((counterState.isTavizi || counterState.isXarab) &&
                        counterState.moshtarakinId != readingData.onOffLoadDtos.get(position).preCounterStateCode) {
                    ShowDialogOnce(this, SERIAL_DIALOG.getValue().concat(readingData.onOffLoadDtos.get(position).eshterak),
                            SerialFragment.newInstance(position));
                } else isShowing = true;
            }
            if (isShowing) {
                makeRing(this, SAVE);
                updateAdapter(position);
                isShowing = false;
                new Update(readingData.onOffLoadDtos.get(position)).execute(this);
                if (currentOfflineAttempts < MAX_OFFLINE_ATTEMPT)
                    new PrepareToSend(sharedPreferenceManager.getStringData(TOKEN.getValue()))
                            .execute(this);
                changePage(binding.viewPager.getCurrentItem() + 1);
            }
        }
    }

    private void showPossible(int position) {
        ShowDialogOnce(this, POSSIBLE_DIALOG.getValue().concat(readingData.onOffLoadDtos.get(position).eshterak),
                PossibleFragment.newInstance(false, position, readingData.onOffLoadDtos.get(position)));
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
        final int src = ReadingUtils.setExceptionImage(readingData, position);
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
        final ImageView imageViewCamera = findViewById(R.id.image_view_camera);
        final ImageView imageViewSearch = findViewById(R.id.image_view_search);
        final ImageView imageViewCheck = findViewById(R.id.image_view_reading_report);
        if (imageViewFlash != null && imageViewCamera != null && imageViewSearch != null && imageViewCheck != null) {
            imageViewFlash.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),
                    R.drawable.img_flash_off));
            imageViewFlash.setOnClickListener(this);

            imageViewCamera.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),
                    R.drawable.img_camera));
            imageViewCamera.setOnClickListener(this);

            imageViewCheck.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),
                    R.drawable.img_checked));
            imageViewCheck.setOnClickListener(this);

            imageViewSearch.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),
                    R.drawable.img_search));
            imageViewSearch.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        if (id == R.id.image_view_flash) {
            boolean isOn = flashLightManager.toggleFlash();
            makeRing(this, isOn ? LIGHT_ON : LIGHT_OFF);
            ((ImageView) view).setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),
                    isOn ? R.drawable.img_flash_on : R.drawable.img_flash_off));
        } else if (id == R.id.image_view_camera) {
            if (readingData.onOffLoadDtos.isEmpty()) {
                showNoEshterakFound();
            } else {
                final OnOffLoadDto onOffLoadDtoTemp = readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem());
                ShowDialogOnce(this, TAKE_PHOTO.getValue().concat(onOffLoadDtoTemp.id),
                        TakePhotoFragment.newInstance(onOffLoadDtoTemp.offLoadStateId > 0,
                                onOffLoadDtoTemp.id, onOffLoadDtoTemp.trackNumber));
            }
        } else if (id == R.id.image_view_reading_report) {
            if (readingData.onOffLoadDtos.isEmpty()) {
                showNoEshterakFound();
            } else {
                final OnOffLoadDto onOffLoadDtoTemp = readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem());
                ShowDialogOnce(this, READING_REPORT.getValue(), ReadingReportFragment
                        .newInstance(onOffLoadDtoTemp.id, onOffLoadDtoTemp.trackNumber,
                                binding.viewPager.getCurrentItem(), onOffLoadDtoTemp.zoneId));
            }
        } else if (id == R.id.image_view_search) {
            if (readingDataTemp.onOffLoadDtos.isEmpty()) {
                showNoEshterakFound();
            } else {
                ShowDialogOnce(this, SEARCH.getValue(), new SearchFragment());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reading_menu, menu);
//        menu.getItem(7).setChecked(sharedPreferenceManager.getBoolData(SORT_TYPE.getValue()));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        try {
            MenuItem checkable = menu.findItem(R.id.menu_sort);
            checkable.setChecked(sharedPreferenceManager.getBoolData(SORT_TYPE.getValue()));
        } catch (Exception e) {
            new CustomToast().warning(e.getMessage());
        }

        return true;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int id = item.getItemId();
        Intent intent;
        if (id == R.id.menu_sort) {
            sharedPreferenceManager.putData(SORT_TYPE.getValue(), !sharedPreferenceManager.getBoolData(SORT_TYPE.getValue()));
            if (readingData.onOffLoadDtos.isEmpty())
                showNoEshterakFound();
            else
                new ChangeSortType(this, sharedPreferenceManager.getBoolData(SORT_TYPE.getValue())).execute(this);
        } else if (id == R.id.menu_navigation) {
            if (readingData.onOffLoadDtos.isEmpty()) {
                showNoEshterakFound();
            } else {
                ShowDialogOnce(this, NAVIGATION.getValue(), NavigationFragment
                        .newInstance(binding.viewPager.getCurrentItem(), readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem())));
            }
        } else if (id == R.id.menu_karbari) {
            if (readingData.onOffLoadDtos.isEmpty()) {
                showNoEshterakFound();
            } else {
                ShowDialogOnce(this, FragmentTags.KARBARI.getValue(),
                        KarbariFragment.newInstance(readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).id,
                                binding.viewPager.getCurrentItem()));
            }
        } else if (id == R.id.menu_ahad) {
            if (readingData.onOffLoadDtos.isEmpty()) {
                showNoEshterakFound();
            } else {
                ShowDialogOnce(this, AHAD.getValue(),
                        AhadFragment.newInstance(readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).id,
                                binding.viewPager.getCurrentItem()));
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
                intent.putExtra(DESCRIPTION.getValue(), onOffLoadDtoTemp.description);
                intent.putExtra(POSITION.getValue(), binding.viewPager.getCurrentItem());
                resultLauncher.launch(intent);
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
                binding.viewPager.setCurrentItem(currentItem, false);
            }
        } else if (id == R.id.menu_verification) {
            showPersonalCode(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    new DataResult(result.getData()).execute(this);
                }
            });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA && resultCode == RESULT_OK) {
            int position = data.getExtras().getInt(POSITION.getValue());
            attemptSend(position, false, false);
        }
    }

    @Override
    public void setResult(int position, String uuid) {
        new Result(position, uuid).execute(this);
    }

    @Override
    public void setPhotoResult(int position) {
        attemptSend(position, false, false);
    }

    @Override
    public int getPosition() {
        return binding.viewPager.getCurrentItem();
    }

    @Override
    public int getCounterStateCode(int position) {
        return readingData.counterStateDtos.get(readingData.onOffLoadDtos.get(position).counterStatePosition).id;
    }

    @Override
    public int getCounterStatePosition(int position) {
        return readingData.onOffLoadDtos.get(position).counterStatePosition;
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            flashLightManager.turnOff();
            final ImageView imageViewFlash = findViewById(R.id.image_view_flash);
            imageViewFlash.setImageDrawable(AppCompatResources.getDrawable(this,
                    R.drawable.img_flash_off));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentOfflineAttempts = 0;
        Debug.getNativeHeapAllocatedSize();
        System.runFinalization();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Runtime.getRuntime().gc();
        System.gc();
    }
}