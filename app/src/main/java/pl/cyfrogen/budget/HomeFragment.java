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

import java.util.ArrayList;

import pl.cyfrogen.budget.firebase.ListDataSet;
import pl.cyfrogen.budget.firebase.WalletEntriesViewModel;
import pl.cyfrogen.budget.firebase.WalletEntriesViewModelFactory;
import pl.cyfrogen.budget.libraries.Gauge;
import pl.cyfrogen.budget.firebase.models.WalletEntry;

public class HomeFragment extends BaseFragment {

    public static final CharSequence TITLE = "Home";
    private ListView favoriteListView;
    private FloatingActionButton addEntryButton;
    private Gauge gauge;

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
        ArrayList<CategoryModel> testModels = new ArrayList<>();
        testModels.add(new CategoryModel("Food", Currency.USD, 100));
        testModels.add(new CategoryModel("Pharmacy", Currency.USD, 200));
        testModels.add(new CategoryModel("Gaming", Currency.USD, 300));
        testModels.add(new CategoryModel("Gaming", Currency.USD, 300));
        testModels.add(new CategoryModel("Gaming", Currency.USD, 300));
        testModels.add(new CategoryModel("Gaming", Currency.USD, 300));


        favoriteListView = view.findViewById(R.id.favourite_categories_list_view);
        favoriteListView.setAdapter(new ItemCategoriesListViewAdapter(testModels, getActivity().getApplicationContext()));

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

        final WalletEntriesViewModel myViewModel = ViewModelProviders.of(getActivity(), new WalletEntriesViewModelFactory(getUid())).get(WalletEntriesViewModel.class);
        myViewModel.getDataSnapshotLiveData().observe(this, new Observer<ListDataSet<WalletEntry>>() {
            @Override
            public void onChanged(@Nullable ListDataSet<WalletEntry> walletEntryListDataSet) {

            }
        });

        gauge = view.findViewById(R.id.gauge2);
        gauge.setValue(50);

    }

}
