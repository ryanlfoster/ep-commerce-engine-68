#!/bin/sh

if [ -z "$JAVA_HOME" ] ; then
	echo "JAVA_HOME environment variable must be set"
	exit 1;
fi

export LIB=${dependency.directory}

for x in $LIB/*.jar; do
    DST_CLASSPATH=$DST_CLASSPATH:$x
done
export DST_CLASSPATH=.:$DST_CLASSPATH

"$JAVA_HOME"/bin/java -cp "$DST_CLASSPATH" -Xmx512m com.elasticpath.tools.sync.client.SynchronizationTool $@
