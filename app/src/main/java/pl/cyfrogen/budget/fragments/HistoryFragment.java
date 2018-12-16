package pl.cyfrogen.budget.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.cyfrogen.budget.R;
import pl.cyfrogen.budget.adapters.WalletEntriesRecyclerViewAdapter;

public class HistoryFragment extends BaseFragment {
    public static final CharSequence TITLE = "History";
    private RecyclerView historyRecyclerView;
    private WalletEntriesRecyclerViewAdapter historyRecyclerViewAdapter;

    public static HistoryFragment newInstance() {

        return new HistoryFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        historyRecyclerView = view.findViewById(R.id.history_recycler_view);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        historyRecyclerViewAdapter = new WalletEntriesRecyclerViewAdapter(getActivity(), getUid());
        historyRecyclerView.setAdapter(historyRecyclerViewAdapter);

        historyRecyclerViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                historyRecyclerView.smoothScrollToPosition(0);
            }
        });
    }






}
