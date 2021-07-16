package com.damnation.etachat.ui.tabs;

import android.os.Bundle;
import android.text.TextWatcher;
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
import com.damnation.etachat.adapter.GroupActionAdapter;
import com.damnation.etachat.adapter.GroupAdapter;
import com.damnation.etachat.model.Group;
import com.damnation.etachat.repository.CallBacks.AddToDBCallback;
import com.damnation.etachat.repository.CallBacks.DataFromNetworkCallback;
import com.damnation.etachat.repository.GroupRepository;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import static com.damnation.etachat.ui.RegisterActivity.getTextWatcher;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupFragment} factory method to
 * create an instance of this fragment.
 */
public class GroupFragment extends Fragment implements GroupActionAdapter.OnItemClicked {

    private GroupAdapter groupAdapter;
    private SwipeRefreshLayout refreshLayout;
    private GroupRepository repository;
    private MaterialAlertDialogBuilder dialogBuilder;
    private View customAlertDialogView;
    private List<Group> groupList;

    public GroupFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group, container, false);

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
        GroupActionAdapter groupActionAdapter = new GroupActionAdapter(this);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(groupAdapter);

        RecyclerView groupRecyclerView = view.findViewById(R.id.groupActions);
        groupRecyclerView.setHasFixedSize(true);
        groupRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        groupRecyclerView.setAdapter(groupActionAdapter);

        refreshLayout = view.findViewById(R.id.refresh);
        refreshLayout.setOnRefreshListener(this::loadDataFromNetwork);

        dialogBuilder = new MaterialAlertDialogBuilder(getContext());

        loadDataFromDatabase();
        loadDataFromNetwork();

        return view;
    }

    private void loadDataFromDatabase() {
        repository.loadDataFromDatabase(list -> {
            groupList = list;
            getActivity().runOnUiThread(() -> groupAdapter.setData(list));
        });
    }

    private void loadDataFromNetwork() {
        refreshLayout.setRefreshing(true);

        repository.loadDataFromNetwork(new DataFromNetworkCallback<Group>() {
            @Override
            public void onSuccess(List<Group> list) {
                groupList = list;
                groupAdapter.setData(list);
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
        Snackbar snackbar = Snackbar.make(rootView, "Error Occurred", Snackbar.LENGTH_INDEFINITE);
        snackbar.setActionTextColor(getResources().getColor(R.color.cyan_500));
        snackbar.setAction("Retry", v -> {
            loadDataFromNetwork();
            snackbar.dismiss();
        });
        snackbar.show();
    }

    @Override
    public void onItemClick(int position) {
        customAlertDialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.group_chat_dialog, null, false);
        switch (position) {
            case 0:
                launchDialog("Create");
                break;
            case 1:
                launchDialog("Join");
                break;
            case 2:
                System.out.println("3");
                break;
        }
    }
    private TextWatcher createTextWatcher(TextInputLayout textInputLayout) {
        return getTextWatcher(textInputLayout);
    }

    private void launchDialog(String title) {
        TextInputLayout textInputLayout = customAlertDialogView.findViewById(R.id.inputText);
        textInputLayout.getEditText().addTextChangedListener(createTextWatcher(textInputLayout));
        dialogBuilder.setTitle(title + " a Room")
                .setView(customAlertDialogView)
                .setNegativeButton("Close", ((dialog, which) -> dialog.dismiss()))
                .setPositiveButton(title + " Room", ((dialog, which) -> {
                    String name = textInputLayout.getEditText().getText().toString();
                    if(name.isEmpty()) {
                        textInputLayout.setError("Name cannot be empty");
                    } else {
                        repository.addOrJoinRoom(new AddToDBCallback<Group>() {
                            @Override
                            public void onSuccess(Group data) {
                                groupAdapter.addData(data);
                                getActivity().runOnUiThread(() -> groupAdapter.notifyItemInserted(groupList.size() - 1));
                            }

                            @Override
                            public void onError() {
                                showErrorSnackbar();
                            }
                        }, name, title);
                        dialog.dismiss();
                    }
                }))
                .show();
    }
}
