package dulleh.akhyou.Settings.HummingbirdSettings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dulleh.akhyou.MainActivity;
import dulleh.akhyou.R;

public class HummingbirdHolderFragment extends Fragment {

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.hummingbird_holder_fragment, container, false);
        setToolbarTitle(getString(R.string.hummingbird_preference_title));

        ViewPager viewPager = (ViewPager) v.findViewById(R.id.hb_view_pager);
        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tabs);

        HummingbirdHolderAdapter adapter = new HummingbirdHolderAdapter(getChildFragmentManager(), getString(R.string.settings_tab), getString(R.string.history_tab));

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        return v;
    }

    public void setToolbarTitle (String title) {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(title);
    }

}
