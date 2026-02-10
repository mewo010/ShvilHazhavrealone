package com.example.sagivproject.adapters.diffUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.sagivproject.models.ForumCategory;

public class ForumCategoryDiffCallback extends DiffUtil.ItemCallback<ForumCategory> {
    @Override
    public boolean areItemsTheSame(@NonNull ForumCategory oldItem, @NonNull ForumCategory newItem) {
        return oldItem.getId().equals(newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull ForumCategory oldItem, @NonNull ForumCategory newItem) {
        return oldItem.getName().equals(newItem.getName());
    }
}
