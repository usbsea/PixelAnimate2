/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ledpixelart.downloader.impl;

import com.ledpixelart.downloader.R;
import com.ledpixelart.downloader.Helpers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;

public class V14CustomNotification implements DownloadNotification.ICustomNotification {

    CharSequence mTitle;
    CharSequence mTicker;
    int mIcon;
    long mTotalKB = -1;
    long mCurrentKB = -1;
    long mTimeRemaining;
    PendingIntent mPendingIntent;

    @Override
    public void setIcon(int icon) {
        mIcon = icon;
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
    }

    @Override
    public void setTotalBytes(long totalBytes) {
        mTotalKB = totalBytes;
    }

    @Override
    public void setCurrentBytes(long currentBytes) {
        mCurrentKB = currentBytes;
    }

    void setProgress(Notification.Builder builder) {

    }

    @Override
    public Notification updateNotification(Context c) {
        // Create notification channel for Android 8+ (API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            String channelId = "download_channel";
            String channelName = "Download Progress";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription("Shows download progress for expansion files");
            channel.setShowBadge(false);
            notificationManager.createNotificationChannel(channel);
        }
        
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(c, "download_channel");
        } else {
            builder = new Notification.Builder(c);
        }
        
        builder.setContentTitle(mTitle);
        if (mTotalKB > 0 && -1 != mCurrentKB) {
            builder.setProgress((int) (mTotalKB >> 8), (int) (mCurrentKB >> 8), false);
        } else {
            builder.setProgress(0, 0, true);
        }
        builder.setContentText(Helpers.getDownloadProgressString(mCurrentKB, mTotalKB));
        builder.setContentInfo(c.getString(R.string.time_remaining_notification,
                Helpers.getTimeRemaining(mTimeRemaining)));
        if (mIcon != 0) {
            builder.setSmallIcon(mIcon);
        } else {
            int iconResource = android.R.drawable.stat_sys_download;
            builder.setSmallIcon(iconResource);
        }
        builder.setOngoing(true);
        builder.setTicker(mTicker);
        builder.setContentIntent(mPendingIntent);
        builder.setOnlyAlertOnce(true);

        return builder.getNotification();
    }

    @Override
    public void setPendingIntent(PendingIntent contentIntent) {
        mPendingIntent = contentIntent;
    }

    @Override
    public void setTicker(CharSequence ticker) {
        mTicker = ticker;
    }

    @Override
    public void setTimeRemaining(long timeRemaining) {
        mTimeRemaining = timeRemaining;
    }

}
