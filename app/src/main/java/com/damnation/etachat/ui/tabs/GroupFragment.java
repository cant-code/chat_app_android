package com.damnation.etachat.ui.tabs;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SearchView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.damnation.etachat.R;
import com.damnation.etachat.adapter.GroupAdapter;
import com.damnation.etachat.http.Group;
import com.damnation.etachat.repository.DataFromNetworkCallback;
import com.damnation.etachat.repository.GroupRepository;
import com.damnation.etachat.repository.UserRepository;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupFragment} factory method to
 * create an instance of this fragment.
 */
public class GroupFragment extends Fragment {

    private GroupAdapter groupAdapter;
    private SwipeRefreshLayout refreshLayout;
    private GroupRepository repository;
    private View view;

    public GroupFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_group, container, false);

        repository = new GroupRepository(view.getContext().getApplicationContext());

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
                groupAdapter.filter(newText);
                return true;
            }
        });

        groupAdapter = new GroupAdapter();
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(groupAdapter);

        refreshLayout = view.findViewById(R.id.refresh);
        refreshLayout.setOnRefreshListener(this::loadDataFromNetwork);

        loadDataFromDatabase();
        loadDataFromNetwork();

        return view;
    }

    private void loadDataFromDatabase() {
        repository.loadDataFromDatabase(groupList -> getActivity().runOnUiThread(() -> groupAdapter.setData(groupList)));
    }

    private void loadDataFromNetwork() {
        refreshLayout.setRefreshing(true);

        repository.loadDataFromNetwork(new DataFromNetworkCallback<Group>() {
            @Override
            public void onSuccess(List<Group> groupList) {
                getActivity().runOnUiThread(() -> {
                    groupAdapter.setData(groupList);
                    refreshLayout.setRefreshing(false);
                });
            }

            @Override
            public void onError() {
                getActivity().runOnUiThread(() -> {
                    refreshLayout.setRefreshing(false);
                    showErrorSnackbar();
                });
            }
        });
    }

    private void showErrorSnackbar() {
        View rootView = view.findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(rootView, "Error during loading users", Snackbar.LENGTH_INDEFINITE);
        snackbar.setActionTextColor(getResources().getColor(R.color.cyan_500));
        snackbar.setAction("Retry", v -> {
            loadDataFromNetwork();
            snackbar.dismiss();
        });
        snackbar.show();
    }
}
