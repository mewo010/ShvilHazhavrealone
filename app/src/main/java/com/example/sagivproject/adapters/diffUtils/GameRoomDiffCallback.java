package com.example.sagivproject.adapters.diffUtils;

import androidx.recyclerview.widget.DiffUtil;

import com.example.sagivproject.models.GameRoom;

import java.util.List;
import java.util.Objects;

public class GameRoomDiffCallback extends DiffUtil.Callback {

    private final List<GameRoom> oldList;
    private final List<GameRoom> newList;

    public GameRoomDiffCallback(List<GameRoom> oldList, List<GameRoom> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getRoomId().equals(newList.get(newItemPosition).getRoomId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        GameRoom oldItem = oldList.get(oldItemPosition);
        GameRoom newItem = newList.get(newItemPosition);

        return Objects.equals(oldItem.getStatus(), newItem.getStatus())
                && oldItem.getPlayer1Score() == newItem.getPlayer1Score()
                && oldItem.getPlayer2Score() == newItem.getPlayer2Score();
    }
}
