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
JDK=/usr/java/jdk1.6.0/bin
#JDK=/usr/lib/jvm/java-1.5.0/bin
#JDK=/usr/java/jdk1.5.0_01/bin
#JDK=/usr/java/j2sdk1.4.1_01/bin
#JDK=/usr/java/j2sdk1.4.0_01/bin
#JDK=/opt/jdk1.4/bin
#JDK=/opt/jdk1.4.0b3/bin
#JDK=/usr/lib/jdk1.3/bin
#JDK=/opt/jdk1.2.2/bin
#JDK=/usr/lib/java/bin

LIBPATH=/home/kredel/java/lib
JUNITPATH=$(LIBPATH)/junit.jar
LOG4JPATH=$(LIBPATH)/log4j.jar
JOMPPATH=$(LIBPATH)/jomp1.0b.jar
TNJPATH=$(LIBPATH)/tnj.jar
LINTPATH=$(LIBPATH)/lint4j.jar

# --- syncing ----------
DRY=--dry-run
DELETE=
RSYNC=rsync -e ssh -avuz $(DRY) $(DELETE) --exclude=*~ --exclude=*.log* --exclude=*.out* --exclude=*.txt* --exclude=.svn 
####--exclude=./test
####--exclude=*.ps --exclude=*.pdf --exclude=spin*
####--exclude=*/.jxta/
PART=jas.j16
VERSION=jas-2.0
SVNVERSION=`grep committed-rev .svn/entries |head -1|awk -F = '{ print $2 }'|sed 's/"//g'`

all:

home:
	$(RSYNC) krum:java/$(PART)/     .

heinz:
	$(RSYNC) ./                heinz@heinz2:$(PART)

krum:
	$(RSYNC) ./                krum:java/$(PART)

pub:
	$(RSYNC) --exclude=*ufd* --exclude=DTD --exclude=lisa* --exclude=*xml ./ krum:htdocs/$(PART)

compute:
	$(RSYNC) ./                compute:java/$(PART)


# no need to change below this line

# command line arguments
cl=

#.EXPORT_ALL_VARIABLES :

JASPATH=/home/kredel/jas
DEFS=$(JASPATH)/arith:$(JASPATH)/poly:$(JASPATH)/vector:$(JASPATH)/ring:$(JASPATH)/ufd:$(JASPATH)/module:$(JASPATH)/util:$(JASPATH)/application
DOCCLASSES=$(JUNITPATH):$(LOG4JPATH):$(JOMPPATH)
#:$(TNJPATH)
DOCOPTS=-package
#DOCOPTS=-package -version -author
#DOCOPTS=-public -protected -package -author -version

MYCLASSPATH = .:$(DEFS):$(JUNITPATH):$(LOG4JPATH):$(JOMPPATH)
#:$(TNJPATH)

JAVAC=$(JDK)/javac -classpath $(MYCLASSPATH) -d . -Xlint:unchecked
#-Xlint:unchecked
#JAVA=$(JDK)/java -classpath $(MYCLASSPATH) -verbose:gc 
JAVA=$(JDK)/java -classpath $(MYCLASSPATH) -server -Xms300M -Xmx600M -XX:+AggressiveHeap -XX:+UseParallelGC -XX:ParallelGCThreads=2 -verbose:gc 
#-Xbatch
#old#JAVA=$(JDK)/java -classpath $(MYCLASSPATH) -Xms300M -Xmx600M -XX:+AggressiveHeap -XX:+UseParallelGC -verbose:gc 
#JAVA=$(JDK)/java -classpath $(MYCLASSPATH) -verbose:gc -Xrunhprof:cpu=times,format=a
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
.PRECIOUS : %.java %.class edu/jas/arith/%.class edu/jas/poly/%.class edu/jas/ring/%.class edu/jas/vector/%.class edu/jas/ufd/%.class edu/jas/module/%.class edu/jas/structure/%.class edu/jas/util/%.class edu/jas/application/%.class edu/jas/kern/%.class edu/jas/%.class

.PHONY    : clean doc

