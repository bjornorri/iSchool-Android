package com.orangejam.ischool;


import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.File;
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
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "ISO-8859-1"));
            int aByte;
            while ((aByte = in.read()) != -1) {
                char c = (char)aByte;
                stringBuilder.append(c);
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

    /* Test the parseAssignments function for a typical assignments page. */
    public void testParseAssignmentsNormal() {
        String html = getHTMLFromAssets("assignments_and_grades.html");
        ArrayList<Assignment> assignments = Parser.parseAssignments(html);

        // Test if all assignments were parsed.
        assertEquals("There should be 2 assignments", 2, assignments.size());

        // Create a date formatter to easily create Calendar objects.
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        // Test assignment at index 0
        if(assignments.size() > 0) {
            Assignment assignment = assignments.get(0);
            Calendar dueDate = Calendar.getInstance();
            try {
                dueDate.setTime(formatter.parse("03.09.2014 23:59"));
            } catch(ParseException e) {
                fail("Could not parse date format string");
            }
            assertEquals("The assignment at index 0 should be in the course Tölvusamskipti", "Tölvusamskipti", assignment.courseName);
            assertEquals("The assignment at index 0 should be named hw1", "hw1", assignment.name);
            assertTrue("The assignment at index 0 should be handed in", assignment.handedIn);
            assertEquals("The assignment should have the due date 03.09.2014 23:59", dueDate, assignment.dueDate);
            assertEquals("The assignment should have the correct URL", "?page=Exe&ID=2.4&ViewMode=2&View=52&verkID=48726&fagid=26711", assignment.URL);
        } else {
            fail("Could not test assignment at index 0");
        }

        // Test assignment at index 1
        if(assignments.size() > 1) {
            Assignment assignment = assignments.get(1);
            Calendar dueDate = Calendar.getInstance();
            try {
                dueDate.setTime(formatter.parse("08.09.2014 23:50"));
            } catch(ParseException e) {
                fail("Could not parse date format string");
            }
            assertEquals("The assignment at index 1 should be in the course Stöðuvélar og reiknanleiki", "Stöðuvélar og reiknanleiki", assignment.courseName);
            assertEquals("The assignment at index 1 should be named Assignment 3", "Assignment 3", assignment.name);
            assertFalse("The assignment at index 1 should not be handed in", assignment.handedIn);
            assertEquals("The assignment should have the due date 08.09.2014 23:50", dueDate, assignment.dueDate);
            assertEquals("The assignment should have the correct URL", "?page=Exe&ID=2.4&ViewMode=2&View=52&verkID=48870&fagid=26706", assignment.URL);
        } else {
            fail("Could not test assignment at index 1");
        }
    }

    /* Test the parseAssignments function on a page that contains assignments but no grades. */
    public void testParseAssignments_OnlyAssignments() {
        String html = getHTMLFromAssets("only_assignments.html");
        ArrayList<Assignment> assignments = Parser.parseAssignments(html);

        // Test if all assignments were parsed.
        assertEquals("There should be 1 assignment", 1, assignments.size());

        // Create a date formatter to easily create Calendar objects.
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        // Test assignment at index 0.
        if(assignments.size() > 0) {
            Assignment assignment = assignments.get(0);
            Calendar dueDate = Calendar.getInstance();
            try {
                dueDate.setTime(formatter.parse("25.08.2014 23:00"));
            } catch(ParseException e) {
                fail("Could not parse date format string");
            }
            assertEquals("The assignment at index 0 should be in the course Stöðuvélar og reiknanleiki", "Stöðuvélar og reiknanleiki", assignment.courseName);
            assertEquals("The assignment at index 0 should be named Assignment 1", "Assignment 1", assignment.name);
            assertTrue("The assignment at index 0 should be handed in", assignment.handedIn);
            assertEquals("The assignment should have the due date 25.08.2014 23:00", dueDate, assignment.dueDate);
            assertEquals("The assignment should have the correct URL", "https://myschool.ru.is/myschool/?page=Exe&ID=2.4&ViewMode=2&View=52&verkID=48417&fagid=26706", assignment.URL);
        } else {
            fail("Could not test assignment at index 0");
        }
    }

    /* Test the parseAssignments function on a page that contains grades but no assignments. */
    public void testParseAssignments_OnlyGrades() {
        String html = getHTMLFromAssets("only_grades.html");
        ArrayList<Assignment> assignments = Parser.parseAssignments(html);

        // Test if all assignments were parsed.
        assertEquals("There should be 0 assignments", 0, assignments.size());
    }

    /* Test the parseGrades function for a typical page. */
    public void testParseGradesNormal() {
        String html = getHTMLFromAssets("assignments_and_grades2.html");
        ArrayList<Grade> grades = Parser.parseGrades(html);

        // Test if all grades were parsed.
        assertEquals("There should be 17 grades", 17, grades.size());

        // Test grade at index 12.
        if(grades.size() > 12) {
            Grade grade = grades.get(12);
            assertEquals("The grade at index 12 should be for the assignment hw1", "hw1", grade.assignmentName);
            assertEquals("The grade at index 12 should have the course name Tölvusamskipti", "Tölvusamskipti", grade.courseName);
            assertEquals("The grade at index 12 should have the value 10", 10, grade.grade);
            assertEquals("The rank for the grade at index 12 should start at 1", new Integer(1), grade.firstRank);
            assertEquals("The rank for the grade at index 12 should end at 3", new Integer(3), grade.lastRank);
            assertEquals("The feedback for the grade at index 12 should be the empty string", "", grade.feedback);
            assertEquals("The grade at index 12 should have the correct URL", "?Page=Exe&ID=2.4&ViewMode=2&fagid=26711&verkID=48726", grade.URL);
        } else {
            fail("Could not test grade at index 12");
        }
    }

    /* Test the parseGrades function for a page that contains grades but no assignments. */
    public void testParseGrades_OnlyGrades() {
        String html = getHTMLFromAssets("only_grades.html");
        ArrayList<Grade> grades = Parser.parseGrades(html);

        // Test if all grades were parsed.
        assertEquals("There should be 84 grades", 84, grades.size());

        // Test grade at index 70.
        if(grades.size() > 60) {
            Grade grade = grades.get(60);
            assertEquals("The grade at index 60 should be for the assignment Final Exam", "Final Exam", grade.assignmentName);
            assertEquals("The grade at index 60 should have the course name Tölvuöryggi", "Tölvuöryggi", grade.courseName);
            assertEquals("The grade at index 60 should have the value 10.7", 10.7, grade.grade);
            assertEquals("The rank for the grade at index 60 should start at 5", new Integer(5), grade.firstRank);
            assertEquals("The rank for the grade at index 60 should end at 5", new Integer(5), grade.lastRank);
            assertEquals("The feedback for the grade at index 60 should be Vel gert!", "Vel gert!", grade.feedback);
            assertEquals("The grade at index 60 should have the correct URL", "?Page=Exe&ID=2.4&ViewMode=2&fagid=25923&verkID=47391", grade.URL);
        } else {
            fail("Could not test grade at index 60");
        }
    }

    /* Test the parseGrades function for a page that contains assignments but no grades. */
    public void testParseGrades_OnlyAssignments() {
        String html = getHTMLFromAssets("only_assignments.html");
        ArrayList<Grade> grades = Parser.parseGrades(html);

        assertEquals("There should be no grades", 0, grades.size());
    }
}