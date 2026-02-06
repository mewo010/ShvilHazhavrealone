package com.example.sagivproject.adapters.diffUtils;

import androidx.recyclerview.widget.DiffUtil;

import com.example.sagivproject.models.ForumMessage;

import java.util.List;

public class ForumDiffCallback extends DiffUtil.Callback {

    private final List<ForumMessage> oldList;
    private final List<ForumMessage> newList;

    public ForumDiffCallback(List<ForumMessage> oldList, List<ForumMessage> newList) {
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
        return oldList.get(oldItemPosition).getMessageId().equals(newList.get(newItemPosition).getMessageId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        ForumMessage oldItem = oldList.get(oldItemPosition);
        ForumMessage newItem = newList.get(newItemPosition);
        return oldItem.getMessage().equals(newItem.getMessage())
                && oldItem.getFullName().equals(newItem.getFullName());
    }
}
