#!/bin/sh
cd ..

JAVA_HOME=$JAVA_HOME
PATH=$PATH:$JAVA_HOME/bin
export MXPARAM=-Xmx"8192"M
export MSPARAM=-Xms"1024"M
JAVA_EXT_OPTION=
JAVA_MISC="-Xbootclasspath/a:./lib/tcmcat-bootstrap.jar -XX:-RelaxAccessControlCheck -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=DumpOutOfMemory.bin"

java -version

export CP=$DIST_PATH:$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar:lib/*:lib/ant/*:lib/asm/*:lib/crypto/*:lib/ext/*:lib/interconnect/*:lib/jdbc/*:lib/mq/*:lib/test/*:lib/tomcat/*:lib/projectLib/*

$JAVA_HOME/bin/java -cp $CP $MXPARAM $MSPARAM -Xss256k $DEBUG $JAVA_MISC $JAVA_EXT_OPTION -Djava.security.policy=mong.policy -Djava.rmi.server.codebase="$CODEBASE_LIST" -Dfile.encoding=UTF-8 -Djava.io.tmpdir=./tmp gpf.GpfMain $*
