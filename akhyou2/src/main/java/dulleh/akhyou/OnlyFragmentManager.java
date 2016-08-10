package dulleh.akhyou;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import dulleh.akhyou.Models.SharedElementTransitionBundle;

public interface OnlyFragmentManager {

    void requestFragment (@NonNull String tag, @Nullable SharedElementTransitionBundle transitionBundle);

}
