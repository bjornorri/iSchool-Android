package com.orangejam.ischool.modules;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import org.w3c.dom.Attr;

/**
 * Created by bjornorri on 02/11/14.
 */
public class RefreshLayout extends SwipeRefreshLayout {

    private ListView childView;

    public RefreshLayout(Context context) {
        super(context);
    }

    public RefreshLayout(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public void setChildView(ListView view) {
        childView = view;
    }

    @Override
    public boolean canChildScrollUp() {
        if(childView.getChildCount() == 0) {
            return false;
        } else {
            return childView.getChildAt(0).getTop() != 0;
        }
    }
}
