package pl.cyfrogen.budget.ui.main.chart;

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

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.cyfrogen.budget.R;
import pl.cyfrogen.budget.base.BaseFragment;
import pl.cyfrogen.budget.firebase.FirebaseElement;
import pl.cyfrogen.budget.firebase.FirebaseObserver;
import pl.cyfrogen.budget.firebase.ListDataSet;
import pl.cyfrogen.budget.firebase.models.User;
import pl.cyfrogen.budget.firebase.models.UserSettings;
import pl.cyfrogen.budget.firebase.models.WalletEntry;
import pl.cyfrogen.budget.firebase.viewmodel_factories.TopWalletEntriesChartViewModelFactory;
import pl.cyfrogen.budget.firebase.viewmodel_factories.UserProfileViewModelFactory;
import pl.cyfrogen.budget.models.Category;
import pl.cyfrogen.budget.models.DefaultCategories;
import pl.cyfrogen.budget.ui.options.OptionsActivity;


public class ChartFragment extends BaseFragment {
    public static final CharSequence TITLE = "Chart";

    private Menu menu;
    private Calendar calendarStart;
    private Calendar calendarEnd;
    private User userData;
    private ListDataSet<WalletEntry> walletEntryListDataSet;
    private PieChart pieChart;
    private ArrayList<TopCategoryChartListViewModel> categoryModelsHome;
    private ListView favoriteListView;
    private TopCategoriesChartAdapter adapter;
    private TextView dividerTextView;

    public static ChartFragment newInstance() {

        return new ChartFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chart, container, false);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        pieChart = view.findViewById(R.id.pie_chart);
        dividerTextView = view.findViewById(R.id.divider_textview);
        categoryModelsHome = new ArrayList<>();
        favoriteListView = view.findViewById(R.id.favourite_categories_list_view);
        adapter = new TopCategoriesChartAdapter(categoryModelsHome, getActivity().getApplicationContext());
        favoriteListView.setAdapter(adapter);

        TopWalletEntriesChartViewModelFactory.getModel(getUid(), getActivity()).observe(this, new FirebaseObserver<FirebaseElement<ListDataSet<WalletEntry>>>() {

            @Override
            public void onChanged(FirebaseElement<ListDataSet<WalletEntry>> firebaseElement) {
                if (firebaseElement.hasNoError()) {
                    ChartFragment.this.walletEntryListDataSet = firebaseElement.getElement();
                    dataUpdated();
                }
            }

        });


