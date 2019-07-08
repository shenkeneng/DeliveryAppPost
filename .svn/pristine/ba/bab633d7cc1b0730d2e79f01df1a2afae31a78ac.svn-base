package com.frxs.delivery.service.apkUpdate;

import android.app.DownloadManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2017/03/16
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class DownloadObserver extends ContentObserver {
    private Handler handler;
    private DownloadManager downloadManager;
    private long downloadId;
    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public DownloadObserver(Handler handler, DownloadManager downloadManager, long downloadId) {
        super(handler);
        this.handler = handler;
        this.downloadManager = downloadManager;
        this.downloadId = downloadId;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        updateProgress();
    }

    private void updateProgress() {
        DownloadManager.Query query = new DownloadManager.Query();
        Cursor cursor = downloadManager.query(query.setFilterById(downloadId));
        if (null != cursor && cursor.moveToFirst()) {
            try {
                int curSize = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                int totalSize = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                handler.sendMessage(handler.obtainMessage(0, curSize, totalSize, status));
            } finally {
                if (null != cursor) {
                    cursor.close();
                }
            }
        }
    }
}
