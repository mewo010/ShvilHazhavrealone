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
import com.example.sagivproject.models.User;
import com.example.sagivproject.utils.SharedPreferencesUtil;

public class ForumActivity extends BaseForumActivity implements BaseForumActivity.ForumPermissions {
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forum);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.forumPage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = SharedPreferencesUtil.getUser(this);

        ViewGroup topMenuContainer = findViewById(R.id.topMenuContainer);
        setupTopMenu(topMenuContainer);

        Button btnSendMessage = findViewById(R.id.btn_forum_send_message);
        EditText edtNewMessage = findViewById(R.id.edt_forum_new_message);
        Button btnNewMessages = findViewById(R.id.btn_forum_new_messages_indicator);
        RecyclerView recyclerForum = findViewById(R.id.recycler_forum);

        btnSendMessage.setOnClickListener(v -> sendMessage());

        initForumViews(recyclerForum, edtNewMessage, btnNewMessages);
        this.permissions = this;
        setupForum();
    }

    @Override
    public boolean canDelete(ForumMessage message) {
        return message.getUserId() != null && message.getUserId().equals(user.getUid());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMessages();
    }
}