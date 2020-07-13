package com.demo.videoplayer.videoUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Observable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;

import androidx.databinding.ObservableField;

import com.demo.videoplayer.R;
import com.demo.videoplayer.listeners.VideoPlaybackStateListener;
import com.demo.videoplayer.viewmodel.HomeScreenViewModel;

public class MediaSessionCallback extends MediaSessionCompat.Callback implements MediaPlayer.OnCompletionListener, SurfaceHolder.Callback, AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = " ~ ~  " + MediaSessionCallback.class.getSimpleName();

    private Context mContext;
    private AudioManager mAudioManager;
    private IntentFilter mNoisyIntentFilter;
    private AudioBecommingNoisy mAudioBecommingNoisy;

    private MediaPlayer mMediaPlayer;

    private VideoPlaybackStateListener mListener;

    private PlaybackStateCompat.Builder mPBuilder;
    private MediaSessionCompat mSession;

    private MediaControllerCompat mController;
    private MediaControllerCompat.TransportControls mControllerTransportControls;

    public static ObservableField<Uri> mVideoPath = new ObservableField<>();

    public MediaSessionCallback(Context context, VideoPlaybackStateListener listener) {
        super();

        mContext = context;
        mListener = listener;

        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mAudioBecommingNoisy = new MediaSessionCallback.AudioBecommingNoisy();
        mNoisyIntentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

        mSession = new MediaSessionCompat(mContext, TAG);
        mSession.setCallback(this);
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mPBuilder = new PlaybackStateCompat.Builder();

        mController = new MediaControllerCompat(mContext, mSession);
        mControllerTransportControls = mController.getTransportControls();

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.v(TAG, "surfaceCreated");
        mMediaPlayer = MediaPlayer.create(mContext, mVideoPath.get(), holder);
        mMediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.v(TAG, "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v(TAG, "surfaceDestroyed");
    }

    public void registerSurfaceViewCallback(SurfaceHolder holder) {
        Log.v(TAG, "registerSurfaceViewCallback");
        holder.addCallback(this);
    }

    public void setVideoPath(Uri path) {
        Log.v(TAG, "setVideoPath");
        mVideoPath.set(path);
    }

    private class AudioBecommingNoisy extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mediaPause();
        }
    }

    @Override
    public void onPlay() {
        super.onPlay();
        Log.v(TAG, "onPlay");
        mediaPlay();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");
        mediaPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
        releaseResources();
    }

    private void releaseResources() {
        Log.v(TAG, "releaseResources");

        mSession.setActive(false);

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void mediaPlay() {
        Log.v(TAG, "mediaPlay");

        mContext.registerReceiver(mAudioBecommingNoisy, mNoisyIntentFilter);
        int requestAudioFocusResult = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (requestAudioFocusResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mSession.setActive(true);
            mPBuilder.setActions(PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_STOP);
            mPBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mMediaPlayer.getCurrentPosition(), 1.0f, SystemClock.elapsedRealtime());
            mSession.setPlaybackState(mPBuilder.build());
            mMediaPlayer.start();
        }
    }

    private void mediaPause() {
        Log.v(TAG, "mediaPause");

        mMediaPlayer.pause();
        mPBuilder.setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_STOP);
        mPBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                mMediaPlayer.getCurrentPosition(), 1.0f, SystemClock.elapsedRealtime());
        mSession.setPlaybackState(mPBuilder.build());
        mAudioManager.abandonAudioFocus(this);
        mContext.unregisterReceiver(mAudioBecommingNoisy);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.v(TAG, "onCompletion");
        mPBuilder.setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_STOP);
        mPBuilder.setState(PlaybackStateCompat.STATE_STOPPED,
                mMediaPlayer.getCurrentPosition(), 1.0f, SystemClock.elapsedRealtime());
        mSession.setPlaybackState(mPBuilder.build());
    }

    @Override
    public void onAudioFocusChange(int audioFocusChanged) {
        Log.v(TAG, "onAudioFocusChange " + audioFocusChanged);
        switch (audioFocusChanged) {
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                mediaPause();
                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                mediaPlay();
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                mediaPause();
                break;
        }
    }

    public void playCurrentTrack() {
        Log.v(TAG, "onPlayClicked");
        if (mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PAUSED ||
                mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_STOPPED ||
                mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_NONE) {
            mControllerTransportControls.play();
        }
    }

    public void pauseCurrentTrack() {
        Log.v(TAG, "onPauseClicked");
        if (mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
            mControllerTransportControls.pause();
        }
    }

    public void onActivityStart() {
        Log.v(TAG, "onActivityStart");
        mController.registerCallback(mControllerCallback);
        mPBuilder.setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mSession.setPlaybackState(mPBuilder.build());
    }

    public void onActivityStop() {
        Log.v(TAG, "onActivityStop");
        mController.unregisterCallback(mControllerCallback);
        if (mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING ||
                mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PAUSED) {
            mControllerTransportControls.stop();
        }
    }

    public void onActivityPause() {
        Log.v(TAG, "onActivityPause");
        if (mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
            mControllerTransportControls.pause();
        }
    }

    public void onActivityDestroy() {
        Log.v(TAG, "onActivityDestroy");
        mSession.release();
    }

    private MediaControllerCompat.Callback mControllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
            Log.v(TAG, "onPlaybackStateChanged " + state);
            mListener.updatePbState(state);
        }
    };
}
