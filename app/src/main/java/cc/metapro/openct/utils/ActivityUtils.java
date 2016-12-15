package cc.metapro.openct.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.google.common.base.Strings;

import cc.metapro.openct.R;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by jeffrey on 11/29/16.
 */

public class ActivityUtils {

    private static ProgressDialog pd;

    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
                                             @NonNull Fragment fragment, int frameId) {
        checkNotNull(fragmentManager);
        checkNotNull(fragment);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment);
        transaction.commit();
    }

    public static ProgressDialog getProgressDialog(Context context, String title, String message) {
        pd = new ProgressDialog(context);
        if (!Strings.isNullOrEmpty(title)) {
            pd.setTitle(title);
        }
        if (!Strings.isNullOrEmpty(message)) {
            pd.setMessage(message);
        }
        return pd;
    }

    public static void dismissProgressDialog() {
        if (pd == null) return;
        if (pd.isShowing()) {
            pd.dismiss();
        }
    }

    public static AlertDialog getCAPTCHADialog(final Context context,
                                               final CaptchaDialogHelper captchaDialogHelper,
                                               String positiveString) {
        AlertDialog.Builder ab = new AlertDialog.Builder(context);

        // set dialog view
        View view = LayoutInflater.from(context).inflate(R.layout.captcha_diaolg, null);
        final AppCompatTextView textView = (AppCompatTextView) view.findViewById(R.id.captcha_image);
        final AppCompatEditText editText = (AppCompatEditText) view.findViewById(R.id.captcha_edit_text);
        captchaDialogHelper.setCAPTCHATextView(textView);
        captchaDialogHelper.setEditText(editText);

        // click to get captcha pic
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captchaDialogHelper.loadCAPTCHA();
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_GO || (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    String code = editText.getText().toString();
                    if (Strings.isNullOrEmpty(code)) {
                        captchaDialogHelper.showOnCodeEmpty();
                    } else {
                        captchaDialogHelper.loadCAPTCHA();
                    }
                    return true;
                }
                return false;
            }
        });

        ab.setPositiveButton(positiveString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String code = editText.getText().toString();
                if (Strings.isNullOrEmpty(code)) {
                    captchaDialogHelper.showOnCodeEmpty();
                } else {
                    captchaDialogHelper.loadOnlineInfo();
                }
            }
        });

        ab.setView(view);
        return ab.create();
    }

    public static void encryptionCheck(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                boolean cmsPasswordEncrypted = preferences.getBoolean(Constants.PREF_CMS_PASSWORD_ENCRYPTED, false);
                if (!cmsPasswordEncrypted) {
                    String cmsPassword = preferences.getString(Constants.PREF_CMS_PASSWORD_KEY, "");
                    try {
                        if (!Strings.isNullOrEmpty(cmsPassword)) {
                            cmsPassword = EncryptionUtils.encrypt(Constants.seed, cmsPassword);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(Constants.PREF_CMS_PASSWORD_KEY, cmsPassword);
                            editor.putBoolean(Constants.PREF_CMS_PASSWORD_ENCRYPTED, true);
                            editor.apply();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                boolean libPasswordEncrypted = preferences.getBoolean(Constants.PREF_LIB_PASSWORD_ENCRYPTED, false);
                if (!libPasswordEncrypted) {
                    try {
                        String libPassword = preferences.getString(Constants.PREF_LIB_PASSWORD_KEY, "");
                        if (!Strings.isNullOrEmpty(libPassword)) {
                            libPassword = EncryptionUtils.encrypt(Constants.seed, libPassword);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(Constants.PREF_LIB_PASSWORD_KEY, libPassword);
                            editor.putBoolean(Constants.PREF_LIB_PASSWORD_ENCRYPTED, true);
                            editor.apply();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static abstract class CaptchaDialogHelper {

        AppCompatTextView mTextView;

        AppCompatEditText mEditText;

        public abstract void loadCAPTCHA();

        public abstract void showOnCodeEmpty();

        public abstract void loadOnlineInfo();

        public String getCode() {
            return mEditText != null ? mEditText.getText().toString() : "";
        }

        public AppCompatTextView getCAPTCHATextView() {
            return mTextView;
        }

        void setCAPTCHATextView(AppCompatTextView textView) {
            mTextView = textView;
        }

        void setEditText(AppCompatEditText editText) {
            mEditText = editText;
        }

    }
}