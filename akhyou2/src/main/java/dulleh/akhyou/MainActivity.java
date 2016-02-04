package dulleh.akhyou;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.List;

import de.greenrobot.event.EventBus;
import dulleh.akhyou.Anime.AnimeFragment;
import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Search.Holder.SearchHolderFragment;
import dulleh.akhyou.Settings.HummingbirdSettings.HummingbirdHolderFragment;
import dulleh.akhyou.Settings.SettingsFragment;
import dulleh.akhyou.Utils.AdapterClickListener;
import dulleh.akhyou.Utils.Events.CircularTransform;
import dulleh.akhyou.Utils.Events.OpenAnimeEvent;
import dulleh.akhyou.Utils.Events.SettingsItemSelectedEvent;
import dulleh.akhyou.Utils.Events.SnackbarEvent;
import nucleus.factory.RequiresPresenter;
import nucleus.view.NucleusAppCompatActivity;

@RequiresPresenter(MainPresenter.class)
public class MainActivity extends NucleusAppCompatActivity<MainPresenter> implements AdapterClickListener<Anime>{
    private SharedPreferences sharedPreferences;
    private android.support.v4.app.FragmentManager fragmentManager;
    private FrameLayout parentLayout;
    private DrawerLayout drawerLayout;
    private RecyclerView favouritesList;
    private DrawerAdapter drawerAdapter;
    private TextView usernameTextView;
    private ImageView userAvatarImageView;
    private ImageView userCoverImageView;

    public static final String SEARCH_FRAGMENT = "SEA";
    public static final String ANIME_FRAGMENT = "ANI";
    public static final String SETTINGS_FRAGMENT = "SET";
    public static final String HUMMINGBIRD_SETTINGS_FRAGMENT = "HUM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        sharedPreferences = getPreferences(MODE_PRIVATE);
        fragmentManager = getSupportFragmentManager();

        getPresenter().setSharedPreferences(sharedPreferences);

