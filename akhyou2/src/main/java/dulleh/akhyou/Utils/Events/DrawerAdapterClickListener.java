package dulleh.akhyou.Utils.Events;

import android.support.annotation.Nullable;
import android.view.View;

import dulleh.akhyou.Models.Anime;

public interface DrawerAdapterClickListener {

    void onCLick(Anime item, @Nullable Integer position, @Nullable View view);

    void onUserItemClicked();

}
