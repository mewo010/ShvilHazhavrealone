package com.example.sagivproject.screens;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sagivproject.BuildConfig;
import com.example.sagivproject.R;
import com.example.sagivproject.bases.BaseActivity;
import com.example.sagivproject.utils.EncryptionAPIKey;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * An activity that provides an interface to interact with a generative AI model.
 * <p>
 * This screen allows users to ask questions and receive answers from the Gemini AI model.
 * The response is displayed with a typewriter animation.
 * </p>
 */
public class AiActivity extends BaseActivity {
    private static final String API_KEY = EncryptionAPIKey.decode(BuildConfig.API_KEY);
    private static final String MODEL = "models/gemini-2.5-flash";
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/" + MODEL + ":generateContent?key=" + API_KEY;
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();
    private Button send;
    private ProgressBar progressBar;
    private EditText questionInput;
    private TextView answerView;
    private Handler animationHandler;
    private int charIndex;

    /**
     * Initializes the activity, sets up the UI, and configures the send button.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ai);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.aiPage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ViewGroup topMenuContainer = findViewById(R.id.topMenuContainer);
        setupTopMenu(topMenuContainer);

        send = findViewById(R.id.btn_Ai_send_to_Ai);
        questionInput = findViewById(R.id.edit_Ai_question);
        answerView = findViewById(R.id.TV_Ai_txt_response);
        progressBar = findViewById(R.id.progressBar_Ai);

        send.setOnClickListener(view -> sendQuestion());
    }

    /**
     * Displays text in a TextView with a typewriter-like animation.
     *
     * @param textView The TextView to display the text in.
     * @param fullText The full text to be displayed.
     */
    private void displayTextWithAnimation(TextView textView, String fullText) {
        textView.setText("");
        charIndex = 0;
        animationHandler = new Handler(Looper.getMainLooper());
        final int delay = 15; // Animation speed in milliseconds per character

        animationHandler.post(new Runnable() {
            @Override
            public void run() {
                if (charIndex < fullText.length()) {
                    textView.append(String.valueOf(fullText.charAt(charIndex++)));
                    animationHandler.postDelayed(this, delay);
                }
            }
        });
    }

    /**
     * Sends the user's question to the generative AI model and displays the response.
     * Handles API call, response parsing, and error cases.
     */
    private void sendQuestion() {
        String q = questionInput.getText().toString().trim();
        if (q.isEmpty()) return;

        progressBar.setVisibility(View.VISIBLE);
        send.setVisibility(View.GONE);
        answerView.setText("");

        try {
            JSONObject textPart = new JSONObject().put("text", q);
            JSONArray parts = new JSONArray().put(textPart);
            JSONObject content = new JSONObject().put("parts", parts);
            JSONArray contents = new JSONArray().put(content);
            JSONObject json = new JSONObject().put("contents", contents);

            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(ENDPOINT)
                    .addHeader("x-goog-api-key", API_KEY)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        answerView.setText(String.format("שגיאה: %s", e.getMessage()));
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    final String r = response.body().string();
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        send.setVisibility(View.VISIBLE);
                        if (!response.isSuccessful()) {
                            answerView.setText(MessageFormat.format("קוד שגיאה: {0}\n{1}", response.code(), r));
                            return;
                        }
                        try {
                            JSONObject obj = new JSONObject(r);
                            String text = obj
                                    .getJSONArray("candidates")
                                    .getJSONObject(0)
                                    .getJSONObject("content")
                                    .getJSONArray("parts")
                                    .getJSONObject(0)
                                    .getString("text");

                            displayTextWithAnimation(answerView, text);
                        } catch (Exception e) {
                            answerView.setText(String.format("פירוק תשובה נכשל: %s", e.getMessage()));
                        }
                    });
                }
            });

        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            send.setVisibility(View.VISIBLE);
            answerView.setText(String.format("שגיאה: %s", e.getMessage()));
        }
    }
}
