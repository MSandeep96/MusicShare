package com.lob.musicshare.activity;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.lob.musicshare.R;
import com.lob.musicshare.util.AndroidOverViewUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageActivity extends AppCompatActivity {

    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final ImageView imageView = (ImageView) findViewById(R.id.image);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));

        toolbar.setTitle(getIntent().getExtras().getString("title"));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageActivity.this.finish();
            }
        });

        AndroidOverViewUtils.setHeader(this);

        Picasso.with(this)
                .load(getIntent().getExtras().getString("image-url"))
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        new PhotoViewAttacher(imageView);
                    }

                    @Override
                    public void onError() {
                        Picasso.with(getApplicationContext())
                                .load(R.drawable.default_image_profile)
                                .into(imageView);
                        floatingActionButton.hide();
                    }
                });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                download(view);
            }
        });

        floatingActionButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Snackbar.make(view, R.string.download_image, Snackbar.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void download(final View view) {
        final String url = getIntent().getExtras().getString("image-url");
        final String person = getIntent().getExtras().getString("title");

        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionsResultAction() {

                    @Override
                    public void onGranted() {
                        Snackbar.make(view, R.string.download_started, Snackbar.LENGTH_SHORT)
                                .setActionTextColor(Color.WHITE).show();

                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                        request.setDescription(person);
                        request.setTitle(getResources().getString(R.string.downloading_image));
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, person);

                        ((DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request);
                    }

                    @Override
                    public void onDenied(String permission) {
                        Snackbar.make(floatingActionButton,
                                R.string.we_need_storage_permissions,
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
    }
}
