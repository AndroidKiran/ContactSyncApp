package sync.contact.com.contactsyncapp;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.SparseArray;

import static sync.contact.com.contactsyncapp.DBUtils.COMMA_SEP;
import static sync.contact.com.contactsyncapp.DBUtils.TYPE_INTEGER;
import static sync.contact.com.contactsyncapp.DBUtils.TYPE_TEXT;
import static sync.contact.com.contactsyncapp.DBUtils.UNIQUE;

/**
 * Created by ravi on 28/09/16.
 */

public class Contact extends BaseEntity {

    public static final String TABLE_NAME = "contacts";
    public static final String PATH = "contacts";
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.contact";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.contact";
    public static final Uri CONTENT_URI = DBUtils.BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();


    public static final class ContactColumns implements BaseColumns {

        public static final String NAME = "name";
        public static final String PHONE_NUM = "phone_num";
        public static final String RANK = "rank";

        public static final String [] ColumnNames = {
                _ID,
                NAME,
                PHONE_NUM,
                RANK
        };

    }

    private static final SparseArray<String> ColumnsMap = new SparseArray<String> ();

    static{
        ColumnsMap.append(0, ContactColumns._ID);
        ColumnsMap.append(1, ContactColumns.PHONE_NUM);
        ColumnsMap.append(2, ContactColumns.NAME);
        ColumnsMap.append(3, ContactColumns.RANK);
    }

    public static final String SQL_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    ContactColumns._ID + TYPE_INTEGER + " PRIMARY KEY" + COMMA_SEP +
                    ContactColumns.PHONE_NUM + TYPE_TEXT + UNIQUE + COMMA_SEP +
                    ContactColumns.NAME + TYPE_TEXT + COMMA_SEP +
                    ContactColumns.RANK + TYPE_INTEGER +
                    ")";

    public static final String SQL_DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;

//    public String id;
    public String name;
    public String phoneNum;
    public int rank;

    @Override
    protected Object get(String value) {
        switch (ColumnsMap.indexOfValue(value)) {
            case 1:
                return phoneNum;
            case 2:
                return name;
            case 3:
                return rank;
        }
        return null;
    }
}
