package dulleh.akhyou.Settings.HummingbirdSettings;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class HummingbirdHolderAdapter extends FragmentStatePagerAdapter {
    final String settings;
    final String history;

    public HummingbirdHolderAdapter(FragmentManager fm, String settingsTabTitle, String historyTabTitle) {
        super(fm);
        settings = settingsTabTitle;
        history = historyTabTitle;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return new HummingbirdSettingsFragment();
            case 1:
                return new HummingbirdHistoryFragment();
            default:
                throw new RuntimeException("No page for this tab number. (hum)");
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0:
                return settings;
            case 1:
                return history;
            default:
                throw new RuntimeException("No title for this tab number. (hum)");
        }
    }
}
