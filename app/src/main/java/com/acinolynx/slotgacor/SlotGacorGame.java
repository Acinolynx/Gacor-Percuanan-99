package com.acinolynx.slotgacor;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;  // <-- Import for Handler
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;
import android.view.Window;
import android.view.WindowManager;
import android.net.Uri;
import android.widget.VideoView;
import android.media.MediaPlayer;
import java.text.NumberFormat;

public class SlotGacorGame extends AppCompatActivity {

    private ImageView reel1, reel2, reel3;
    private Button spinButton, autoSpinButton;
    private TextView resultText, coinText, betText;
    private int playerCoins = 500000;
    private int currentBet = 500;
    private int[] betOptions = {500, 2000, 5000, 10000};
    private int betIndex = 0;

    private boolean isAutoSpinning = false;
    private Handler autoSpinHandler = new Handler();

    private int[] images = {
            R.drawable.ic_slot1, R.drawable.ic_slot2, R.drawable.ic_slot3,
            R.drawable.ic_slot4, R.drawable.ic_slot5
    };

    private int[] rewards = {-25, 25, 50, 100, 250}; // Multiplier for each slot image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Initialize VideoView
        VideoView videoView = findViewById(R.id.videoBackground);

        // Set the video path (assuming the video file is in the raw folder)
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.background_video);
        videoView.setVideoURI(videoUri);

        // Start the video
        videoView.start();

        // Loop the video
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        reel1 = findViewById(R.id.reel1);
        reel2 = findViewById(R.id.reel2);
        reel3 = findViewById(R.id.reel3);
        spinButton = findViewById(R.id.spinButton);
        autoSpinButton = findViewById(R.id.autoSpinButton); // <-- Added auto-spin button
        resultText = findViewById(R.id.resultText);
        coinText = findViewById(R.id.coinText);
        betText = findViewById(R.id.betText);

        updateUI();

        Button betButton = findViewById(R.id.betButton);
        betButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cycleBet();
            }
        });

        spinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinReels();
            }
        });

        autoSpinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAutoSpinning) {
                    stopAutoSpin();
                } else {
                    startAutoSpin();
                }
            }
        });
    }

    private void startAutoSpin() {
        isAutoSpinning = true;
        autoSpinButton.setText("Stop");
        autoSpinHandler.post(autoSpinRunnable);
    }

    private void stopAutoSpin() {
        isAutoSpinning = false;
        autoSpinButton.setText("Auto");
        autoSpinHandler.removeCallbacks(autoSpinRunnable);
    }

    private Runnable autoSpinRunnable = new Runnable() {
        @Override
        public void run() {
            if (isAutoSpinning) {
                spinReels();
                autoSpinHandler.postDelayed(this, 500); // Spin every 1 second
            }
        }
    };

    private void spinReels() {
        if (playerCoins < currentBet) {
            resultText.setText("Not enough coins!");
            return;
        }

        playerCoins -= currentBet;

        Random rand = new Random();
        int reel1Result = rand.nextInt(images.length);
        int reel2Result = rand.nextInt(images.length);
        int reel3Result = rand.nextInt(images.length);

        reel1.setImageResource(images[reel1Result]);
        reel2.setImageResource(images[reel2Result]);
        reel3.setImageResource(images[reel3Result]);

        if (reel1Result == reel2Result && reel2Result == reel3Result) {
            calculateReward(rewards[reel1Result]);
        }

        updateUI();
    }

    private void calculateReward(int multiplier) {
        int reward = currentBet * multiplier;
        playerCoins += reward;
        updateUI();
        updateResultText(reward);
    }

    private void updateResultText(int reward) {
        NumberFormat formatter = NumberFormat.getInstance();
        if (reward > 0) {
            resultText.setText("You won " + formatter.format(reward) + " coins!");
        } else if (reward < 0) {
            resultText.setText("You lost " + formatter.format(-reward) + " coins!");
        } else {
            resultText.setText(""); // Keep text blank if no change in coins
        }
    }

    private void cycleBet() {
        betIndex = (betIndex + 1) % betOptions.length;
        currentBet = betOptions[betIndex];
        updateUI();
    }

    private void updateUI() {
        NumberFormat formatter = NumberFormat.getInstance();
        coinText.setText("Coins: " + formatter.format(playerCoins));
        betText.setText("Bet: " + formatter.format(currentBet));
    }
}
