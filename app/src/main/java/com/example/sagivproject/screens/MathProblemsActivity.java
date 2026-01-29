package com.example.sagivproject.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sagivproject.R;
import com.example.sagivproject.bases.BaseActivity;
import com.example.sagivproject.models.User;
import com.example.sagivproject.utils.SharedPreferencesUtil;

import java.text.MessageFormat;

public class MathProblemsActivity extends BaseActivity {
    private final StringBuilder userInput = new StringBuilder();
    private TextView tvCorrect, tvWrong;
    private User user;
    private TextView tvQuestion, tvAnswer;
    private int correctAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_math_problems);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mathProblemsPage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = SharedPreferencesUtil.getUser(MathProblemsActivity.this);

        Button btnToMain = findViewById(R.id.btn_MathProblemsPage_to_main);
        Button btnToContact = findViewById(R.id.btn_MathProblemsPage_to_contact);
        Button btnToDetailsAboutUser = findViewById(R.id.btn_MathProblemsPage_to_DetailsAboutUser);
        Button btnToExit = findViewById(R.id.btn_MathProblemsPage_to_exit);
        ImageButton btnToSettings = findViewById(R.id.btn_MathProblemsPage_to_settings);
        tvCorrect = findViewById(R.id.tv_MathProblemsPage_correct);
        tvWrong = findViewById(R.id.tv_MathProblemsPage_wrong);
        tvQuestion = findViewById(R.id.tv_MathProblemsPage_question);
        tvAnswer = findViewById(R.id.tv_MathProblemsPage_user_answer);

        btnToMain.setOnClickListener(view -> startActivity(new Intent(this, MainActivity.class)));
        btnToContact.setOnClickListener(view -> startActivity(new Intent(this, ContactActivity.class)));
        btnToDetailsAboutUser.setOnClickListener(view -> startActivity(new Intent(this, DetailsAboutUserActivity.class)));
        btnToExit.setOnClickListener(view -> logout());
        btnToSettings.setOnClickListener(view -> startActivity(new Intent(this, SettingsActivity.class)));

        generateProblem();
        setupKeypad();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatsUI();
    }

    private void generateProblem() {
        int a = 10 + (int) (Math.random() * 90);
        int b = 10 + (int) (Math.random() * 90);

        correctAnswer = a + b;

        tvQuestion.setTextDirection(View.TEXT_DIRECTION_LTR);
        tvQuestion.setText(MessageFormat.format("{0} + {1} =", a, b));

        userInput.setLength(0);
        tvAnswer.setText("");
    }

    private void setupKeypad() {
        GridLayout keypad = findViewById(R.id.keypad_MathProblemsPage);

        for (int i = 0; i < keypad.getChildCount(); i++) {
            View v = keypad.getChildAt(i);

            if (v instanceof Button) {
                Button btn = (Button) v;
                String text = btn.getText().toString();

                if (text.matches("\\d+")) {
                    btn.setOnClickListener(view -> {
                        userInput.append(text);
                        tvAnswer.setText(userInput.toString());
                    });
                }
            }
        }

        findViewById(R.id.btn_MathProblemsPage_delete).setOnClickListener(v -> deleteLast());
        findViewById(R.id.btn_MathProblemsPage_clear).setOnClickListener(v -> clearInput());
        findViewById(R.id.btn_MathProblemsPage_submit).setOnClickListener(v -> checkAnswer());
    }

    private void deleteLast() {
        if (userInput.length() > 0) {
            userInput.deleteCharAt(userInput.length() - 1);
            tvAnswer.setText(userInput.toString());
        }
    }

    private void clearInput() {
        userInput.setLength(0);
        tvAnswer.setText("");
    }

    private void checkAnswer() {
        if (userInput.length() == 0) return;

        int userAnswer = Integer.parseInt(userInput.toString());

        if (userAnswer == correctAnswer) {
            user.getMathProblemsStats().setCorrectAnswers(user.getMathProblemsStats().getCorrectAnswers() + 1);

            Toast.makeText(this, "נכון! ✅", Toast.LENGTH_SHORT).show();
            generateProblem();
            databaseService.addCorrectAnswer(user.getUid());
        } else {
            user.getMathProblemsStats().setWrongAnswers(user.getMathProblemsStats().getWrongAnswers() + 1);

            Toast.makeText(this, "טעות, נסה שוב ❌", Toast.LENGTH_SHORT).show();
            databaseService.addWrongAnswer(user.getUid());
        }

        SharedPreferencesUtil.saveUser(this, user);

        updateStatsUI();
    }

    private void updateStatsUI() {
        tvCorrect.setText(MessageFormat.format("נכונות: {0}", user.getMathProblemsStats().getCorrectAnswers()));
        tvWrong.setText(MessageFormat.format("טעויות: {0}", user.getMathProblemsStats().getWrongAnswers()));
    }
}