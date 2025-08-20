package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import com.dst.abacustrainner.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.MediaItem;

public class VideoPlayerActivity extends AppCompatActivity {

    private ExoPlayer player;
    private PlayerView playerView;
    private long playbackPosition = 0;
    private boolean playWhenReady = true;
    private String videoUrl;

    private static final String PREF_NAME = "VideoPrefs";
    private static final String KEY_POSITION = "video_position";
    private static final String KEY_URL = "video_url";
    ImageView ivArrow;
    LinearLayout layoutPractice;

    private String Hi;

    Button btnViewVideoResult, btnPracticeVideoNow,butPracticeVideoVisualization,butVisualizationVideoResult;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        playerView = findViewById(R.id.playerView);
        videoUrl = getIntent().getStringExtra("VIDEO_URL");

        ivArrow = findViewById(R.id.ivArrow);
        layoutPractice = findViewById(R.id.layout_practice);
        btnViewVideoResult = findViewById(R.id.btnViewResul);
        btnPracticeVideoNow = findViewById(R.id.btnPracticeNo);
        butPracticeVideoVisualization = findViewById(R.id.btnPractice);
        butVisualizationVideoResult = findViewById(R.id.btnViewResulvisualization);

        final boolean[] isExpanded = {false};

        ivArrow.setOnClickListener(v -> {
            if (isExpanded[0]) {
                // Collapse
                layoutPractice.setVisibility(View.GONE);
                ivArrow.setImageResource(R.drawable.baseline_keyboard_arrow_down_24);
            } else {
                // Expand
                layoutPractice.setVisibility(View.VISIBLE);
                ivArrow.setImageResource(R.drawable.baseline_keyboard_arrow_up_24);
            }
            isExpanded[0] = !isExpanded[0];
        });

        // 🔹 Load saved position if the same video was played before
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String savedUrl = prefs.getString(KEY_URL, "");
        if (savedUrl.equals(videoUrl)) {
            playbackPosition = prefs.getLong(KEY_POSITION, 0);
        }

        // Initialize ExoPlayer
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
        player.setMediaItem(mediaItem);

        // Start from last saved position
        player.seekTo(playbackPosition);
        player.setPlayWhenReady(false);  // <-- 👈 important line
        player.prepare();

        btnViewVideoResult.setOnClickListener(v -> {
            Intent intent = new Intent(VideoPlayerActivity.this, LevelVideoResultActivity.class);
            //intent.putExtra("topicName", topicList.get(position).getName());
           startActivity(intent);
        });

        btnPracticeVideoNow.setOnClickListener(v -> {
            Intent intent = new Intent(VideoPlayerActivity.this, LevelVideoExamPracticeActivity.class);
            //intent.putExtra("topicName", topicList.get(position).getName());
            startActivity(intent);
        });

        butVisualizationVideoResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VideoPlayerActivity.this, LevelVideoVisualiztionResultActivity.class);
                //intent.putExtra("topicName", topicList.get(position).getName());
                startActivity(intent);
            }
        });
        butPracticeVideoVisualization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VideoPlayerActivity.this, LevelVideoExamvisualizationPracticeActivity.class);
                //intent.putExtra("topicName", topicList.get(position).getName());
               startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            // 🔹 Save position and URL in SharedPreferences
            playbackPosition = player.getCurrentPosition();
            playWhenReady = player.getPlayWhenReady();

            SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(KEY_POSITION, playbackPosition);
            editor.putString(KEY_URL, videoUrl);
            editor.apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (player == null) {
            player = new ExoPlayer.Builder(this).build();
            playerView.setPlayer(player);

            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
            player.setMediaItem(mediaItem);

            SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            playbackPosition = prefs.getLong(KEY_POSITION, 0);

            player.seekTo(playbackPosition);
            player.setPlayWhenReady(playWhenReady);
            player.prepare();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}