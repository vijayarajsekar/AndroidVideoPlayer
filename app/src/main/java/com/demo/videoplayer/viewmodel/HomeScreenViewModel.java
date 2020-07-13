package com.demo.videoplayer.viewmodel;

import android.content.Context;
import android.net.Uri;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;

import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

import com.demo.videoplayer.R;
import com.demo.videoplayer.databinding.ActivityMainBinding;
import com.demo.videoplayer.listeners.VideoPlaybackStateListener;
import com.demo.videoplayer.videoUtils.MediaSessionCallback;

import static com.demo.videoplayer.utils.AppConstants.CURRENT_STATE_NONE;
import static com.demo.videoplayer.utils.AppConstants.CURRENT_STATE_PAUSED;
import static com.demo.videoplayer.utils.AppConstants.CURRENT_STATE_PLAYING;
import static com.demo.videoplayer.utils.AppConstants.CURRENT_STATE_STOPPED;

public class HomeScreenViewModel extends ViewModel implements VideoPlaybackStateListener {

    private static final String TAG = " ~ ~  " + HomeScreenViewModel.class.getSimpleName();

    private Context mContext;
    private ActivityMainBinding mBinding;

    private MediaSessionCallback mMediaSessionCallback;
    private VideoPlaybackStateListener mListener;

    private ObservableField<PlaybackStateCompat> mCurrentPlaybackState = new ObservableField<>();

    public void init(Context context, ActivityMainBinding binding) {
        Log.v(TAG, "init");

        this.mContext = context;
        this.mBinding = binding;
        this.mListener = this;

        mMediaSessionCallback = new MediaSessionCallback(mContext, mListener);
        mMediaSessionCallback.setVideoPath(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.balettante_pranaya_kavitha));
        mMediaSessionCallback.registerSurfaceViewCallback(mBinding.fullScreenView.getHolder());
    }

    public void onPlaybackClicked(View v) {
        Log.v(TAG, "onPlaybackClicked");

        if (mCurrentPlaybackState.get().getState() == PlaybackStateCompat.STATE_PLAYING) {
            mMediaSessionCallback.pauseCurrentTrack();
        } else {
            mMediaSessionCallback.playCurrentTrack();
        }
    }

    public void onStart() {
        Log.v(TAG, "onStart");
        mMediaSessionCallback.onActivityStart();
    }

    public void onPause() {
        Log.v(TAG, "onPause");
        mMediaSessionCallback.onActivityPause();
    }

    public void onStop() {
        Log.v(TAG, "onStop");
        mMediaSessionCallback.onActivityStop();
    }

    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        mMediaSessionCallback.onActivityDestroy();
    }

    @Override
    public void updatePbState(PlaybackStateCompat state) {

        mCurrentPlaybackState.set(state);

        if (CURRENT_STATE_PLAYING == state.getState()) {
            mBinding.imagePlayPause.setImageResource(R.drawable.pause_icon);
        } else if (CURRENT_STATE_PAUSED == state.getState() || CURRENT_STATE_NONE == state.getState() || CURRENT_STATE_STOPPED == state.getState()) {
            mBinding.imagePlayPause.setImageResource(R.drawable.play_icon);
        }
    }
}
