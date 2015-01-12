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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Rafal Soudani on 11-01-2015.
 */
public class MainActivity extends ActionBarActivity {

    public static final String BASE_SERVER_URL = "http://192.168.1.12:8080";
    public static final String DOWNLOAD_IMAGE_FILTER =
            "com.rsoudani.rafalsoudani.DOWNLOAD_IMAGE_COMPLETE";
    private final IntentFilter filter_image =
            new IntentFilter(DOWNLOAD_IMAGE_FILTER);
    public static final String DOWNLOAD_JSON_FILTER =
            "com.rsoudani.rafalsoudani.DOWNLOAD_JSON_COMPLETE";
    private final IntentFilter filter_json =
            new IntentFilter(DOWNLOAD_JSON_FILTER);
    private static final String LIST_INSTANCE_STATE = "list_state";
    private static String fileName = "page_0.json";
    private final BroadcastReceiver jsonDownloadedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            JSONParser jsonParser = new JSONParser(MainActivity.this, fileName);
            jsonParser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            checkAllImagesDownloaded();
        }
    };
    private static boolean isLoading = false;
    private final BroadcastReceiver imageDownloadedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            adapter.notifyDataSetChanged();


            int firstVisiblePosition = lv.getFirstVisiblePosition();
            Item item = items.get(firstVisiblePosition+1);
            int i = 1;
            while (item.getImage() != null) {
                i++;
                if ((firstVisiblePosition + i) < items.size()) {
                    item = items.get(firstVisiblePosition + i);
                } else {
                    checkAllImagesDownloaded();
                    break;
                }
            }
            if (item.getImage() == null) {
                item.loadImage(getApplicationContext());
            }

        }
    };
    private DownloadTask downloadTask;
    private ArrayList<Item> items;
    private ListView lv;
    private MyAdapter adapter;

    private static String getFileName() {
        return fileName;
    }

    public static void setFileName(String fileName) {

        MainActivity.fileName = fileName;
    }

    public static void setLoadingToFalse() {
        MainActivity.isLoading = false;
    }

    private void checkAllImagesDownloaded() {
        for (Item item : items) {
            if (item.getImage() == null) {
                item.loadImage(getApplicationContext());
                break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(imageDownloadedReceiver, filter_image);
        registerReceiver(jsonDownloadedReceiver, filter_json);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setVariables();


        if (savedInstanceState != null) {
            items = savedInstanceState.getParcelableArrayList("key");
            adapter = new MyAdapter(this, items);
            lv.setAdapter(adapter);
            lv.onRestoreInstanceState(savedInstanceState.getParcelable(LIST_INSTANCE_STATE));
            if (isLoading) startDownload();
        } else {
            EndlessScrollListener.resetListener();
            fileName = "page_0.json";
            downloadTask = new DownloadTask(this, fileName);
            downloadTask.execute();
        }
    }

    @Override
    public void onPause() {
        unregisterReceiver(imageDownloadedReceiver);
        unregisterReceiver(jsonDownloadedReceiver);
        super.onPause();
    }

    private void setVariables() {

        items = new ArrayList<>();
        lv = (ListView) findViewById(R.id.mainListView);
        adapter = new MyAdapter(this, items);
        lv.setAdapter(adapter);

        Button logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("key", items);
        outState.putParcelable(LIST_INSTANCE_STATE, lv.onSaveInstanceState());
        downloadTask.cancel(true);
    }

    public void startDownload() {
        downloadTask = new DownloadTask(this, MainActivity.getFileName());
        downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        isLoading = true;
    }

    private class JSONParser extends AsyncTask<String, String, ArrayList<Item>> {

        private final String fileName;
        private ProgressDialog progressDialog;
        private Context context = null;

        public JSONParser(Context context, String fileName) {
            this.context = context;
            this.fileName = fileName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Processing Data ...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected ArrayList<Item> doInBackground(String... params) {

            try {
                JSONObject jsonObject = new JSONObject(loadJSONFromFile(fileName));
                JSONArray jsonArray = jsonObject.getJSONArray("array");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject singleObject = jsonArray.getJSONObject(i);

                    String title = singleObject.getString("title");
                    String desc = singleObject.getString("desc");
                    String url = singleObject.getString("url");

                    Item item = new Item(title, desc, url);

                    items.add(item);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return items;
        }

        @Override
        protected void onPostExecute(ArrayList<Item> items) {
            super.onPostExecute(items);
            progressDialog.dismiss();

            adapter.notifyDataSetChanged();
            Item item = items.get(0);
            item.loadImage(getApplicationContext());

            lv.setOnScrollListener(new EndlessScrollListener(MainActivity.this));

        }

        public String loadJSONFromFile(String fileName) {
            String json;
            try {

                InputStream is = context.openFileInput(fileName);

                int size = is.available();

                byte[] buffer = new byte[size];

                //noinspection ResultOfMethodCallIgnored
                is.read(buffer);

                is.close();

                json = new String(buffer, "UTF-8");


            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
            return json;

        }
    }

}
