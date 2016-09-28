package sync.contact.com.contactsyncapp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

/**
 * Created by ravi on 28/09/16.
 */

public class ContactProvider extends ContentProvider {

    public static ContactDatabase mDatabaseHelper;

    private static final String AUTHORITY = DBUtils.CONTENT_AUTHORITY;
    public static final int CONTACTS = 101;
    public static final int CONTACTS_ID = 102;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHORITY, Contact.PATH, CONTACTS);
        sUriMatcher.addURI(AUTHORITY, Contact.PATH+"/*", CONTACTS_ID);
    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new ContactDatabase(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        SelectionBuilder builder = new SelectionBuilder();
        int uriMatch = sUriMatcher.match(uri);
        String id = "";
        Cursor cursor = null;
        Context context = getContext();
        switch (uriMatch) {
            case CONTACTS_ID:
                id = uri.getLastPathSegment();
                builder.where(BaseColumns._ID + "=?", id);
            case CONTACTS:
                builder.table(Contact.TABLE_NAME).where(selection, selectionArgs);
                break;
        }
        cursor = builder.query(db, projection, sortOrder);
        cursor.setNotificationUri(context.getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTACTS:
                return Contact.CONTENT_TYPE;
            case CONTACTS_ID:
                return Contact.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        assert db != null;
        final int match = sUriMatcher.match(uri);
        Uri result = null;
        long id;
        switch (match) {
            case CONTACTS:
                id = db.insertOrThrow(Contact.TABLE_NAME, null, contentValues);
                result = Uri.parse(Contact.CONTENT_URI + "/" + id);
                break;

            case CONTACTS_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return result;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SelectionBuilder builder = new SelectionBuilder();
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count;
        String id;
        switch (match) {
            case CONTACTS:
                count = builder.table(Contact.TABLE_NAME).where(selection, selectionArgs).delete(db);
                break;
            case CONTACTS_ID:
                id = uri.getLastPathSegment();
                count = builder.table(Contact.TABLE_NAME).where(BaseColumns._ID + "=?", id).where(selection, selectionArgs).delete(db);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SelectionBuilder builder = new SelectionBuilder();
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count;
        String id;
        switch (match) {
            case CONTACTS:
                count = builder.table(Contact.TABLE_NAME).where(selection, selectionArgs).update(db, values);
                break;
            case CONTACTS_ID:
                id = uri.getLastPathSegment();
                count = builder.table(Contact.TABLE_NAME).where(BaseColumns._ID + "=?", id).where(selection, selectionArgs).update(db, values);
                break;
            default:

                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return count;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        String tableName = null;
        switch (sUriMatcher.match(uri)) {
            case CONTACTS:
                tableName = Contact.TABLE_NAME;
                break;

            case CONTACTS_ID:
                throw new UnsupportedOperationException("Bulk Insert not supported on URI: " + uri);

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return insertBulkWithNotifyChange(db, tableName, values, uri);
    }

    private int insertBulkWithNotifyChange(SQLiteDatabase db, String tableName, ContentValues[] allValues, Uri uri) {
        int rowsAdded = 0;
        long rowId = 0;
        ContentValues values;
        try {
            db.beginTransaction();

            for (ContentValues initialValues : allValues) {
                values = initialValues == null ? new ContentValues() : new ContentValues(initialValues);

                rowId = db.insert(tableName, null, values);
                if (rowId > 0) {
                    rowsAdded++;
                }
            }

            db.setTransactionSuccessful();
            getContext().getContentResolver().notifyChange(uri, null);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            db.endTransaction();
        }

        return rowsAdded;
    }
}
