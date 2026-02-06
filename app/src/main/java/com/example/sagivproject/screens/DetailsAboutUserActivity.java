package com.example.sagivproject.screens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sagivproject.R;
import com.example.sagivproject.bases.BaseActivity;
import com.example.sagivproject.models.User;
import com.example.sagivproject.screens.dialogs.EditUserDialog;
import com.example.sagivproject.screens.dialogs.FullImageDialog;
import com.example.sagivproject.screens.dialogs.ProfileImageDialog;
import com.example.sagivproject.services.interfaces.DatabaseCallback;
import com.example.sagivproject.utils.CalendarUtil;
import com.example.sagivproject.utils.ImageUtil;
import com.example.sagivproject.utils.Validator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DetailsAboutUserActivity extends BaseActivity {
    @Inject
    CalendarUtil calendarUtil;
    @Inject
    Validator validator;
    private TextView txtTitle, txtEmail, txtPassword, txtAge, txtBirthDate, txtWins;
    private ImageView imgUserProfile;
    private User user;
    private ActivityResultLauncher<Void> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details_about_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detailsAboutUserPage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = sharedPreferencesUtil.getUser();

        assert user != null;
        boolean isAdmin = user.isAdmin();

        ViewGroup topMenuContainer = findViewById(R.id.topMenuContainer);
        setupTopMenu(topMenuContainer);

        if (!isAdmin) {
            View separatorLine = findViewById(R.id.separatorLine);
            separatorLine.setVisibility(View.VISIBLE);
        }

        Button btnEditUser = findViewById(R.id.btn_DetailsAboutUser_edit_user);
        btnEditUser.setOnClickListener(v -> openEditDialog());

        imgUserProfile = findViewById(R.id.img_DetailsAboutUser_user_profile);
        FloatingActionButton btnChangePhoto = findViewById(R.id.btn_DetailsAboutUser_change_photo);
        btnChangePhoto.setOnClickListener(v -> openImagePicker());
        imgUserProfile.setOnClickListener(v -> {
            if (user.getProfileImage() != null) {
                new FullImageDialog(
                        this,
                        imgUserProfile.getDrawable()
                ).show();
            }
        });

        txtTitle = findViewById(R.id.txt_DetailsAboutUser_title);
        txtAge = findViewById(R.id.txt_DetailsAboutUser_age);
        txtBirthDate = findViewById(R.id.txt_DetailsAboutUser_birth_date);
        txtWins = findViewById(R.id.txt_DetailsAboutUser_wins);
        txtEmail = findViewById(R.id.txt_DetailsAboutUser_email);
        txtPassword = findViewById(R.id.txt_DetailsAboutUser_password);

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicturePreview(),
                bitmap -> {
                    if (bitmap != null) {
                        handleImageBitmap(bitmap);
                    }
                }
        );

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(Objects.requireNonNull(result.getData().getData())));
                            if (bitmap != null) {
                                handleImageBitmap(bitmap);
                            }
                        } catch (Exception e) {
                            Toast.makeText(DetailsAboutUserActivity.this, "שגיאה בטעינת גלריית התמונות", Toast.LENGTH_SHORT).show();

                        }
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserFromDatabase();
    }

    private void loadUserFromDatabase() {
        databaseService.users().getUser(user.getUid(), new DatabaseCallback<>() {
            @Override
            public void onCompleted(User dbUser) {
                user = dbUser;
                sharedPreferencesUtil.saveUser(user);
                loadUserDetailsToUI();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(DetailsAboutUserActivity.this, "שגיאה בטעינת נתונים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserDetailsToUI() {
        txtTitle.setText(user.getFullName());
        txtEmail.setText(user.getEmail());
        txtPassword.setText(user.getPassword());

        if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
            Bitmap bmp = ImageUtil.convertFrom64base(user.getProfileImage());
            if (bmp != null) {
                imgUserProfile.setImageBitmap(bmp);
            }
        } else {
            imgUserProfile.setImageResource(R.drawable.ic_user);
        }

        int age = user.getAge();
        txtAge.setText(String.valueOf(age));

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(user.getBirthDateMillis());

        String birthDate = String.format(
                Locale.ROOT,
                "%02d/%02d/%04d",
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.YEAR)
        );

        txtBirthDate.setText(birthDate);

        txtWins.setText(String.valueOf(user.getCountWins()));
    }

    private void openEditDialog() {
        new EditUserDialog(this, user, () -> {
            sharedPreferencesUtil.saveUser(user);
            loadUserDetailsToUI();
        }, databaseService.auth(), calendarUtil, validator).show();
    }

    private void openImagePicker() {
        boolean hasImage = user.getProfileImage() != null && !user.getProfileImage().isEmpty();

        new ProfileImageDialog(this, hasImage, new ProfileImageDialog.ImagePickerListener() {
            @Override
            public void onCamera() {
                cameraLauncher.launch(null);
            }

            @Override
            public void onGallery() {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                galleryLauncher.launch(galleryIntent);
            }

            @Override
            public void onDelete() {
                deleteProfileImage();
            }
        }).show();
    }

    private void deleteProfileImage() {
        user.setProfileImage(null);

        imgUserProfile.setImageResource(R.drawable.ic_user);

        databaseService.users().updateUser(user, new DatabaseCallback<>() {
            @Override
            public void onCompleted(Void object) {
                sharedPreferencesUtil.saveUser(user);
                Toast.makeText(DetailsAboutUserActivity.this, "תמונת הפרופיל נמחקה", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(DetailsAboutUserActivity.this, "שגיאה במחיקת התמונה", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleImageBitmap(Bitmap bitmap) {
        imgUserProfile.setImageBitmap(bitmap);

        //המרה ל־Base64 ושמירה
        String base64 = ImageUtil.convertTo64Base(imgUserProfile);
        user.setProfileImage(base64);

        saveProfileImage();
    }

    private void saveProfileImage() {
        databaseService.users().updateUser(user, new DatabaseCallback<>() {
            @Override
            public void onCompleted(Void object) {
                sharedPreferencesUtil.saveUser(user);

                Toast.makeText(DetailsAboutUserActivity.this, "תמונת הפרופיל עודכנה!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(DetailsAboutUserActivity.this, "שגיאה בעדכון התמונה: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
