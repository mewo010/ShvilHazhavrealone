package com.example.sagivproject.screens;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sagivproject.R;
import com.example.sagivproject.adapters.MemoryGameAdapter;
import com.example.sagivproject.adapters.diffUtils.CardDiffCallback;
import com.example.sagivproject.bases.BaseActivity;
import com.example.sagivproject.models.Card;
import com.example.sagivproject.models.GameRoom;
import com.example.sagivproject.models.ImageData;
import com.example.sagivproject.models.User;
import com.example.sagivproject.screens.dialogs.ExitGameDialog;
import com.example.sagivproject.screens.dialogs.GameEndDialog;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryGameActivity extends BaseActivity implements MemoryGameAdapter.MemoryGameListener {
    private static final long TURN_TIME_LIMIT = 15000; //15 שניות
    private RecyclerView recyclerCards;
    private boolean endDialogShown = false, localLock = false;
    private String roomId;
    private User user;
    private GameRoom currentRoom;
    private MemoryGameAdapter adapter;
    private TextView tvTimer, tvTurnStatus, tvScore, tvOpponentName;
    private CountDownTimer turnTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_memory_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.memoryGamePage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        roomId = getIntent().getStringExtra("roomId");
        user = sharedPreferencesUtil.getUser();

        recyclerCards = findViewById(R.id.recycler_OnlineMemoryGame);
        recyclerCards.setLayoutManager(new GridLayoutManager(this, 3));

        tvTimer = findViewById(R.id.tv_OnlineMemoryGame_timer);
        tvTurnStatus = findViewById(R.id.tv_OnlineMemoryGame_turn_status);
        tvScore = findViewById(R.id.tv_OnlineMemoryGame_score);
        tvOpponentName = findViewById(R.id.tv_OnlineMemoryGame_opponent_name);

        adapter = new MemoryGameAdapter(new ArrayList<>(), this);
        recyclerCards.setAdapter(adapter);

        Button btnExit = findViewById(R.id.btn_OnlineMemoryGame_to_exit);
        btnExit.setOnClickListener(v -> showExitGameDialog());

        listenToGame();
    }

    private void showExitGameDialog() {
        new ExitGameDialog(this, () -> {
            if (currentRoom != null && !"finished".equals(currentRoom.getStatus())) {
                String myUid = user.getId();
                String opponentUid = myUid.equals(currentRoom.getPlayer1().getId()) ?
                        currentRoom.getPlayer2().getId() : currentRoom.getPlayer1().getId();

                databaseService.getGameService().updateRoomField(roomId, "winnerUid", opponentUid);
                databaseService.getGameService().updateRoomField(roomId, "status", "finished");
            }

            Intent intent = new Intent(this, GameHomeScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }).show();
    }

    private void showGameEndDialog(GameRoom room) {
        if (endDialogShown) return;
        endDialogShown = true;

        String winnerUid = room.getWinnerUid();
        String message;

        if ("draw".equals(winnerUid)) {
            message = "זה נגמר בתיקו!";
        } else if (user.getId().equals(winnerUid)) {
            message = "כל הכבוד! ניצחת והתווסף לך ניצחון!";
        } else {
            message = "הפעם הפסדת... לא נורא!";
        }

        new GameEndDialog(this, message, () -> {
            // מעבר למסך הבית בעת לחיצה על "אישור"
            Intent intent = new Intent(MemoryGameActivity.this, GameHomeScreenActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }).show();
    }

    private void updateScoreUI(GameRoom room) {
        boolean amIPlayer1 = user.getId().equals(room.getPlayer1().getId());

        int myScore = amIPlayer1 ? room.getPlayer1Score() : room.getPlayer2Score();
        int opponentScore = amIPlayer1 ? room.getPlayer2Score() : room.getPlayer1Score();

        tvScore.setText(MessageFormat.format("אני: {0} | יריב: {1}", myScore, opponentScore));
    }

    private void setupGameBoard(GameRoom room) {
        // רק השחקן הראשון יוצר את הלוח
        if (room.getCards() == null && user.getId().equals(room.getPlayer1().getId())) {
            databaseService.getImageService().getAllImages(new DatabaseCallback<>() {
                @Override
                public void onCompleted(List<ImageData> allImages) {
                    if (allImages == null || allImages.size() < 6) {
                        Toast.makeText(MemoryGameActivity.this, "אין מספיק תמונות כדי להתחיל את המשחק.", Toast.LENGTH_LONG).show();
                        databaseService.getGameService().cancelRoom(roomId, null); // Cancel the room as the game cannot start
                        finish();
                        return;
                    }

                    // בחירת 6 תמונות רנדומליות ליצירת 12 קלפים (זוגות)
                    Collections.shuffle(allImages);
                    List<ImageData> selected = allImages.subList(0, 6);

                    List<Card> cards = new ArrayList<>();
                    for (ImageData img : selected) {
                        // יצירת שני קלפים עם אותו ID ותוכן Base64
                        cards.add(new Card(img.getId(), img.getBase64()));
                        cards.add(new Card(img.getId(), img.getBase64()));
                    }
                    Collections.shuffle(cards);

                    // שמירה ל-Firebase
                    databaseService.getGameService().initGameBoard(roomId, cards, room.getPlayer1().getId(), null);
                }

                @Override
                public void onFailed(Exception e) {
                    Toast.makeText(MemoryGameActivity.this, "שגיאה בטעינת תמונות המשחק", Toast.LENGTH_SHORT).show();
                    databaseService.getGameService().cancelRoom(roomId, null); // Cancel the room on error
                    finish(); // חזרה למסך הקודם
                }
            });
        }
    }

    public boolean isMyTurn() {
        return currentRoom != null &&
                user.getId().equals(currentRoom.getCurrentTurnUid()) &&
                !currentRoom.isProcessingMatch();
    }

    @Override
    public void onCardClicked(Card card, View itemView, ImageView imageView) {
        if (currentRoom == null) return;

        if (localLock) return;

        if (!isMyTurn()) return;

        int cardIndex = adapter.getCards().indexOf(card);
        if (card.getIsMatched() || card.getIsRevealed()) return;

        handleCardSelection(cardIndex);
    }

    private void handleCardSelection(int clickedIndex) {
        Integer firstIndex = currentRoom.getFirstSelectedCardIndex();

        if (firstIndex == null) {
            // --- בחירת קלף ראשון ---
            databaseService.getGameService().updateCardStatus(roomId, clickedIndex, true, false);
            databaseService.getGameService().updateRoomField(roomId, "firstSelectedCardIndex", clickedIndex);
        } else {
            // --- בחירת קלף שני ---
            if (firstIndex == clickedIndex) return; //לחיצה על אותו קלף

            localLock = true;
            databaseService.getGameService().setProcessing(roomId, true); //חסימת לחיצות נוספות
            databaseService.getGameService().updateCardStatus(roomId, clickedIndex, true, false);

            //בדיקה אם יש התאמה
            new Handler(Looper.getMainLooper()).postDelayed(() -> checkMatch(firstIndex, clickedIndex), 1000); //השהייה כדי שהשחקן יראה את הקלף השני
        }
    }

    private void checkMatch(int idx1, int idx2) {
        Card c1 = currentRoom.getCards().get(idx1);
        Card c2 = currentRoom.getCards().get(idx2);

        if (c1.getId().equals(c2.getId())) {
            // --- הצלחה ---
            adapter.animateSuccess(idx1, recyclerCards);
            adapter.animateSuccess(idx2, recyclerCards);

            databaseService.getGameService().updateCardStatus(roomId, idx1, true, true);
            databaseService.getGameService().updateCardStatus(roomId, idx2, true, true);

            String scoreField = user.getId().equals(currentRoom.getPlayer1().getId()) ? "player1Score" : "player2Score";

            databaseService.getGameService().updateRoomField(roomId, scoreField, user.getId().equals(currentRoom.getPlayer1().getId()) ?
                    currentRoom.getPlayer1Score() + 1 : currentRoom.getPlayer2Score() + 1);

        } else {
            // --- טעות ---
            adapter.animateError(idx1, recyclerCards);
            adapter.animateError(idx2, recyclerCards);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                databaseService.getGameService().updateCardStatus(roomId, idx1, false, false);
                databaseService.getGameService().updateCardStatus(roomId, idx2, false, false);

                String nextTurn = user.getId().equals(currentRoom.getPlayer1().getId())
                        ? currentRoom.getPlayer2().getId()
                        : currentRoom.getPlayer1().getId();
                databaseService.getGameService().updateRoomField(roomId, "currentTurnUid", nextTurn);
            }, 600);
        }

        databaseService.getGameService().updateRoomField(roomId, "firstSelectedCardIndex", null);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            databaseService.getGameService().setProcessing(roomId, false);
            localLock = false;
        }, 700);
    }

    private void checkIfGameFinished() {
        boolean allCardsMatched = true;
        for (Card card : currentRoom.getCards()) {
            if (!card.getIsMatched()) {
                allCardsMatched = false;
                break;
            }
        }

        if (allCardsMatched) {
            finishGame(currentRoom);
        }
    }

    private void finishGame(GameRoom room) {
        if ("finished".equals(room.getStatus())) return;

        String winnerUid = calculateWinner(room);
        databaseService.getGameService().updateRoomField(roomId, "winnerUid", winnerUid);
        databaseService.getGameService().updateRoomField(roomId, "status", "finished");
    }

    private void listenToGame() {
        databaseService.getGameService().listenToGame(roomId, new DatabaseCallback<>() {
            @Override
            public void onCompleted(GameRoom room) {
                if (room == null) {
                    Toast.makeText(MemoryGameActivity.this, "החדר נמחק.", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                currentRoom = room;

                if (room.getPlayer1() != null && room.getPlayer2() != null) {
                    String opponentName = user.getId().equals(room.getPlayer1().getId()) ?
                            room.getPlayer2().getFullName() : room.getPlayer1().getFullName();
                    tvOpponentName.setText(String.format("משחק נגד: %s", opponentName));
                }

                updateScoreUI(room);

                if (room.getCards() == null || room.getCards().isEmpty()) {
                    setupGameBoard(room);
                    return;
                }

                if (room.getCards() != null) {
                    List<Card> oldCards = new ArrayList<>(adapter.getCards());
                    CardDiffCallback diffCallback = new CardDiffCallback(oldCards, room.getCards());
                    DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

                    adapter.getCards().clear();
                    adapter.getCards().addAll(room.getCards());
                    diffResult.dispatchUpdatesTo(adapter);
                }

                String myUid = user.getId();
                String opponentUid = myUid.equals(room.getPlayer1().getId()) ?
                        room.getPlayer2().getId() : room.getPlayer1().getId();
                databaseService.getGameService().setupForfeitOnDisconnect(roomId, opponentUid);

                if ("finished".equals(room.getStatus())) {
                    if (turnTimer != null) turnTimer.cancel();
                    databaseService.getGameService().removeForfeitOnDisconnect(roomId);

                    if (myUid.equals(room.getWinnerUid())) {
                        databaseService.getGameService().addUserWin(myUid);
                    }

                    showGameEndDialog(room);
                    return;
                }

                checkIfGameFinished();

                boolean isMyTurn = user.getId().equals(room.getCurrentTurnUid());
                if (isMyTurn) {
                    tvTurnStatus.setText("תורך!");
                    tvTurnStatus.setTextColor(getColor(android.R.color.holo_green_dark));
                    startTurnTimer();
                } else {
                    tvTurnStatus.setText("תור היריב...");
                    tvTurnStatus.setTextColor(getColor(android.R.color.holo_red_dark));
                    if (turnTimer != null) turnTimer.cancel();
                    tvTimer.setText("");
                }
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(MemoryGameActivity.this, "שגיאה בהאזנה למשחק.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (roomId != null) {
            databaseService.getGameService().stopListeningToGame(roomId);
        }
    }

    private String calculateWinner(GameRoom room) {
        int p1 = room.getPlayer1Score();
        int p2 = room.getPlayer2Score();

        if (p1 > p2) {
            return room.getPlayer1().getId();
        }
        if (p2 > p1) {
            return room.getPlayer2().getId();
        }
        return "draw";
    }

    private void startTurnTimer() {
        if (turnTimer != null) turnTimer.cancel();

        turnTimer = new android.os.CountDownTimer(TURN_TIME_LIMIT, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText(MessageFormat.format("זמן נותר: {0}", millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                if (currentRoom.getFirstSelectedCardIndex() != null) {
                    databaseService.getGameService().updateCardStatus(roomId, currentRoom.getFirstSelectedCardIndex(), false, false);
                    databaseService.getGameService().updateRoomField(roomId, "firstSelectedCardIndex", null);
                }

                String opponentUid = user.getId().equals(currentRoom.getPlayer1().getId()) ?
                        currentRoom.getPlayer2().getId() : currentRoom.getPlayer1().getId();
                databaseService.getGameService().updateRoomField(roomId, "currentTurnUid", opponentUid);
            }
        }.start();
    }
}
