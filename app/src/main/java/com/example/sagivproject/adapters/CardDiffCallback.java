package com.example.sagivproject.adapters;

import androidx.recyclerview.widget.DiffUtil;

import com.example.sagivproject.models.Card;

import java.util.List;

public class CardDiffCallback extends DiffUtil.Callback {

    private final List<Card> oldList;
    private final List<Card> newList;

    public CardDiffCallback(List<Card> oldList, List<Card> newList) {
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
        // Since cards can be identical, we rely on position
        return true;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Card oldCard = oldList.get(oldItemPosition);
        Card newCard = newList.get(newItemPosition);
        return oldCard.getIsRevealed() == newCard.getIsRevealed() &&
                oldCard.getIsMatched() == newCard.getIsMatched();
    }
}
