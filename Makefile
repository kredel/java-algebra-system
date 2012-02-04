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
#JDK=/usr/lib/jvm/java/bin
#JDK=/usr/java/jdk1.6.0_02/bin
#JDK=/usr/lib/jvm/java-1.5.0/bin
#JDK=/usr/java/jdk1.5.0_01/bin
#JDK=/usr/java/j2sdk1.4.1_01/bin
#JDK=/usr/java/j2sdk1.4.0_01/bin
#JDK=/opt/jdk1.4/bin
#JDK=/opt/jdk1.4.0b3/bin
#JDK=/usr/lib/jdk1.3/bin
#JDK=/opt/jdk1.2.2/bin
#JDK=/usr/lib/java/bin
JDK=/usr/java/latest/bin
#JDK=/usr/lib/jvm/java-1.5.0-sun-1.5.0/bin

#JASPATH=$(HOME)/jas
SVNREPO=/home/SUBVERSION
LIBPATH=$(HOME)/java/lib
JUNITPATH=$(LIBPATH)/junit.jar
LOG4JPATH=$(LIBPATH)/log4j.jar
#LOG4JPATH=$(LIBPATH)/mylog.jar
JOMPPATH=$(LIBPATH)/jomp1.0b.jar
TNJPATH=$(LIBPATH)/tnj.jar
LINTPATH=$(LIBPATH)/lint4j.jar
PYPATH=$(LIBPATH)/jython.jar

# --- syncing ----------
DRY=--dry-run
DELETE=
RSYNC=rsync -e ssh -avuz $(DRY) $(DELETE) --exclude=*~ --include=svn_change.log --include=jdepend-report.txt --exclude=*.log* --exclude=*.out* --exclude=*.txt* --exclude=.svn 
####--exclude=./test
####--exclude=*.ps --exclude=*.pdf --exclude=spin*
####--exclude=*/.jxta/
PART=jas.j16
VERSION=jas-2.4
#BRANCH=2.3
SVNVERSION=`grep committed-rev .svn/entries |head -1|awk -F = '{ print $2 }'|sed 's/"//g'`

all:

home:
	$(RSYNC) krum:java/$(PART)/     .

heinz:
	$(RSYNC) ./                heinz@heinz2:$(PART)

krum:
	$(RSYNC) -e 'ssh -p 2222' ./                krum:java/$(PART)

pub:
	$(RSYNC) --exclude=*ufd* --exclude=DTD --exclude=lisa* --exclude=*xml ./ krum:htdocs/$(PART)

compute:
	$(RSYNC) ./                compute:java/$(VERSION)

# --- end syncing ----------


# no need to change below this line

# command line arguments
cl=

#.EXPORT_ALL_VARIABLES :

#DEFS=$(JASPATH)/arith:$(JASPATH)/poly:$(JASPATH)/ps:$(JASPATH)/vector:$(JASPATH)/gb:$(JASPATH)/ufd:$(JASPATH)/gbmod:$(JASPATH)/util:$(JASPATH)/application:$(JASPATH)/root
DOCCLASSES=$(JUNITPATH):$(LOG4JPATH):$(JOMPPATH)
#:$(TNJPATH)
DOCOPTS=-package
#DOCOPTS=-package -version -author
#DOCOPTS=-public -protected -package -author -version

#MYCLASSPATH = .:$(DEFS):$(JUNITPATH):$(JOMPPATH)
#MYCLASSPATH = $(LOG4JPATH):.:$(DEFS):$(JUNITPATH):$(JOMPPATH):$(PYPATH)
MYCLASSPATH = $(LOG4JPATH):.:$(JUNITPATH):$(JOMPPATH):$(PYPATH)
#:$(TNJPATH)

#JAVA_MEM=-Xms1100M -Xmx1900M
JAVA_MEM=-Xms350M -Xmx800M

#SOPTS="-J-cp ../lib/log4j.jar:../lib/junit.jar:. -J-verbose:gc -J-Xms1100M -J-Xmx1900M"
SOPTS="-J-cp ../lib/log4j.jar:../lib/junit.jar:. -J-verbose:gc -J-Xms350M -J-Xmx800M"


