package pl.cyfrogen.budget;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    public static final CharSequence TITLE = "Home";
    private ListView favoriteListView;

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
    }

}
