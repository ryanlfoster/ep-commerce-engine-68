#!/bin/bash

if [ -z "$JAVA_HOME" ] ; then
	echo "JAVA_HOME environment variable must be set"
	exit 1;
fi

export LIB=${dependency.directory}

export CT_CLASSPATH=`echo $LIB/*.jar|tr ' ' ':'`

"$JAVA_HOME"/bin/java -ea -Xms512m -Xmx2048m -cp $CT_CLASSPATH com.elasticpath.util.cryptotool.CryptoTool $@ 
