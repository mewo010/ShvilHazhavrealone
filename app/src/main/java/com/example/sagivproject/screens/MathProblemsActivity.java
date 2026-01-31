package com.example.sagivproject.screens;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sagivproject.R;
import com.example.sagivproject.bases.BaseActivity;
import com.example.sagivproject.models.User;
import com.example.sagivproject.models.enums.Operation;
import com.example.sagivproject.screens.dialogs.ResetMathStatsDialog;
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

        ViewGroup topMenuContainer = findViewById(R.id.topMenuContainer);
        setupTopMenu(topMenuContainer);

        tvCorrect = findViewById(R.id.tv_MathProblemsPage_correct);
        tvWrong = findViewById(R.id.tv_MathProblemsPage_wrong);
        Button btnResetStats = findViewById(R.id.btn_MathProblemsPage_resetStats);
        tvQuestion = findViewById(R.id.tv_MathProblemsPage_question);
        tvAnswer = findViewById(R.id.tv_MathProblemsPage_user_answer);

        btnResetStats.setOnClickListener(v -> new ResetMathStatsDialog(this, this::resetStats).show());

        generateProblem();
        setupKeypad();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatsUI();
    }

    private void updateStatsUI() {
        tvCorrect.setText(MessageFormat.format("נכונות: {0}", user.getMathProblemsStats().getCorrectAnswers()));
        tvWrong.setText(MessageFormat.format("טעויות: {0}", user.getMathProblemsStats().getWrongAnswers()));
    }

    private void resetStats() {
        user.getMathProblemsStats().setCorrectAnswers(0);
        user.getMathProblemsStats().setWrongAnswers(0);

        databaseService.resetMathStats(user.getUid());
        SharedPreferencesUtil.saveUser(this, user);

        updateStatsUI();
        Toast.makeText(this, "הנתונים אופסו בהצלחה", Toast.LENGTH_SHORT).show();
    }

    private void generateProblem() {
        Operation operation = Operation.values()[(int) (Math.random() * Operation.values().length)];

        int a, b;

        switch (operation) {
            case ADD:
                a = rand(10, 99);
                b = rand(10, 99);
                correctAnswer = a + b;
                tvQuestion.setText(MessageFormat.format("{0} + {1} =", a, b));
                break;

            case SUBTRACT:
                a = rand(10, 99);
                b = rand(10, a);
                correctAnswer = a - b;
                tvQuestion.setText(MessageFormat.format("{0} - {1} =", a, b));
                break;

            case MULTIPLY:
                a = rand(2, 12);
                b = rand(2, 12);
                correctAnswer = a * b;
                tvQuestion.setText(MessageFormat.format("{0} × {1} =", a, b));
                break;

            case DIVIDE:
                b = rand(2, 12);
                correctAnswer = rand(2, 12);
                a = b * correctAnswer;
                tvQuestion.setText(MessageFormat.format("{0} ÷ {1} =", a, b));
                break;

            case POWER:
                a = rand(2, 5);
                b = rand(2, 3);
                correctAnswer = (int) Math.pow(a, b);
                tvQuestion.setText(MessageFormat.format("{0}^{1} =", a, b));
                break;

            case SQRT:
                correctAnswer = rand(2, 12);
                a = correctAnswer * correctAnswer;
                tvQuestion.setText(MessageFormat.format("√{0} =", a));
                break;
        }

        userInput.setLength(0);
        tvAnswer.setText("");
    }

    private int rand(int min, int max) {
        return min + (int) (Math.random() * (max - min + 1));
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
}