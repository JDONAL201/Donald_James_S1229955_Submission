package org.james.donald.s1229955.gcu.trafficjam;

import android.view.View;
import android.widget.LinearLayout;
/*
 * STUDENT NAME: JAMES DONALD
 * METRIC NUMBER : S1229955
 * COURSE: COMPUTER GAMES SOFTWARE DEVELOPMENT (YEAR 4)
 * */
public class ViewAnimationWrapper
{
    private View view;

    public ViewAnimationWrapper(View view)
    {
        if(view.getLayoutParams() instanceof LinearLayout.LayoutParams)
        {
            this.view = view;
        }
        else
        {
            throw  new IllegalArgumentException("Parent needs to be Linear");
        }
    }
    public void setWeight(float weight)
    {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.weight = weight;
        view.getParent().requestLayout();
    }
    public float getWeight()
    {
        return ((LinearLayout.LayoutParams)view.getLayoutParams()).weight;
    }

}
