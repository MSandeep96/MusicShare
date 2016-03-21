package com.lob.musicshare.util.web;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.lob.musicshare.R;
import com.lob.musicshare.activity.ContentActivity;
import com.lob.musicshare.json.ParseJson;
import com.lob.musicshare.query.Query;
import com.lob.musicshare.util.Constants;
import com.lob.musicshare.util.ParseValues;
import com.lob.musicshare.util.PreferencesUtils;

import dmax.dialog.SpotsDialog;

public class LoginHandler extends AsyncTask<String, Void, String> {

    private final String password, email;
    private final Activity activity;

    private String result;

    private AlertDialog progressDialog;

    public LoginHandler(Activity activity, String email, String password) {
        this.email = email;
        this.password = password;
        this.activity = activity;

        progressDialog = new SpotsDialog(activity);
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
            PreferencesUtils.setSuccessfulLogIn(activity, email, password);

            saveName();

            Intent contentActivity = new Intent(activity, ContentActivity.class);
            activity.startActivity(contentActivity);
        } else {
            Toast.makeText(activity, R.string.error, Toast.LENGTH_LONG).show();
        }
    }

    private void saveName() {
        Query.getInstance(activity, Query.QueryType.GET_USER_INFO)
                .setShowDialog(false)
                .setHisEmail(ParseValues.getParsedEmail(PreferencesUtils.getEmail(activity)))
                .setOnResultListener(new Query.OnResultListener() {
                    @Override
                    public void onResult(String result) {
                        PreferencesUtils.setName(activity,
                                ParseJson.generateUsers(result).get(0).userName);
                    }
                }).startQuery();
    }
}