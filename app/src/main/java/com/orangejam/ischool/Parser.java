package com.orangejam.ischool;

import android.util.Log;

import com.orangejam.ischool.model.Class;
import com.orangejam.ischool.model.Assignment;
import com.orangejam.ischool.model.Grade;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by bjornorri on 07/10/14.
 */
public class Parser {

    public static ArrayList<Class> parseClasses(String html) {
        ArrayList<Class> classes = new ArrayList<Class>();
        // Create a Jsoup Document object.
        Document page = Jsoup.parse(html);
        String query = "div.ruContentPage > center:eq(0) > table > tbody > tr";
        Elements nodes = page.select(query);

        if(nodes.size() > 0) {
            // Ignore irrelevant nodes (first two and the last)
            nodes.remove(nodes.size() - 1);
            for(int i = 0; i < 2; i++) {
                if(nodes.size() > 0) {
                    nodes.remove(0);
                } else {
                    // This should never happen. Return early.
                    return classes;
                }
            }

            // Extract the date row at the top of the timetable.
            Element dateRow = nodes.get(0);
            nodes.remove(0);

            // Put the date strings inside an array list.
            ArrayList<String> dateStrings = new ArrayList<String>();
            Elements dateColumns = dateRow.getElementsByTag("th");
            // Ignore first irrelevant column.
            dateColumns.remove(0);
            for(Element column : dateColumns) {
                String text = column.text();
                dateStrings.add(text);
            }

            // Create a date formatter to easily create Calendar objects.
            DateFormat formatter = new SimpleDateFormat("dd.MM.yyyyHH:mm");

            // Loop through the rows of the timetable.
            for(Element row : nodes) {
                // Split the row into columns.
                Elements columns = row.getElementsByTag("td");
                // Extract the column with the start and end time and then remove it.
                Element timeColumn = columns.remove(0);
                // Extract the strings for the start and end time.
                String timeText = timeColumn.text();
                String[] times = timeText.split("\u00a0"); // Split the string on &nbsp
                String startTime = times[0];
                String endTime = times[1];

                // Loop through the remaining columns of the row.
                for(int i = 0; i < columns.size(); i++) {
                    Element column = columns.get(i);
                    Elements classSpans = column.getElementsByTag("span");
                    if(classSpans.size() > 0) {
                        Calendar startDate = Calendar.getInstance();
                        Calendar endDate = Calendar.getInstance();
                        try {
                            startDate.setTime(formatter.parse(dateStrings.get(i) + startTime));
                            endDate.setTime(formatter.parse(dateStrings.get(i) + endTime));
                        } catch(ParseException e) {
                            Log.d("Parser", "ParseException", e);
                        }

                        // Create all the classes in this box.
                        for(Element classSpan : classSpans) {
                            String infoString = classSpan.attr("title");
                            String[] info = infoString.split("\n");
                            // Get the course name.
                            String courseName = "";
                            if(info.length > 0) {
                                courseName = info[0];
                            }
                            // Get the type.
                            String typeString = "";
                            if(info.length > 2) {
                                typeString = info[2];
                            }
                            Log.d("", "Type: " + typeString);
                            String type;
                            // Could get more information here.

                            // Map the type string to the appropriate type.
                            if(typeString.equals("Fyrirlestrar")) {
                                type = Class.Lecture;
                            } else if(typeString.equals("Dæmatímar")) {
                                type = Class.Discussion;
                            } else if(typeString.equals("Viðtalstímar")) {
                                type = Class.Assistance;
                            } else if(typeString.equals("Lokapróf")) {
                                type = Class.Exam;
                            } else {
                                type = Class.Other;
                            }
                            // Get the location of the class.
                            String location = "";
                            Elements links = classSpan.getElementsByTag("a");
                            if(links.size() > 0) {
                                Elements smalls = links.first().getElementsByTag("small");
                                if(smalls.size() > 0) {
                                    Element small = smalls.last();
                                    String smallText = small.text();
                                    String[] parts = smallText.split(", ");
                                    location = parts[parts.length - 1];
                                }

                            }
                            // Create the class and add it to the array list. Prevent duplicates.
                            Class c = new Class(courseName, type, location, startDate, endDate);
                            if(!classes.contains(c)) {
                                classes.add(c);
                            }
                        }
                    }
                }
            }
        }
        // Sort the classes based on their start date.
        Collections.sort(classes);
        return classes;
    }

    public static ArrayList<Assignment> parseAssignments(String html) {
        ArrayList<Assignment> assignments = new ArrayList<Assignment>();
        return assignments;
    }

    public static ArrayList<Grade> parseGrades(String html) {
        ArrayList<Grade> grades = new ArrayList<Grade>();
        return grades;
    }
}
