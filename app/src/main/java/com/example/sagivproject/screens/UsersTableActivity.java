package com.example.sagivproject.screens;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sagivproject.R;
import com.example.sagivproject.adapters.UsersTableAdapter;
import com.example.sagivproject.adapters.diffUtils.UserDiffCallback;
import com.example.sagivproject.bases.BaseActivity;
import com.example.sagivproject.models.User;
import com.example.sagivproject.models.enums.UserRole;
import com.example.sagivproject.screens.dialogs.AddUserDialog;
import com.example.sagivproject.screens.dialogs.EditUserDialog;
import com.example.sagivproject.screens.dialogs.FullImageDialog;
import com.example.sagivproject.services.IDatabaseService;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UsersTableActivity extends BaseActivity {
    private final List<User> usersList = new ArrayList<>(), filteredList = new ArrayList<>();
    private UsersTableAdapter adapter;
    private EditText editSearch;
    private Spinner spinnerSearchType;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_users_table);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.usersTablePage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        currentUser = sharedPreferencesUtil.getUser();

        ViewGroup topMenuContainer = findViewById(R.id.topMenuContainer);
        setupTopMenu(topMenuContainer);

        Button btnAddUser = findViewById(R.id.btn_UsersTable_add_user);
        btnAddUser.setOnClickListener(v -> new AddUserDialog(this, newUser -> loadUsers(), databaseService.getAuthService()).show());

        adapter = new UsersTableAdapter(filteredList, currentUser,
                new UsersTableAdapter.OnUserActionListener() {

                    @Override
                    public void onToggleAdmin(User user) {
                        handleToggleAdmin(user);
                    }

                    @Override
                    public void onDeleteUser(User user) {
                        handleDeleteUser(user);
                    }

                    @Override
                    public void onUserClicked(User clickedUser) {
                        new EditUserDialog(
                                UsersTableActivity.this,
                                clickedUser,
                                () -> loadUsers(),
                                databaseService.getAuthService()
                        ).show();
                    }

                    @Override
                    public void onUserImageClicked(User user, ImageView imageView) {
                        String base64Image = user.getProfileImage();
                        if (base64Image == null || base64Image.isEmpty()) return;

                        Drawable drawable = imageView.getDrawable();
                        if (drawable == null) return;

                        new FullImageDialog(UsersTableActivity.this, drawable).show();
                    }
                });

        RecyclerView recyclerView = findViewById(R.id.recycler_UsersTable);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        editSearch = findViewById(R.id.edit_UsersTable_search);
        spinnerSearchType = findViewById(R.id.spinner_UsersTable_search_type);

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString().trim());
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.search_types)
        ) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view;
                Typeface typeface = ResourcesCompat.getFont(UsersTableActivity.this, R.font.text_hebrew);
                tv.setTypeface(typeface);
                tv.setTextColor(getResources().getColor(R.color.text_color, null));
                tv.setBackgroundColor(getResources().getColor(R.color.background_color_buttons, null));

                tv.setTextSize(22);
                tv.setPadding(24, 24, 24, 24);
                return tv;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                Typeface typeface = ResourcesCompat.getFont(UsersTableActivity.this, R.font.text_hebrew);
                tv.setTypeface(typeface);
                tv.setTextColor(getResources().getColor(R.color.text_color, null));
                tv.setBackgroundColor(getResources().getColor(R.color.background_color_buttons, null));

                tv.setTextSize(22);
                tv.setPadding(24, 24, 24, 24);
                return tv;
            }
        };

        spinnerSearchType.setAdapter(adapter);

        spinnerSearchType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterUsers(editSearch.getText().toString().trim());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers();
    }

    private void loadUsers() {
        databaseService.getUserService().getUserList(new IDatabaseService.DatabaseCallback<>() {
            @Override
            public void onCompleted(List<User> list) {
                usersList.clear();

                for (User user : list) {
                    if (user != null && user.getId() != null) {
                        usersList.add(user);
                    }
                }

                filterUsers(editSearch.getText().toString().trim());
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(UsersTableActivity.this, "שגיאה בהעלאת משתמשים", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleToggleAdmin(User user) {
        UserRole newRole = user.getRole() == UserRole.ADMIN ? UserRole.REGULAR : UserRole.ADMIN;

        databaseService.getUserService().updateUserRole(user.getId(), newRole, new IDatabaseService.DatabaseCallback<>() {
            @Override
            public void onCompleted(Void object) {
                Toast.makeText(
                        UsersTableActivity.this,
                        "הסטטוס עודכן בהצלחה",
                        Toast.LENGTH_SHORT
                ).show();
                loadUsers(); // רענון
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(
                        UsersTableActivity.this,
                        "שגיאה בעדכון סטטוס",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void handleDeleteUser(User user) {
        boolean isSelf = user.equals(currentUser);
        databaseService.getUserService().deleteUser(user.getId(), new IDatabaseService.DatabaseCallback<>() {
            @Override
            public void onCompleted(Void object) {
                if (isSelf) {
                    sharedPreferencesUtil.signOutUser();
                    Intent intent = new Intent(UsersTableActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    return;
                }

                Toast.makeText(UsersTableActivity.this, "המשתמש נמחק", Toast.LENGTH_SHORT).show();
                loadUsers();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(UsersTableActivity.this, "שגיאה במחיקה", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterUsers(String query) {
        List<User> oldList = new ArrayList<>(filteredList);
        filteredList.clear();
        String searchType = spinnerSearchType.getSelectedItem().toString();
        String lowerQuery = query.toLowerCase();

        switch (searchType) {
            case "מנהלים":
                for (User user : usersList) {
                    if (user.isAdmin() &&
                            user.getFullName().toLowerCase().contains(lowerQuery)) {
                        filteredList.add(user);
                    }
                }
                break;

            case "משתמשים רגילים":
                for (User user : usersList) {
                    if (!user.isAdmin() &&
                            user.getFullName().toLowerCase().contains(lowerQuery)) {
                        filteredList.add(user);
                    }
                }
                break;

            case "ניצחונות":
                for (User user : usersList) {
                    if (user.getFullName().toLowerCase().contains(lowerQuery)) {
                        filteredList.add(user);
                    }
                }
                filteredList.sort(
                        (u1, u2) -> Integer.compare(u2.getCountWins(), u1.getCountWins())
                );
                break;

            case "שם פרטי":
                for (User user : usersList) {
                    if (user.getFirstName() != null &&
                            user.getFirstName().toLowerCase().contains(lowerQuery)) {
                        filteredList.add(user);
                    }
                }
                break;

            case "שם משפחה":
                for (User user : usersList) {
                    if (user.getLastName() != null &&
                            user.getLastName().toLowerCase().contains(lowerQuery)) {
                        filteredList.add(user);
                    }
                }
                break;

            case "אימייל":
                for (User user : usersList) {
                    if (user.getEmail() != null &&
                            user.getEmail().toLowerCase().contains(lowerQuery)) {
                        filteredList.add(user);
                    }
                }
                break;

            case "הכל":
            default:
                filteredList.addAll(usersList);
                break;
        }
        UserDiffCallback diffCallback = new UserDiffCallback(oldList, filteredList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        diffResult.dispatchUpdatesTo(adapter);
    }
}
