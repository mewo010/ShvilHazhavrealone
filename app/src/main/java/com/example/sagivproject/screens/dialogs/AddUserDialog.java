package com.example.sagivproject.screens.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sagivproject.R;
import com.example.sagivproject.models.User;
import com.example.sagivproject.services.AuthService;
import com.example.sagivproject.utils.InputValidator;

public class AddUserDialog {
    private final Context context;
    private final AddUserListener listener;
    private final AuthService authService;

    public interface AddUserListener {
        void onUserAdded(User newUser);
    }

    public AddUserDialog(Context context, AddUserListener listener) {
        this.context = context;
        this.listener = listener;
        this.authService = new AuthService(context);
    }

    public void show() {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_add_user);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        EditText inputFirstName = dialog.findViewById(R.id.inputAddUserFirstName);
        EditText inputLastName = dialog.findViewById(R.id.inputAddUserLastName);
        EditText inputEmail = dialog.findViewById(R.id.inputAddUserEmail);
        EditText inputPassword = dialog.findViewById(R.id.inputAddUserPassword);

        Button btnAdd = dialog.findViewById(R.id.btnAddUserSave);
        Button btnCancel = dialog.findViewById(R.id.btnAddUserCancel);

        btnAdd.setOnClickListener(v -> {

            String fName = inputFirstName.getText().toString().trim();
            String lName = inputLastName.getText().toString().trim();
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();

            if (!validateInput(fName, lName, email, password,
                    inputFirstName, inputLastName, inputEmail, inputPassword)) {
                return;
            }

            authService.addUser(fName, lName, email, password, new AuthService.AddUserCallback() {
                @Override
                public void onSuccess(User user) {
                    if (listener != null) {
                        listener.onUserAdded(user);
                    }
                    Toast.makeText(context, "משתמש נוסף בהצלחה", Toast.LENGTH_SHORT).show();
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

    private boolean validateInput(String fName, String lName, String email, String password, EditText firstName, EditText lastName, EditText emailEdt, EditText passwordEdt) {
        if (fName.isEmpty() || lName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!InputValidator.isNameValid(fName)) {
            firstName.requestFocus();
            Toast.makeText(context, "שם פרטי קצר מדי", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!InputValidator.isNameValid(lName)) {
            lastName.requestFocus();
            Toast.makeText(context, "שם משפחה קצר מדי", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!InputValidator.isEmailValid(email)) {
            emailEdt.requestFocus();
            Toast.makeText(context, "כתובת האימייל לא תקינה", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!InputValidator.isPasswordValid(password)) {
            passwordEdt.requestFocus();
            Toast.makeText(context, "הסיסמה קצרה מדי", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}