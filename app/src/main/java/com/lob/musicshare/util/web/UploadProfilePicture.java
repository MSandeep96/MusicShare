package com.lob.musicshare.util.web;

import android.app.Activity;
import android.app.AlertDialog;
import android.widget.Toast;

import com.lob.musicshare.R;
import com.lob.musicshare.util.Constants;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import dmax.dialog.SpotsDialog;

import static com.lob.musicshare.util.BitmapUtils.getBitmapFromPath;
import static com.lob.musicshare.util.BitmapUtils.getResizedBitmap;
import static com.lob.musicshare.util.BitmapUtils.saveImage;

public class UploadProfilePicture {

    private final Activity activity;

    private AlertDialog progressDialog;

    private int serverResponseCode = 0;
    private String upLoadServerUri = Constants.UPLOAD_PROFILE_PICTURE_PHP_SCRIPT;
    private String uploadFilePath;

    private HttpURLConnection httpUrlConnection;
    private DataOutputStream dataOutputStream;
    private String lineEnd = "\r\n";
    private String twoHyphens = "--";
    private String boundary = "*****";
    private int bytesRead, bytesAvailable, bufferSize;
    private byte[] buffer;
    private int maxBufferSize = 1024 * 1024;

    public UploadProfilePicture(final Activity activity, final String email, final String uploadFilePath) {
        this.activity = activity;
        this.uploadFilePath = uploadFilePath;

        progressDialog = new SpotsDialog(activity);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final String path = saveImage(getResizedBitmap(getBitmapFromPath(uploadFilePath), 240, 240));
        new Thread(new Runnable() {
            public void run() {
                uploadFile(email, path);
            }
        }).start();
    }

    private int uploadFile(final String email, final String sourceFileUri) {
        File sourceFile = new File(sourceFileUri);
        if (!sourceFile.isFile()) {
            progressDialog.dismiss();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, R.string.file_not_exist, Toast.LENGTH_LONG).show();
                }
            });
            return 0;
        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                httpUrlConnection = (HttpURLConnection) url.openConnection();
                httpUrlConnection.setDoInput(true);
                httpUrlConnection.setDoOutput(true);
                httpUrlConnection.setUseCaches(false);
                httpUrlConnection.setRequestMethod("POST");
                httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
                httpUrlConnection.setRequestProperty("ENCTYPE", "multipart/form-data");
                httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                httpUrlConnection.setRequestProperty("uploaded_file", sourceFileUri);

                dataOutputStream = new DataOutputStream(httpUrlConnection.getOutputStream());

                // String extensionString = sourceFileUri.substring(sourceFileUri.lastIndexOf("."));

                final String finalName = (email).replace("@", "__at__").replace(".", "__dot__");

                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\""
                        + finalName + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dataOutputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);


                serverResponseCode = httpUrlConnection.getResponseCode();

                if (serverResponseCode == 200) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(activity, R.string.done, Toast.LENGTH_LONG).show();
                        }
                    });
                }

                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(activity, R.string.error, Toast.LENGTH_LONG).show();
                    }
                });
            }
            progressDialog.dismiss();
            return serverResponseCode;
        }
    }
}
