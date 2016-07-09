package dulleh.akhyou.Settings.HummingbirdSettings;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.nononsenseapps.filepicker.FilePickerActivity;

import dulleh.akhyou.R;

public class ResettableFilePickerActivity extends FilePickerActivity {
    public static final String EXTRA_DEFAULT_PATH = "nononsense.intent.DEFAULT_PATH";

    private static final int RESET_ITEM_ID = 4949449;

    private String defaultPath;

    public ResettableFilePickerActivity () {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: " +  getIntent().getStringExtra(EXTRA_DEFAULT_PATH));
        defaultPath = getIntent().getStringExtra(EXTRA_DEFAULT_PATH);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(Menu.NONE, RESET_ITEM_ID, 2, getString(R.string.reset_to_default));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == RESET_ITEM_ID) {
            Log.d(TAG, "onOptionsItemSelected: " + defaultPath);
            if (defaultPath != null) {
                onFilePicked(Uri.parse(defaultPath));
            } else {
                onFilePicked(Uri.parse(Environment.getExternalStorageDirectory().getPath()));
            }
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }
}
