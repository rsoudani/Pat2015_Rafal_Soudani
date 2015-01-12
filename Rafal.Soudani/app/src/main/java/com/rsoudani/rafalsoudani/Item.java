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
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 *  Created by Rafal Soudani on 12-01-2015.
 */
public class Item implements Parcelable{
    private final String title;
    private final String desc;
    private final String url;
    private Bitmap image;

    public Item(String title, String desc, String url) {
        this.title = title;
        this.desc = desc;
        this.url = url;
        image = null;
    }

    private Item(Parcel in){
        title = in.readString();
        desc = in.readString();
        url = in.readString();
        image = in.readParcelable(null);
    }

    public void loadImage(Context context) {
        if (url != null && !url.equals("") && image == null) {
            new ImageDownloaderTask(context, this).execute(url);
        }
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(desc);
        dest.writeString(url);
        dest.writeParcelable(image, flags);
    }

    public static final Creator CREATOR = new Parcelable.Creator() {
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

}
