package sync.contact.com.contactsyncapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ravi on 28/09/16.
 */

public abstract class ContactFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


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

    protected final String[] CONTACTS_PROJECTION = new String[]{
            Contact.ContactColumns._ID,
            Contact.ContactColumns.NAME,
            Contact.ContactColumns.PHONE_NUM,
            Contact.ContactColumns.RANK
    };

    protected int offSet;
    protected int limit = 20;
    protected int pagLimit = 20;

    public abstract void swapCursor(Cursor cursor);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            limit = 20;
        } else {
            limit = savedInstanceState.getInt("LIMIT");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initContactLoader();
        initDbLoader();
    }


    public void initContactLoader() {
        mContactLoader = getLoaderManager().getLoader(1);
        if (mContactLoader != null && !mContactLoader.isReset()) {
            getLoaderManager().restartLoader(1, null, this);
        } else {
            getLoaderManager().initLoader(1, null, this);
        }
    }

    public void initDbLoader() {
        mDbLoader = getLoaderManager().getLoader(2);
        if (mDbLoader != null && !mDbLoader.isReset()) {
            getLoaderManager().restartLoader(2, null, this);
        } else {
            getLoaderManager().initLoader(2, null, this);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case 1:
                Uri phoneContactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String selection = "((" + DISPLAY_NAME_COMPAT + " NOTNULL) AND ("
                        + ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + "=1) AND ("
                        + DISPLAY_NAME_COMPAT + " != '' ))";
                String phoneContactSortOrder = DISPLAY_NAME_COMPAT + " COLLATE LOCALIZED ASC";
                return new CursorLoader(getActivity(), phoneContactUri, CONTACTS_SUMMARY_PROJECTION, selection, null, phoneContactSortOrder);

            case 2:
                Uri appContactUri = Contact.CONTENT_URI;
                String appContactOrder = Contact.ContactColumns.RANK + " COLLATE LOCALIZED DESC" + " LIMIT " + offSet + " , " + limit;
                return new CursorLoader(getActivity(), appContactUri, CONTACTS_PROJECTION, null, null, appContactOrder);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0) {
            switch (loader.getId()) {
                case 1:
                    List<ContentValues> contactList = new ArrayList<ContentValues>();
                    data.moveToFirst();
                    do {
                        Contact newContact = new Contact();
//                newContact.id = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
                        newContact.name = data.getString(data.getColumnIndex(DISPLAY_NAME_COMPAT));
                        newContact.phoneNum = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        newContact.rank = 0;
                        contactList.add(newContact.getContenValues(Contact.ContactColumns.ColumnNames));
                    } while (data.moveToNext());
                    System.out.println("size ======= " + contactList.size());

                    if (contactList.size() != 0) {
                        ContentValues[] contactContentValues = contactList.toArray(new ContentValues[0]);
                        System.out.println("count values ======= " + contactContentValues.length);
                        int count = getActivity().getContentResolver().bulkInsert(Contact.CONTENT_URI, contactContentValues);
                        System.out.println("count ======= " + count);
                    }
                    break;

                case 2:
                    offSet = data.getCount();
                    swapCursor(data);
                    break;
            }

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        swapCursor(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("LIMIT", offSet);
        super.onSaveInstanceState(outState);
    }
}
