package com.chenjianhong.floatingwindow.library;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * Created by 陈剑虹 on 16-9-11.
 */
public class FloatingListWindow extends FloatingWindow {

    private ListView mListView;

    public FloatingListWindow(Context context) {
        super(context);
        View rootView = LayoutInflater.from(context).inflate(R.layout.view_list, null);
        mListView = (ListView) rootView.findViewById(R.id.lv_view);
        setContentView(rootView);
    }

    public void setListAdapter(BaseAdapter adapter) {
        if (adapter != null)
            mListView.setAdapter(adapter);
    }

}
