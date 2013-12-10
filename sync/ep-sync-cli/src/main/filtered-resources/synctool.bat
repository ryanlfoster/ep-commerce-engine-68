@echo off
rem Windows XP Syntax
set LIB=${dependency.directory}
"%JAVA_HOME%"\bin\java -cp ".;%LIB%\*" -Xmx512m com.elasticpath.tools.sync.client.SynchronizationTool %*
@echo on
