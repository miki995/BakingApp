package com.inc.miki.bakingapp.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.baking.R;
import com.example.android.baking.activity.RecipeDetailsActivity;
import com.example.android.baking.activity.StepDetailsActivity;
import com.example.android.baking.data.Step;
import com.example.android.baking.media.MediaSessionCallback;
import com.example.android.baking.util.ImageUtils;
import com.example.android.baking.util.NetworkUtils;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StepDetailsFragment.StepDetailsOnClickListener} interface
 * to handle interaction events.
 * Use the {@link StepDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StepDetailsFragment extends Fragment implements ExoPlayer.EventListener {

    private static final String TAG = StepDetailsFragment.class.getSimpleName();
    private static final String POSITION_MILLISECONDS = "position_milliseconds";
    private static final String PLAY_WHEN_READY = "state_playing";

    @BindView(R.id.nested_scroll_view_step)
    NestedScrollView nestedScrollViewStep;

    @BindView(R.id.player_view_step)
    SimpleExoPlayerView playerView;

    @BindView(R.id.image_view_step)
    ImageView imageViewStep;

    @BindView(R.id.text_view_step_description)
    TextView textViewStepDescription;

    @BindView(R.id.button_previous_step)
    Button buttonPreviousStep;

    @BindView(R.id.button_next_step)
    Button buttonNextStep;

    private static MediaSessionCompat mediaSession;
    private StepDetailsOnClickListener listener;
    private SimpleExoPlayer exoPlayer;
    private PlaybackStateCompat.Builder stateBuilder;
    private String videoUrl;
    private long playbackPosition;
    private boolean playWhenReady = true;

    public StepDetailsFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment StepDetailsFragment.
     */
    public static StepDetailsFragment newInstance(Bundle bundle) {
        StepDetailsFragment stepDetailsFragment = new StepDetailsFragment();
        stepDetailsFragment.setArguments(bundle);
        return stepDetailsFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step_details, container, false);
        ButterKnife.bind(this, view);

        Context context = getContext();
        Bundle bundle = getArguments();
        List<Step> steps = null;
        int position = -1;
        if (bundle != null) {
            steps = bundle.getParcelableArrayList(getString(R.string.steps));
            position = bundle.getInt(getString(R.string.step_position));
        } else {
            Toast.makeText(context, R.string.media_url_not_found, Toast.LENGTH_SHORT).show();
        }

        if (steps != null) {
            if (position < 0 || position > steps.size()) {
                Toast.makeText(context, R.string.step_could_not_be_loaded, Toast.LENGTH_SHORT).show();
                return view;
            }
            Step currentStep = steps.get(position);

            if (currentStep == null) {
                Toast.makeText(context, R.string.step_could_not_be_loaded, Toast.LENGTH_SHORT).show();
                return view;
            }

            textViewStepDescription.setText(currentStep.getDescription());
            setUpStepButton(buttonPreviousStep, steps, position, -1);
            setUpStepButton(buttonNextStep, steps, position, 1);

            AppCompatActivity activity = ((AppCompatActivity) getActivity());
            ActionBar actionBar = null;
            if (activity != null) {
                actionBar = activity.getSupportActionBar();
            }

            videoUrl = currentStep.getVideoUrl();
            if (!TextUtils.isEmpty(videoUrl) && NetworkUtils.isOnline(context)) {
                initializeMediaSession(context);

                Configuration configuration = getResources().getConfiguration();
                if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
                        && !getResources().getBoolean(R.bool.isTablet)) {
                    if (actionBar != null) {
                        actionBar.hide();
                    }

                    if (activity != null) {
                        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    }

                    nestedScrollViewStep.setVisibility(View.GONE);
                    buttonPreviousStep.setVisibility(View.GONE);
                    buttonNextStep.setVisibility(View.GONE);
                } else {
                    imageViewStep.setVisibility(View.GONE);
                    RelativeLayout.LayoutParams layoutParams =
                            (RelativeLayout.LayoutParams) textViewStepDescription.getLayoutParams();
                    layoutParams.addRule(RelativeLayout.BELOW, R.id.player_view_step);
                    textViewStepDescription.setLayoutParams(layoutParams);
                }
            } else {
                playerView.setVisibility(View.GONE);
                String recipeName = null;
                if (actionBar != null) {
                    if (actionBar.getTitle() != null) {
                        recipeName = actionBar.getTitle().toString();
                    }
                }

                String imageUrl = currentStep.getThumbnailUrl();
                int placeholderId = ImageUtils.getImageResourceId(context, recipeName);
                if (!TextUtils.isEmpty(imageUrl)) {
                    Picasso.with(context)
                            .load(imageUrl)
                            .placeholder(placeholderId)
                            .into(imageViewStep);
                } else {
                    Picasso.with(context)
                            .load(placeholderId)
                            .into(imageViewStep);
                }

                RelativeLayout.LayoutParams layoutParams =
                        (RelativeLayout.LayoutParams) textViewStepDescription.getLayoutParams();
                layoutParams.addRule(RelativeLayout.BELOW, R.id.image_view_step);
                textViewStepDescription.setLayoutParams(layoutParams);
            }

            if (savedInstanceState != null) {
                playbackPosition = savedInstanceState.getLong(POSITION_MILLISECONDS);
                playWhenReady = savedInstanceState.getBoolean(PLAY_WHEN_READY);
            }
        }

        return view;
    }

    private void setUpStepButton(Button button, final List<Step> steps, final int position, final int offset) {
        int newPosition = position + offset;
        Step step = null;
        if (newPosition > -1 && newPosition < steps.size()) {
            step = steps.get(newPosition);
        }
        if (step != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onStepSelected(position + offset);
                    if (textViewStepDescription != null) {
                        textViewStepDescription.setText(null);
                    }
                }
            });
        } else {
            button.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof StepDetailsOnClickListener) {
            listener = (StepDetailsOnClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + getString(R.string.must_implement)
                    + StepDetailsOnClickListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (exoPlayer != null) {
            outState.putLong(POSITION_MILLISECONDS, exoPlayer.getCurrentPosition());
            outState.putBoolean(PLAY_WHEN_READY, playWhenReady);
        }

        outState.putIntArray(getString(R.string.scroll_position),
                new int[]{nestedScrollViewStep.getScrollX(), nestedScrollViewStep.getScrollY()});
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            final int[] position = savedInstanceState.getIntArray(getString(R.string.scroll_position));
            if(position != null) {
                nestedScrollViewStep.post(new Runnable() {
                    public void run() {
                        nestedScrollViewStep.scrollTo(position[0], position[1]);
                    }
                });
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Context context = getContext();
        if (!TextUtils.isEmpty(videoUrl) && NetworkUtils.isOnline(context)) {
            initializePlayer(Uri.parse(videoUrl), context);
            exoPlayer.seekTo(playbackPosition);
            exoPlayer.setPlayWhenReady(playWhenReady);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (exoPlayer == null) {
            return;
        }
        playbackPosition = exoPlayer.getCurrentPosition();
        playWhenReady = exoPlayer.getPlayWhenReady();
        exoPlayer.setPlayWhenReady(false);
    }

    /**
     * Releases the player when the activity is destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();

        if (mediaSession != null) {
            mediaSession.setActive(false);
        }
    }

    /**
     * Releases ExoPlayer.
     */
    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    private void initializeMediaSession(Context context) {
        mediaSession = new MediaSessionCompat(context, TAG);
        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        );

        mediaSession.setMediaButtonReceiver(null);
        stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE
                );

        mediaSession.setPlaybackState(stateBuilder.build());
        mediaSession.setCallback(new MediaSessionCallback(exoPlayer));
        mediaSession.setActive(true);

    }

    private void initializePlayer(Uri uri, Context context) {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(context),
                    new DefaultTrackSelector(), new DefaultLoadControl());

            playerView.setPlayer(exoPlayer);
            exoPlayer.addListener(this);

            exoPlayer.setPlayWhenReady(true);
            MediaSource mediaSource = buildMediaSource(uri);
            exoPlayer.prepare(mediaSource, true, false);
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource(uri,
                new DefaultHttpDataSourceFactory(getString(R.string.app_name)),
                new DefaultExtractorsFactory(), null, null);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if(playbackState == ExoPlayer.STATE_READY && playWhenReady){
            stateBuilder.setState(
                    PlaybackStateCompat.STATE_PLAYING,
                    exoPlayer.getCurrentPosition(),
                    1f
            );

            if (getResources().getBoolean(R.bool.isTablet)) {
                RecipeDetailsActivity recipeDetailsActivity = (RecipeDetailsActivity) getActivity();
                if (recipeDetailsActivity != null) {
                    recipeDetailsActivity.setIdleState(true);
                }
            } else {
                StepDetailsActivity stepDetailsActivity = (StepDetailsActivity) getActivity();
                if (stepDetailsActivity != null) {
                    stepDetailsActivity.setIdleState(true);
                }
            }
        } else if (playbackState == ExoPlayer.STATE_READY) {
            stateBuilder.setState(
                    PlaybackStateCompat.STATE_PAUSED,
                    exoPlayer.getCurrentPosition(),
                    1f
            );
        }
        mediaSession.setPlaybackState(stateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface StepDetailsOnClickListener {
        void onStepSelected(int position);
    }
}
