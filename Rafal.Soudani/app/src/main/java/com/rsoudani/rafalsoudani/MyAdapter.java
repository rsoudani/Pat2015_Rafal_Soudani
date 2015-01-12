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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 *  Created by Rafal Soudani on 10-01-2015.
 */
class MyAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;
    private final List<Item> mItems;

    public MyAdapter(Context context, List<Item> items){
        mInflater = LayoutInflater.from(context);
        mItems = items;
    }



    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.list_view_row, parent, false);
            holder = new ViewHolder();
            holder.mTitle = (TextView) convertView.findViewById(R.id.title);
            holder.mDesc = (TextView) convertView.findViewById(R.id.description);
            holder.mImage = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        Item item = mItems.get(position);
        holder.mTitle.setText(item.getTitle());
        holder.mDesc.setText(item.getDesc());

        if (item.getImage() != null){
            holder.mImage.setImageBitmap(item.getImage());
        } else {
            holder.mImage.setImageResource(R.drawable.nophoto);
        }

        return convertView;
    }

    private class ViewHolder {
        TextView mTitle;
        TextView mDesc;
        ImageView mImage;
    }
}
