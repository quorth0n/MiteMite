package dulleh.akhyou.Search.Holder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.greenrobot.event.EventBus;
import dulleh.akhyou.Models.Providers;
import dulleh.akhyou.R;
import dulleh.akhyou.Settings.SettingsFragment;
import dulleh.akhyou.Utils.Events.SearchEvent;

public class SearchHolderFragment extends Fragment{
    public static int SEARCH_GRID_TYPE = 0;

    private ViewPager pager;
    private SearchHolderAdapter adapter;
    private List<String> enabledProviders;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setHasOptionsMenu(true);

        enabledProviders = new ArrayList<>();

        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        SEARCH_GRID_TYPE = preferences.getInt(SettingsFragment.SEARCH_GRID_PREFERENCE, 0);
    }

    private void refreshEnabledProviders (@NonNull SharedPreferences preferences) {
        enabledProviders = new ArrayList<>();
        Set<String> enabledProvidersSet = preferences.getStringSet(SettingsFragment.ENABLED_PROVIDERS_PREF, new HashSet<>(0));
        // set defaults
        if (enabledProvidersSet.isEmpty()) {
            enabledProviders.addAll(Providers.ALL_PROVIDER_TITLES);
        } else {
            enabledProviders.addAll(enabledProvidersSet);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_holder_fragment, container, false);

        pager = (ViewPager) view.findViewById(R.id.search_view_pager);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);

        adapter = new SearchHolderAdapter(getChildFragmentManager(), enabledProviders);

        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (getView() != null) {
            super.onCreateOptionsMenu(menu, inflater);

            MenuItem searchItem = menu.findItem(R.id.search_item);

            if (searchItem == null) {
                inflater.inflate(R.menu.search_menu, menu);
                searchItem = menu.findItem(R.id.search_item);
            }

            SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

            searchView.setMaxWidth(10000000); //god...
            searchView.setQueryHint(getString(R.string.search_item));
            searchView.setIconifiedByDefault(false);
            searchView.setIconified(false);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {

                    if (!query.isEmpty()) {
                        EventBus.getDefault().postSticky(new SearchEvent(query));
                        searchView.clearFocus();
                        pager.requestFocus();
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
            searchView.clearFocus();
            pager.requestFocus();
        }
    }

    @Override
    public void onResume() { // not in onCreate so that tabs are updated when changed from settings
        super.onResume();
        refreshEnabledProviders(getActivity().getPreferences(Context.MODE_PRIVATE));
        if (adapter == null) {
            adapter = new SearchHolderAdapter(getChildFragmentManager(), enabledProviders);
        } else {
            adapter.setProviders(enabledProviders);
            adapter.notifyDataSetChanged();
        }
    }

}
