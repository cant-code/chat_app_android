package com.damnation.etachat.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.damnation.etachat.R;
import com.damnation.etachat.adapter.GroupMessagesAdapter;
import com.damnation.etachat.http.CallBacks.RegisterCallback;
import com.damnation.etachat.http.HTTPClient;
import com.damnation.etachat.model.Group;
import com.damnation.etachat.model.GroupMessages;
import com.damnation.etachat.repository.CallBacks.DataFromNetworkCallback;
import com.damnation.etachat.repository.MessagesRepository;
import com.damnation.etachat.socket.ChatSocket;
import com.damnation.etachat.token.Token;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.util.List;

import static com.damnation.etachat.ui.RegisterActivity.getTextWatcher;

public class GroupChatActivity extends AppCompatActivity {

    private GroupMessagesAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private MessagesRepository repository;
    private List<GroupMessages> messagesList;
    private Group group;
    private TextInputLayout messageInput;
    private RecyclerView recyclerView;
    private HTTPClient httpClient;
    private Socket socket;
    private Gson gson;
    private ImageButton sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        httpClient = HTTPClient.INSTANCE;
        repository = new MessagesRepository(getApplicationContext());

        group = getIntent().getExtras().getParcelable("EXTRAS");
        ((TextView) findViewById(R.id.chat_name)).setText(group.getUsername());
        ((ImageView) findViewById(R.id.imageAvatar)).setImageResource(R.drawable.ic_baseline_group_24);

        Token token = Token.INSTANCE;
        adapter = new GroupMessagesAdapter(token.getId());

        gson = new Gson();
        socket = ChatSocket.getmSocket();
        socket.on("messages", onNewMessage);
        socket.connect();
        String json = gson.toJson(token);
        socket.emit("clientInfo", json);
        socket.emit("joingroup", group.get_id());

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        Menu menu = toolbar.getMenu();
        CardView avatarCard = findViewById(R.id.avatarCard);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnSearchClickListener(v -> avatarCard.setVisibility(View.INVISIBLE));
        searchView.setOnCloseListener(() -> {
            avatarCard.setVisibility(View.VISIBLE);
            return false;
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });

        messageInput = findViewById(R.id.message);
        messageInput.getEditText().setImeOptions(EditorInfo.IME_ACTION_SEND);
        messageInput.getEditText().setRawInputType(InputType.TYPE_CLASS_TEXT);
        messageInput.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage();
                handled = true;
            }
            return handled;
        });
        sendButton = findViewById(R.id.send);
        sendButton.setOnClickListener(v -> sendMessage());

        recyclerView = findViewById(R.id.chats);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        refreshLayout = findViewById(R.id.refresh);
        refreshLayout.setOnRefreshListener(this::loadDataFromNetwork);

        loadDataFromNetwork();
        loadDataFromDatabase();

        messageInput.getEditText().addTextChangedListener(getTextWatcher(messageInput));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
        socket.off("messages", onNewMessage);
    }

    private final Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(() -> {
                GroupMessages messages = gson.fromJson(args[0].toString(), GroupMessages.class);
                messagesList.add(messages);
                adapter.notifyItemInserted(messagesList.size() - 1);
                recyclerView.scrollToPosition(messagesList.size() - 1);
                repository.addMessageToDB(messages);
            });
        }
    };

    public void sendMessage() {
        sendButton.setEnabled(false);
        String message = messageInput.getEditText().getText().toString();
        if(message.isEmpty()) {
            messageInput.setError("Message field cannot be empty");
        } else {
            httpClient.sendGroupMessage(new RegisterCallback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(() -> messageInput.getEditText().getText().clear());
                }

                @Override
                public void onError(String message) {
                    genericSnackbar(message);
                }
            }, message, group.get_id());
        }
        sendButton.setEnabled(true);
    }

    public static void startGroupChatActivity(Activity activity, Group group) {
        Intent intent = new Intent(activity, GroupChatActivity.class);
        intent.putExtra("EXTRAS", group);
        activity.startActivity(intent);
    }

    private void loadDataFromDatabase() {
        repository.loadGroupDataFromDatabase(list -> {
            messagesList = list;
            runOnUiThread(() -> adapter.setData(list));
        }, group.get_id());
    }

    private void loadDataFromNetwork() {
        refreshLayout.setRefreshing(true);
        repository.loadGroupDataFromNetwork(new DataFromNetworkCallback<GroupMessages>() {
            @Override
            public void onSuccess(List<GroupMessages> list) {
                messagesList = list;
                runOnUiThread(() -> adapter.setData(list));
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onError() {
                refreshLayout.setRefreshing(false);
                showErrorSnackbar();
            }
        }, group.get_id());
    }

    private void genericSnackbar(String message) {
        View rootView = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Close", v -> snackbar.dismiss());
        snackbar.show();
    }

    private void showErrorSnackbar() {
        View rootView = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(rootView, "Error Occurred", Snackbar.LENGTH_INDEFINITE);
        snackbar.setActionTextColor(ContextCompat.getColor(getApplicationContext(), R.color.cyan_500));
        snackbar.setAction("Retry", v -> {
            loadDataFromNetwork();
            snackbar.dismiss();
        });
        snackbar.show();
    }
}
