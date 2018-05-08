package com.baking.app.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baking.app.R;
import com.baking.app.model.Step;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class StepDetailFragment extends Fragment implements View.OnClickListener{

    @BindView(R.id.step_video_player)
    PlayerView mPlayerView;

    @BindView(R.id.text_step_description)
    TextView mStepDescription;

    @BindView(R.id.button_previous_step)
    Button mPreviousStepButton;

    @BindView(R.id.button_next_step)
    Button mNextStepButton;

    @BindView(R.id.navigation_bar)
    RelativeLayout mNavigationLayout;

    private List<Step> mRecipeSteps;
    private int mStepIndex;

    SimpleExoPlayer mPlayer;
    long playbackPosition = 0;
    int currentWindow = 0;
    boolean playWhenReady = true;

    StepNavigationListener mStepNavigationListener;

    public StepDetailFragment (){
        // Required empty public constructor
    }

    public void setNavigationListener(StepNavigationListener listener) {
        this.mStepNavigationListener = listener;

    }

    public void setRecipeSteps(List<Step> recipeSteps) {
        this.mRecipeSteps = recipeSteps;
    }

    public void setStepIndex(int index) {
        this.mStepIndex = index;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_step_detail, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPreviousStepButton.setOnClickListener(this);
        mNextStepButton.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        initializePlayer();
        displayStepDetails(mRecipeSteps.get(mStepIndex));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mStepNavigationListener = (StepNavigationListener) context;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mStepNavigationListener = (StepNavigationListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mStepNavigationListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();

        releasePlayer();
    }

    private void initializePlayer() {
        mPlayer = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(getActivity()),
                new DefaultTrackSelector(), new DefaultLoadControl());

        mPlayerView.setPlayer(mPlayer);
        mPlayer.setPlayWhenReady(playWhenReady);

        int orientation = this.getResources().getConfiguration().orientation;
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if(orientation == Configuration.ORIENTATION_LANDSCAPE && !isTablet) {
            hideSystemUi();
            mNavigationLayout.setVisibility(View.GONE);
            mStepDescription.setVisibility(View.GONE);
            mPlayerView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer-baking")).
                createMediaSource(uri);
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        mPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void releasePlayer() {
        if (mPlayer != null) {
            playbackPosition = mPlayer.getCurrentPosition();
            currentWindow = mPlayer.getCurrentWindowIndex();
            playWhenReady = mPlayer.getPlayWhenReady();
            mPlayer.release();
            mPlayer = null;
        }
    }

    public interface StepNavigationListener {
        void navigateToStep(int index);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.button_previous_step) {
            goToStep(--mStepIndex);
        } else if(id == R.id.button_next_step) {
            goToStep(++mStepIndex);
        }
    }

    private void goToStep(int index) {
        mStepIndex = index;
        if(mStepIndex >= 0 && mStepIndex < mRecipeSteps.size()) {
            playbackPosition = 0;
            displayStepDetails(mRecipeSteps.get(mStepIndex));
        }

        mPreviousStepButton.setEnabled(mStepIndex > 0);
        mNextStepButton.setEnabled(mStepIndex < mRecipeSteps.size() - 1);

        if(mStepNavigationListener != null) {
            mStepNavigationListener.navigateToStep(mStepIndex);
        }
    }

    private void displayStepDetails(Step step){
        mStepDescription.setText(step.description);
        String url = "";
        if(!TextUtils.isEmpty(step.videoURL)) {
            url = step.videoURL;
        } else if(!TextUtils.isEmpty(step.thumbnailURL)) {
            url = step.thumbnailURL;
        }
        if(!TextUtils.isEmpty(url)) {
            Uri uri = Uri.parse(url);
            MediaSource mediaSource = buildMediaSource(uri);
            mPlayer.prepare(mediaSource,
                    true, false);
            mPlayer.seekTo(currentWindow, playbackPosition);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList("recipe_steps", new ArrayList<>(mRecipeSteps));
        outState.putInt("step_index", mStepIndex);
        outState.putLong("playback_position", playbackPosition);
        outState.putInt("current_window", currentWindow);
        outState.putBoolean("play_when_ready", playWhenReady);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null) {
            mRecipeSteps = savedInstanceState.getParcelableArrayList("recipe_steps");
            mStepIndex = savedInstanceState.getInt("step_index", 0);

            playbackPosition = savedInstanceState.getLong("playback_position", 0);
            currentWindow = savedInstanceState.getInt("current_window", 0);
            playWhenReady = savedInstanceState.getBoolean("play_when_ready", true);
        }
    }
}
