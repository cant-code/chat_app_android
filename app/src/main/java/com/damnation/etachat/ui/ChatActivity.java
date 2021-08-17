package com.damnation.etachat.ui;

import android.app.Activity;
import android.content.Intent;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.damnation.etachat.R;
import com.damnation.etachat.adapter.MessagesAdapter;
import com.damnation.etachat.http.CallBacks.RegisterCallback;
import com.damnation.etachat.http.HTTPClient;
import com.damnation.etachat.model.Messages;
import com.damnation.etachat.model.User;
import com.damnation.etachat.repository.CallBacks.DataFromNetworkCallback;
import com.damnation.etachat.repository.MessagesRepository;
import com.damnation.etachat.socket.ChatSocket;
import com.damnation.etachat.token.Token;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.util.Arrays;
import java.util.List;

import static com.damnation.etachat.ui.RegisterActivity.getTextWatcher;

public class ChatActivity extends AppCompatActivity {

    private MessagesAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private MessagesRepository repository;
    private List<Messages> messagesList;
    private Token token;
    private User user;
    private TextInputLayout messageInput;
    private RecyclerView recyclerView;
    private HTTPClient httpClient;
    private Socket socket;
    private Gson gson;
    private boolean isGlobal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        httpClient = HTTPClient.INSTANCE;
        repository = new MessagesRepository(getApplicationContext());

        user = getIntent().getExtras().getParcelable("EXTRAS");
        isGlobal = user == null;
        ((TextView) findViewById(R.id.chat_name)).setText(isGlobal ? "Global Chat" : user.getUsername());

        token = Token.INSTANCE;
        adapter = new MessagesAdapter(token.getId());

        gson = new Gson();
        socket = ChatSocket.getmSocket();
        socket.on("messages", onNewMessage);
        socket.connect();
        String json = gson.toJson(token);
        socket.emit("clientInfo", json);
        if (isGlobal) socket.emit("joingroup", "global");

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
        ImageButton sendButton = findViewById(R.id.send);
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
                Messages messages = gson.fromJson(args[0].toString(), Messages.class);
                messagesList.add(messages);
                adapter.notifyItemInserted(messagesList.size() - 1);
                recyclerView.scrollToPosition(messagesList.size() - 1);
            });
        }
    };

    public void sendMessage() {
        String message = messageInput.getEditText().getText().toString();
        if(message.isEmpty()) {
            messageInput.setError("Message field cannot be empty");
        } else {
            httpClient.sendMessage(new RegisterCallback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(() -> messageInput.getEditText().getText().clear());
                }

                @Override
                public void onError(String message) {
                    genericSnackbar(message);
                }
            }, message, isGlobal ? "global" : user.get_id());
        }
    }

    public static void startChatActivity(Activity activity, User user) {
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra("EXTRAS", user);
        activity.startActivity(intent);
    }

    private void loadDataFromDatabase() {
        if (isGlobal) {
            repository.loadGlobalDataFromDatabase(list -> {
                messagesList = list;
                runOnUiThread(() -> adapter.setData(list));
            });
        } else {
            repository.loadDataFromDatabase(list -> {
                messagesList = list;
                runOnUiThread(() -> adapter.setData(list));
            }, user.get_id(), token.getId());
        }
    }

    private void loadDataFromNetwork() {
        refreshLayout.setRefreshing(true);

        if (isGlobal) {
            repository.loadGlobalDataFromNetwork(new DataFromNetworkCallback<Messages>() {
                @Override
                public void onSuccess(List<Messages> list) {
                    messagesList = list;
                    runOnUiThread(() -> adapter.setData(list));
                    refreshLayout.setRefreshing(false);
                }

                @Override
                public void onError() {
                    refreshLayout.setRefreshing(false);
                    showErrorSnackbar();
                }
            });
        } else {
            repository.loadDataFromNetwork(new DataFromNetworkCallback<Messages>() {
                @Override
                public void onSuccess(List<Messages> list) {
                    messagesList = list;
                    runOnUiThread(() -> adapter.setData(list));
                    refreshLayout.setRefreshing(false);
                }

                @Override
                public void onError() {
                    refreshLayout.setRefreshing(false);
                    showErrorSnackbar();
                }
            }, user.get_id());
        }
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
        snackbar.setActionTextColor(getResources().getColor(R.color.cyan_500));
        snackbar.setAction("Retry", v -> {
            loadDataFromNetwork();
            snackbar.dismiss();
        });
        snackbar.show();
    }
}
