package in.reweyou.reweyouforums;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import in.reweyou.reweyouforums.utils.Utils;

public class YoutubeActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener {

    private String videoUrl = "nCgQDjiotG0";
    private String TAG = YoutubeActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        videoUrl = getIntent().getStringExtra("url");

        Log.d("kjk", "onCreate: " + videoUrl);
        YouTubePlayerSupportFragment frag =
                (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);
        frag.initialize(Utils.YOUTUBE_API_KEY, this);

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (!b) {
            youTubePlayer.cueVideo(videoUrl.replace("\n", ""));
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            Log.d(TAG, "onInitializationFailure: " + errorReason.toString());
            if (errorReason.toString().equals("SERVICE_MISSING"))
                errorReason.getErrorDialog(YoutubeActivity.this, Utils.RECOVERY_DIALOG_YOUTUBE_APP).show();
        } else {
            String errorMessage = String.format(getString(R.string.error_player), errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.RECOVERY_DIALOG_YOUTUBE_APP) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.google.android.youtube")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Log.d("youtube", "onActivityResult: " + requestCode + "    " + resultCode);
    }
}
