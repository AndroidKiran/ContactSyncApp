package sync.contact.com.contactsyncapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ravi on 28/09/16.
 */

public abstract class ContactActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private Loader<Cursor> mContactLoader;
    private Loader<Cursor> mDbLoader;


    private String DISPLAY_NAME_COMPAT = Build.VERSION.SDK_INT
            >= Build.VERSION_CODES.HONEYCOMB ?
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY :
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;


    private final String[] CONTACTS_SUMMARY_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone._ID,
            DISPLAY_NAME_COMPAT,
            ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };

    private final String[] CONTACTS_PROJECTION = new String[]{
            Contact.ContactColumns._ID,
            Contact.ContactColumns.NAME,
            Contact.ContactColumns.PHONE_NUM,
            Contact.ContactColumns.RANK
    };

    public abstract void swapCursor(Cursor cursor);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContactLoader();
        initDbLoader();
    }


    public void initContactLoader() {
        mContactLoader = getSupportLoaderManager().getLoader(1);
        if (mContactLoader != null && !mContactLoader.isReset()) {
            getSupportLoaderManager().restartLoader(1, null, this);
        } else {
            getSupportLoaderManager().initLoader(1, null, this);
        }
    }

    public void initDbLoader() {
        mDbLoader = getSupportLoaderManager().getLoader(2);
        if (mDbLoader != null && !mDbLoader.isReset()) {
            getSupportLoaderManager().restartLoader(2, null, this);
        } else {
            getSupportLoaderManager().initLoader(2, null, this);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case 1:
            Uri phoneContactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String selection = "((" + DISPLAY_NAME_COMPAT + " NOTNULL) AND ("
                    + ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + "=1) AND ("
                    + DISPLAY_NAME_COMPAT + " != '' ))";
            String phoneContactSortOrder = DISPLAY_NAME_COMPAT + " COLLATE LOCALIZED ASC";
            return new CursorLoader(this, phoneContactUri, CONTACTS_SUMMARY_PROJECTION, selection, null, phoneContactSortOrder);

            case 2:
                Uri appContactUri = Contact.CONTENT_URI;
                String appContactOrder = Contact.ContactColumns.RANK + " COLLATE LOCALIZED DESC";
                return new CursorLoader(this, appContactUri, CONTACTS_PROJECTION, null, null, appContactOrder);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0) {
            switch (loader.getId()){
                case 1:
                    List<ContentValues> contactList = new ArrayList<ContentValues>();
                    data.moveToFirst();
                    do {
                        Contact newContact = new Contact();
//                newContact.id = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
                        newContact.name = data.getString(data.getColumnIndex(DISPLAY_NAME_COMPAT));
                        newContact.phoneNum = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contactList.add(newContact.getContenValues(Contact.ContactColumns.ColumnNames));
                    } while (data.moveToNext());
                    System.out.println("size ======= " + contactList.size());

                    if (contactList.size() != 0) {
                        ContentValues[] contactContentValues = contactList.toArray(new ContentValues[0]);
                        int count = getContentResolver().bulkInsert(Contact.CONTENT_URI, contactContentValues);
                        System.out.println("count ======= " + count);
                    }
                    break;

                case 2:
                    swapCursor(data);
                    break;
            }

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
