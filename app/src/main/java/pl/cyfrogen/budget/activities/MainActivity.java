package pl.cyfrogen.budget.activities;

import android.os.Bundle;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import pl.cyfrogen.budget.R;
import pl.cyfrogen.budget.adapters.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = findViewById(R.id.topbar);
        setSupportActionBar(findViewById(R.id.topbar));
        viewPager = findViewById(R.id.pager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                ((AppBarLayout)toolbar.getParent()).setExpanded(true,true);

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        tabLayout = findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager);



    }
}