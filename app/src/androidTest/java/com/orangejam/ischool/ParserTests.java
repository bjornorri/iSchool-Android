package com.orangejam.ischool;


import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.orangejam.ischool.model.Class;
import com.orangejam.ischool.model.Assignment;
import com.orangejam.ischool.model.Grade;

/**
 * Created by bjornorri on 07/10/14.
 */
public class ParserTests extends ApplicationTestCase<Application> {
    public ParserTests() {
        super(Application.class);
    }

    private String getHTMLFromAssets(String pageName) {
        String page = null;
        try {
            InputStream inputStream = getContext().getAssets().open(pageName);
            StringBuilder stringBuilder =  new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String str;
            while ((str = in.readLine()) != null) {
                stringBuilder.append(str);
            }
            inputStream.close();
            page = stringBuilder.toString();
        } catch(IOException e) {
            fail("getPageFromAssets could not open file: " + pageName);
        }
        return page;
    }

    /* Test the parseTimetable function for a typical timetable. */
    public void testParseTimetableNormal() {
        // Get the test data timetable page.
        String pageHTML = getHTMLFromAssets("timetable_normal.html");
        // Parse the classes from the test page.
        ArrayList<Class> classes = Parser.parseClasses(pageHTML);

        // There should be 20 classes.
        assertEquals("There should be 20 classes", 20, classes.size());

        // Create a date formatter to easily create Calendar objects.
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        // Test class at index 6
        if(classes.size() > 6) {
            Calendar startDate = Calendar.getInstance();
            Calendar endDate = Calendar.getInstance();
            try {
                startDate.setTime(formatter.parse("02.09.2014 13:10"));
                endDate.setTime(formatter.parse("02.09.2014 13:55"));
            } catch(ParseException e) {
                fail("Could not parse date format string");
            }
            Class c = classes.get(6);
            assertEquals("The course name of the class at index 6 should be Tölvusamskipti", "Tölvusamskipti", c.courseName);
            assertEquals("The class at index 6 should be a lecture", Class.Lecture, c.type);
            assertEquals("The location of the class at index 6 should be V201", "V201", c.location);
            assertEquals("The class at index 6 should start at 02.09.2014 13:10", startDate, c.startDate);
            assertEquals("The class at index 6 should end at 02.09.2014 13:55", endDate, c.endDate);
        } else {
            fail("Could not test class at index 6");
        }

        // Test class at index 7
        if(classes.size() > 7) {
            Calendar startDate = Calendar.getInstance();
            Calendar endDate = Calendar.getInstance();
            try {
                startDate.setTime(formatter.parse("03.09.2014 13:10"));
                endDate.setTime(formatter.parse("03.09.2014 13:55"));
            } catch(ParseException e) {
                fail("Could not parse date format string");
            }
            Class c = classes.get(7);
            assertEquals("The course name of the class at index 6 should be Stærðfræði I", "Stærðfræði I", c.courseName);
            assertEquals("The class at index 6 should be a discussion class", Class.Discussion, c.type);
            assertEquals("The location of the class at index 6 should be V105", "V105", c.location);
            assertEquals("The class at index 6 should start at 03.09.2014 13:10", startDate, c.startDate);
            assertEquals("The class at index 7 should end at 03.09.2014 13:55", endDate, c.endDate);
        } else {
            fail("Could not test class at index 7");
        }
    }

    /* Test the parseTimetable function for a timetable during final exams. */
    public void testParseTimetableExams() {
        // Get the test data timetable page.
        String pageHTML = getHTMLFromAssets("timetable_exams.html");
        // Parse the classes from the test page.
        ArrayList<Class> classes = Parser.parseClasses(pageHTML);

        // There should be 8 classes.
        assertEquals("There should be 8 classes (exams)", 8, classes.size());

        // Create a date formatter to easily create Calendar objects.
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        // Test class at index 2.
        if(classes.size() > 2) {
            Calendar startDate = Calendar.getInstance();
            Calendar endDate = Calendar.getInstance();
            try {
                startDate.setTime(formatter.parse("08.04.2014 15:45"));
                endDate.setTime(formatter.parse("08.04.2014 16:30"));
            } catch(ParseException e) {
                fail("Could not parse date format string");
            }
            Class c = classes.get(2);
            assertEquals("The course name of the class (exam) at index 2 should be Forritunarmál", "Forritunarmál", c.courseName);
            assertEquals("The class at index 2 should be an exam", Class.Exam, c.type);
            assertEquals("The location of the class at index 2 should be M201", "M201", c.location);
            assertEquals("The class at index 2 should start at 08.04.2014 15:45", startDate, c.startDate);
            assertEquals("The class at index 2 should end at 08.04.2014 16:30", endDate, c.endDate);
        } else {
            fail("Could not test class (exam) at index 2");
        }

        // Test class at index 6.
        if(classes.size() > 6) {
            Calendar startDate = Calendar.getInstance();
            Calendar endDate = Calendar.getInstance();
            try {
                startDate.setTime(formatter.parse("11.04.2014 15:45"));
                endDate.setTime(formatter.parse("11.04.2014 16:30"));
            } catch(ParseException e) {
                fail("Could not parse date format string");
            }
            Class c = classes.get(6);
            assertEquals("The course name of the class (exam) at index 6 should be Hugbúnaðarfræði II", "Hugbúnaðarfræði II", c.courseName);
            assertEquals("The class at index 6 should be an exam", Class.Exam, c.type);
            assertEquals("The location of the class at index 6 should be M101", "M101", c.location);
            assertEquals("The class at index 6 should start at 11.04.2014 15:45", startDate, c.startDate);
            assertEquals("The class at index 6 should end at 11.04.2014 16:30", endDate, c.endDate);
        } else {
            fail("Could not test class (exam) at index 6");
        }
    }
}