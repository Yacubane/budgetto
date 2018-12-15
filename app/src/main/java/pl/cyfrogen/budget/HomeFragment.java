package pl.cyfrogen.budget;

import android.app.ActivityOptions;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pl.cyfrogen.budget.firebase.ListDataSet;
import pl.cyfrogen.budget.firebase.WalletEntriesViewModelFactory;
import pl.cyfrogen.budget.libraries.Gauge;
import pl.cyfrogen.budget.firebase.models.WalletEntry;

public class HomeFragment extends BaseFragment {

    public static final CharSequence TITLE = "Home";
    private ListView favoriteListView;
    private FloatingActionButton addEntryButton;
    private Gauge gauge;
    private ItemCategoriesListViewAdapter adapter;
    private ArrayList<CategoryModelHome> testModels;
    private TextView totalBalanceTextView;

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
        testModels = new ArrayList<>();

        totalBalanceTextView = view.findViewById(R.id.total_balance_textview);

        favoriteListView = view.findViewById(R.id.favourite_categories_list_view);
        adapter = new ItemCategoriesListViewAdapter(testModels, getActivity().getApplicationContext());
        favoriteListView.setAdapter(adapter);

        addEntryButton = view.findViewById(R.id.add_wallet_entry_fab);
        addEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options =
                            ActivityOptions.makeSceneTransitionAnimation(getActivity(), addEntryButton, addEntryButton.getTransitionName());
                    startActivity(new Intent(getActivity(), AddBudgetEntryActivity.class), options.toBundle());
                } else {
                    startActivity(new Intent(getActivity(), AddBudgetEntryActivity.class));
                }

            }
        });

        final WalletEntriesViewModelFactory.Model myViewModel = WalletEntriesViewModelFactory.getModel(getUid(), getActivity());
        myViewModel.observe(this, new Observer<ListDataSet<WalletEntry>>() {
            @Override
            public void onChanged(@Nullable ListDataSet<WalletEntry> walletEntryListDataSet) {
                dataUpdated(walletEntryListDataSet);
            }
        });

        gauge = view.findViewById(R.id.gauge2);
        gauge.setValue(50);

    }

    private void dataUpdated(ListDataSet<WalletEntry> walletEntryListDataSet) {
        int sum = 0;
        List<WalletEntry> entryList = walletEntryListDataSet.getList();
        ArrayList<CategoryModel> categoryModels = new ArrayList<>();
        for(WalletEntry walletEntry : entryList) {
            sum += walletEntry.balanceDifference;
            CategoryModel categoryModel = DefaultCategoryModels.searchCategory(walletEntry.categoryID);
            if(!categoryModels.contains(categoryModel)) categoryModels.add(categoryModel);
        }

        testModels.clear();
        for(CategoryModel categoryModel : categoryModels) {
            testModels.add(new CategoryModelHome(categoryModel.getCategoryVisibleName(getContext()),Currency.USD, 100));
        }

        adapter.notifyDataSetChanged();

        totalBalanceTextView.setText(Currency.USD.formatString(sum));
    }

}
