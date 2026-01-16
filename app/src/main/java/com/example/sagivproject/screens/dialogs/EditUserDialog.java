package com.example.sagivproject.screens.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sagivproject.R;
import com.example.sagivproject.models.User;
import com.example.sagivproject.services.AuthService;
import com.example.sagivproject.utils.Validator;

public class EditUserDialog {
    private final Context context;
    private final User user;
    private final Runnable onSuccess;
    private final AuthService authService;

    public EditUserDialog(Context context, User user, Runnable onSuccess) {
        this.context = context;
        this.user = user;
        this.onSuccess = onSuccess;
        this.authService = new AuthService(context);
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

            if (!validateInput(fName, lName, email, pass,
                    inputFirstName, inputLastName, inputEmail, inputPassword)) {
                return;
            }

            authService.updateUser(user, fName, lName, email, pass, new AuthService.UpdateUserCallback() {
                @Override
                public void onSuccess(User updatedUser) {
                    Toast.makeText(context, "הפרטים עודכנו!", Toast.LENGTH_SHORT).show();
                    if (onSuccess != null) {
                        onSuccess.run();
                    }
                    dialog.dismiss();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }
            });
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private boolean validateInput(String fName, String lName, String email, String pass, EditText firstName, EditText lastName, EditText emailEdt, EditText passEdt) {
        if (fName.isEmpty() || lName.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(context, "כל השדות חובה", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Validator.isNameValid(fName)) {
            firstName.requestFocus();
            Toast.makeText(context, "שם פרטי קצר מדי", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!Validator.isNameValid(lName)) {
            lastName.requestFocus();
            Toast.makeText(context, "שם משפחה קצר מדי", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!Validator.isEmailValid(email)) {
            emailEdt.requestFocus();
            Toast.makeText(context, "כתובת האימייל לא תקינה", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!Validator.isPasswordValid(pass)) {
            passEdt.requestFocus();
            Toast.makeText(context, "הסיסמה קצרה מדי", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}