JAVAC=$(JDK)/javac -classpath $(MYCLASSPATH) -d . -Xlint:unchecked
#-Xlint:unchecked
#-Djava.util.logging.config.file=logging.properties 
#JAVA=$(JDK)/java -classpath $(MYCLASSPATH) -verbose:gc 
JAVA=$(JDK)/java -classpath $(MYCLASSPATH) -server $(JAVA_MEM) -XX:+AggressiveHeap -XX:+UseParallelGC -XX:ParallelGCThreads=2 -verbose:gc 
#-Xrunhprof:cpu=samples,heap=sites,force=n
#-Xbatch
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
.PRECIOUS : %.java %.class edu/jas/arith/%.class edu/jas/poly/%.class edu/jas/ps/%.class edu/jas/gb/%.class edu/jas/vector/%.class edu/jas/ufd/%.class edu/jas/gbmod/%.class edu/jas/structure/%.class edu/jas/util/%.class edu/jas/application/%.class edu/jas/kern/%.class edu/jas/root/%.class edu/jas/%.class

.PHONY    : clean doc

all:
	$(JAVAC) src/edu/jas/*/*.java src/edu/mas/*/*.java trc/edu/jas/*/*.java trc/edu/mas/*/*.java

%.class: %.java
	$(JAVAC) $<

edu/jas/%.class: src/edu/jas/%.java
	$(JAVAC) $<


edu/jas/arith/%Test.class: trc/edu/jas/arith/%Test.java
	$(JAVAC) $<

edu/jas/arith/%.class: src/edu/jas/arith/%.java
	$(JAVAC) $<


edu/jas/poly/%Test.class: trc/edu/jas/poly/%Test.java
	$(JAVAC) $<

edu/jas/poly/%.class: src/edu/jas/poly/%.java
	$(JAVAC) $<


edu/jas/ps/%Test.class: trc/edu/jas/ps/%Test.java
	$(JAVAC) $<

edu/jas/ps/%.class: src/edu/jas/ps/%.java
	$(JAVAC) $<


edu/jas/gb/%Test.class: trc/edu/jas/gb/%Test.java
	$(JAVAC) $<

edu/jas/gb/%.class: src/edu/jas/gb/%.java
	$(JAVAC) $<


edu/jas/ufd/%Test.class: trc/edu/jas/ufd/%Test.java 
	$(JAVAC) $<

edu/jas/ufd/%.class: src/edu/jas/ufd/%.java 
	$(JAVAC) $<


edu/jas/vector/%Test.class: trc/edu/jas/vector/%Test.java
	$(JAVAC) $<

edu/jas/vector/%.class: src/edu/jas/vector/%.java
	$(JAVAC) $<


edu/jas/gbmod/%Test.class: trc/edu/jas/gbmod/%Test.java
	$(JAVAC) $<

edu/jas/gbmod/%.class: src/edu/jas/gbmod/%.java
	$(JAVAC) $<


edu/jas/gbufd/%Test.class: trc/edu/jas/gbufd/%Test.java
	$(JAVAC) $<

edu/jas/gbufd/%.class: src/edu/jas/gbufd/%.java
	$(JAVAC) $<


edu/jas/structure/%Test.class: trc/edu/jas/structure/%Test.java
	$(JAVAC) $<

edu/jas/structure/%.class: src/edu/jas/structure/%.java
	$(JAVAC) $<


edu/jas/util/%Test.class: trc/edu/jas/util/%Test.java
	$(JAVAC) $<

edu/jas/util/%.class: src/edu/jas/util/%.java
	$(JAVAC) $<


edu/jas/application/%Test.class: trc/edu/jas/application/%Test.java
	$(JAVAC) $<

edu/jas/application/%.class: src/edu/jas/application/%.java
	$(JAVAC) $<


edu/jas/root/%Test.class: trc/edu/jas/root/%Test.java
	$(JAVAC) $<

edu/jas/root/%.class: src/edu/jas/root/%.java
	$(JAVAC) $<


edu/jas/kern/%Test.class: trc/edu/jas/kern/%Test.java
	$(JAVAC) $<

edu/jas/kern/%.class: src/edu/jas/kern/%.java
	$(JAVAC) $<


edu/jas/integrate/%Test.class: trc/edu/jas/integrate/%Test.java
	$(JAVAC) $<

edu/jas/integrate/%.class: src/edu/jas/integrate/%.java
	$(JAVAC) $<


edu/mas/kern/%Test.class: trc/edu/mas/kern/%Test.java
	$(JAVAC) $<

edu/mas/kern/%.class: src/edu/mas/kern/%.java
	$(JAVAC) $<


edu.jas.%: edu/jas/%.class
	$(JAVA) $@ $(cl)

edu.jas.arith.%: edu/jas/arith/%.class
	$(JAVA) $@ $(cl)

edu.jas.poly.%: edu/jas/poly/%.class
	$(JAVA) $@ $(cl)

