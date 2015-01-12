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

import android.widget.AbsListView;

/**
 *  Created by Rafal Soudani on 12-01-2015.
 */
class EndlessScrollListener implements AbsListView.OnScrollListener {

    private final MainActivity mainActivity;
    private static int previousTotal = 0;

    private static boolean loading = true;
    private static boolean lastPage = false;

    private static int fileNumber = 0;

    public EndlessScrollListener(MainActivity activity) {
        this.mainActivity = activity;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem)) {
            if (!lastPage) {
                fileNumber++;
                loading = true;
                MainActivity.setFileName("page_" + fileNumber + ".json");
                mainActivity.startDownload();
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    public static void resetListener() {
        fileNumber = 0;
        previousTotal = 0;
    }

    public static void setLoadingToFalse() {
        if (loading) {
            loading = false;
            fileNumber--;
        }
    }

    public static void setLoadingTrue() {
        loading = true;
    }

    public static void setLastPage() {
        lastPage = true;
    }
}
