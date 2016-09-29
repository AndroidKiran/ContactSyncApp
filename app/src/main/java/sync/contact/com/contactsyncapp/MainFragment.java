package sync.contact.com.contactsyncapp;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainFragment extends ContactFragment implements ContactAdapter.OnItemClickListener {

    private RecyclerView contactRecyclerView;
    private ContactAdapter contactAdapter;
    private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
    public static final String TAG =  MainFragment.class.getSimpleName();


    public static MainFragment show(FragmentManager fragmentManager){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MainFragment mainFragment = new MainFragment();
        fragmentTransaction.replace(R.id.container, mainFragment, TAG);
        fragmentTransaction.commitAllowingStateLoss();
        return mainFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initControls(view);
        initAdapter();
    }

    public void initControls(View view){
        contactRecyclerView = (RecyclerView) view.findViewById(R.id.contact_recycler_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        contactRecyclerView.setHasFixedSize(true);
        contactRecyclerView.setLayoutManager(layoutManager);

        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(layoutManager) {


            @Override
            public void onLoadMore(int currentItem) {
                String appContactOrder = Contact.ContactColumns.RANK + " COLLATE LOCALIZED DESC" + " LIMIT " + offSet + " , " + pagLimit;
                UpdateQueryHandler updateQueryHandler = new UpdateQueryHandler(getActivity().getContentResolver());
                updateQueryHandler.startQuery(2, null, Contact.CONTENT_URI, CONTACTS_PROJECTION, null, null, appContactOrder);
            }
        };

        contactRecyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
    }

    public void initAdapter(){
        contactAdapter = new ContactAdapter(null);
        contactAdapter.setOnItemClickListener(this);
        contactRecyclerView.setAdapter(contactAdapter);
    }


    @Override
    public void swapCursor(Cursor cursor) {
        endlessRecyclerOnScrollListener.reset();
        contactAdapter.swapCursor(cursor);
    }

    @Override
    public void onItemClick(View view, Contact contact) {
        contact.rank +=  1;
        String selection = Contact.ContactColumns.PHONE_NUM + " = ?";
        String[] selectionArgs = new String[]{contact.phoneNum};
        UpdateQueryHandler updateQueryHandler = new UpdateQueryHandler(getActivity().getContentResolver());
        if (updateQueryHandler != null) {
            updateQueryHandler.startUpdate(0, contact, Contact.CONTENT_URI, contact.getContenValues(Contact.ContactColumns.ColumnNames), selection, selectionArgs);
        }
    }


    public class UpdateQueryHandler extends AsyncQueryHandler {

        public UpdateQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onUpdateComplete(int token, Object cookie, int result) {
            System.out.println("result ==== "+ result);
            super.onUpdateComplete(token, cookie, result);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            offSet = contactAdapter.getCursor().getCount();
            swapCursor(new MergeCursor(new Cursor[]{contactAdapter.getCursor(), cursor}));
            super.onQueryComplete(token, cookie, cursor);
        }
    }
}
