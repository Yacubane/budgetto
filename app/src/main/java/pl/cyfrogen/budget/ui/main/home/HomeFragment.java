package pl.cyfrogen.budget.ui.main.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.cyfrogen.budget.firebase.FirebaseElement;
import pl.cyfrogen.budget.firebase.FirebaseObserver;
import pl.cyfrogen.budget.firebase.models.UserSettings;
import pl.cyfrogen.budget.base.BaseFragment;
import pl.cyfrogen.budget.util.CalendarHelper;
import pl.cyfrogen.budget.util.CategoriesHelper;
import pl.cyfrogen.budget.models.Category;
import pl.cyfrogen.budget.ui.options.OptionsActivity;
import pl.cyfrogen.budget.util.CurrencyHelper;
import pl.cyfrogen.budget.R;
import pl.cyfrogen.budget.firebase.ListDataSet;
import pl.cyfrogen.budget.firebase.viewmodel_factories.UserProfileViewModelFactory;
import pl.cyfrogen.budget.firebase.viewmodel_factories.TopWalletEntriesViewModelFactory;
import pl.cyfrogen.budget.firebase.models.User;
import pl.cyfrogen.budget.libraries.Gauge;
import pl.cyfrogen.budget.firebase.models.WalletEntry;

public class HomeFragment extends BaseFragment {
    private User user;
    private ListDataSet<WalletEntry> walletEntryListDataSet;

    public static final CharSequence TITLE = "Home";
    private Gauge gauge;
    private TopCategoriesAdapter adapter;
    private ArrayList<TopCategoryListViewModel> categoryModelsHome;
    private TextView totalBalanceTextView;
    private TextView gaugeLeftBalanceTextView;
    private TextView gaugeLeftLine1TextView;
    private TextView gaugeLeftLine2TextView;
    private TextView gaugeRightBalanceTextView;
    private TextView gaugeRightLine1TextView;
    private TextView gaugeRightLine2TextView;
    private TextView gaugeBalanceLeftTextView;

