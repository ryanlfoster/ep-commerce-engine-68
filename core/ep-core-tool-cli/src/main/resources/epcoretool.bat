@echo off
rem Windows XP Syntax
set LIB=lib
"%JAVA_HOME%"\bin\java -cp ".;%LIB%\*" -Xmx512m com.elasticpath.tools.epcoretool.client.Cli %*
@echo on
