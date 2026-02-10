package com.example.sagivproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sagivproject.R;
import com.example.sagivproject.adapters.diffUtils.ForumCategoryDiffCallback;
import com.example.sagivproject.models.ForumCategory;

public class ForumCategoryAdapter extends ListAdapter<ForumCategory, ForumCategoryAdapter.CategoryViewHolder> {
    private final OnCategoryInteractionListener listener;
    private final boolean isAdmin;

    public ForumCategoryAdapter(@NonNull OnCategoryInteractionListener listener, boolean isAdmin) {
        super(new ForumCategoryDiffCallback());
        this.listener = listener;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forum_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        ForumCategory category = getItem(position);
        holder.categoryName.setText(category.getName());

        if (isAdmin) {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDelete(category);
                }
            });
        } else {
            holder.deleteButton.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(category);
            }
        });
    }

    public interface OnCategoryInteractionListener {
        void onDelete(ForumCategory category);

        void onClick(ForumCategory category);
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        final TextView categoryName;
        final ImageButton deleteButton;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.txt_category_name);
            deleteButton = itemView.findViewById(R.id.btn_delete_category);
        }
    }
}