    public static HomeFragment newInstance() {

        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        categoryModelsHome = new ArrayList<>();

        gauge = view.findViewById(R.id.gauge);
        gauge.setValue(50);

        totalBalanceTextView = view.findViewById(R.id.total_balance_textview);
        gaugeLeftBalanceTextView = view.findViewById(R.id.gauge_left_balance_text_view);
        gaugeLeftLine1TextView = view.findViewById(R.id.gauge_left_line1_textview);
        gaugeLeftLine2TextView = view.findViewById(R.id.gauge_left_line2_textview);
        gaugeRightBalanceTextView = view.findViewById(R.id.gauge_right_balance_text_view);
        gaugeRightLine1TextView = view.findViewById(R.id.gauge_right_line1_textview);
        gaugeRightLine2TextView = view.findViewById(R.id.gauge_right_line2_textview);
        gaugeBalanceLeftTextView = view.findViewById(R.id.left_balance_textview);


        ListView favoriteListView = view.findViewById(R.id.favourite_categories_list_view);
        adapter = new TopCategoriesAdapter(categoryModelsHome, getActivity().getApplicationContext());
        favoriteListView.setAdapter(adapter);


        TopWalletEntriesViewModelFactory.getModel(getUid(), getActivity()).observe(this, new FirebaseObserver<FirebaseElement<ListDataSet<WalletEntry>>>() {

            @Override
            public void onChanged(FirebaseElement<ListDataSet<WalletEntry>> firebaseElement) {
                if (firebaseElement.hasNoError()) {
                    HomeFragment.this.walletEntryListDataSet = firebaseElement.getElement();
                    dataUpdated();
                }
            }
        });


        UserProfileViewModelFactory.getModel(getUid(), getActivity()).observe(this, new FirebaseObserver<FirebaseElement<User>>() {
            @Override
            public void onChanged(FirebaseElement<User> firebaseElement) {
                if (firebaseElement.hasNoError()) {
                    HomeFragment.this.user = firebaseElement.getElement();
                    dataUpdated();

                    Calendar startDate = CalendarHelper.getUserPeriodStartDate(user);
                    Calendar endDate = CalendarHelper.getUserPeriodEndDate(user);
                    TopWalletEntriesViewModelFactory.getModel(getUid(), getActivity()).setDateFilter(startDate, endDate);
                }
            }
        });


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_options:
                startActivity(new Intent(getActivity(), OptionsActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void dataUpdated() {
        if (user == null || walletEntryListDataSet == null) return;

        List<WalletEntry> entryList = new ArrayList<>(walletEntryListDataSet.getList());

        Calendar startDate = CalendarHelper.getUserPeriodStartDate(user);
        Calendar endDate = CalendarHelper.getUserPeriodEndDate(user);

        DateFormat dateFormat = new SimpleDateFormat("dd-MM");


        long expensesSumInDateRange = 0;
        long incomesSumInDateRange = 0;

        HashMap<Category, Long> categoryModels = new HashMap<>();
        for (WalletEntry walletEntry : entryList) {
            if (walletEntry.balanceDifference > 0) {
                incomesSumInDateRange += walletEntry.balanceDifference;
                continue;
            }
            expensesSumInDateRange += walletEntry.balanceDifference;
            Category category = CategoriesHelper.searchCategory(user, walletEntry.categoryID);
            if (categoryModels.get(category) != null)
                categoryModels.put(category, categoryModels.get(category) + walletEntry.balanceDifference);
            else
                categoryModels.put(category, walletEntry.balanceDifference);

        }

        categoryModelsHome.clear();
        for (Map.Entry<Category, Long> categoryModel : categoryModels.entrySet()) {
            categoryModelsHome.add(new TopCategoryListViewModel(categoryModel.getKey(), categoryModel.getKey().getCategoryVisibleName(getContext()),
                    user.currency, categoryModel.getValue()));
        }

        Collections.sort(categoryModelsHome, new Comparator<TopCategoryListViewModel>() {
            @Override
            public int compare(TopCategoryListViewModel o1, TopCategoryListViewModel o2) {
                return Long.compare(o1.getMoney(), o2.getMoney());
            }
        });


        adapter.notifyDataSetChanged();
        totalBalanceTextView.setText(CurrencyHelper.formatCurrency(user.currency, user.wallet.sum));

        if (user.userSettings.homeCounterType == UserSettings.HOME_COUNTER_TYPE_SHOW_LIMIT) {
            gaugeLeftBalanceTextView.setText(CurrencyHelper.formatCurrency(user.currency, 0));
            gaugeLeftLine1TextView.setText(dateFormat.format(startDate.getTime()));
            gaugeLeftLine2TextView.setVisibility(View.INVISIBLE);
            gaugeRightBalanceTextView.setText(CurrencyHelper.formatCurrency(user.currency, user.userSettings.limit));
            gaugeRightLine1TextView.setText(dateFormat.format(endDate.getTime()));
            gaugeRightLine2TextView.setVisibility(View.INVISIBLE);

            gauge.setPointStartColor(ContextCompat.getColor(getContext(), R.color.gauge_white));
            gauge.setPointEndColor(ContextCompat.getColor(getContext(), R.color.gauge_white));
            gauge.setStrokeColor(ContextCompat.getColor(getContext(), R.color.gauge_gray));

            long limit = user.userSettings.limit;
            long expenses = -expensesSumInDateRange;
            int percentage = (int) (expenses * 100 / (double) limit);
            if (percentage > 100) percentage = 100;
            gauge.setValue(percentage);
            gaugeBalanceLeftTextView.setText(CurrencyHelper.formatCurrency(user.currency, limit - expenses) + " left");


        } else {
            gaugeLeftBalanceTextView.setText(CurrencyHelper.formatCurrency(user.currency, incomesSumInDateRange));
            gaugeLeftLine1TextView.setText("Incomes");
            gaugeLeftLine2TextView.setVisibility(View.INVISIBLE);
            gaugeRightBalanceTextView.setText(CurrencyHelper.formatCurrency(user.currency, expensesSumInDateRange));
            gaugeRightLine1TextView.setText("Expenses");
            gaugeRightLine2TextView.setVisibility(View.INVISIBLE);

            gauge.setPointStartColor(ContextCompat.getColor(getContext(), R.color.gauge_income));
            gauge.setPointEndColor(ContextCompat.getColor(getContext(), R.color.gauge_income));
            gauge.setStrokeColor(ContextCompat.getColor(getContext(), R.color.gauge_expense));
            if (incomesSumInDateRange - expensesSumInDateRange != 0)
                gauge.setValue((int) (incomesSumInDateRange * 100 / (double) (incomesSumInDateRange - expensesSumInDateRange)));

            gaugeBalanceLeftTextView.setText(dateFormat.format(startDate.getTime()) + " - " +
                    dateFormat.format(endDate.getTime()));
        }
    }

}
