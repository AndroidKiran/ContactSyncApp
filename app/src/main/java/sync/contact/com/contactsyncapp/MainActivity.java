package sync.contact.com.contactsyncapp;

import android.database.Cursor;
import android.os.Bundle;

public class MainActivity extends ContactActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public void swapCursor(Cursor cursor) {

    }
}
