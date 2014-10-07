package com.orangejam.ischool.model;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by bjornorri on 07/10/14.
 */
public class Class {

    public static final String Lecture = "Fyrirlestur";
    public static final String Discussion = "Dæmatími";
    public static final String Assistance = "Viðtalstími";
    public static final String Exam = "Próf";
    public static final String Other = "Annað";

    public String courseName;
    public String type;
    public String location;
    public Calendar startDate;
    public Calendar endDate;

    public boolean isOver()
    {
        Calendar now = new GregorianCalendar();
        return now.after(this.endDate);
    }

    public boolean isNow()
    {
        Calendar now = new GregorianCalendar();
        return (now.after(this.startDate) && now.before(this.endDate));
    }
}
