package dulleh.akhyou.Settings.HummingbirdSettings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;

import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import dulleh.akhyou.MainModel;
import dulleh.akhyou.R;
import dulleh.akhyou.Utils.Events.HummingbirdCredentialsUpdatedEvent;
import nucleus.view.NucleusSupportFragment;
import rx.Subscriber;
import rx.Subscription;

public class HummingbirdSettingsFragment extends NucleusSupportFragment<HummingbirdSettingsPresenter> {
    private SharedPreferences sharedPreferences;
    private Subscription usernameListener;
    private Subscription passwordListener;

    TextInputEditText usernameEditText;
    TextInputEditText passwordEditText;

    private String username;
    private String password;
    private boolean changed = false;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        username = sharedPreferences.getString(MainModel.HB_USERNAME_PREF, null);
        password = sharedPreferences.getString(MainModel.HB_PASSWORD_PREF, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.hummingbird_settings_fragment, container, false);

        usernameEditText = (TextInputEditText) v.findViewById(R.id.username_edit_text);
        passwordEditText = (TextInputEditText) v.findViewById(R.id.password_edit_text);
        usernameEditText.setText(username);
        passwordEditText.setText(password);
        subscribeUsernameListener();
        subscribePasswordListener();

        return v;
    }

    private void subscribeUsernameListener () {
        usernameListener = RxTextView.textChangeEvents(usernameEditText)
                .debounce(1, TimeUnit.SECONDS)
                .subscribe(new Subscriber<TextViewTextChangeEvent>() {
                    @Override
                    public void onNext(TextViewTextChangeEvent textViewTextChangeEvent) {
                        username = textViewTextChangeEvent.text().toString();
                        changed = true;
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    private void subscribePasswordListener () {
        passwordListener = RxTextView.textChangeEvents(passwordEditText)
                .debounce(1, TimeUnit.SECONDS)
                .subscribe(new Subscriber<TextViewTextChangeEvent>() {
                    @Override
                    public void onNext(TextViewTextChangeEvent textViewTextChangeEvent) {
                        password = textViewTextChangeEvent.text().toString();
                        changed = true;
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (usernameListener.isUnsubscribed()) subscribeUsernameListener();
        if (passwordListener.isUnsubscribed()) subscribePasswordListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        usernameListener.unsubscribe();
        passwordListener.unsubscribe();

        if (changed) {
            updateHummingbirdCredentials();
        }
    }

    public void updateHummingbirdCredentials () {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MainModel.HB_USERNAME_PREF, username);
        editor.putString(MainModel.HB_PASSWORD_PREF, password);
        editor.apply();

        EventBus.getDefault().post(new HummingbirdCredentialsUpdatedEvent(username, password));
    }

}
