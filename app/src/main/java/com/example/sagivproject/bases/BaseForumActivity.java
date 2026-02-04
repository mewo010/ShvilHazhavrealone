package com.example.sagivproject.bases;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sagivproject.adapters.ForumAdapter;
import com.example.sagivproject.models.ForumMessage;
import com.example.sagivproject.models.User;
import com.example.sagivproject.services.interfaces.DatabaseCallback;
import com.example.sagivproject.services.interfaces.IForumService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class BaseForumActivity extends BaseActivity {
    protected final List<ForumMessage> messages = new ArrayList<>();
    protected RecyclerView recycler;
    protected EditText edtMessage;
    protected Button btnNewMessagesIndicator;
    protected ForumAdapter adapter;
    protected IForumService forumService;
    protected ForumPermissions permissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initForumViews(RecyclerView recycler, EditText edtMessage, Button btnNewMessages, IForumService forumService) {
        this.recycler = recycler;
        this.edtMessage = edtMessage;
        this.btnNewMessagesIndicator = btnNewMessages;
        this.forumService = forumService;

        //הגדרת לחיצה על כפתור "הודעות חדשות"
        if (btnNewMessagesIndicator != null) {
            btnNewMessagesIndicator.setOnClickListener(v -> {
                scrollToBottom(true);
                btnNewMessagesIndicator.setVisibility(View.GONE);
            });
        }
    }

    protected void setupForum() {
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ForumAdapter(messages);
        recycler.setAdapter(adapter);

        //האזנה לגלילה של המשתמש - אם הוא מגיע לסוף ידנית, נעלים את הכפתור
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (isLastItemVisible() && btnNewMessagesIndicator != null) {
                    btnNewMessagesIndicator.setVisibility(View.GONE);
                }
            }
        });

        adapter.setForumMessageListener(new ForumAdapter.ForumMessageListener() {
            @Override
            public void onClick(ForumMessage message) {
                forumService.deleteMessage(message.getMessageId(), new DatabaseCallback<>() {
                    @Override
                    public void onCompleted(Void data) {
                        Toast.makeText(BaseForumActivity.this, "ההודעה נמחקה", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Toast.makeText(BaseForumActivity.this, "שגיאה במחיקה", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public boolean isShowMenuOptions(ForumMessage message) {
                return permissions.canDelete(message);
            }
        });
    }

    protected void loadMessages() {
        forumService.listenToMessages(new DatabaseCallback<>() {
            @Override
            public void onCompleted(List<ForumMessage> list) {
                //בודקים אם המשתמש היה בסוף לפני העדכון
                boolean wasAtBottom = isLastItemVisible();

                messages.clear();
                messages.addAll(list);
                adapter.notifyDataSetChanged();

                if (wasAtBottom) {
                    //אם הוא כבר היה למטה, נמשיך לגלול אותו למטה עם ההודעה החדשה
                    scrollToBottom(false);
                } else if (!list.isEmpty() && btnNewMessagesIndicator != null) {
                    //אם הוא באמצע הרשימה והגיעה הודעה - נציג את הכפתור
                    btnNewMessagesIndicator.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(BaseForumActivity.this, "שגיאה בטעינה", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void sendMessage() {
        String text = edtMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        User user = sharedPreferencesUtil.getUser();

        forumService.sendMessage(Objects.requireNonNull(user), text, new DatabaseCallback<>() {
            @Override
            public void onCompleted(Void data) {
                edtMessage.setText("");
                //גלילה למטה ברגע שאני שלחתי הודעה
                scrollToBottom(true);
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(BaseForumActivity.this, "שגיאה בשליחה", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //פונקציית עזר לבדיקה אם המשתמש רואה את ההודעה האחרונה ברשימה
    private boolean isLastItemVisible() {
        LinearLayoutManager lm = (LinearLayoutManager) recycler.getLayoutManager();
        if (lm == null || adapter == null || adapter.getItemCount() == 0) return true;

        int lastVisible = lm.findLastCompletelyVisibleItemPosition();
        return lastVisible >= adapter.getItemCount() - 1;
    }

    //פונקציית עזר לגלילה לסוף הרשימה
    private void scrollToBottom(boolean smooth) {
        if (adapter != null && adapter.getItemCount() > 0) {
            if (smooth) {
                recycler.smoothScrollToPosition(adapter.getItemCount() - 1);
            } else {
                recycler.post(() -> recycler.scrollToPosition(adapter.getItemCount() - 1));
            }
        }
    }

    public interface ForumPermissions {
        boolean canDelete(ForumMessage message);
    }
}
