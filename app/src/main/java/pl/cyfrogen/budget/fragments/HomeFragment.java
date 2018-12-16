package pl.cyfrogen.budget.fragments;

import android.app.ActivityOptions;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pl.cyfrogen.budget.activities.AddWalletEntryActivity;
import pl.cyfrogen.budget.firebase.FirebaseElement;
import pl.cyfrogen.budget.firebase.FirebaseObserver;
import pl.cyfrogen.budget.models.Category;
import pl.cyfrogen.budget.models.CurrencyHelper;
import pl.cyfrogen.budget.models.TopCategoryListViewModel;
import pl.cyfrogen.budget.models.DefaultCategories;
import pl.cyfrogen.budget.adapters.TopCategoriesAdapter;
import pl.cyfrogen.budget.R;
import pl.cyfrogen.budget.firebase.ListDataSet;
import pl.cyfrogen.budget.firebase.UserProfileViewModelFactory;
import pl.cyfrogen.budget.firebase.WalletEntriesViewModelFactory;
import pl.cyfrogen.budget.firebase.models.User;
import pl.cyfrogen.budget.libraries.Gauge;
import pl.cyfrogen.budget.firebase.models.WalletEntry;

public class HomeFragment extends BaseFragment {
    private User userData;
    private ListDataSet<WalletEntry> walletEntryListDataSet;

    public static final CharSequence TITLE = "Home";
    private ListView favoriteListView;
    private FloatingActionButton addEntryButton;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        System.out.println("#FRAGMENT VIEW");
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


        favoriteListView = view.findViewById(R.id.favourite_categories_list_view);
        adapter = new TopCategoriesAdapter(categoryModelsHome, getActivity().getApplicationContext());
        favoriteListView.setAdapter(adapter);

        addEntryButton = view.findViewById(R.id.add_wallet_entry_fab);
        addEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options =
                            ActivityOptions.makeSceneTransitionAnimation(getActivity(), addEntryButton, addEntryButton.getTransitionName());
                    startActivity(new Intent(getActivity(), AddWalletEntryActivity.class), options.toBundle());
                } else {
                    startActivity(new Intent(getActivity(), AddWalletEntryActivity.class));
                }

            }
        });

        WalletEntriesViewModelFactory.getModel(getUid(), getActivity()).observe(this, new Observer<ListDataSet<WalletEntry>>() {

            @Override
            public void onChanged(@Nullable ListDataSet<WalletEntry> walletEntryListDataSet) {
                HomeFragment.this.walletEntryListDataSet = walletEntryListDataSet;
                dataUpdated();
            }
        });


        UserProfileViewModelFactory.getModel(getUid(), getActivity()).observe(this, new FirebaseObserver<FirebaseElement<User>>() {
            @Override
            public void onChanged(FirebaseElement<User> firebaseElement) {
                if (firebaseElement.hasNoError()) {
                    HomeFragment.this.userData = firebaseElement.getElement();
                    dataUpdated();
                }
            }
        });


    }

    private void dataUpdated() {
        if (userData == null || walletEntryListDataSet == null) return;

        List<WalletEntry> entryList = new ArrayList<>(walletEntryListDataSet.getList());


        Calendar startDate = getStartDate(userData);
        Calendar endDate = getEndDate(userData);

        DateFormat dateFormat = new SimpleDateFormat("dd-MM");

        long sum = 0;
        for (WalletEntry walletEntry : entryList) {
            sum += walletEntry.balanceDifference;
        }


        Iterator<WalletEntry> iterator = entryList.iterator();
        while (iterator.hasNext()) {
            long timestamp = -iterator.next().timestamp;
            if (timestamp < startDate.getTimeInMillis() || timestamp > endDate.getTimeInMillis())
                iterator.remove();
        }


        long expensesSumInDateRange = 0;
        long incomesSumInDateRange = 0;

        HashMap<Category, Long> categoryModels = new HashMap<>();
        for (WalletEntry walletEntry : entryList) {
            if (walletEntry.balanceDifference > 0) {
                incomesSumInDateRange += walletEntry.balanceDifference;
                continue;
            }
            expensesSumInDateRange += walletEntry.balanceDifference;
            Category category = DefaultCategories.searchCategory(walletEntry.categoryID);
            if (categoryModels.get(category) != null)
                categoryModels.put(category, categoryModels.get(category) + walletEntry.balanceDifference);
            else
                categoryModels.put(category, walletEntry.balanceDifference);

        }

        categoryModelsHome.clear();
        for (Map.Entry<Category, Long> categoryModel : categoryModels.entrySet()) {
            categoryModelsHome.add(new TopCategoryListViewModel(categoryModel.getKey(), categoryModel.getKey().getCategoryVisibleName(getContext()), CurrencyHelper.DEFAULT, categoryModel.getValue()));
        }

        Collections.sort(categoryModelsHome, new Comparator<TopCategoryListViewModel>() {
            @Override
            public int compare(TopCategoryListViewModel o1, TopCategoryListViewModel o2) {
                return Long.compare(o1.getMoney(), o2.getMoney());
            }
        });


        adapter.notifyDataSetChanged();
        totalBalanceTextView.setText(CurrencyHelper.DEFAULT.formatString(sum));

        boolean showLimit = false;
        if (showLimit) {

        } else {
            gaugeLeftBalanceTextView.setText(CurrencyHelper.DEFAULT.formatString(incomesSumInDateRange));
            gaugeLeftLine1TextView.setText("Incomes");
            gaugeLeftLine2TextView.setVisibility(View.INVISIBLE);
            gaugeRightBalanceTextView.setText(CurrencyHelper.DEFAULT.formatString(expensesSumInDateRange));
            gaugeRightLine1TextView.setText("Expenses");
            gaugeRightLine2TextView.setVisibility(View.INVISIBLE);

            gauge.setPointStartColor(ContextCompat.getColor(getContext(), R.color.gauge_income));
            gauge.setPointEndColor(ContextCompat.getColor(getContext(), R.color.gauge_income));
            gauge.setStrokeColor(ContextCompat.getColor(getContext(), R.color.gauge_expense));
            if (incomesSumInDateRange - expensesSumInDateRange != 0)
                gauge.setValue((int) (incomesSumInDateRange * 100 / (incomesSumInDateRange - expensesSumInDateRange)));

            gaugeBalanceLeftTextView.setText(dateFormat.format(startDate.getTime()) + " - " +
                    dateFormat.format(endDate.getTime()));


        }
    }

    private Calendar getStartDate(User userData) {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());

        return cal;
    }

    private Calendar getEndDate(User userData) {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.clear(Calendar.MILLISECOND);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        cal.add(Calendar.DAY_OF_YEAR, 6);
        return cal;
    }
}
