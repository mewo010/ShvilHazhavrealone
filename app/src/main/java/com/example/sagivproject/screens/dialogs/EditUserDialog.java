package com.example.sagivproject.screens.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sagivproject.R;
import com.example.sagivproject.models.User;
import com.example.sagivproject.services.DatabaseService;
import com.example.sagivproject.utils.InputValidator;

public class EditUserDialog {
    private final Context context;
    private final User user;
    private final Runnable onSuccess;

    public EditUserDialog(Context context, User user, Runnable onSuccess) {
        this.context = context;
        this.user = user;
        this.onSuccess = onSuccess;
    }

    public void show() {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_edit_user);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        EditText inputFirstName = dialog.findViewById(R.id.inputEditUserFirstName);
        EditText inputLastName = dialog.findViewById(R.id.inputEditUserLastName);
        EditText inputEmail = dialog.findViewById(R.id.inputEditUserEmail);
        EditText inputPassword = dialog.findViewById(R.id.inputEditUserPassword);
        Button btnSave = dialog.findViewById(R.id.btnEditUserSave);
        Button btnCancel = dialog.findViewById(R.id.btnEditUserCancel);

        inputFirstName.setText(user.getFirstName());
        inputLastName.setText(user.getLastName());
        inputEmail.setText(user.getEmail());
        inputPassword.setText(user.getPassword());

        btnSave.setOnClickListener(v -> {
            String fName = inputFirstName.getText().toString().trim();
            String lName = inputLastName.getText().toString().trim();
            String email = inputEmail.getText().toString().trim();
            String pass = inputPassword.getText().toString().trim();

            if (fName.isEmpty() || lName.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(context, "כל השדות חובה", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!InputValidator.isNameValid(fName)) {
                inputFirstName.requestFocus();
                Toast.makeText(context, "שם פרטי קצר מדי", Toast.LENGTH_LONG).show();
                return;
            }
            if (!InputValidator.isNameValid(lName)) {
                inputLastName.requestFocus();
                Toast.makeText(context, "שם משפחה קצר מדי", Toast.LENGTH_LONG).show();
                return;
            }
            if (!InputValidator.isEmailValid(email)) {
                inputEmail.requestFocus();
                Toast.makeText(context, "כתובת האימייל לא תקינה", Toast.LENGTH_LONG).show();
                return;
            }
            if (!InputValidator.isPasswordValid(pass)) {
                inputPassword.requestFocus();
                Toast.makeText(context, "הסיסמה קצרה מדי", Toast.LENGTH_LONG).show();
                return;
            }

            boolean emailChanged = !email.equals(user.getEmail());

            if (emailChanged) {
                DatabaseService.getInstance().checkIfEmailExists(email, new DatabaseService.DatabaseCallback<Boolean>() {
                    @Override
                    public void onCompleted(Boolean exists) {
                        if (exists) {
                            Toast.makeText(context, "אימייל זה תפוס", Toast.LENGTH_SHORT).show();
                        } else {
                            updateUser(dialog, fName, lName, email, pass);
                        }
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Toast.makeText(context, "שגיאה בבדיקת אימייל", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                updateUser(dialog, fName, lName, email, pass);
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void updateUser(Dialog dialog, String fName, String lName, String email, String pass) {
        user.setFirstName(fName);
        user.setLastName(lName);
        user.setEmail(email);
        user.setPassword(pass);

        DatabaseService.getInstance().updateUser(user, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Toast.makeText(context, "הפרטים עודכנו!", Toast.LENGTH_SHORT).show();
                if (onSuccess != null) onSuccess.run();
                dialog.dismiss();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(context, "שגיאה: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}