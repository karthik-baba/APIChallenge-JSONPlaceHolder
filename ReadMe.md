Pre-requisites
--------------
* Maven
* Java 1.8

Framework Used
--------------

REST-Assured - https://github.com/rest-assured/rest-assured

To Run the Tests
-----------------
From command prompt, **mvn clean compile test-compile test**  

[By Default it runs for test environment]

OR

If you need to run for specific environment, **mvn clean compile test-compile test -Denv="test"**

Following scenarios does not work as expected
----------------------------------------------

These are mentioned in the https://github.com/typicode/jsonplaceholder

* Post - Created record will not be present once create is done
* Put - Updated Records will not be updated after update through PUT method
* Patch - Updated records will not be updated after update through PATCH method
* Delete - Deleted records will be present in the list after delete. 
