package com.leon.counter_reading.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.leon.counter_reading.databinding.ActivityStartBinding;

public class StartActivity extends AppCompatActivity {
    private ActivityStartBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    private void initialize() {

    }

//    private void displayView(int position) {
//        final String tag = Integer.toString(position);
//        if (getFragmentManager().findFragmentByTag(tag) != null && getFragmentManager().findFragmentByTag(tag).isVisible()) {
//            return;
//        }
//        final FragmentManager fragmentManager = getSupportFragmentManager();
//        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.setCustomAnimations(R.animator.enter, R.animator.exit,
//                R.animator.pop_enter, R.animator.pop_exit);
//        fragmentTransaction.replace(binding.containerBody.getId(), getFragment(position), tag);
//        // Home fragment is not added to the stack
//        if (position != HOME_FRAGMENT) {
//            fragmentTransaction.addToBackStack(null);
//        }
//        fragmentTransaction.commitAllowingStateLoss();
//        getFragmentManager().executePendingTransactions();
//    }
//
//    private Fragment getFragment(int position) {
//        switch (position) {
//            case DUTIES_FRAGMENT:
//                return DutiesListFragment.newInstance();
//            case DOWNLOAD_FRAGMENT:
//                return DownloadFragment.newInstance();
//            case REQUEST_FRAGMENT:
//                return SendRequestFragment.newInstance();
//            case UPLOAD_FRAGMENT:
//                return UploadFragment.newInstance();
//            case HELP_FRAGMENT:
//                return HelpFragment.newInstance();
//            case HOME_FRAGMENT:
//            default:
//                return HomeFragment.newInstance();
//        }
//    }
}