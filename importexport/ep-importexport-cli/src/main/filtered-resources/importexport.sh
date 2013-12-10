#!/bin/sh


if [ -z "$JAVA_HOME" ] ; then
	echo "JAVA_HOME environment variable must be set"
	exit 1;
fi

export LIB=${dependency.directory}


for x in $LIB/*.jar; do
    IE_CLASSPATH=$IE_CLASSPATH:$x
done
export IE_CLASSPATH=.:$IE_CLASSPATH

"$JAVA_HOME"/bin/java -cp "$IE_CLASSPATH" -Xmx512m com.elasticpath.importexport.client.Index $@
