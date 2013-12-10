This package does not contain any JDBC drivers. 

Set CT_CLASSPATH to the fully-qualified path and jar filename of your JDBC driver, for example, Linux users could do:

 export CT_CLASSPATH=/home/ep/.m2/repository/mysql/mysql-connector-java/5.1.14/mysql-connector-java-5.1.14.jar

This will be automatically picked up by the cryptotool.sh script.