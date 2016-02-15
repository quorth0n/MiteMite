package dulleh.akhyou.Settings.HummingbirdSettings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dulleh.akhyou.R;
import nucleus.view.NucleusSupportFragment;

public class HummingbirdHistoryFragment extends NucleusSupportFragment<HummingbirdHistoryPresenter> {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.hummingbird_history_fragment, container, false);

        return v;
    }

}
