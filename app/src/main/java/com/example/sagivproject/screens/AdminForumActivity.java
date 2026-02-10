package com.example.sagivproject.screens;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sagivproject.R;
import com.example.sagivproject.bases.BaseForumActivity;
import com.example.sagivproject.models.ForumMessage;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AdminForumActivity extends BaseForumActivity implements BaseForumActivity.ForumPermissions {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_forum);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.adminForumPage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String categoryId = getIntent().getStringExtra("categoryId");
        String categoryName = getIntent().getStringExtra("categoryName");

        ViewGroup topMenuContainer = findViewById(R.id.topMenuContainer);
        setupTopMenu(topMenuContainer);

        Button btnSendMessage = findViewById(R.id.btn_AdminForum_send_message);
        EditText edtNewMessage = findViewById(R.id.edt_AdminForum_new_message);
        Button btnNewMessages = findViewById(R.id.btn_AdminForum_new_messages_indicator);
        RecyclerView recyclerForum = findViewById(R.id.recycler_AdminForum);
        btnSendMessage.setOnClickListener(v -> sendMessage());

        initForumViews(recyclerForum, edtNewMessage, btnNewMessages);
        this.permissions = this;
        setupForum(categoryId, categoryName);
    }

    @Override
    public boolean canDelete(ForumMessage message) {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMessages();
    }
}
