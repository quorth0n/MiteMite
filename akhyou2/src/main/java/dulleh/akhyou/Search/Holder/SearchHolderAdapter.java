package dulleh.akhyou.Search.Holder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import dulleh.akhyou.Models.Providers;
import dulleh.akhyou.Search.Holder.Item.SearchFragment;

public class SearchHolderAdapter extends FragmentStatePagerAdapter{
    public static final String PROVIDER_TYPE_KEY = "PROVIDER_TYPE_KEY";

    private List<String> providers;

    public SearchHolderAdapter(FragmentManager fm, List<String> providers) {
        super(fm);
        this.providers = providers;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment searchFragment = new SearchFragment();
        Bundle args = new Bundle(1);

        CharSequence providerTitle = providers.get(position);

        if (providerTitle.equals(Providers.RUSH_TITLE)) {
            args.putInt(PROVIDER_TYPE_KEY, Providers.RUSH);
        } else if (providerTitle.equals(Providers.RAM_TITLE)) {
            args.putInt(PROVIDER_TYPE_KEY, Providers.RAM);
        } else if (providerTitle.equals(Providers.BAM_TITLE)) {
            args.putInt(PROVIDER_TYPE_KEY, Providers.BAM);
        } else if (providerTitle.equals(Providers.KISS_TITLE)) {
            args.putInt(PROVIDER_TYPE_KEY, Providers.KISS);
        }

        searchFragment.setArguments(args);
        return searchFragment;
    }

    @Override
    public int getCount() {
        return providers.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return providers.get(position);
    }

    public void setProviders(List<String> providers) {
        this.providers = providers;
    }
}
