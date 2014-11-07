iSchool
=======
The app fetches data from MySchool, parses it and displays it to the user in a more user friendly way than MySchool does on mobile platforms. The app displays the classes for the week, the assignments to be handed in and grades for previous assignments. Below we discuss some of the Android features that we used.

Data fetching
=============
All data is fetched from pages on MySchool. The pages are loaded asynchronously and then parsed by our parser. The old MyRU app often crashes because of faulty parsing logic so this time we did some heavy unit testing on the parsing functions. The unit tests are included in the project. When a page has been parsed the data is stored in a data store that broadcasts a notification that the data has been updated. The receivers of this broadcast message then act accordingly.

Fragments and master-detail flow
================================
We used a TabsPagerAdapter to create the tabs and each tab contains a fragment. The timetable view is just a list but the assignments and grades tabs have master-detail flows. On a small device, a new activity is created whenever an item is selected but there is another layout for tablets (sw600dp) that allows both the master fragment and the detail fragment to be displayed at the same time.
The detail views for the assignments and grades are web views that show the MySchool page for the given assignment/grade. We were forced to used web views simply because it is impossible to parse the data on the detail MySchool pages. However, we made the webviews execute some JavaScript when they are loaded to make them look better on mobile devices.

Locality
========
We took advantage of being able to have multiple string resource files and created one in Icelandic and one in English. The user can easily switch languages with a single click in the options menu.

Widget
======
We created a home screen widget to go with the app. The widget displays the current class, the next class today or the first class tomorrow, depending on if there are any classes going on now/today/tomorrow.
Widgets need to be updated and the minimum update time interval is 15 or 30 minutes. Because one class can end and another one start in just 5 minutes, we created a service that updates the widget every minute.

Notes
=====
*You might want to review the app sooner than later because soon there will be no classes and no assignments to view.
*The widget only shows classes that are either today or tomorrow. If the widget is empty, it is because there are no classes at least until the day after tomorrow (for example on a Saturday).
