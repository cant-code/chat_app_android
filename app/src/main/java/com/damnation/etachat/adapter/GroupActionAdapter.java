package com.damnation.etachat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.damnation.etachat.R;
import com.damnation.etachat.model.GroupAction;

import java.util.ArrayList;
import java.util.List;

public class GroupActionAdapter extends RecyclerView.Adapter<GroupActionAdapter.GroupActionAdapterViewHolder> {

    private final List<GroupAction> originalList;
    private final OnItemClicked onItemClicked;

    public GroupActionAdapter(OnItemClicked onClick) {
        originalList = new ArrayList<>();
        originalList.add(new GroupAction("Create new Room", R.drawable.ic_baseline_create_24));
        originalList.add(new GroupAction("Join a Room", R.drawable.ic_baseline_add_24));
        originalList.add(new GroupAction("Global", R.drawable.ic_baseline_public_24));
        this.onItemClicked = onClick;
    }

    @NonNull
    @Override
    public GroupActionAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_main, parent, false);
        return new GroupActionAdapter.GroupActionAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupActionAdapterViewHolder holder, int position) {
        holder.bindTo(originalList.get(position));
    }

    @Override
    public int getItemCount() {
        return originalList.size();
    }

    public interface OnItemClicked {
        void onItemClick(int position);
    }

    class GroupActionAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView textView;
        private final ImageView imageView;

        public GroupActionAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textName);
            imageView = itemView.findViewById(R.id.imageAvatar);
            itemView.setOnClickListener(this);
        }

        void bindTo(GroupAction groupAction) {
            textView.setText(groupAction.getName());
            imageView.setImageResource(groupAction.getIcon());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            onItemClicked.onItemClick(position);
        }
    }
}
