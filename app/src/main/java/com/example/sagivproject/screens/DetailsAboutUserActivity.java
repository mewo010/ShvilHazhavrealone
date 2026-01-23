package com.example.sagivproject.screens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sagivproject.R;
import com.example.sagivproject.bases.BaseActivity;
import com.example.sagivproject.models.User;
import com.example.sagivproject.screens.dialogs.EditUserDialog;
import com.example.sagivproject.screens.dialogs.FullImageDialog;
import com.example.sagivproject.screens.dialogs.ProfileImageDialog;
import com.example.sagivproject.services.DatabaseService;
import com.example.sagivproject.utils.ImageUtil;
import com.example.sagivproject.utils.SharedPreferencesUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

public class DetailsAboutUserActivity extends BaseActivity {
    private TextView txtTitle, txtEmail, txtPassword, txtAge, txtBirthDate, txtWins;
    private ImageView imgUserProfile;
    private static final int REQ_CAMERA = 100, REQ_GALLERY = 200;
    private User user;

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

        user = SharedPreferencesUtil.getUser(this);

        //משתמש מחובר
        LinearLayout topMenu = findViewById(R.id.topMenuDetailsAboutUser);
        Button btnToMain = findViewById(R.id.btn_DetailsAboutUser_to_main);
        Button btnToDetailsAboutUser = findViewById(R.id.btn_DetailsAboutUser_to_DetailsAboutUserPage);
        Button btnToContact = findViewById(R.id.btn_DetailsAboutUser_to_contact);
        Button btnToExit = findViewById(R.id.btn_DetailsAboutUser_to_exit);
        ImageButton btnToSettings = findViewById(R.id.btn_DetailsAboutUser_to_settings);
        View separatorLine = findViewById(R.id.separatorLine_DetailsAboutUser);

        //מנהל
        Button btnToAdmin = findViewById(R.id.btn_DetailsAboutUser_to_admin);

        boolean isAdmin = user.getIsAdmin();

        if (isAdmin) {
            //הופך את כפתורי המנהל ל-VISIBLE
            btnToAdmin.setVisibility(View.VISIBLE);
            topMenu.setVisibility(View.GONE);
            separatorLine.setVisibility(View.GONE);
        }
        else {
            //הופך את כפתורי המשתמש המחובר ל-VISIBLE
            btnToMain.setVisibility(View.VISIBLE);
            btnToDetailsAboutUser.setVisibility(View.VISIBLE);
            btnToContact.setVisibility(View.VISIBLE);
            btnToExit.setVisibility(View.VISIBLE);
            btnToSettings.setVisibility(View.VISIBLE);
            separatorLine.setVisibility(View.VISIBLE);
            topMenu.setVisibility(View.VISIBLE);
        }

        btnToMain.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        btnToContact.setOnClickListener(v -> startActivity(new Intent(this, ContactActivity.class)));
        btnToExit.setOnClickListener(v -> logout());
        btnToSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        btnToAdmin.setOnClickListener(v -> startActivity(new Intent(this, AdminPageActivity.class)));

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserFromDatabase();
    }

    private void loadUserFromDatabase() {
        DatabaseService.getInstance().getUser(user.getUid(), new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User dbUser) {
                user = dbUser;
                SharedPreferencesUtil.saveUser(DetailsAboutUserActivity.this, user);
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
            SharedPreferencesUtil.saveUser(DetailsAboutUserActivity.this, user);
            loadUserDetailsToUI();
        }).show();
    }

    private void openImagePicker() {
        boolean hasImage = user.getProfileImage() != null && !user.getProfileImage().isEmpty();

        new ProfileImageDialog(this, hasImage, new ProfileImageDialog.ImagePickerListener() {
            @Override
            public void onCamera() {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQ_CAMERA);
            }

            @Override
            public void onGallery() {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, REQ_GALLERY);
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

        databaseService.updateUser(user, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                SharedPreferencesUtil.saveUser(DetailsAboutUserActivity.this, user);
                Toast.makeText(DetailsAboutUserActivity.this, "תמונת הפרופיל נמחקה", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(DetailsAboutUserActivity.this, "שגיאה במחיקת התמונה", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;

        Bitmap bitmap = null;

        if (requestCode == REQ_CAMERA && data != null) {
            bitmap = (Bitmap) data.getExtras().get("data");
        }
        else if (requestCode == REQ_GALLERY && data != null) {
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData())
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (bitmap != null) {
            imgUserProfile.setImageBitmap(bitmap);

            //המרה ל־Base64 ושמירה
            String base64 = ImageUtil.convertTo64Base(imgUserProfile);
            user.setProfileImage(base64);

            saveProfileImage();
        }
    }

    private void saveProfileImage() {
        databaseService.updateUser(user, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                SharedPreferencesUtil.saveUser(DetailsAboutUserActivity.this, user);

                Toast.makeText(DetailsAboutUserActivity.this, "תמונת הפרופיל עודכנה!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(DetailsAboutUserActivity.this, "שגיאה בעדכון התמונה: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}