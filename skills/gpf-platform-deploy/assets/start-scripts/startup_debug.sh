#!/bin/sh

JAVA_HOME=$JAVA_HOME
PATH=$PATH:$JAVA_HOME/bin
MXPARAM=-Xmx"2800"M
MSPARAM=-Xms"256"M
JAVA_EXT_OPTION=
JAVA_MISC="-Xbootclasspath/a:./lib/tcmcat-bootstrap.jar -XX:-RelaxAccessControlCheck -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=DumpOutOfMemory.bin"

java -version

export CP=$DIST_PATH:$JAVA_HOME/lib/tools.jar:lib/*:lib/ant/*:lib/asm/*:lib/crypto/*:lib/ext/*:lib/interconnect/*:lib/jdbc/*:lib/mq/*:lib/test/*:lib/tomcat/*:lib/projectLib/*

JAVA_DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=$1"
echo JAVA_DEBUG_PARAM=$JAVA_DEBUG

$JAVA_HOME/bin/java -cp $CP $MXPARAM $MSPARAM -Xss256k $JAVA_DEBUG $JAVA_MISC $JAVA_EXT_OPTION -Djava.security.policy=mong.policy -Djava.rmi.server.codebase="$CODEBASE_LIST" com.leavay.starter.StarterMain $*
