package com.example.sagivproject.adapters;

import androidx.recyclerview.widget.DiffUtil;

import com.example.sagivproject.models.ImageData;

import java.util.List;

public class ImageDiffCallback extends DiffUtil.Callback {

    private final List<ImageData> oldList;
    private final List<ImageData> newList;

    public ImageDiffCallback(List<ImageData> oldList, List<ImageData> newList) {
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
        return oldList.get(oldItemPosition).getId().equals(newList.get(newItemPosition).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getBase64().equals(newList.get(newItemPosition).getBase64());
    }
}
