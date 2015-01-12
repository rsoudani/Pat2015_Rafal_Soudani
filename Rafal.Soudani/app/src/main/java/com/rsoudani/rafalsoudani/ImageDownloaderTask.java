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

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 *Created by Rafal Soudani on 12-01-2015
 */
class ImageDownloaderTask extends AsyncTask<String, String, Bitmap> {
    private static final int IMAGE_HEIGHT = 48;
    private static final int IMAGE_WIDTH = 48;
    private final Context mContext;
    private final Item mItem;

    public ImageDownloaderTask(Context context, Item item) {
        mContext = context;
        mItem = item;
    }

    private static Bitmap getBitmapFromURL(String src) {
        try {

            URL url = new URL(src);
            URLConnection connection = url.openConnection();
            connection.setUseCaches(true);
            return createScaledBitmapFromStream((InputStream) connection.getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Bitmap createScaledBitmapFromStream(InputStream s) {

        final BufferedInputStream is = new BufferedInputStream(s, 32 * 1024);
        try {
            final BitmapFactory.Options decodeBitmapOptions = new BitmapFactory.Options();


            final BitmapFactory.Options decodeBoundsOptions = new BitmapFactory.Options();
            decodeBoundsOptions.inJustDecodeBounds = true;
            is.mark(32 * 1024);
            BitmapFactory.decodeStream(is, null, decodeBoundsOptions);
            is.reset();

            final int originalWidth = decodeBoundsOptions.outWidth;
            final int originalHeight = decodeBoundsOptions.outHeight;
            
            decodeBitmapOptions.inSampleSize = Math.max(1, Math.min(originalWidth / IMAGE_WIDTH, originalHeight / IMAGE_HEIGHT));


            return BitmapFactory.decodeStream(is, null, decodeBitmapOptions);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                is.close();
            } catch (IOException ignored) {
            }
        }

    }

    @Override
    protected Bitmap doInBackground(String... params) {
        return getBitmapFromURL(mItem.getUrl());
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (bitmap != null) {
            mItem.setImage(bitmap);
        }else {
            mItem.setImage(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.nophoto));
        }
            Intent intent = new Intent();
            intent.setAction(MainActivity.DOWNLOAD_IMAGE_FILTER);
            mContext.sendBroadcast(intent);


    }

}
