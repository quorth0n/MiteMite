package dulleh.akhyou.Search.Holder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import dulleh.akhyou.Models.Anime;
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
                args.putInt(PROVIDER_TYPE_KEY, Anime.ANIME_RUSH);
                break;
            case 1:
                args.putInt(PROVIDER_TYPE_KEY, Anime.ANIME_RAM);
                break;
            case 2:
                args.putInt(PROVIDER_TYPE_KEY, Anime.ANIME_BAM);
                break;
        }

        searchFragment.setArguments(args);
        return searchFragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0:
                return Anime.ANIME_RUSH_TITLE;
            case 1:
                return Anime.ANIME_RAM_TITLE;
            case 2:
                return Anime.ANIME_BAM_TITLE;
            default:
                return null;
        }
    }
}
