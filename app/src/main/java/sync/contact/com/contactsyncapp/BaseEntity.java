package sync.contact.com.contactsyncapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

/**
 * Created by ravi on 28/09/16.
 */

public abstract class BaseEntity {

    public final ContentValues getContenValues(String[] columnNames){
        ContentValues contentValues = new ContentValues();
        for (String string : columnNames) {
            Object value = get(string);
            if(null == value){
                if(string.compareToIgnoreCase(BaseColumns._ID) == 0){
                    continue;
                }
                else{
                    contentValues.putNull(string);
                }
            }
            else if(value instanceof String){
                contentValues.put(string, (String)value);
            }
            else if(value instanceof Integer){
                contentValues.put(string, (Integer)value);
            }
            else if(value instanceof Long){
                contentValues.put(string, (Long)value);
            }
            else if(value instanceof Double){
                contentValues.put(string, (Double)value);
            }
            else if(value instanceof Boolean){
                contentValues.put(string, (Boolean)value);
            }
        }
        return contentValues;
    }

    protected abstract Object get(String value);

    public static String getCursorString(Cursor cursor, String columnName) {
        final int index = cursor.getColumnIndex(columnName);
        return (index != -1) ? cursor.getString(index) : null;
    }

    /**
     * Missing or null values are returned as 0.
     */
    public static int getCursorInt(Cursor cursor, String columnName) {
        final int index = cursor.getColumnIndex(columnName);
        return (index != -1) ? cursor.getInt(index) : 0;
    }
}
