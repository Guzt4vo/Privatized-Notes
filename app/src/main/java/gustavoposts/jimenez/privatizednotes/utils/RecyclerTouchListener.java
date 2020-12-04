package gustavoposts.jimenez.privatizednotes.utils;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


//this class implements interactivity with the recyclerview by opening a menu
//each time the user presses on a note for a long duration of time
public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener
{

    private ClickListener clicklistener;
    private GestureDetector gestureDetector;

    //this is where the motion event for the recycler view is processed
    public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener)
    {

        this.clicklistener = clickListener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){

            @Override
            public boolean onSingleTapUp(MotionEvent e)
            {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e)
            {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if(child != null && clickListener != null)
                {
                    clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e)
    {
        View child = rv.findChildViewUnder(e.getX(), e.getY());
        if(child != null && clicklistener != null && gestureDetector.onTouchEvent(e)){
            clicklistener.onClick(child, rv.getChildAdapterPosition(child));
        }

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e)
    {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept)
    {
    }

    public interface ClickListener
    {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }
}
