package sync.contact.com.contactsyncapp;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 * Created by ravi on 29/09/16.
 */

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener{
    public static String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private static final int DEFAULT_VISIBLE_THRESHOLD = 50;
    private int visibleThreshold; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private int current_page = 1;

    private LinearLayoutManager mLinearLayoutManager;

    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this(linearLayoutManager, DEFAULT_VISIBLE_THRESHOLD);
    }

    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager, int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        if (loading) {

            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }

        if (!loading && (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + visibleThreshold)) {
            // End has been reached

            // Do something
            current_page++;

            onLoadMore(current_page);

            loading = true;
        } else {
            Log.d("Contacts: " ,""+ loading);
            Log.d("Contacts: " ,""+ (totalItemCount - visibleItemCount));
            Log.d("Contacts: " ,""+ (firstVisibleItem + visibleThreshold));
        }
    }

    public abstract void onLoadMore(int currentItem);

    public void reset() {
        this.loading = false;
        previousTotal = 0;
        current_page = 1;
    }
}
