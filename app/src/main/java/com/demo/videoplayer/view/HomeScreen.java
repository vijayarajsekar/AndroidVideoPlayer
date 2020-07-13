package com.demo.videoplayer.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.demo.videoplayer.R;
import com.demo.videoplayer.databinding.ActivityMainBinding;
import com.demo.videoplayer.utils.AppUtils;
import com.demo.videoplayer.viewmodel.HomeScreenViewModel;

public class HomeScreen extends AppCompatActivity {

    private static final String TAG = " ~ ~  " + HomeScreen.class.getSimpleName();

    private Context mContext;

    private ActivityMainBinding mBinding;
    private HomeScreenViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppUtils.hideSystemUI(getWindow());
        super.onCreate(savedInstanceState);

        Log.v(TAG, "onCreate");

        mContext = this;

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mViewModel = new ViewModelProvider(this).get(HomeScreenViewModel.class);

        mBinding.setViewModel(mViewModel);

        mViewModel.init(mContext, mBinding);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mViewModel.onStart();
    }

    @Override
    protected void onStop() {
        mViewModel.onStop();
        super.onStop();
    }

    @Override
    protected void onPause() {
        mViewModel.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.v(TAG, "onWindowFocusChanged");

        AppUtils.onWindowFocusChanged(getWindow(), hasFocus);
    }
}