package dulleh.akhyou.Anime;

import android.support.v7.graphics.Palette;

public interface AnimeAdapterListener {

    void onFavouriteCheckedChanged (boolean favourite);

    void showImageDialog ();

    void onMajorColourChanged (Palette palette);

}
