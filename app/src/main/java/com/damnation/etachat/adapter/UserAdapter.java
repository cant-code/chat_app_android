package com.damnation.etachat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.damnation.etachat.R;
import com.damnation.etachat.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends ListAdapter<User, UserAdapter.UserAdapterViewHolder> {

    private List<User> originalList = new ArrayList<>();

    public UserAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public UserAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_main, parent, false);
        return new UserAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapterViewHolder holder, int position) {
        holder.bindTo(getItem(position));
    }

    public void setData(@Nullable List<User> list) {
        originalList = list;
        super.submitList(list);
    }

    public void filter(String query) {
        List<User> filteredList = new ArrayList<>();
        for (User user: originalList) {
            if(user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(user);
            }
        }
        submitList(filteredList);
    }

    static class UserAdapterViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private ImageView imageView;
        private User user;

        public UserAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textName);
            imageView = itemView.findViewById(R.id.imageAvatar);
        }

        void bindTo(User user) {
            this.user = user;
            textView.setText(user.getUsername());
            imageView.setImageResource(R.drawable.ic_baseline_person_24);
        }
    }

    private static final DiffUtil.ItemCallback<User> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<User>() {
                @Override
                public boolean areItemsTheSame(@NonNull User oldData,
                                               @NonNull User newData) {
                    return oldData.get_id().equals(newData.get_id());
                }

                @Override
                public boolean areContentsTheSame(@NonNull User oldData,
                                                  @NonNull User newData) {
                    return oldData.equals(newData);
                }
            };
}
