package com.damnation.etachat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class MessagesAdapter extends ListAdapter<Messages, MessagesAdapter.MessagesSentAdapterViewHolder> {

    private static final int MESSAGE_SENT = 1;
    private static final int MESSAGE_RECEIVED = 2;
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
        } else {
            return MESSAGE_RECEIVED;
        }
    }

    @NonNull
    @NotNull
    @Override
    public MessagesSentAdapterViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == MESSAGE_SENT) {
            view = inflater.inflate(R.layout.chat_item_self, parent, false);
        } else {
            view = inflater.inflate(R.layout.chat_item_other, parent, false);
        }
        return new MessagesSentAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MessagesSentAdapterViewHolder holder, int position) {
        holder.bindTo(getItem(position));
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