        parentLayout = (FrameLayout) findViewById(R.id.container);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        RelativeLayout drawerSettingsButton = (RelativeLayout) findViewById(R.id.drawer_settings);
        drawerSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestFragment(MainActivity.SETTINGS_FRAGMENT);
                closeDrawer();
            }
        });

        RelativeLayout drawerUserButton = (RelativeLayout) findViewById(R.id.drawer_user);
        drawerUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestFragment(HUMMINGBIRD_SETTINGS_FRAGMENT);
                closeDrawer();
            }
        });

        favouritesList = (RecyclerView) findViewById(R.id.drawer_recycler_view);
        favouritesList.setLayoutManager(new LinearLayoutManager(this));

        usernameTextView = (TextView) findViewById(R.id.drawer_user_name);
        userAvatarImageView = (ImageView) findViewById(R.id.drawer_user_avatar);
        userCoverImageView = (ImageView) findViewById(R.id.drawer_user_cover);

        //getPresenter().refreshFavouritesList();
        setFavouritesAdapter();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setContentInsetsRelative(0, 0);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // must be after set as actionbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        Intent openingIntent = getIntent();

        if (openingIntent.getAction() != null && openingIntent.getAction().equals(Intent.ACTION_SEND)) {
            String intentExtra = openingIntent.getStringExtra(Intent.EXTRA_TEXT);

            if (intentExtra != null && intentExtra.contains("hummingbird.me/anime/")) {
                getPresenter().launchFromHbLink(intentExtra);
            }

        } else if (savedInstanceState == null){
            getPresenter().onFreshStart(this);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainApplication.getRefWatcher(this).watch(this);
    }

    public void closeDrawer () {
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void setTheme () {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        int themePref = sharedPreferences.getInt(SettingsFragment.THEME_PREFERENCE, 0);

        switch (themePref) {
            case 1:
                setTheme(R.style.PinkTheme);
                break;

            case 2:
                setTheme(R.style.PurpleTheme);
                break;

            case 3:
                setTheme(R.style.DeepPurpleTheme);
                break;

            case 4:
                setTheme(R.style.IndigoTheme);
                break;

            case 5:
                setTheme(R.style.LightBlueTheme);
                break;

            case 6:
                setTheme(R.style.CyanTheme);
                break;

            case 7:
                setTheme(R.style.TealTheme);
                break;

            case 8:
                setTheme(R.style.GreenTheme);
                break;

            case 9:
                setTheme(R.style.LightGreenTheme);
                break;

            case 10:
                setTheme(R.style.LimeTheme);
                break;

            case 11:
                setTheme(R.style.YellowTheme);
                break;

            case 12:
                setTheme(R.style.OrangeTheme);
                break;

            case 13:
                setTheme(R.style.DeepOrangeTheme);
                break;

            case 14:
                setTheme(R.style.BrownTheme);
                break;

            case 15:
                setTheme(R.style.GreyTheme);
                break;

            case 16:
                setTheme(R.style.BlueGreyTheme);
                break;

            default:
                setTheme(R.style.AkhyouRedTheme);
                break;
        }
    }

    public void requestFragment (String tag) {
        boolean seaInBackStack = false;
        boolean aniInBackStack = false;
        boolean setInBackStack = false;
        boolean humInBackStack = false;

        fragmentManager.getBackStackEntryCount();

        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
            String name = fragmentManager.getBackStackEntryAt(i).getName();

            if (name.equals(SEARCH_FRAGMENT)) seaInBackStack = true;
            if (name.equals(ANIME_FRAGMENT)) aniInBackStack = true;
            if (name.equals(SETTINGS_FRAGMENT)) setInBackStack = true;
            if (name.equals(HUMMINGBIRD_SETTINGS_FRAGMENT)) humInBackStack = true;
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null) {
                    String fragTag = fragment.getTag();

                    if (fragTag.equals(SEARCH_FRAGMENT) && !seaInBackStack && !tag.equals(SEARCH_FRAGMENT))
                        fragmentTransaction.addToBackStack(SEARCH_FRAGMENT);

                    else if (fragTag.equals(ANIME_FRAGMENT) && !aniInBackStack && !tag.equals(SEARCH_FRAGMENT) && !tag.equals(ANIME_FRAGMENT))
                        fragmentTransaction.addToBackStack(ANIME_FRAGMENT);

                    else if (fragTag.equals(SETTINGS_FRAGMENT) && !setInBackStack && !tag.equals(SEARCH_FRAGMENT) && !tag.equals(ANIME_FRAGMENT) && !tag.equals(SETTINGS_FRAGMENT))
                        fragmentTransaction.addToBackStack(SETTINGS_FRAGMENT);

                    else if (fragTag.equals(HUMMINGBIRD_SETTINGS_FRAGMENT) && !humInBackStack && !tag.equals(SEARCH_FRAGMENT) && !tag.equals(ANIME_FRAGMENT) && !tag.equals(SETTINGS_FRAGMENT) && !tag.equals(HUMMINGBIRD_SETTINGS_FRAGMENT))
                        fragmentTransaction.addToBackStack(HUMMINGBIRD_SETTINGS_FRAGMENT);
                }
            }
        }

        switch (tag) {

            case SEARCH_FRAGMENT:
                Fragment searchFragment = fragmentManager.findFragmentByTag(SEARCH_FRAGMENT);

                if (searchFragment == null) {

                    fragmentTransaction
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.container, new SearchHolderFragment(), SEARCH_FRAGMENT);

                } else {
                    fragmentManager.popBackStackImmediate(ANIME_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }

                break;

            case ANIME_FRAGMENT:
                Fragment animeFragment = fragmentManager.findFragmentByTag(ANIME_FRAGMENT);

                if (animeFragment == null) {

                    fragmentTransaction
                            .setCustomAnimations(R.anim.enter_right, 0, 0, R.anim.exit_right)
                            .replace(R.id.container, new AnimeFragment(), ANIME_FRAGMENT);

                } else {
                    fragmentManager.popBackStackImmediate(ANIME_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }

                break;

            case SETTINGS_FRAGMENT:
                Fragment settingsFragment = fragmentManager.findFragmentByTag(SETTINGS_FRAGMENT);

                if (settingsFragment == null) {

                    fragmentTransaction
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.container, new SettingsFragment(), SETTINGS_FRAGMENT);

                } else {
                    fragmentManager.popBackStackImmediate(SETTINGS_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }

                break;

            case HUMMINGBIRD_SETTINGS_FRAGMENT:
                Fragment hummingbirdSettingsFragment = fragmentManager.findFragmentByTag(HUMMINGBIRD_SETTINGS_FRAGMENT);

                if (hummingbirdSettingsFragment == null) {

                    fragmentTransaction
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.container, new HummingbirdHolderFragment(), HUMMINGBIRD_SETTINGS_FRAGMENT);

                } else {

                    fragmentManager.popBackStackImmediate(HUMMINGBIRD_SETTINGS_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                }

                break;

        }

        fragmentTransaction.commit();
    }

    public void showSnackBar (SnackbarEvent event) {
        if (event.actionTitle == null) {
            Snackbar.make(parentLayout, event.message, event.duration)
                    .show();
        } else {
            Snackbar.make(parentLayout, event.message, event.duration)
                    .setAction(event.actionTitle, event.onClickListener)
                    .setActionTextColor(event.actionColor)
                    .show();
        }
    }

    @Override
    public void onCLick(Anime item, @Nullable Integer position) {
        EventBus.getDefault().postSticky(new OpenAnimeEvent(item));

        requestFragment(MainActivity.ANIME_FRAGMENT);

        /*if (fragmentManager.findFragmentByTag(SETTINGS_FRAGMENT) != null) {
            fragmentManager
                    .beginTransaction()
                    .remove(fragmentManager.findFragmentByTag(SETTINGS_FRAGMENT))
                    .commit();
        }*/

        closeDrawer();
    }

    @Override
    public void onLongClick(Anime item, @Nullable Integer position) {

    }

    private void setFavouritesAdapter () {
        drawerAdapter = new DrawerAdapter(this, getPresenter().getFavourites());
        favouritesList.setAdapter(drawerAdapter);
    }

    public void favouritesChanged () {
        if (drawerAdapter != null) {
            drawerAdapter.setFavourites(getPresenter().getFavourites());
            drawerAdapter.notifyDataSetChanged();
        } else {
            setFavouritesAdapter();
            drawerAdapter.notifyDataSetChanged();
        }
    }

    public void refreshDrawerUser(String hbUsername, String avatar, String cover) {
        if (hbUsername == null) {
            hbUsername = getString(R.string.hummingbird_username_placeholder);
        }
        usernameTextView.setText(hbUsername);

        RequestCreator avatarRequest;
        if (avatar != null) {
            avatarRequest = Picasso.with(this).load(avatar);
        } else {
            avatarRequest = Picasso.with(this).load(R.drawable.user_stock_avatar);
        }

        RequestCreator coverRequest;
        if (cover != null) {
            coverRequest = Picasso.with(this).load(cover);
        } else {
            coverRequest = Picasso.with(this).load(R.drawable.user_stock_cover);
        }

        int avatarLength = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, Resources.getSystem().getDisplayMetrics());

        avatarRequest
                .fit()
                .centerCrop()
                .transform(new CircularTransform(avatarLength, 0))
                .into(userAvatarImageView);

        coverRequest
                .fit()
                .centerCrop()
                .into(userCoverImageView);
    }

    public void promptForUpdate (String newUpdateVersion) {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.update_title))
                .content(newUpdateVersion)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        getPresenter().downloadUpdate(MainModel.LATEST_RELEASE_LINK);
                    }
                })
                .positiveText(getString(R.string.update))
                .negativeText(getString(R.string.cancel))
                .show();
    }

}