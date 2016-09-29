package sync.contact.com.contactsyncapp;

import android.database.Cursor;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ravi on 29/09/16.
 */

public class ContactAdapter extends CursorRecyclerViewAdapter<ContactAdapter.ContactViewHolder> {


    private int rankColumnIndex;
    private int nameColumnIndex;
    private int phoneColumnIndex;
    private OnItemClickListener mItemClickListener;

    public ContactAdapter(Cursor cursor) {
        super(cursor);
        initIndex(cursor);
    }

    private void initIndex(Cursor cursor) {
        if (cursor != null && cursor != getCursor()) {
            rankColumnIndex = cursor.getColumnIndex(Contact.ContactColumns.RANK);
            nameColumnIndex = cursor.getColumnIndex(Contact.ContactColumns.NAME);
            phoneColumnIndex = cursor.getColumnIndex(Contact.ContactColumns.PHONE_NUM);
        }
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        initIndex(newCursor);
        return super.swapCursor(newCursor);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder viewHolder, Cursor cursor) {
        Contact contact = new Contact();
        contact.name = cursor.getString(nameColumnIndex);
        contact.phoneNum = cursor.getString(phoneColumnIndex);
        contact.rank = cursor.getInt(rankColumnIndex);

        viewHolder.setContactItem(contact);
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_contacts, parent, false);
        return new ContactViewHolder(itemView);
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final AppCompatTextView mRank;
        private final AppCompatTextView mName;
        private final AppCompatTextView mNumber;

        public ContactViewHolder(View itemView) {
            super(itemView);
            mRank = (AppCompatTextView) itemView.findViewById(R.id.rank_badge);
            mName = (AppCompatTextView) itemView.findViewById(R.id.name);
            mNumber = (AppCompatTextView) itemView.findViewById(R.id.num);
            itemView.setOnClickListener(this);
        }

        public void setContactItem(Contact contact) {
            mRank.setText(String.valueOf(contact.rank));
            mName.setText(contact.name);
            mNumber.setText(contact.phoneNum);

        }

        @Override
        public void onClick(View view) {
            if (getCursor().moveToPosition(getLayoutPosition())){
                Contact contact = new Contact();
                contact.name = getCursor().getString(nameColumnIndex);
                contact.phoneNum = getCursor().getString(phoneColumnIndex);
                contact.rank = getCursor().getInt(rankColumnIndex);
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(view, contact);
                }
            }
        }
    }


    public interface OnItemClickListener {
        void onItemClick(View view, Contact contact);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}