edu.jas.ps.%: edu/jas/ps/%.class
	$(JAVA) $@ $(cl)

edu.jas.gb.%: edu/jas/gb/%.class
	$(JAVA) $@ $(cl)

edu.jas.ufd.%: edu/jas/ufd/%.class
	$(JAVA) $@ $(cl)

edu.jas.gbufd.%: edu/jas/gbufd/%.class
	$(JAVA) $@ $(cl)

edu.jas.vector.%: edu/jas/vector/%.class
	$(JAVA) $@ $(cl)

edu.jas.gbmod.%: edu/jas/gbmod/%.class
	$(JAVA) $@ $(cl)

edu.jas.structure.%: edu/jas/structure/%.class
	$(JAVA) $@ $(cl)

edu.jas.util.%: edu/jas/util/%.class
	$(JAVA) $@ $(cl)

edu.jas.application.%: edu/jas/application/%.class
	$(JAVA) $@ $(cl)

edu.jas.root.%: edu/jas/root/%.class
	$(JAVA) $@ $(cl)

edu.jas.kern.%: edu/jas/kern/%.class
	$(JAVA) $@ $(cl)

edu.jas.integrate.%: edu/jas/integrate/%.class
	$(JAVA) $@ $(cl)

edu.mas.kern.%: edu/mas/kern/%.class
	$(JAVA) $@ $(cl)


