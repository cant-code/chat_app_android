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
import com.damnation.etachat.http.Group;

import java.util.ArrayList;
import java.util.List;

public class GroupAdapter extends ListAdapter<Group, GroupAdapter.GroupAdapterViewHolder> {

    private List<Group> originalList = new ArrayList<>();

    public GroupAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public GroupAdapter.GroupAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_main, parent, false);
        return new GroupAdapter.GroupAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupAdapter.GroupAdapterViewHolder holder, int position) {
        holder.bindTo(getItem(position));
    }

    public void setData(@Nullable List<Group> list) {
        originalList = list;
        super.submitList(list);
    }

    public void filter(String query) {
        List<Group> filteredList = new ArrayList<>();
        for (Group group: originalList) {
            if(group.getUsername().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(group);
            }
        }
        submitList(filteredList);
    }

    static class GroupAdapterViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private ImageView imageView;
        private Group group;

        public GroupAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textName);
            imageView = itemView.findViewById(R.id.imageAvatar);
        }

        void bindTo(Group group) {
            this.group = group;
            textView.setText(group.getUsername());
            imageView.setImageResource(R.drawable.ic_baseline_group_24);
        }
    }

    private static final DiffUtil.ItemCallback<Group> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Group>() {
                @Override
                public boolean areItemsTheSame(@NonNull Group oldData,
                                               @NonNull Group newData) {
                    return oldData.get_id().equals(newData.get_id());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Group oldData,
                                                  @NonNull Group newData) {
                    return oldData.equals(newData);
                }
            };
}
