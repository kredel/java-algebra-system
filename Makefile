#
# $Id$
#
# Makefile with default rules for the Java-Algebra-System
# by Heinz kredel
#
# created 2.6.2000 hk 
# modified 1.1.2003 hk 
# todo 

# set this to your jdk binaries path
JDK=/usr/java/j2sdk1.4.1_01/bin
#JDK=/usr/java/j2sdk1.4.0_01/bin
#JDK=/opt/jdk1.4/bin
#JDK=/opt/jdk1.4.0b3/bin
#JDK=/usr/lib/jdk1.3/bin
#JDK=/opt/jdk1.2.2/bin
#JDK=/usr/lib/java/bin

JUNITPATH=/home/kredel/java/lib/junit.jar
LOG4JPATH=/home/kredel/java/lib/log4j.jar
JOMPPATH=/home/kredel/java/lib/jomp1.0b.jar

# no need to change below this line

# command line arguments
cl=

#.EXPORT_ALL_VARIABLES :

JASPATH=/home/kredel/jas
DEFS=$(JASPATH)/arith:$(JASPATH)/poly:$(JASPATH)/ring
DOCCLASSES=$(JUNITPATH):$(LOG4JPATH):$(JOMPPATH)
DOCOPTS=-package
#DOCOPTS=-package -version -author
#DOCOPTS=-public -protected -package -author -version

MYCLASSPATH = .:$(DEFS):$(JUNITPATH):$(LOG4JPATH):$(JOMPPATH)

JAVAC=$(JDK)/javac -classpath $(MYCLASSPATH) -d .
JAVA=$(JDK)/java -classpath $(MYCLASSPATH)  
#-verbose:gc
#JAVA=$(JDK)/java -classpath $(MYCLASSPATH)  -verbose:gc -Xrunhprof:cpu=times,format=a
#JAVA=$(JDK)/java -classpath $(MYCLASSPATH) -verbose:gc -verbose:class -verbose:jni
DOC=$(JDK)/javadoc -classpath $(DOCCLASSES)

usage:
	echo; echo "usage: make <name> cl='cmd'"; echo


FIX       = fixm2j
GETC      = getc.pl
#JOPT      = -prof:gb.prof -tm
#-mx100000000


.SUFFIXES :
.SUFFIXES : .class .java 
.PRECIOUS : %.java %.class edu/jas/arith/%.class edu/jas/poly/%.class edu/jas/ring/%.class
.PHONY    : clean doc

all:
	$(JAVAC) *.java

%.class: %.java
	$(JAVAC) $<

edu/jas/%.class: %.java
	$(JAVAC) $<

edu/jas/arith/%.class: %.java
	$(JAVAC) $<

edu/jas/poly/%.class: %.java
	$(JAVAC) $<

edu.jas.%: edu/jas/%.class
	$(JAVA) $@ $(cl)

edu.jas.arith.%: edu/jas/arith/%.class
	$(JAVA) $@ $(cl)

edu.jas.poly.%: edu/jas/poly/%.class
	$(JAVA) $@ $(cl)


#%: edu/jas/arith/%.class
#	$(JAVA) edu.jas.arith.$@ $(cl)



.PRECIOUS : %.java %.class edu/jas/%.class

FILES=$(wildcard *.java arith/*.java poly/*.java ring/*.java)


doc: $(FILES)
	$(DOC) $(DOCOPTS) -d doc $(FILES) 

jar: $(FILES) jas-log.html Makefile
	$(JDK)/jar -cvf jas.jar $(FILES) edu/ jas-log.html Makefile
	cp jas.jar /tmp/jas-`date +%Y%j`.jar
	cp jas.jar /mnt/i/e/kredel/jas-`date +%Y%j`.jar


clean:
	find . -name "*~" -follow -print -exec rm {} \;

# -eof-
