package com.akshit.akshitsfdc.allpuranasinhindi.contracts;

import android.provider.BaseColumns;

import java.util.ArrayList;

public class BooksContract {

    private BooksContract() {
    }

    public static final class SoftBookEntry implements BaseColumns {

        public static final String TABLE_NAME_OFFLINE = "offlineList";
        public static final String TRIGGER_NAME_AFTER_UPDATE = "afterUpdateTrigger";

        public static final String PIC_URL = "picUrl";
        public static final String NAME = "name";
        public static final String LANGUAGE = "language";
        public static final String FREE = "free";
        public static final String PRICE = "price";
        public static final String DOWNLOAD_URL = "downloadUrl";
        public static final String DESCRIPTION = "description";
        public static final String PAGES = "pages";
        public static final String BOOK_ID = "bookId";
        public static final String VIDEO_OPTION = "videoOption";
        public static final String PRIORITY = "priority";
        public static final String TYPE = "type";
        public static final String CATEGORY = "category";
        public static final String ADDED_TIME = "addedTime";
        public static final String READING_PAGE = "readingPage";
        public static final String IS_FAVORITE = "isFavorite";
        public static final String BOOK_URI = "bookUri";
        public static final String COVER_URI = "coverUri";
        public static final String COLUMN_TIMESTAMP = "timestamp";

    }
}
