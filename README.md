The web application for booking of metting rooms
=============================

The app is a calendar where you can book meetings for a particular day and time.

The minimum booking period is 30 minutes, maximum 24 hours.

Events can be created by all users, but only those who created them can delete and edit them.

In the calendar you can scroll through the weeks to the future and to the past.

REQUIREMENTS
------------

* JAVA 8 and more
* Postgres 10

INSTALLATION
------------

Create new Postgres database and change config for connection in file application.properties:

* spring.datasource.url=
* spring.datasource.username=
* spring.datasource.password=

The application uses the Flyaway migration. 
The database will perform all migrations and fill with test data when the app is first launched.

Then you must go to the file folder and build an app in a console:
* mvn clean package

Then you must to run app:
* java -jar target/bookingMeetingRoom-0.0.1-SNAPSHOT.jar

And go to web browser onto the page:
* http://localhost:8080/

USERS FOR LOGIN
------------

* admin/123456
* user/123
