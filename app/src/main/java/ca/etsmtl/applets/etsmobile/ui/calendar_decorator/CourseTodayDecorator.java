package ca.etsmtl.applets.etsmobile.ui.calendar_decorator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.style.StyleSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.ArrayList;
import java.util.HashSet;

import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by komlan on 28/03/16.
 */
public class CourseTodayDecorator implements DayViewDecorator {


    private HashSet<CalendarDay> dates;
    private Context context;

    public CourseTodayDecorator(Context context,ArrayList<CalendarDay>dates) {
        this.context = context;
        this.dates = new HashSet<>(dates);
    }




    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(CalendarDay.today()) && dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.calendar_course_today_circle));
        view.addSpan(new StyleSpan(Typeface.BOLD));
    }

}
