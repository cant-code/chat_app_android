package com.damnation.etachat.ui.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.damnation.etachat.R;
import com.damnation.etachat.adapter.UserAdapter;
import com.damnation.etachat.model.User;
import com.damnation.etachat.repository.CallBacks.DataFromNetworkCallback;
import com.damnation.etachat.repository.UserRepository;
import com.damnation.etachat.ui.ChatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {

    private UserAdapter userAdapter;
    private SwipeRefreshLayout refreshLayout;
    private UserRepository repository;
    private List<User> usersList;

    public UserFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        repository = new UserRepository(view.getContext().getApplicationContext());

        MaterialToolbar toolbar = getActivity().findViewById(R.id.toolbar);
        MenuItem searchItem = toolbar.getMenu().findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                userAdapter.filter(newText);
                return true;
            }
        });

        userAdapter = new UserAdapter(user -> ChatActivity.startChatActivity(getActivity(), user));
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(userAdapter);

        refreshLayout = view.findViewById(R.id.refresh);
        refreshLayout.setOnRefreshListener(this::loadDataFromNetwork);

        loadDataFromDatabase();
        loadDataFromNetwork();

        return view;
    }

    private void loadDataFromDatabase() {
        repository.loadDataFromDatabase(userList -> {
            usersList = userList;
            getActivity().runOnUiThread(() -> userAdapter.setData(userList));
        });
    }

    private void loadDataFromNetwork() {
        refreshLayout.setRefreshing(true);

        repository.loadDataFromNetwork(new DataFromNetworkCallback<User>() {
            @Override
            public void onSuccess(List<User> userList) {
                usersList = userList;
                userAdapter.setData(userList);
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onError() {
                refreshLayout.setRefreshing(false);
                showErrorSnackbar();
            }
        });
    }

    private void showErrorSnackbar() {
        View rootView = getActivity().findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(rootView, "Error during loading users", Snackbar.LENGTH_INDEFINITE);
        snackbar.setActionTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.cyan_500));
        snackbar.setAction("Retry", v -> {
            loadDataFromNetwork();
            snackbar.dismiss();
        });
        snackbar.show();
    }
}
