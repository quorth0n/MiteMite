package dulleh.akhyou.Search.Holder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import dulleh.akhyou.Models.Providers;
import dulleh.akhyou.Search.Holder.Item.SearchFragment;

public class SearchHolderAdapter extends FragmentStatePagerAdapter{
    public static final String PROVIDER_TYPE_KEY = "PROVIDER_TYPE_KEY";

    public SearchHolderAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment searchFragment = new SearchFragment();
        Bundle args = new Bundle(1);

        switch(position) {
            case 0:
                args.putInt(PROVIDER_TYPE_KEY, Providers.RUSH);
                break;
            case 1:
                args.putInt(PROVIDER_TYPE_KEY, Providers.RAM);
                break;
            case 2:
                args.putInt(PROVIDER_TYPE_KEY, Providers.BAM);
                break;
            case 3:
                args.putInt(PROVIDER_TYPE_KEY, Providers.KISS);
                break;
        }

        searchFragment.setArguments(args);
        return searchFragment;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0:
                return Providers.RUSH_TITLE;
            case 1:
                return Providers.RAM_TITLE;
            case 2:
                return Providers.BAM_TITLE;
            case 3:
                return Providers.KISS_TITLE;
            default:
                throw new RuntimeException("No title for this tab number. (sea)");
        }
    }
}
