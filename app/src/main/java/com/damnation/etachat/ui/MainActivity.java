package com.damnation.etachat.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;
import com.damnation.etachat.ui.tabs.GroupFragment;
import com.damnation.etachat.R;
import com.damnation.etachat.ui.tabs.UserFragment;
import com.damnation.etachat.ui.tabs.SectionsPagerAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserFragment userFragment = new UserFragment();
        GroupFragment groupFragment = new GroupFragment();

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this);
        sectionsPagerAdapter.addFragment(userFragment, "Users", R.drawable.ic_baseline_contacts_24);
        sectionsPagerAdapter.addFragment(groupFragment, "Chat Rooms", R.drawable.ic_baseline_forum_24);
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        new TabLayoutMediator(tabs, viewPager, (tab, position) -> {
            tab.setText(sectionsPagerAdapter.getFragmentName(position));
            tab.setIcon(sectionsPagerAdapter.getIcon(position));
        }).attach();
    }
}
