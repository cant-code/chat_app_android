package com.damnation.etachat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.damnation.etachat.R;
import com.damnation.etachat.model.GroupMessages;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GroupMessagesAdapter extends ListAdapter<GroupMessages, RecyclerView.ViewHolder> {

    private static final int MESSAGE_SENT = 1;
    private static final int MESSAGE_RECEIVED = 2;
    private final String id;
    private List<GroupMessages> originalList = new ArrayList<>();

    public GroupMessagesAdapter(String id) {
        super(DIFF_CALLBACK);
        this.id = id;
    }

    @Override
    public int getItemViewType(int position) {
        GroupMessages messages = getItem(position);
        if(messages.getFrom().equals(id)) {
            return MESSAGE_SENT;
        } else {
            return MESSAGE_RECEIVED;
        }
    }

    public void setData(@Nullable List<GroupMessages> list) {
        originalList = list;
        super.submitList(list);
    }

    public void filter(String query) {
        List<GroupMessages> filteredList = new ArrayList<>();
        for(GroupMessages messages: originalList) {
            if(messages.getBody().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(messages);
            }
        }
        submitList(filteredList);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == MESSAGE_RECEIVED) {
            view = inflater.inflate(R.layout.chat_item_group, parent, false);
            return new GroupMessagesAdapter.MessagesGroupAdapterViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.chat_item_self, parent, false);
            return new GroupMessagesAdapter.MessagesSentAdapterViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == MESSAGE_RECEIVED) {
            ((GroupMessagesAdapter.MessagesGroupAdapterViewHolder) holder).bindTo(getItem(position));
        } else {
            ((GroupMessagesAdapter.MessagesSentAdapterViewHolder) holder).bindTo(getItem(position));
        }
    }

    static class MessagesGroupAdapterViewHolder extends RecyclerView.ViewHolder {

        private final TextView username, body, date;

        public MessagesGroupAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            ConstraintLayout messageBody = itemView.findViewById(R.id.messageBody);
            body = messageBody.findViewById(R.id.textName);
            date = messageBody.findViewById(R.id.date);
        }

        void bindTo(GroupMessages messages) {
            body.setText(messages.getBody());
            username.setText(messages.getUser().getUsername());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            String dateString = simpleDateFormat.format(Long.parseLong(messages.getDate()));
            date.setText(dateString);
        }
    }

    static class MessagesSentAdapterViewHolder extends RecyclerView.ViewHolder {

        private final TextView body, date;

        public MessagesSentAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            body = itemView.findViewById(R.id.textName);
            date = itemView.findViewById(R.id.date);
        }

        void bindTo(GroupMessages messages) {
            body.setText(messages.getBody());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            String dateString = simpleDateFormat.format(Long.parseLong(messages.getDate()));
            date.setText(dateString);
        }
    }

    private static final DiffUtil.ItemCallback<GroupMessages> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<GroupMessages>() {
                @Override
                public boolean areItemsTheSame(@NonNull GroupMessages oldData,
                                               @NonNull GroupMessages newData) {
                    return oldData.get_id().equals(newData.get_id());
                }

                @Override
                public boolean areContentsTheSame(@NonNull GroupMessages oldData,
                                                  @NonNull GroupMessages newData) {
                    return oldData.equals(newData);
                }
            };
}
