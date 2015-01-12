/*
 * Copyright (c) 2015. The Android Open Source Project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.rsoudani.rafalsoudani;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *  Created by Rafal Soudani on 11-01-2015.
 */
class DownloadTask extends AsyncTask<String, Integer, String> {

    private static final String TAG = "DownloadTask";

    private final Context context;
    private final String mFileName;
    private ProgressDialog progressDialog;

    public DownloadTask(Context context, String fileName) {
        this.context = context;
        this.mFileName = fileName;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        EndlessScrollListener.setLoadingTrue();
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Getting Data ...");
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancel(true);
            }
        });
        progressDialog.show();

    }

    @Override
    protected String doInBackground(String... params) {

        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        HttpURLConnection connection = null;

        try {
            URL url = new URL((MainActivity.BASE_SERVER_URL + "/" + mFileName));
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                if (connection.getResponseCode() == 404) EndlessScrollListener.setLastPage();
                this.cancel(true);
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }


            outputStream = context.openFileOutput(mFileName, Context.MODE_PRIVATE);
            inputStream = connection.getInputStream();

            byte[] buffer = new byte[8];
            int bufferLength;

            while ((bufferLength = inputStream.read(buffer)) > 0) {

                // allow canceling
                if (isCancelled()) {
                    inputStream.close();
                    this.cancel(true);
                    return null;
                }
                outputStream.write(buffer, 0, bufferLength);
            }


        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null)
                    outputStream.close();
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (connection != null)
                connection.disconnect();
        }
        return mFileName;
    }

    @Override
    protected void onPostExecute(String fileName) {
        super.onPostExecute(fileName);
        MainActivity.setLoadingToFalse();
        progressDialog.dismiss();
        Intent intent = new Intent();
        intent.setAction(MainActivity.DOWNLOAD_JSON_FILTER);
        context.sendBroadcast(intent);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
            EndlessScrollListener.setLoadingToFalse();
        }

    }
}
