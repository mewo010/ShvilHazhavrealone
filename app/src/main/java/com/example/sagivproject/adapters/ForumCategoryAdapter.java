package com.example.sagivproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sagivproject.R;
import com.example.sagivproject.models.ForumCategory;

import java.util.List;

public class ForumCategoryAdapter extends RecyclerView.Adapter<ForumCategoryAdapter.CategoryViewHolder> {
    private final List<ForumCategory> categories;
    private final OnCategoryDeleteListener listener;

    public ForumCategoryAdapter(List<ForumCategory> categories, OnCategoryDeleteListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forum_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        ForumCategory category = categories.get(position);
        holder.categoryName.setText(category.getName());
        holder.deleteButton.setOnClickListener(v -> listener.onDelete(category));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public interface OnCategoryDeleteListener {
        void onDelete(ForumCategory category);
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        final TextView categoryName;
        final ImageButton deleteButton;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.txt_category_name);
            deleteButton = itemView.findViewById(R.id.btn_delete_category);
        }
    }
}
