package com.example.sagivproject.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sagivproject.R;
import com.example.sagivproject.adapters.LeaderboardAdapter;
import com.example.sagivproject.bases.BaseActivity;
import com.example.sagivproject.models.GameRoom;
import com.example.sagivproject.models.User;
import com.example.sagivproject.services.IDatabaseService;
import com.example.sagivproject.services.IGameService;
import com.google.firebase.database.ValueEventListener;

import java.text.MessageFormat;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GameHomeScreenActivity extends BaseActivity {
    private Button btnFindEnemy;
    private Button btnCancelFindEnemy;
    private TextView TVictories, TVStatusOfFindingEnemy;
    private GameRoom currentRoom;
    private boolean gameStarted = false;
    private ValueEventListener roomListener;
    private RecyclerView rvLeaderboard;
    private LeaderboardAdapter adapter;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_home_screen);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.gameHomeScreenPage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = sharedPreferencesUtil.getUser();

        ViewGroup topMenuContainer = findViewById(R.id.topMenuContainer);
        setupTopMenu(topMenuContainer);

        btnFindEnemy = findViewById(R.id.btn_GameHomeScreen_find_enemy);
        btnCancelFindEnemy = findViewById(R.id.btn_GameHomeScreen_cancel_find_enemy);
        TVictories = findViewById(R.id.tv_GameHomeScreen_victories);
        TVStatusOfFindingEnemy = findViewById(R.id.tv_GameHomeScreen_status_of_finding_enemy);
        rvLeaderboard = findViewById(R.id.recyclerView_GameHomeScreen_leaderboard);

        btnFindEnemy.setOnClickListener(view -> findEnemy());
        btnCancelFindEnemy.setOnClickListener(view -> cancel());
        rvLeaderboard.setLayoutManager(new LinearLayoutManager(this));
        setupLeaderboard();
    }

    @Override
    protected void onResume() {
        super.onResume();
        user = sharedPreferencesUtil.getUser();
        loadWins(user);
    }

    private void loadWins(User user) {
        TVictories.setText(MessageFormat.format("ניצחונות: {0}", user.getCountWins()));
    }

    private void setupLeaderboard() {
        databaseService.getUserService().getUserList(new IDatabaseService.DatabaseCallback<>() {
            @Override
            public void onCompleted(List<User> users) {
                if (users != null) {
                    users.removeIf(User::isAdmin);

                    //מיון הרשימה לפי כמות ניצחונות מהגבוה לנמוך
                    users.sort((u1, u2) -> Integer.compare(u2.getCountWins(), u1.getCountWins()));

                    adapter = new LeaderboardAdapter(users);
                    rvLeaderboard.setAdapter(adapter);
                }
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(GameHomeScreenActivity.this, "שגיאה בטבלת המובילים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void findEnemy() {
        TVStatusOfFindingEnemy.setVisibility(View.VISIBLE);
        btnCancelFindEnemy.setVisibility(View.VISIBLE);
        btnFindEnemy.setVisibility(View.GONE);

        databaseService.getGameService().findOrCreateRoom(user, new IDatabaseService.DatabaseCallback<>() {
            @Override
            public void onCompleted(GameRoom room) {
                currentRoom = room;
                listenToRoom(room.getId());
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(GameHomeScreenActivity.this, "שגיאה במציאת יריב", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancel() {
        if (currentRoom != null && "waiting".equals(currentRoom.getStatus()) && user.getId().equals(currentRoom.getPlayer1().getId())) {
            databaseService.getGameService().cancelRoom(currentRoom.getId(), null);
            currentRoom = null;
        }

        TVStatusOfFindingEnemy.setVisibility(View.GONE);
        btnCancelFindEnemy.setVisibility(View.GONE);
        btnFindEnemy.setVisibility(View.VISIBLE);
    }

    private void listenToRoom(String roomId) {
        roomListener = databaseService.getGameService().listenToRoomStatus(roomId, new IGameService.IRoomStatusCallback() {
            @Override
            public void onRoomStarted(GameRoom startedRoom) {
                if (gameStarted) return;
                gameStarted = true;
                startGame(startedRoom);
            }

            @Override
            public void onRoomDeleted() {
                cancel();
            }

            @Override
            public void onRoomFinished(GameRoom room) {
                cancel();
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }

    private void startGame(GameRoom room) {
        if (roomListener != null) {
            databaseService.getGameService().removeRoomListener(room.getId(), roomListener);
            roomListener = null;
        }

        currentRoom = room;

        TVStatusOfFindingEnemy.setVisibility(View.GONE);
        btnCancelFindEnemy.setVisibility(View.GONE);

        Intent intent = new Intent(this, MemoryGameActivity.class);
        intent.putExtra("roomId", room.getId());
        startActivity(intent);
        finish();
    }
}
