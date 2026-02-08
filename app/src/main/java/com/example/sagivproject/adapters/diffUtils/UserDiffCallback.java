package com.example.sagivproject.adapters.diffUtils;

import androidx.recyclerview.widget.DiffUtil;

import com.example.sagivproject.models.User;

import java.util.List;
import java.util.Objects;

public class UserDiffCallback extends DiffUtil.Callback {

    private final List<User> oldList;
    private final List<User> newList;

    public UserDiffCallback(List<User> oldList, List<User> newList) {
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
        User oldUser = oldList.get(oldItemPosition);
        User newUser = newList.get(newItemPosition);

        return Objects.equals(oldUser.getFirstName(), newUser.getFirstName())
                && Objects.equals(oldUser.getLastName(), newUser.getLastName())
                && oldUser.getRole() == newUser.getRole()
                && Objects.equals(oldUser.getProfileImage(), newUser.getProfileImage())
                && oldUser.getCountWins() == newUser.getCountWins();
    }
}
