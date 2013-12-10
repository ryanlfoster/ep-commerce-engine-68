#EP Core Tool

##This package does not contain any JDBC drivers. 

Set IE_CLASSPATH to the fully-qualified path and jar filename of your JDBC driver, for example, Linux users could do:

    export IE_CLASSPATH=/home/ep/.m2/repository/mysql/mysql-connector-java/5.1.14/mysql-connector-java-5.1.14.jar


##Commands

bulk-set-settings <bulk-settings.properties>
  Updates the setting value in the Elastic Path database. If a value already
  exists, it will be removed before being re-added.
 
index-status
  Display the current search server index rebuild status.
 
ping-search
  Ping search server.

request-reindex [<index>]
  Adds a rebuild request to the index notification queue.
 
set-setting <setting>@<context>=<value>
set-setting <setting>=<value>
  Updates the setting value in the Elastic Path database. If a value already
  exists, it will be removed.

set-cmuser-password <username>=<password>
  Update cmuser password.
 
unset-setting <setting>@<context>
unset-setting <setting>
  Updates the setting value in the Elastic Path database. If a value already
  exists, it will be removed.
