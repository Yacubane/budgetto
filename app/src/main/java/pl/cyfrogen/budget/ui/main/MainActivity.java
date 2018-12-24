package pl.cyfrogen.budget.ui.main;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import pl.cyfrogen.budget.R;
import pl.cyfrogen.budget.ui.add_entry.AddWalletEntryActivity;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;
    private FloatingActionButton addEntryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = findViewById(R.id.topbar);
        setSupportActionBar(findViewById(R.id.topbar));

        addEntryButton = findViewById(R.id.add_wallet_entry_fab);
        addEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityOptions options =
                        ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, addEntryButton, addEntryButton.getTransitionName());
                startActivity(new Intent(MainActivity.this, AddWalletEntryActivity.class), options.toBundle());

            }
        });

        viewPager = findViewById(R.id.pager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout = findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager);


    }
}