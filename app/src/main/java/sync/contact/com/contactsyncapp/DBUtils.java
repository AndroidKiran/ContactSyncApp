package sync.contact.com.contactsyncapp;

import android.net.Uri;
import android.text.TextUtils;

/**
 * Created by ravi on 28/09/16.
 */

public class DBUtils {

    public static final String TYPE_TEXT = " TEXT";
    public static final String TYPE_INTEGER = " INTEGER";
    public static final String TYPE_BOOLEAN = " BOOLEAN";
    public static final String TYPE_FLOAT = " FLOAT";
    public static final String TYPE_DOUBLE = " DOUBLE PRECISION";
    public static final String TYPE_REAL = " REAL";

    public static final String COMMA_SEP = ",";
    public static final String SEMI_COLON = ";";
    public static final String UNIQUE = " UNIQUE";
    public static final String CONTENT_AUTHORITY = "sync.contact.com.contactsyncapp.provider.data";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String DOT = ".";
    public static final String CLOSING_BRACES = ")";
    public static final String OPENING_BRACES = " (";
    public static final String PRIMARY_KEY = " PRIMARY KEY";

    public static final String GROUP_CONCAT_FORMAT = " GROUP_CONCAT(%s) ";
    public static final String DROP_COLUMN = " DROP COLUMN ";
    public static final String IF_NULL_FORMAT = " IFNULL(%s,%s) ";
    public static final String GROUP_CONCAT_WITH_DELIMITER_FORMAT = " GROUP_CONCAT(%s,%s) ";
    public static final String DISTINCT_FORMAT = " DISTINCT(%s) ";
    public static final String GROUP_CONCAT_DELIMITER = "'`<|~'";
    public static final String GROUP_CONCAT_DELIMITER_REGX = "\\`\\<\\|\\~";
    //public static final String GROUP_CONCAT_DELIMITER_COMMA = ",";
    public static final String SPACE = "''";
    public static final String COUNT = "COUNT";
    public static final String STAR = "*";


    private DBUtils() {
    }

    public static String getDbFormattedValue(String value) {
        // Try to convert booleans into 0 or 1
        if (!TextUtils.isEmpty(value) && (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))) {
            return value.equalsIgnoreCase("true") ? "1" : "0";
        }
        return value;
    }



    public static String getGroupConcatProjection(String columnName) {
        return String.format(GROUP_CONCAT_FORMAT, columnName);
    }
    public static String getDistinctProjection(String columnName) {
        return String.format(DISTINCT_FORMAT, columnName);
    }

    public static String getIfNullProjection(String columnName, String returnNullValue) {
        return String.format(IF_NULL_FORMAT, columnName, returnNullValue);
    }

    public static String getGroupConcatProjection(String columnName, String delimiter) {
        return String.format(GROUP_CONCAT_WITH_DELIMITER_FORMAT, columnName, delimiter);
    }


    public static String getStingElementFromArray (String[] array, int index){
        if(array != null && array.length > index ){
            return  array[index];
        }
        return "";
    }

    public static int getIntegerElementFromArray (String[] array, int index){
        if(array != null && array.length > index ){
            if(!TextUtils.isEmpty(array[index]))
                return  Integer.parseInt(array[index]);
        }
        return 0;
    }

    public static String[] getSplitArray(String concatString,String spliter){
        if (!TextUtils.isEmpty(concatString)) {
            String[] mArray = concatString.split(spliter);
            if (mArray != null && mArray.length > 0) {
                return mArray;
            }
        }
        return null;
    }
}
