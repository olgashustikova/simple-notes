# simple-notes

The project allows user to create notes. There is not database, notes are saved in the server memory and removed if the sever is down. 

**Project includes:**

1. Simple webservices written with java and spring.
2. Simple front-end with html (bootstrap) and javascript (with html templates)
3. Postman tests.
4. Selenium tests.

**To compile:**


mvn clean package spring-boot:repackage -DargLine="-D**WEB_DRIVER_TYPE**=**WEB_DRIVER_PATH**"

for example:

mvn clean package spring-boot:repackage -DargLine="-Dwebdriver.chrome.driver=/opt/chromedriver/chromedriver"


**To run:**

go to **target** folder

java -jar simple-notes-0.0.1-SNAPSHOT.jar

**localhost:8080** should be available