package com.damnation.etachat.ui;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.damnation.etachat.model.Messages;
import com.damnation.etachat.model.User;
import com.damnation.etachat.repository.CallBacks.DataFromNetworkCallback;
import com.damnation.etachat.repository.MessagesRepository;
import com.damnation.etachat.token.Token;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private MessagesAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private MessagesRepository repository;
    private List<Messages> messagesList;
    private Token token;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        repository = new MessagesRepository(getApplicationContext());

        user = getIntent().getExtras().getParcelable("EXTRAS");

        ((TextView) findViewById(R.id.chat_name)).setText(user.getUsername());

        token = Token.INSTANCE;
        adapter = new MessagesAdapter(token.getId());

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
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });

        RecyclerView recyclerView = findViewById(R.id.chats);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        refreshLayout = findViewById(R.id.refresh);
        refreshLayout.setOnRefreshListener(this::loadDataFromNetwork);

        loadDataFromNetwork();
        loadDataFromDatabase();
    }

    public static void startChatActivity(Activity activity, User user) {
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra("EXTRAS", user);
        activity.startActivity(intent);
    }

    private void loadDataFromDatabase() {
        repository.loadDataFromDatabase(list -> {
            messagesList = list;
            runOnUiThread(() -> adapter.setData(list));
        }, user.get_id(), token.getId());
    }

    private void loadDataFromNetwork() {
        refreshLayout.setRefreshing(true);

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
