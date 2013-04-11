package com.yulingtech.lycommon.widget;
 
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ListView;
 
public class OverScrollListView extends ListView{
    private static final int MAX_Y_OVERSCROLL_DISTANCE = 50;
     
    private Context mContext;
    private int mMaxYOverscrollDistance;
     
    public OverScrollListView(Context context){
        super(context);
        mContext = context;
        initBounceListView();
    }
     
    public OverScrollListView(Context context, AttributeSet attrs){
        super(context, attrs);
        mContext = context;
        initBounceListView();
    }
     
    public OverScrollListView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        mContext = context;
        initBounceListView();
    }
     
    private void initBounceListView(){
        //get the density of the screen and do some maths with it on the max overscroll distance
        //variable so that you get similar behaviors no matter what the screen size
         
        final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
            final float density = metrics.density;
         
        mMaxYOverscrollDistance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);
    }
     
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent){ 
        //This is where the magic happens, we have replaced the incoming maxOverScrollY with our own custom variable mMaxYOverscrollDistance; 
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, mMaxYOverscrollDistance, isTouchEvent);  
    }
     
}