all:
	$(JAVAC) */*.java

%.class: %.java
	$(JAVAC) $<


edu/jas/%.class: %.java
	$(JAVAC) $<

edu/jas/arith/%.class: %.java
	$(JAVAC) $<

edu/jas/poly/%.class: %.java
	$(JAVAC) $<

edu/jas/ring/%.class: %.java
	$(JAVAC) $<

edu/jas/ufd/%.class: %.java
	$(JAVAC) $<

edu/jas/vector/%.class: %.java
	$(JAVAC) $<

edu/jas/module/%.class: %.java
	$(JAVAC) $<

edu/jas/structure/%.class: %.java
	$(JAVAC) $<

edu/jas/util/%.class: %.java
	$(JAVAC) $<

edu/jas/application/%.class: %.java
	$(JAVAC) $<

edu/jas/kern/%.class: %.java
	$(JAVAC) $<


edu.jas.%: edu/jas/%.class
	$(JAVA) $@ $(cl)

edu.jas.arith.%: edu/jas/arith/%.class
	$(JAVA) $@ $(cl)

edu.jas.poly.%: edu/jas/poly/%.class
	$(JAVA) $@ $(cl)

edu.jas.ring.%: edu/jas/ring/%.class
	$(JAVA) $@ $(cl)

edu.jas.ufd.%: edu/jas/ufd/%.class
	$(JAVA) $@ $(cl)

edu.jas.vector.%: edu/jas/vector/%.class
	$(JAVA) $@ $(cl)

edu.jas.module.%: edu/jas/module/%.class
	$(JAVA) $@ $(cl)

edu.jas.structure.%: edu/jas/structure/%.class
	$(JAVA) $@ $(cl)

edu.jas.util.%: edu/jas/util/%.class
	$(JAVA) $@ $(cl)

edu.jas.application.%: edu/jas/application/%.class
	$(JAVA) $@ $(cl)

edu.jas.kern.%: edu/jas/kern/%.class
	$(JAVA) $@ $(cl)



#FILES=$(wildcard src/edu/jas/structure/*.java src/edu/jas/arith/*.java src/edu/jas/poly/*.java src/edu/jas/ring/*.java src/edu/jas/ufd/*.java src/edu/jas/application/*.java src/edu/jas/vector/*.java src/edu/jas/module/*.java src/edu/jas/util/*.java src/edu/jas/kern/*.java)
FILES=$(wildcard src/edu/jas/structure/*.java src/edu/jas/arith/*.java src/edu/jas/poly/*.java src/edu/jas/ring/*.java src/edu/jas/application/*.java src/edu/jas/vector/*.java src/edu/jas/module/*.java src/edu/jas/util/*.java src/edu/jas/ufd/*.java src/edu/jas/kern/*.java)

LIBS=$(JUNITPATH) $(LOG4JPATH) $(JOMPPATH) $(TNJPATH)

#CLASSES=$(wildcard edu/jas/structure/*.java edu/jas/arith/*.class edu/jas/poly/*.class edu/jas/ring/*.class edu/jas/ufd/*.class edu/jas/application/*.class edu/jas/vector/*.class edu/jas/module/*.class edu/jas/util/*.class edu/jas/kern/*.class)
#CLASSES=edu/jas

#CLASSES=edu/jas/structure/ edu/jas/arith/ edu/jas/poly/ edu/jas/ring/ edu/jas/ufd/ edu/jas/application/ edu/jas/vector/ edu/jas/module/ edu/jas/util/ edu/jas/kern/

CLASSES=edu/jas/structure/ edu/jas/arith/ edu/jas/poly/ edu/jas/ring/ edu/jas/application/ edu/jas/vector/ edu/jas/module/ edu/jas/util/ edu/jas/ufd/ edu/jas/kern/

PYS=$(wildcard *.py)
EXAMPY=$(wildcard examples/*.py)

DOCU=$(wildcard jas-log.html index.html problems.html design.html COPYING* sample.jythonrc overview.html)
# */package.html 

doc: $(FILES)
	$(DOC) $(DOCOPTS) -d doc $(FILES) 

ALLJAR=$(FILES) $(DOCU) Makefile build.xml log4j.properties $(PYS)

jas-all.jar: $(ALLJAR)
	$(JDK)/jar -cvf jas.jar $(ALLJAR) edu/ 
	mv jas.jar /tmp/jas-`date +%Y%j`.jar

jas.tgz: $(FILES) *.html */package.html TODO
	tar -cvzf jas.tgz $(FILES) *.html */package.html TODO
	mv jas.tgz /tmp/jas-`date +%Y%j`.tgz

#	cp jas.jar /mnt/i/e/kredel/jas-`date +%Y%j`.jar
#jas-run.jar: GBManifest.MF $(TOJAR)
#	$(JDK)/jar -cvfm jas-run.jar GBManifest.MF $(TOJAR)


TOJAR=$(FILES) $(CLASSES) Makefile build.xml log4j.properties $(EXAMPY) examples/machines.test $(wildcard COPYING*)

jas.jar: $(FILES) Makefile build.xml log4j.properties $(EXAMPY)
#	$(JDK)/jar -cf jas.jar $(TOJAR)
	$(JDK)/jar -cf jas.jar $(filter-out %/ufd/, $(filter-out src/edu/jas/ufd/%.java, $(TOJAR)))

jas-doc.jar: $(DOCU) doc/
	$(JDK)/jar -cvf jas-doc.jar $(DOCU) doc/


dist: jas.jar jas-run.jar jas-doc.jar $(LIBS)
	tar -cvzf jas-dist.tgz jas.jar jas-run.jar jas-doc.jar $(LIBS)

#

jars: jas-run.jar jas-doc.jar
#jars: jas.jar jas-run.jar jas-doc.jar


lint:
	$(JDK)/java -jar $(LINTPATH) -v 3 -classpath $(DOCCLASSES):jas.jar -sourcepath src edu.jas.*
#	$(JDK)/java -jar $(LINTPATH) -v 5 -exact -classpath $(DOCCLASSES) -sourcepath src edu.jas.*


clean:
	find . -name "*~" -follow -print -exec rm {} \;
	rm -f application/application arith/arith kern/kern module/module poly/poly ring/ring structure/structure ufd/ufd util/util vector/vector


#svn copy file:///home/SUBVERSION/jas/trunk file:///home/SUBVERSION/jas/tags/$(VERSION)

export:
	rm -rf ~/jas-versions/$(VERSION)
	svn export --quiet file:///home/SUBVERSION/jas/trunk ~/jas-versions/$(VERSION)
	cd ~/jas-versions/; jar -cf $(VERSION)-src.jar $(VERSION)/
	cd ~/jas-versions/$(VERSION)/; ant compile
	cd ~/jas-versions/$(VERSION)/; jar -cf ../$(VERSION)-bin.jar edu/
	cd ~/jas-versions/$(VERSION)/; ant test
	cd ~/jas-versions/$(VERSION)/; sh ./tests.sh > test.out
	cd ~/jas-versions/$(VERSION)/; ant doc
	cd ~/jas-versions/$(VERSION)/; jar -cf ../$(VERSION)-doc.jar doc/ *.html

xxx:
	grep committed-rev .svn/entries |head -1|awk -F = '{ print $$2 }'|sed 's/"//g' > make.svnversion
	echo svn1 `cat make.svnversion`

# -eof-
