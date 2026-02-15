package com.example.sagivproject.screens;

import android.os.Bundle;
import android.view.ViewGroup;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * An activity that displays a "Tip of the Day".
 * The tip is fetched from a generative AI service and stored locally for the day.
 */
public class TipOfTheDayActivity extends BaseActivity {
    private static final String API_KEY = EncryptionAPIKey.decode(BuildConfig.API_KEY);
    private static final String MODEL = "models/gemini-2.5-flash";
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/" + MODEL + ":generateContent?key=" + API_KEY;
    private static final String KEY_DAILY_TIP_TEXT = "daily_tip_text";
    private static final String KEY_DAILY_TIP_DATE = "daily_tip_date";
    private TextView tipContent;

    /**
     * Called when the activity is first created.
     * This is where you should do all of your normal static set up: create views,
     * bind data to lists, etc.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_of_the_day);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tipOfTheDayPage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ViewGroup topMenuContainer = findViewById(R.id.topMenuContainer);
        setupTopMenu(topMenuContainer);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        TextView tvDate = findViewById(R.id.tv_tip_of_the_day_date);
        tvDate.setText(currentDate);

        tipContent = findViewById(R.id.tv_tip_content);
        tipContent.setAlpha(0f);
        tipContent.animate().alpha(1f).setDuration(800);

        checkDailyTip();
    }

    /**
     * Checks if a daily tip is already stored for the current date.
     * If a tip exists and the date matches, it's displayed.
     * Otherwise, a new tip is fetched from the AI service.
     */
    private void checkDailyTip() {
        String today = new SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                .format(new Date());

        String savedDate = sharedPreferencesUtil.getString(KEY_DAILY_TIP_DATE, null);
        String savedTip = sharedPreferencesUtil.getString(KEY_DAILY_TIP_TEXT, null);

        if (today.equals(savedDate) && savedTip != null) {
            tipContent.setText(savedTip);
        } else {
            fetchDailyTipFromAI(today);
        }
    }

    /**
     * Fetches a daily tip from the generative AI service.
     * Upon a successful fetch, the tip is displayed and saved in shared preferences
     * along with the current date to avoid fetching it again on the same day.
     *
     * @param today The current date in "yyyyMMdd" format.
     */
    private void fetchDailyTipFromAI(String today) {
        tipContent.setText("טוען טיפ יומי...");

        String prompt = "תן טיפ יומי קצר לחיים בעברית. עד 6 משפטים. בלי אימוג'ים.";

        try {
            JSONObject textPart = new JSONObject().put("text", prompt);
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

            new OkHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> tipContent.setText("שגיאה בטעינת הטיפ"));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    final String r = response.body().string();

                    runOnUiThread(() -> {
                        if (!response.isSuccessful()) {
                            tipContent.setText(MessageFormat.format("שגיאה: {0}", response.code()));
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

                            sharedPreferencesUtil.saveString(KEY_DAILY_TIP_TEXT, text);
                            sharedPreferencesUtil.saveString(KEY_DAILY_TIP_DATE, today);

                            tipContent.setAlpha(0f);
                            tipContent.setText(text);
                            tipContent.animate().alpha(1f).setDuration(800);

                        } catch (Exception e) {
                            tipContent.setText("פירוק תשובה נכשל");
                        }
                    });
                }
            });

        } catch (Exception e) {
            tipContent.setText("שגיאה בבקשה");
        }
    }
}
