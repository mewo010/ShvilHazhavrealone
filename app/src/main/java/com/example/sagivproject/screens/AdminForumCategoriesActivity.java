package com.example.sagivproject.screens;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sagivproject.R;
import com.example.sagivproject.adapters.ForumCategoryAdapter;
import com.example.sagivproject.bases.BaseActivity;
import com.example.sagivproject.models.ForumCategory;
import com.example.sagivproject.services.IDatabaseService.DatabaseCallback;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class AdminForumCategoriesActivity extends BaseActivity {
    private final List<ForumCategory> categories = new ArrayList<>();
    private ForumCategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_forum_categories);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.adminForumCatergoriesPage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ViewGroup topMenuContainer = findViewById(R.id.topMenuContainer);
        setupTopMenu(topMenuContainer);

        RecyclerView recyclerView = findViewById(R.id.recycler_forum_categories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ForumCategoryAdapter(categories, category -> databaseService.getForumCategoriesService().deleteCategory(category.getId(), new DatabaseCallback<>() {
            @Override
            public void onCompleted(Void data) {
                loadCategories();
                Toast.makeText(AdminForumCategoriesActivity.this, "קטגוריה נמחקה", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(AdminForumCategoriesActivity.this, "שגיאה במחיקה", Toast.LENGTH_SHORT).show();
            }
        }));
        recyclerView.setAdapter(adapter);

        EditText edtNewCategoryName = findViewById(R.id.edt_new_category_name);
        Button btnAddCategory = findViewById(R.id.btn_add_category);

        btnAddCategory.setOnClickListener(v -> {
            String categoryName = edtNewCategoryName.getText().toString().trim();
            if (!categoryName.isEmpty()) {
                databaseService.getForumCategoriesService().addCategory(categoryName, new DatabaseCallback<>() {
                    @Override
                    public void onCompleted(Void data) {
                        edtNewCategoryName.setText("");
                        loadCategories();
                        Toast.makeText(AdminForumCategoriesActivity.this, "קטגוריה נוספה", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Toast.makeText(AdminForumCategoriesActivity.this, "שגיאה בהוספה", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        loadCategories();
    }

    private void loadCategories() {
        databaseService.getForumCategoriesService().getCategories(new DatabaseCallback<>() {
            @Override
            public void onCompleted(List<ForumCategory> data) {
                categories.clear();
                categories.addAll(data);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(AdminForumCategoriesActivity.this, "שגיאה בטעינת קטגוריות", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
