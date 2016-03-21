package com.lob.musicshare.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;

import com.lob.musicshare.R;
import com.lob.musicshare.util.AndroidOverViewUtils;
import com.lob.musicshare.util.Constants;
import com.lob.musicshare.util.ParseValues;
import com.lob.musicshare.util.PhotoSelectUtils;
import com.lob.musicshare.util.PreferencesUtils;
import com.lob.musicshare.util.web.LoginHandler;
import com.lob.musicshare.util.web.ServerConnectionUtils;
import com.lob.musicshare.util.web.UploadProfilePicture;

import com.soundcloud.android.crop.Crop;

import java.io.File;

import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity {

    private static final int SELECT_PHOTO = 100;

    private String uploadProfilePicturePath;

    private TextInputLayout nameTextInputLayout, surnameTextInputLayout, emailTextInputLayout, passwordTextInputLayout;
    private TextView logoTextView;
    private EditText nameEditText, surnameEditText, emailEditText, passwordEditText, artistsEditText, genresEditText;
    private Button registerButton, pickProfilePictureButton;

    private boolean canWriteStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_register);

        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        AndroidOverViewUtils.setHeader(this);

        nameTextInputLayout = (TextInputLayout) findViewById(R.id.text_input_layout_name);
        surnameTextInputLayout = (TextInputLayout) findViewById(R.id.text_input_layout_surname);
        emailTextInputLayout = (TextInputLayout) findViewById(R.id.text_input_layout_email);
        passwordTextInputLayout = (TextInputLayout) findViewById(R.id.text_input_layout_password);

        logoTextView = (TextView) findViewById(R.id.logo_register);

        nameEditText = (EditText) findViewById(R.id.edit_text_name);
        surnameEditText = (EditText) findViewById(R.id.edit_text_surname);
        emailEditText = (EditText) findViewById(R.id.edit_text_email);
        passwordEditText = (EditText) findViewById(R.id.edit_text_password);
        artistsEditText = (EditText) findViewById(R.id.edit_text_artists);
        genresEditText = (EditText) findViewById(R.id.edit_text_genres);

        nameEditText.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        surnameEditText.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        emailEditText.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        passwordEditText.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        artistsEditText.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        genresEditText.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        registerButton = (Button) findViewById(R.id.button_register);
        pickProfilePictureButton = (Button) findViewById(R.id.button_profile_picture);

        logoTextView.setTypeface(Typeface.createFromAsset(getAssets(), Constants.TYPEFACE_NAME));

        pickProfilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterActivity.this.askPermissions();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInputData();
            }
        });
    }

    private void askPermissions() {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionsResultAction() {

                    @Override
                    public void onGranted() {
                        canWriteStorage = true;
                        PhotoSelectUtils.selectPhoto(RegisterActivity.this, SELECT_PHOTO);
                    }

                    @Override
                    public void onDenied(String permission) {
                        Toast.makeText(getApplicationContext(),
                                "Sorry, we need the Storage Permission to do that",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkInputData() {
        String name = nameEditText.getText().toString();
        String surname = surnameEditText.getText().toString();
        String email = emailEditText.getText().toString().toLowerCase();
        String password = passwordEditText.getText().toString();
        String artists = artistsEditText.getText().toString();
        String genres = genresEditText.getText().toString();

        if (name.matches("")) {
            nameTextInputLayout.setError(getString(R.string.insert_name));
            return;
        }

        if (surname.matches("")) {
            surnameTextInputLayout.setError(getString(R.string.insert_surname));
            return;
        }

        if (email.matches("")) {
            emailTextInputLayout.setError(getString(R.string.insert_email));
            return;
        }

        if (email.contains(" ")) {
            emailTextInputLayout.setError(getString(R.string.email_no_blank_spaces));
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTextInputLayout.setError(getString(R.string.email_not_valid));
            return;
        }

        if (password.matches("")) {
            passwordTextInputLayout.setError(getString(R.string.insert_password));
            return;
        }

        if (password.contains(" ")) {
            passwordTextInputLayout.setError(getString(R.string.pwd_no_blank_spaces));
            return;
        }

        if (!isAlphanumeric(password)) {
            passwordTextInputLayout.setError(getString(R.string.email_not_valid));
            return;
        }

        if (uploadProfilePicturePath != null) {
            if (!canWriteStorage) {
                askPermissions();
            } else {
                new UploadProfilePicture(RegisterActivity.this, email, uploadProfilePicturePath);
            }
        }

        PreferencesUtils.setName(getApplicationContext(), name + " " + surname);

        new RegisterHandler(name, surname, password, email, artists, genres).execute("");

    }

    private boolean isAlphanumeric(final String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    uploadProfilePicturePath = selectedImage.getPath();
                    Crop.of(selectedImage, Uri.fromFile(new File(Constants.PROFILE_IMAGE_PATH))).
                            asSquare().start(RegisterActivity.this);
                }
                break;
            case Crop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {
                    uploadProfilePicturePath = Crop.getOutput(imageReturnedIntent).getPath();
                    pickProfilePictureButton.setText(R.string.image_selected);
                }
                break;
        }
    }

    private class RegisterHandler extends AsyncTask<String, Void, String> {

        private final String name, surname, password, email, artists, genres;
        private String result;

        private AlertDialog progressDialog;

        RegisterHandler(String name, String surname, String password, String email,
                        String artists, String genres) {
            this.name = name;
            this.surname = surname;
            this.password = password;
            this.email = email;
            this.artists = artists;
            this.genres = genres;

            progressDialog = new SpotsDialog(RegisterActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String url = Constants.REGISTER_URL
                        + "?name=" + ParseValues.getParsedName(name)
                        + "&surname=" + ParseValues.getParsedSurname(surname)
                        + "&pwd=" + ParseValues.getParsedPassword(password)
                        + "&email=" + ParseValues.getParsedEmail(email)
                        + (artists != null ? "&artists=" + ParseValues.getParsedArtists(artists) : "")
                        + (genres != null ? "&genres=" + ParseValues.getParsedGenres(genres) : "");
                result = ServerConnectionUtils.getContent(url);
            } catch (Exception e) {
                e.printStackTrace();
                result = e.getMessage();
            }
            return "Executed";
        }


        @Override
        protected void onPostExecute(String string) {
            progressDialog.dismiss();
            if (result.equals(Constants.RESULT_SUCCESS)) {
                showToast(getString(R.string.success));

                RegisterActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new LoginHandler(RegisterActivity.this, email, password).execute("");
                    }
                });
            } else if (result.contains(Constants.RESULT_ERROR)) {
                showToast(getString(R.string.error) + ":" + " " + result);
            } else {
                showToast(result);
            }
        }

        private void showToast(String message) {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}
