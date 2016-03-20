package com.lob.musicshare.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.digitus.Digitus;
import com.afollestad.digitus.FingerprintDialog;
import com.lob.musicshare.R;
import com.lob.musicshare.json.ParseJson;
import com.lob.musicshare.query.Query;
import com.lob.musicshare.util.AndroidOverViewUtils;
import com.lob.musicshare.util.Constants;
import com.lob.musicshare.util.ParseValues;
import com.lob.musicshare.util.PreferencesUtils;
import com.lob.musicshare.util.web.InternetUtils;
import com.lob.musicshare.util.web.ServerConnectionUtils;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity implements FingerprintDialog.Callback {

    private TextInputLayout emailTextInputLayout, passwordTextInputLayout;
    private EditText emailEditText, passwordEditText;
    private TextView registerNowTextView, logoTextView;
    private Button loginButton;

    private String previousEmail, previousPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_login);

        if (PreferencesUtils.isFirstTime(this)) {
            Intent intent = new Intent(this, IntroductionActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }

        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        AndroidOverViewUtils.setHeader(this);

        if (InternetUtils.hasActiveInternetConnection(getApplicationContext())) {

            previousEmail = PreferencesUtils.getEmail(getApplicationContext());
            previousPassword = PreferencesUtils.getPassword(getApplicationContext());

            checkInputData(true);

            emailTextInputLayout = (TextInputLayout) findViewById(R.id.text_input_layout_email);
            passwordTextInputLayout = (TextInputLayout) findViewById(R.id.text_input_layout_password);

            emailEditText = (EditText) findViewById(R.id.edit_text_email);
            passwordEditText = (EditText) findViewById(R.id.edit_text_password);

            registerNowTextView = (TextView) findViewById(R.id.text_view_not_registered);
            logoTextView = (TextView) findViewById(R.id.logo_login);

            loginButton = (Button) findViewById(R.id.button_login);

            emailEditText.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            passwordEditText.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

            logoTextView.setTypeface(Typeface.createFromAsset(getAssets(), Constants.TYPEFACE_NAME));

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginActivity.this.checkInputData(false);
                }
            });

            registerNowTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent registerActivity = new Intent(LoginActivity.this.getApplicationContext(), RegisterActivity.class);
                    LoginActivity.this.startActivity(registerActivity);
                    LoginActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(R.string.warning);
            alertDialogBuilder.setMessage(R.string.this_app_requires_internet);
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    LoginActivity.this.finish();
                }
            });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Digitus.get().handleResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onFingerprintDialogAuthenticated() {
        new LoginHandler(previousEmail, previousPassword).execute("");
    }

    @Override
    public void onFingerprintDialogVerifyPassword(FingerprintDialog fingerprintDialog, String string) {
        if (string.equals(previousPassword)) {
            new LoginHandler(previousEmail, previousPassword).execute("");
        } else {
            Toast.makeText(getApplicationContext(), R.string.wrong_password, Toast.LENGTH_LONG).show();
            fingerprintDialog.dismiss();
        }
    }

    @Override
    public void onFingerprintDialogStageUpdated(FingerprintDialog dialog, FingerprintDialog.Stage stage) {
    }

    @Override
    public void onFingerprintDialogCancelled() {
    }

    private void checkInputData(boolean withoutInput) {
        if (!withoutInput) {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (email.matches("")) {
                emailTextInputLayout.setError(getString(R.string.insert_email));
                return;
            }

            if (password.matches("")) {
                passwordTextInputLayout.setError(getString(R.string.insert_password));
                return;
            }

            new LoginHandler(email, password).execute("");
        } else if (!previousEmail.equals("") && !previousPassword.equals("")) {
            boolean useFingerprint = FingerprintManagerCompat.from(getApplicationContext()).hasEnrolledFingerprints();
            if (useFingerprint) {
                FingerprintDialog.show(this, getString(R.string.app_name), 69);
            } else {
                new LoginHandler(previousEmail, previousPassword).execute("");
            }
        }

    }

    private void saveName() {
        Query.getInstance(LoginActivity.this, Query.QueryType.GET_USER_INFO)
                .setShowDialog(false)
                .setHisEmail(ParseValues.getParsedEmail(PreferencesUtils.getEmail(getApplicationContext())))
                .setOnResultListener(new Query.OnResultListener() {
                    @Override
                    public void onResult(String result) {
                        PreferencesUtils.setName(LoginActivity.this.getApplicationContext(),
                                ParseJson.generateUsers(result).get(0).userName);
                    }
                }).startQuery();
    }

    private class LoginHandler extends AsyncTask<String, Void, String> {

        private final String password, email;

        private String result;

        private AlertDialog progressDialog;

        LoginHandler(String email, String password) {
            this.email = email;
            this.password = password;

            progressDialog = new SpotsDialog(LoginActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.show();

            progressDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String url = Constants.LOGIN_URL
                        + "?email=" + email.replace(".", "__dot__").replace("@", "__at__")
                        + "&pwd=" + password.replace(".", "__dot__").replace("@", "__at__");
                result = ServerConnectionUtils.getContent(url);
            } catch (Exception e) {
                result = "error: unknown error";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String string) {
            progressDialog.dismiss();
            if (result.equals(Constants.RESULT_SUCCESS)) {
                PreferencesUtils.setSuccessfulLogIn(getApplicationContext(), email, password);

                saveName();

                Intent contentActivity = new Intent(getApplicationContext(), ContentActivity.class);
                startActivity(contentActivity);
            } else {
                Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_LONG).show();
            }
        }

    }
}
