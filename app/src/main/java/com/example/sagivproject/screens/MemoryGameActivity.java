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
import com.example.sagivproject.services.IDatabaseService.DatabaseCallback;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The main activity for the online memory game.
 * <p>
 * This screen manages the entire lifecycle of a memory game between two players.
 * It handles setting up the game board, listening for real-time updates from Firebase,
 * managing player turns, checking for matches, updating scores, and determining the winner.
 * </p>
 */
public class MemoryGameActivity extends BaseActivity implements MemoryGameAdapter.MemoryGameListener {
    private static final long TURN_TIME_LIMIT = 15000; // 15 seconds
    private RecyclerView recyclerCards;
    private boolean endDialogShown = false, localLock = false;
    private String roomId;
    private User user;
    private GameRoom currentRoom;
    private MemoryGameAdapter adapter;
    private TextView tvTimer, tvTurnStatus, tvScore, tvOpponentName;
    private CountDownTimer turnTimer;

    /**
     * Initializes the activity, sets up the UI components, and starts listening for game updates.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
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

    /**
     * Shows a confirmation dialog for exiting the game. If confirmed, the opponent is declared the winner.
     */
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

    /**
     * Shows a dialog announcing the end of the game with a win, loss, or draw message.
     *
     * @param room The final state of the game room.
     */
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
            Intent intent = new Intent(MemoryGameActivity.this, GameHomeScreenActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }).show();
    }

    /**
     * Updates the score display text view.
     *
     * @param room The current game room state.
     */
    private void updateScoreUI(GameRoom room) {
        boolean amIPlayer1 = user.getId().equals(room.getPlayer1().getId());

        int myScore = amIPlayer1 ? room.getPlayer1Score() : room.getPlayer2Score();
        int opponentScore = amIPlayer1 ? room.getPlayer2Score() : room.getPlayer1Score();

        tvScore.setText(MessageFormat.format("אני: {0} | יריב: {1}", myScore, opponentScore));
    }

    /**
     * Sets up the initial game board by fetching random images, creating card pairs,
     * shuffling them, and saving the board state to Firebase. This is only done by Player 1.
     *
     * @param room The game room.
     */
    private void setupGameBoard(GameRoom room) {
        if (room.getCards() == null && user.getId().equals(room.getPlayer1().getId())) {
            databaseService.getImageService().getAllImages(new DatabaseCallback<>() {
                @Override
                public void onCompleted(List<ImageData> allImages) {
                    if (allImages == null || allImages.size() < 6) {
                        Toast.makeText(MemoryGameActivity.this, "אין מספיק תמונות כדי להתחיל את המשחק.", Toast.LENGTH_LONG).show();
                        databaseService.getGameService().cancelRoom(roomId, null);
                        finish();
                        return;
                    }

                    Collections.shuffle(allImages);
                    List<ImageData> selected = allImages.subList(0, 6);

                    List<Card> cards = new ArrayList<>();
                    for (ImageData img : selected) {
                        cards.add(new Card(img.getId(), img.getBase64()));
                        cards.add(new Card(img.getId(), img.getBase64()));
                    }
                    Collections.shuffle(cards);

                    databaseService.getGameService().initGameBoard(roomId, cards, room.getPlayer1().getId(), null);
                }

                @Override
                public void onFailed(Exception e) {
                    Toast.makeText(MemoryGameActivity.this, "שגיאה בטעינת תמונות המשחק", Toast.LENGTH_SHORT).show();
                    databaseService.getGameService().cancelRoom(roomId, null);
                    finish();
                }
            });
        }
    }

    /**
     * Checks if it is the current user's turn to play.
     *
     * @return True if it's the user's turn, false otherwise.
     */
    public boolean isMyTurn() {
        return currentRoom != null &&
                user.getId().equals(currentRoom.getCurrentTurnUid()) &&
                !currentRoom.isProcessingMatch();
    }

    /**
     * Handles a click on a card. It allows a card to be selected only if it is the player's turn.
     *
     * @param card      The card that was clicked.
     * @param itemView  The view of the item that was clicked.
     * @param imageView The ImageView within the item.
     */
    @Override
    public void onCardClicked(Card card, View itemView, ImageView imageView) {
        if (currentRoom == null || localLock || !isMyTurn()) return;

        int cardIndex = adapter.getCards().indexOf(card);
        if (card.getIsMatched() || card.getIsRevealed()) return;

        handleCardSelection(cardIndex);
    }

    /**
     * Manages the logic for selecting one or two cards.
     *
     * @param clickedIndex The index of the card that was just clicked.
     */
    private void handleCardSelection(int clickedIndex) {
        Integer firstIndex = currentRoom.getFirstSelectedCardIndex();

        if (firstIndex == null) {
            databaseService.getGameService().updateCardStatus(roomId, clickedIndex, true, false);
            databaseService.getGameService().updateRoomField(roomId, "firstSelectedCardIndex", clickedIndex);
        } else {
            if (firstIndex == clickedIndex) return;

            localLock = true;
            databaseService.getGameService().setProcessing(roomId, true);
            databaseService.getGameService().updateCardStatus(roomId, clickedIndex, true, false);

            new Handler(Looper.getMainLooper()).postDelayed(() -> checkMatch(firstIndex, clickedIndex), 1000);
        }
    }

    /**
     * Checks if the two selected cards are a match.
     * Updates scores and card states accordingly.
     *
     * @param idx1 The index of the first selected card.
     * @param idx2 The index of the second selected card.
     */
    private void checkMatch(int idx1, int idx2) {
        Card c1 = currentRoom.getCards().get(idx1);
        Card c2 = currentRoom.getCards().get(idx2);

        if (c1 != null && c2 != null && c1.getId().equals(c2.getId())) {
            adapter.animateSuccess(idx1, recyclerCards);
            adapter.animateSuccess(idx2, recyclerCards);

            databaseService.getGameService().updateCardStatus(roomId, idx1, true, true);
            databaseService.getGameService().updateCardStatus(roomId, idx2, true, true);

            String scoreField = user.getId().equals(currentRoom.getPlayer1().getId()) ? "player1Score" : "player2Score";

            databaseService.getGameService().updateRoomField(roomId, scoreField, user.getId().equals(currentRoom.getPlayer1().getId()) ?
                    currentRoom.getPlayer1Score() + 1 : currentRoom.getPlayer2Score() + 1);

        } else {
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

    /**
     * Checks if all cards on the board have been matched. If so, finishes the game.
     */
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

    /**
     * Finishes the game by calculating the winner and updating the game room status.
     *
     * @param room The final state of the game room.
     */
    private void finishGame(GameRoom room) {
        if ("finished".equals(room.getStatus())) return;

        String winnerUid = calculateWinner(room);
        databaseService.getGameService().updateRoomField(roomId, "winnerUid", winnerUid);
        databaseService.getGameService().updateRoomField(roomId, "status", "finished");
    }

    /**
     * Sets up a real-time listener for the game room to react to changes in the game state.
     */
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
                    List<Card> newCards = new ArrayList<>(room.getCards());

                    CardDiffCallback diffCallback = new CardDiffCallback(oldCards, newCards);
                    DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

                    adapter.getCards().clear();
                    adapter.getCards().addAll(newCards);
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

    /**
     * Cleans up resources, particularly the game listener, when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (roomId != null) {
            databaseService.getGameService().stopListeningToGame(roomId);
        }
    }

    /**
     * Determines the winner of the game based on the final scores.
     *
     * @param room The final game room state.
     * @return The UID of the winning player, or "draw" for a tie.
     */
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

    /**
     * Starts a countdown timer for the current player's turn. If the time runs out,
     * the turn automatically passes to the opponent.
     */
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
