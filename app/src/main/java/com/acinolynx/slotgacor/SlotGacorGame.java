package com.acinolynx.slotgacor;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class SlotGacorGame extends AppCompatActivity {

    private ImageView reel1, reel2, reel3;
    private Button spinButton;
    private TextView resultText, coinText, betText;
    private int playerCoins = 500000;
    private int currentBet = 500;
    private int[] betOptions = {500, 2000, 5000, 10000};
    private int betIndex = 0;

    private int[] images = {R.drawable.ic_slot1, R.drawable.ic_slot2, R.drawable.ic_slot3, R.drawable.ic_slot4, R.drawable.ic_slot5};

    private int[] rewards = {-25, 25, 50, 100, 250}; // Multiplier for each slot image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reel1 = findViewById(R.id.reel1);
        reel2 = findViewById(R.id.reel2);
        reel3 = findViewById(R.id.reel3);
        spinButton = findViewById(R.id.spinButton);
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
    }

    // Corrected calculateReward method
    private void calculateReward(int multiplier) {
        int reward = currentBet * multiplier;
        playerCoins += reward; // Update playerCoins based on the reward
        updateUI(); // This will update both the coin and bet TextViews
        updateResultText(reward);
    }

    private void updateResultText(int reward) {
        if (reward > 0) {
            resultText.setText("You won " + reward + " coins!");
        } else if (reward < 0) {
            resultText.setText("You lost " + (-reward) + " coins!");
        } else {
            resultText.setText("No change in coins.");
        }
    }

    private void cycleBet() {
        betIndex = (betIndex + 1) % betOptions.length;
        currentBet = betOptions[betIndex];
        updateUI();
    }

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
            calculateReward(rewards[reel1Result]);  // Using calculateReward here to handle the reward
        }

        updateUI();
    }

    // Update the UI with the latest coins and bet values
    private void updateUI() {
        coinText.setText("Coins: " + playerCoins);
        betText.setText("Bet: " + currentBet);
    }
}
