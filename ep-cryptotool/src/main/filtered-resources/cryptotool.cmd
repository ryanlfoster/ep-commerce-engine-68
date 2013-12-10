@echo off
rem Windows XP Syntax
set LIB=${dependency.directory}
"%JAVA_HOME%"\bin\java -ea -Xms512m -Xmx1024m -cp "%LIB%\*" com.elasticpath.util.cryptotool.CryptoTool %*
@echo on