        UserProfileViewModelFactory.getModel(getUid(), getActivity()).observe(this, new FirebaseObserver<FirebaseElement<User>>() {
            @Override
            public void onChanged(FirebaseElement<User> firebaseElement) {
                if (firebaseElement.hasNoError()) {
                    ChartFragment.this.userData = firebaseElement.getElement();
                    dataUpdated();

                    calendarStart = getStartDate(userData);
                    calendarEnd = getEndDate(userData);

                    updateCalendarIcon(false);
                    calendarUpdated();

                }
            }
        });

    }


    private void dataUpdated() {
        if (calendarStart != null && calendarEnd != null && walletEntryListDataSet != null) {

            List<WalletEntry> entryList = new ArrayList<>(walletEntryListDataSet.getList());


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

            ArrayList<PieEntry> pieEntries = new ArrayList<>();
            ArrayList<Integer> pieColors = new ArrayList<>();

            for (Map.Entry<Category, Long> categoryModel : categoryModels.entrySet()) {
                float percentage = categoryModel.getValue() / (float) expensesSumInDateRange;
                float minPercentageToShowLabelOnChart = 0.1f;
                categoryModelsHome.add(new TopCategoryChartListViewModel(categoryModel.getKey(), categoryModel.getKey().getCategoryVisibleName(getContext()),
                        userData.currency, categoryModel.getValue(), percentage));
                pieEntries.add(new PieEntry(-categoryModel.getValue(), percentage > minPercentageToShowLabelOnChart ? categoryModel.getKey().getCategoryVisibleName(getContext()) : ""));
                pieColors.add(categoryModel.getKey().getIconColor());
            }

            PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
            pieDataSet.setDrawValues(false);
            pieDataSet.setColors(pieColors);
            pieDataSet.setSliceSpace(2f);

            PieData data = new PieData(pieDataSet);
            pieChart.setData(data);
            pieChart.setTouchEnabled(false);
            pieChart.getLegend().setEnabled(false);
            pieChart.getDescription().setEnabled(false);
            pieChart.setDrawHoleEnabled(false);


            pieChart.setDrawHoleEnabled(true);
            pieChart.setHoleColor(ContextCompat.getColor(getContext(), R.color.backgroundPrimary));
            pieChart.setHoleRadius(55f);
            pieChart.setTransparentCircleRadius(55f);
            pieChart.setDrawCenterText(true);
            pieChart.setRotationAngle(270);
            pieChart.setRotationEnabled(false);
            pieChart.setHighlightPerTapEnabled(true);

            pieChart.invalidate();

            Collections.sort(categoryModelsHome, new Comparator<TopCategoryChartListViewModel>() {
                @Override
                public int compare(TopCategoryChartListViewModel o1, TopCategoryChartListViewModel o2) {
                    return Long.compare(o1.getMoney(), o2.getMoney());
                }
            });


            adapter.notifyDataSetChanged();

            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");

            dividerTextView.setText("Top categories: (" + dateFormat.format(calendarStart.getTime())
                    + "  -  " + dateFormat.format(calendarEnd.getTime()) + ")");
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.chart_fragment_menu, menu);
        this.menu = menu;
        updateCalendarIcon(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void updateCalendarIcon(boolean updatedFromUI) {
        if (menu == null) return;
        MenuItem calendarIcon = menu.findItem(R.id.action_date_range);
        if (calendarIcon == null) return;
        if (updatedFromUI) {
            calendarIcon.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.icon_calendar_active));
        } else {
            calendarIcon.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.icon_calendar));
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_date_range:
                showSelectDateRangeDialog();
                return true;
            case R.id.action_options:
                startActivity(new Intent(getActivity(), OptionsActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSelectDateRangeDialog() {
        SmoothDateRangePickerFragment datePicker = SmoothDateRangePickerFragment.newInstance(new SmoothDateRangePickerFragment.OnDateRangeSetListener() {
            @Override
            public void onDateRangeSet(SmoothDateRangePickerFragment view, int yearStart, int monthStart, int dayStart, int yearEnd, int monthEnd, int dayEnd) {
                calendarStart = Calendar.getInstance();
                calendarStart.set(yearStart, monthStart, dayStart);
                calendarStart.set(Calendar.HOUR_OF_DAY, 0);
                calendarStart.set(Calendar.MINUTE, 0);
                calendarStart.set(Calendar.SECOND, 0);

                calendarEnd = Calendar.getInstance();
                calendarEnd.set(yearEnd, monthEnd, dayEnd);
                calendarEnd.set(Calendar.HOUR_OF_DAY, 23);
                calendarEnd.set(Calendar.MINUTE, 59);
                calendarEnd.set(Calendar.SECOND, 59);
                calendarUpdated();
                updateCalendarIcon(true);
            }
        });
        datePicker.show(getActivity().getFragmentManager(), "TAG");
        //todo library doesn't respect other method than deprecated
    }

    private Calendar getStartDate(User userData) {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(getUserFirstDayOfWeek(userData));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        if (userData.userSettings.homeCounterPeriod == UserSettings.HOME_COUNTER_PERIOD_WEEKLY) {
            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
            if (new Date().getTime() < cal.getTime().getTime())
                cal.add(Calendar.DATE, -7);
        } else {
            cal.set(Calendar.DAY_OF_MONTH, userData.userSettings.dayOfMonthStart + 1);
            if (new Date().getTime() < cal.getTime().getTime())
                cal.add(Calendar.MONTH, -1);
        }

        return cal;
    }


    private Calendar getEndDate(User userData) {
        Calendar cal = getStartDate(userData);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.clear(Calendar.MILLISECOND);
        if (userData.userSettings.homeCounterPeriod == UserSettings.HOME_COUNTER_PERIOD_WEEKLY) {
            cal.add(Calendar.DATE, 6);
        } else {
            cal.add(Calendar.MONTH, 1);
            cal.add(Calendar.DATE, -1);
        }
        return cal;
    }

    private int getUserFirstDayOfWeek(User userData) {
        switch (userData.userSettings.dayOfWeekStart) {
            case 0:
                return Calendar.MONDAY;
            case 1:
                return Calendar.TUESDAY;
            case 2:
                return Calendar.WEDNESDAY;
            case 3:
                return Calendar.THURSDAY;
            case 4:
                return Calendar.FRIDAY;
            case 5:
                return Calendar.SATURDAY;
            case 6:
                return Calendar.SUNDAY;
        }
        return 0;
    }

    private void calendarUpdated() {
        TopWalletEntriesChartViewModelFactory.getModel(getUid(), getActivity()).setDateFilter(calendarStart, calendarEnd);

    }


}
