package com.example.sagivproject.models;

import java.io.Serializable;
import java.util.List;

/**
 * Represents the state of an online memory game room.
 * <p>
 * This class holds all the information about a single game, including the players,
 * the game board (list of cards), the current turn, scores, and the overall game status.
 * It is used to synchronize the game state between players via the Firebase database.
 * </p>
 */
public class GameRoom implements Serializable, Idable {
    private String id;
    private String status; // Can be "waiting", "playing", or "finished"

    private User player1;
    private User player2;

    private List<Card> cards;
    private String currentTurnUid;

    private int player1Score;
    private int player2Score;

    private Integer firstSelectedCardIndex; // Index of the first card selected in a turn
    private boolean processingMatch; // Flag to prevent clicks during match processing
    private String winnerUid; // UID of the winner, or "draw"

    /**
     * Default constructor required for calls to DataSnapshot.getValue(GameRoom.class).
     */
    public GameRoom() {
    }

    /**
     * Constructs a new waiting GameRoom.
     *
     * @param id      The unique ID of the room.
     * @param player1 The user who created the room.
     */
    public GameRoom(String id, User player1) {
        this.id = id;
        this.player1 = player1;
        this.player2 = null;
        this.status = "waiting";
        this.player1Score = 0;
        this.player2Score = 0;
        this.firstSelectedCardIndex = null;
        this.processingMatch = false;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getPlayer1() {
        return player1;
    }

    public void setPlayer1(User player1) {
        this.player1 = player1;
    }

    public User getPlayer2() {
        return player2;
    }

    public void setPlayer2(User player2) {
        this.player2 = player2;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public String getCurrentTurnUid() {
        return currentTurnUid;
    }

    public void setCurrentTurnUid(String currentTurnUid) {
        this.currentTurnUid = currentTurnUid;
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public void setPlayer1Score(int player1Score) {
        this.player1Score = player1Score;
    }

    public int getPlayer2Score() {
        return player2Score;
    }

    public void setPlayer2Score(int player2Score) {
        this.player2Score = player2Score;
    }

    public Integer getFirstSelectedCardIndex() {
        return firstSelectedCardIndex;
    }

    public void setFirstSelectedCardIndex(Integer index) {
        this.firstSelectedCardIndex = index;
    }

    public boolean isProcessingMatch() {
        return processingMatch;
    }

    public void setProcessingMatch(boolean processingMatch) {
        this.processingMatch = processingMatch;
    }

    public String getWinnerUid() {
        return winnerUid;
    }

    public void setWinnerUid(String winnerUid) {
        this.winnerUid = winnerUid;
    }
}
