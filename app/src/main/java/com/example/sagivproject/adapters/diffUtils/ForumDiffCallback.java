package com.example.sagivproject.adapters.diffUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.sagivproject.models.ForumMessage;

public class ForumDiffCallback extends DiffUtil.ItemCallback<ForumMessage> {

    @Override
    public boolean areItemsTheSame(@NonNull ForumMessage oldItem, @NonNull ForumMessage newItem) {
        return oldItem.getId().equals(newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull ForumMessage oldItem, @NonNull ForumMessage newItem) {
        return oldItem.getMessage().equals(newItem.getMessage())
                && oldItem.getFullName().equals(newItem.getFullName());
    }
}