FILES=$(wildcard src/edu/jas/structure/*.java src/edu/jas/arith/*.java src/edu/jas/poly/*.java src/edu/jas/ps/*.java src/edu/jas/gb/*.java src/edu/jas/application/*.java src/edu/jas/vector/*.java src/edu/jas/gbmod/*.java src/edu/jas/util/*.java src/edu/jas/ufd/*.java src/edu/jas/kern/*.java src/edu/jas/root/*.java src/edu/jas/integrate/*.java)

TESTFILES=$(wildcard trc/edu/jas/structure/*.java trc/edu/jas/arith/*.java trc/edu/jas/poly/*.java trc/edu/jas/ps/*.java trc/edu/jas/gb/*.java trc/edu/jas/application/*.java trc/edu/jas/vector/*.java trc/edu/jas/gbmod/*.java trc/edu/jas/util/*.java trc/edu/jas/ufd/*.java trc/edu/jas/kern/*.java trc/edu/jas/root/*.java trc/edu/jas/integrate/*.java)

LIBS=$(JUNITPATH) $(LOG4JPATH) $(JOMPPATH) $(TNJPATH)

CLASSES=edu/jas/structure/ edu/jas/arith/ edu/jas/poly/ edu/jas/ps/ edu/jas/gb/ edu/jas/application/ edu/jas/vector/ edu/jas/gbmod/ edu/jas/util/ edu/jas/ufd/ edu/jas/kern/ edu/jas/root/ edu/jas/integrate/

PYS=$(wildcard *.py)
EXAMPY=$(wildcard examples/*.py)

DOCU=$(wildcard jas-log.html index.html problems.html design.html COPYING* sample.jythonrc overview.html)
# */package.html 

doc: $(FILES) $(TESTFILES)
	$(DOC) $(DOCOPTS) -d doc/api $(FILES) $(TESTFILES)

epydoc: examples/jas.py
	epydoc -o doc/jython -n "Python to JAS" -u ../../index.html examples/jas.py

rdoc: examples/jas.rb
	jrdoc -o doc/jruby -U -S -N -t "Ruby to JAS" examples/jas.rb

texdoc: $(FILES) $(TESTFILES)
	mkdir -p doc/tex
	rm -f doc/tex/*
	$(DOC) $(DOCOPTS) -doclet TexGen -docletpath ~/java/lib/texgen.jar -dest doc/tex $(FILES) $(TESTFILES) 
	ls doc/tex/* | grep -v Test | grep -v allclasses | xargs cat > doc/tex/allclasses.tex
	sed -i -f doc/totex.sed doc/tex/allclasses.tex
	cd doc; pdflatex jas_texgen.tex

ALLJAR=$(FILES) $(TESTFILES) $(DOCU) Makefile build.xml log4j.properties $(PYS)

jas-all.jar: $(ALLJAR)
	$(JDK)/jar -cvf jas.jar $(ALLJAR) edu/ 
	mv jas.jar /tmp/jas-`date +%Y%j`.jar

jas.tgz: $(FILES) $(TESTFILES) *.html TODO
	tar -cvzf jas.tgz $(FILES) $(TESTFILES) *.html TODO
	cp jas.tgz /tmp/jas-`date +%Y%j`.tgz

#	cp jas.jar ...../jas-`date +%Y%j`.jar
#jas-run.jar: GBManifest.MF $(TOJAR)
#	$(JDK)/jar -cvfm jas-run.jar GBManifest.MF $(TOJAR)


TOJAR=$(FILES) $(TESTFILES) $(CLASSES) Makefile build.xml log4j.properties $(EXAMPY) examples/machines.test $(wildcard COPYING*)

jas.jar: $(FILES) $(TESTFILES) Makefile build.xml log4j.properties $(EXAMPY)
	$(JDK)/jar -cf jas.jar $(TOJAR)
#	$(JDK)/jar -cf jas.jar $(filter-out %/ufd/, $(filter-out src/edu/jas/ufd/%.java, $(TOJAR)))

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
	#rm -f application/application arith/arith kern/kern gbmod/gbmod poly/poly ps/ps gb/gb structure/structure ufd/ufd util/util vector/vector


tests:
	ant test 2>&1 | tee t.out
	ant exam 2>&1 | tee e.out
	find examples -name "*.py"|grep -v jas.py |grep -v plot|grep -v versuch|sort|xargs -L 1 echo "time jython $(SOPTS)" | awk '{ printf "echo %s\n", $$0; printf "%s\n", $$0 }' > ./all_jython.sh
	time bash all_jython.sh 2>&1 | tee tjy.out
	find examples -name "*.rb"|grep -v jas.rb |grep -v versuch|sort|xargs -L 1 echo "time jruby $(SOPTS)" | awk '{ printf "echo %s\n", $$0; printf "%s\n", $$0 }' > ./all_jruby.sh
	time bash all_jruby.sh 2>&1 | tee tjr.out
	make edu.jas.application.RunGB cl="seq  examples/trinks6.jas"   | tee tr.out
	make edu.jas.application.RunGB cl="seq+ examples/trinks6.jas"   | tee -a tr.out
	make edu.jas.application.RunGB cl="par  examples/trinks6.jas 4" | tee -a tr.out
	make edu.jas.application.RunGB cl="par+ examples/trinks6.jas 4" | tee -a tr.out
	-grep FAIL t.out
	-grep Exception e.out | grep -v GCDProxy
	-grep File tjy.out
	-grep -i error tjr.out
	-grep Exception tr.out | grep Usage tr.out

metrics:
	ant jdepend
	../java/javancss-32.53/bin/javancss -all -recursive -out test/javanccs-`date +%Y-%m-%d`.out src

#svn copy file:///$(SVNREPO)/jas/trunk file:///$(SVNREPO)/jas/tags/$(VERSION)

SVNREV=svnlook youngest $(SVNREPO)/jas
SVNDATE=svnlook date $(SVNREPO)/jas
# Jan 2008 SVNSRT=1584 
# Jun 2008 SVNSRT=1882
# Sep 2008 SVNSRT=2118r
# jan 2009 SVNSRT=2288
# jun 2009 SVNSRT=2668
# jan 2010 SVNSRT=2978
# jun 2010 SVNSRT=3188
# jan 2011
SVNSRT=3458

export:
	rm -rf ~/jas-versions/$(VERSION)
	svn export --quiet file:///$(SVNREPO)/jas/trunk ~/jas-versions/$(VERSION)
	cd ~/jas-versions/$(VERSION); jas_dosed $(VERSION) `$(SVNREV)` download.html
	svn log -v -r HEAD:$(SVNSRT) file:///$(SVNREPO)/jas/trunk src trc examples > ~/jas-versions/$(VERSION)/svn_change.log
	cd ~/jas-versions/; jar -cfM $(VERSION).`$(SVNREV)`-src.zip $(VERSION)/
	cd ~/jas-versions/$(VERSION)/; ant compile > ant_compile.out
	cd ~/jas-versions/$(VERSION)/; jar -cfm ../$(VERSION).`$(SVNREV)`-bin.jar GBManifest.MF edu/ COPYING* log4j.properties
	cd ~/jas-versions/$(VERSION)/; jar -uf ../$(VERSION).`$(SVNREV)`-bin.jar -C ~/jas-versions/$(VERSION)/examples jas.rb -C ~/jas-versions/$(VERSION)/examples jas.py
	cd ~/jas-versions/$(VERSION)/; ant doc > ant_doc.out
	cd ~/jas-versions/$(VERSION)/; epydoc -v -o doc/jython -n "Python to JAS" -u ../../index.html examples/jas.py > epydoc.out
	cd ~/jas-versions/$(VERSION)/; jrdoc -o doc/jruby -U -S -N -t "Ruby to JAS" examples/jas.rb > rdoc.out 2>&1
	cd ~/jas-versions/$(VERSION)/; jar -cfM ../$(VERSION).`$(SVNREV)`-doc.zip doc/ *.html *.css
	cd ~/jas-versions/$(VERSION)/; ant test > ant_test.out
	cd ~/jas-versions/$(VERSION)/; sh ./jython_tests.sh >jython_tests.out 2>&1
	cd ~/jas-versions/$(VERSION)/; sh ./jruby_tests.sh >jruby_tests.out 2>&1
	cp ~/jas-versions/$(VERSION).`$(SVNREV)`-bin.jar $(LIBPATH)/jas.jar
	cp ~/jas-versions/$(VERSION).`$(SVNREV)`-bin.jar ~/jas-versions/$(VERSION)/jas.jar
	mv ~/jas-versions/$(VERSION).`$(SVNREV)`-*.jar ~/jas-versions/$(VERSION)/
	mv ~/jas-versions/$(VERSION).`$(SVNREV)`-*.zip ~/jas-versions/$(VERSION)/
	cd ~/jas-versions/$(VERSION)/meditor; jas_dosed $(VERSION) `$(SVNREV)` manifest.mf
	cd ~/jas-versions/$(VERSION)/meditor; make > ~/jas-versions/$(VERSION)/make_meditor.out
	cd ~/jas-versions/log4j_adapter; make > ~/jas-versions/$(VERSION)/make_mylog.out
	cp ~/java/lib/mylog.jar ~/jas-versions/$(VERSION)/
	cd ~/jas-versions/jlinalg_adapter; make > ~/jas-versions/$(VERSION)/make_jlinalg.out
	cp ~/java/lib/jlinalg_adapter.jar ~/jas-versions/$(VERSION)/
	cd ~/jas-versions/commons-math_adapter; make > ~/jas-versions/$(VERSION)/make_commons-math.out
	cp ~/java/lib/commons-math_adapter.jar ~/jas-versions/$(VERSION)/
	cd ~/jas-versions/$(VERSION)/jython; make > ~/jas-versions/$(VERSION)/make_jython.out

deploy:
	$(RSYNC) -e 'ssh -p 2222' --delete-after --exclude=DTD --exclude=*xml ~/jas-versions/$(VERSION)/ krum:htdocs/$(VERSION)


git-export:
	cd ~/jas-versions/jas-git/jas; git svn rebase > ~/jas-versions/$(VERSION)/git_svn.out
	cd ~/jas-versions/jas-git/jas; git push -v deploy > ~/jas-versions/$(VERSION)/git_push.out

git-deploy:
	$(RSYNC) -e 'ssh -p 2222' --delete-after ~/jas-versions/jas-git/jas.git/ krum:htdocs/jas.git
	cd ~/jas-versions/jas-git/jas; git push -v $(DRY) google >> ~/jas-versions/$(VERSION)/git_push.out


young:
	echo youngest revision `svnlook youngest $(SVNREPO)/jas`

subst:
	cd ~/jas-versions/$(VERSION); jas_dosed $(VERSION) `$(SVNREV)` download.html


# lines of code and number of classes
loc: young
	(find src -name "*.java"; find trc -name "*.java")| wc -l
	find src -name "*.java" | grep -v Test | wc -l
	find trc -name "*.java" | grep    Test | wc -l 
	(find src -name "*.java"; find trc -name "*.java") | xargs cat | wc
	find src -name "*.java" | grep -v Test | xargs cat | wc
	find trc -name "*.java" | grep    Test | xargs cat | wc
	find trc -name "*.java" | grep    Test | xargs cat | grep "void test" | wc -l 
	find ~/jas-versions/log4j_adapter -name "*.java" | wc -l 
	find ~/jas-versions/log4j_adapter -name "*.java" | xargs cat | wc
	find ~/jas-versions/jlinalg_adapter -name "*.java" | wc -l
	find ~/jas-versions/jlinalg_adapter -name "*.java" | xargs cat | wc
	find ~/jas-versions/commons-math_adapter -name "*.java" | wc -l
	find ~/jas-versions/commons-math_adapter -name "*.java" | xargs cat | wc
	cat examples/jas.py | wc
	find examples -name "*.py" | grep -v jas.py | xargs cat | wc
	cat examples/jas.rb | wc
	find examples -name "*.rb" | grep -v jas.rb | xargs cat | wc

# -eof-
