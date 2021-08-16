package com.damnation.etachat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.damnation.etachat.R;
import com.damnation.etachat.model.Messages;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MessagesAdapter extends ListAdapter<Messages, RecyclerView.ViewHolder> {

    private static final int MESSAGE_SENT = 1;
    private static final int MESSAGE_RECEIVED = 2;
    private static final int MESSAGE_GROUP = 3;
    private final String id;

    public MessagesAdapter(String id) {
        super(DIFF_CALLBACK);
        this.id = id;
    }

    @Override
    public int getItemViewType(int position) {
        Messages messages = getItem(position);
        if(messages.getFrom().equals(id)) {
            return MESSAGE_SENT;
        } else if(messages.getTo() == null) {
            return MESSAGE_GROUP;
        } else {
            return MESSAGE_RECEIVED;
        }
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        switch (viewType) {
            case MESSAGE_RECEIVED:
                view = inflater.inflate(R.layout.chat_item_other, parent, false);
                break;
            case MESSAGE_GROUP:
                view = inflater.inflate(R.layout.chat_item_group, parent, false);
                return new MessagesGroupAdapterViewHolder(view);
            default:
                view = inflater.inflate(R.layout.chat_item_self, parent, false);
        }
        return new MessagesSentAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == MESSAGE_GROUP) {
            ((MessagesGroupAdapterViewHolder) holder).bindTo(getItem(position));
        } else {
            ((MessagesSentAdapterViewHolder) holder).bindTo(getItem(position));
        }
    }

    public void setData(@Nullable List<Messages> list) {
        super.submitList(list);
    }

    public void filter(String query) {
        List<Messages> filteredList = new ArrayList<>();
        for(Messages messages: getCurrentList()) {
            if(messages.getBody().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(messages);
            }
        }
        submitList(filteredList);
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

        void bindTo(Messages messages) {
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

        void bindTo(Messages messages) {
            body.setText(messages.getBody());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            String dateString = simpleDateFormat.format(Long.parseLong(messages.getDate()));
            date.setText(dateString);
        }
    }

    private static final DiffUtil.ItemCallback<Messages> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Messages>() {
                @Override
                public boolean areItemsTheSame(@NonNull Messages oldData,
                                               @NonNull Messages newData) {
                    return oldData.get_id().equals(newData.get_id());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Messages oldData,
                                                  @NonNull Messages newData) {
                    return oldData.equals(newData);
                }
            };
}
