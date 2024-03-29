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
#JDK=/usr/lib/java/bin
#JDK=/usr/java/latest/bin
JDK=/usr/lib64/jvm/java/bin
#JDK=/usr/lib/jvm/java-1.5.0-sun-1.5.0/bin

#JASPATH=$(HOME)/jas
SVNREPO=/home/SUBVERSION
LIBPATH=$(HOME)/java/lib
#JUNITPATH=$(LIBPATH)/junit.jar
JUNITPATH=$(LIBPATH)/junit-4.13.1.jar:$(LIBPATH)/hamcrest-core-1.3.jar
#LOG4JPATH=$(LIBPATH)/log4j-1.2.17.jar
#LOG4JPATH=$(LIBPATH)/log4j-core-2.5.jar:$(LIBPATH)/log4j-api-2.5.jar:$(LIBPATH)/log4j-1.2-api-2.5.jar
LOG4JPATH=$(LIBPATH)/log4j-core-2.17.1.jar:$(LIBPATH)/log4j-api-2.17.1.jar
#LOG4JPATH=$(LIBPATH)/mylog.jar
#LOG4JPATH=$(LIBPATH)/nolog.jar
JOMPPATH=$(LIBPATH)/jomp1.0b.jar
TNJPATH=$(LIBPATH)/tnj.jar
LINTPATH=$(LIBPATH)/lint4j.jar
PYPATH=$(LIBPATH)/jython.jar

# --- syncing ----------
DRY=--dry-run
DELETE=
RSYNC=rsync -e ssh -avuz $(DRY) $(DELETE) --exclude=*~ --include=doc/git_change.log --include=jdepend-report.txt --exclude=*.log* --exclude=*.out* --exclude=*.txt* --exclude=.svn --exclude=build/ --exclude=target/ --exclude=reports/ --exclude=.gradle/ --exclude=*.exec
####--exclude=./test
####--exclude=*.ps --exclude=*.pdf --exclude=spin*
####--exclude=*/.jxta/
PART=jas.j18
VERSION=jas-2.7
DEBVERSION=jas-java_2.7
#SVNVERSION=`grep committed-rev .svn/entries |head -1|awk -F = '{ print $2 }'|sed 's/"//g'`

all: usage

home:
	$(RSYNC) krum:java/$(PART)/     .

heinz:
	$(RSYNC) ./                heinz@heinz2:$(PART)

krum:
	$(RSYNC) -e 'ssh' ./       krum:java/$(PART)

pub:
	$(RSYNC) --exclude=*ufd* --exclude=DTD --exclude=lisa* --exclude=*xml ./ krum:htdocs/$(PART)

compute:
	$(RSYNC) ./                compute:java/$(VERSION)

# --- end syncing ----------


# no need to change below this line

# command line arguments, cl is deprecated, use args
cl=
args=$(cl)

#.EXPORT_ALL_VARIABLES :

#DEFS=$(JASPATH)/arith:$(JASPATH)/poly:$(JASPATH)/ps:$(JASPATH)/vector:$(JASPATH)/gb:$(JASPATH)/ufd:$(JASPATH)/gbmod:$(JASPATH)/util:$(JASPATH)/application:$(JASPATH)/root
DOCCLASSES=$(JUNITPATH):$(LOG4JPATH):$(JOMPPATH)
#:$(TNJPATH)
#DOCOPTS=-package
#DOCOPTS=-package -version -author
DOCOPTS=-public -protected -package -author -version

#MYCLASSPATH = .:$(DEFS):$(JUNITPATH):$(JOMPPATH)
#MYCLASSPATH = $(LOG4JPATH):.:$(DEFS):$(JUNITPATH):$(JOMPPATH):$(PYPATH)
MYCLASSPATH = $(LOG4JPATH):.:$(JUNITPATH):$(JOMPPATH):$(PYPATH)
#:$(TNJPATH)

#JAVA_MEM=-Xms1500M -Xmx2900M
#JAVA_MEM=-Xms800M -Xmx1500M
JAVA_MEM=

#SOPTS="-J-cp ../lib/log4j-core-2.5.jar:../lib/log4j-api-2.5.jar:../lib/junit.jar-4.13.1.jar:../lib/hamcrest-core-1.3.jar:. -J-verbose:gc -J-Xms500M -J-Xmx900M"
SOPTS="-J-cp ../lib/log4j-core-2.17.1.jar:../lib/log4j-api-2.17.1.jar:../lib/junit.jar-4.13.1.jar:../lib/hamcrest-core-1.3.jar:. "
#SOPTS="-J-verbose:gc -J-Xms1500M -J-Xmx2900M"
#SOPTS="-J-verbose:gc -J-Xms500M -J-Xmx900M"


JAVAC=$(JDK)/javac -classpath $(MYCLASSPATH) -d . -Xlint:unchecked
# -verbose:class 
#-Djava.util.logging.config.file=logging.properties 
#JAVA=$(JDK)/java -classpath $(MYCLASSPATH) -verbose:gc 
JAVA=$(JDK)/java -classpath $(MYCLASSPATH) -server $(JAVA_MEM) -XX:+AggressiveHeap -XX:+UseParallelGC -XX:ParallelGCThreads=2 -verbose:gc 
#-Xrunhprof:cpu=samples,heap=sites,force=n
#-Xbatch
#JAVA=$(JDK)/java -classpath $(MYCLASSPATH) -verbose:gc -Xrunhprof:cpu=times,format=a
#JAVA=$(JDK)/java -classpath $(MYCLASSPATH) -verbose:gc -verbose:class -verbose:jni
DOC=$(JDK)/javadoc -classpath $(DOCCLASSES)

usage:
	echo; echo "usage: make <class> args='cmd'"; echo


FIX       = fixm2j
GETC      = getc.pl
#JOPT      = -prof:gb.prof -tm
#-mx100000000


.SUFFIXES :
.SUFFIXES : .class .java 
.PRECIOUS : %.java %.class edu/jas/arith/%.class edu/jas/poly/%.class edu/jas/ps/%.class edu/jas/gb/%.class edu/jas/vector/%.class edu/jas/ufd/%.class edu/jas/fd/%.class edu/jas/gbmod/%.class edu/jas/structure/%.class edu/jas/util/%.class edu/jas/application/%.class edu/jas/kern/%.class edu/jas/root/%.class edu/jas/%.class

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


edu/jas/fd/%Test.class: trc/edu/jas/fd/%Test.java 
	$(JAVAC) $<

edu/jas/fd/%.class: src/edu/jas/fd/%.java 
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
	$(JAVA) $@ $(args)

edu.jas.arith.%: edu/jas/arith/%.class
	$(JAVA) $@ $(args)

edu.jas.poly.%: edu/jas/poly/%.class
	$(JAVA) $@ $(args)

edu.jas.ps.%: edu/jas/ps/%.class
	$(JAVA) $@ $(args)

edu.jas.gb.%: edu/jas/gb/%.class
	$(JAVA) $@ $(args)

edu.jas.ufd.%: edu/jas/ufd/%.class
	$(JAVA) $@ $(args)

edu.jas.fd.%: edu/jas/fd/%.class
	$(JAVA) $@ $(args)

edu.jas.gbufd.%: edu/jas/gbufd/%.class
	$(JAVA) $@ $(args)

edu.jas.vector.%: edu/jas/vector/%.class
	$(JAVA) $@ $(args)

edu.jas.gbmod.%: edu/jas/gbmod/%.class
	$(JAVA) $@ $(args)

edu.jas.structure.%: edu/jas/structure/%.class
	$(JAVA) $@ $(args)

edu.jas.util.%: edu/jas/util/%.class
	$(JAVA) $@ $(args)

edu.jas.application.%: edu/jas/application/%.class
	$(JAVA) $@ $(args)

edu.jas.root.%: edu/jas/root/%.class
	$(JAVA) $@ $(args)

edu.jas.kern.%: edu/jas/kern/%.class
	$(JAVA) $@ $(args)

edu.jas.integrate.%: edu/jas/integrate/%.class
	$(JAVA) $@ $(args)

edu.mas.kern.%: edu/mas/kern/%.class
	$(JAVA) $@ $(args)


FILES=$(wildcard src/edu/jas/structure/*.java src/edu/jas/arith/*.java src/edu/jas/poly/*.java src/edu/jas/ps/*.java src/edu/jas/gb/*.java src/edu/jas/application/*.java src/edu/jas/vector/*.java src/edu/jas/gbmod/*.java src/edu/jas/gbufd/*.java src/edu/jas/util/*.java src/edu/jas/ufd/*.java src/edu/jas/fd/*.java src/edu/jas/kern/*.java src/edu/jas/root/*.java src/edu/jas/integrate/*.java)

TESTFILES=$(wildcard trc/edu/jas/structure/*.java trc/edu/jas/arith/*.java trc/edu/jas/poly/*.java trc/edu/jas/ps/*.java trc/edu/jas/gb/*.java trc/edu/jas/application/*.java trc/edu/jas/vector/*.java trc/edu/jas/gbmod/*.java trc/edu/jas/gbufd/*.java trc/edu/jas/util/*.java trc/edu/jas/ufd/*.java trc/edu/jas/fd/*.java trc/edu/jas/kern/*.java trc/edu/jas/root/*.java trc/edu/jas/integrate/*.java)

LIBS=$(subst :,, $(JUNITPATH)) $(subst :,, $(LOG4JPATH)) $(JOMPPATH) $(TNJPATH)

CLASSES=edu/jas/structure/ edu/jas/arith/ edu/jas/poly/ edu/jas/ps/ edu/jas/gb/ edu/jas/gbufd edu/jas/application/ edu/jas/vector/ edu/jas/gbmod/ edu/jas/util/ edu/jas/ufd/ edu/jas/fd/ edu/jas/kern/ edu/jas/root/ edu/jas/integrate/

PYS=$(wildcard *.py)
EXAMPY=$(wildcard examples/*.py)
EXAMRB=$(wildcard examples/*.rb)
EXAMJAS=$(subst examples/,, $(wildcard examples/*.jas))

DOCU=$(wildcard doc/jas-log.html index.html doc/problems.html doc/design.html COPYING* sample.jythonrc doc/overview.html)
# */package.html 

doc: $(FILES) $(TESTFILES)
	$(DOC) $(DOCOPTS) -d doc/api $(FILES) $(TESTFILES)

epydoc: examples/jas.py
#	epydoc -o doc/jython -n "Python to JAS" -u ../../index.html `find examples -name "*.py"|grep -v versuch`
#       --exclude-parse=java.lang --exclude-parse=com.xhaus.jyson 
	epydoc --check -o doc/jython -n "Python to JAS" -u ../../index.html examples/jas.py examples/basic_sigbased_gb.py examples/sdjas.py

rdoc: examples/jas.rb
	jrdoc -o doc/jruby -C -U -N -t "Ruby to JAS" examples/jas.rb examples/sdjas.rb

texdoc: $(FILES) $(TESTFILES)
	mkdir -p doc/tex
	rm -f doc/tex/*
	$(DOC) $(DOCOPTS) -doclet TexGen -docletpath ~/java/lib/texgen.jar -dest doc/tex $(FILES) $(TESTFILES) 
	ls doc/tex/* | grep -v Test | grep -v allclasses | xargs cat > doc/tex/allclasses.tex
	sed -i -f doc/totex.sed doc/tex/allclasses.tex
	cd doc; pdflatex jas_texgen.tex

ALLJAR=$(FILES) $(TESTFILES) $(DOCU) Makefile build.xml $(PYS)
#log4j2.properties

jas-all.jar: $(ALLJAR)
	$(JDK)/jar -cvf jas.jar $(ALLJAR) edu/ 
	cp jas.jar $(LIBPATH)/
	mv jas.jar /tmp/jas-`date +%Y%j`.jar

jas.tgz: $(FILES) $(TESTFILES) *.html doc/*.html doc/TODO README
	tar -cvzf jas.tgz $(FILES) $(TESTFILES) *.html doc/*.html doc/TODO README
	cp jas.tgz /tmp/jas-`date +%Y%j`.tgz

#	cp jas.jar ...../jas-`date +%Y%j`.jar
#jas-run.jar: GBManifest.MF $(TOJAR)
#	$(JDK)/jar -cvfm jas-run.jar GBManifest.MF $(TOJAR)


TOJAR=$(FILES) $(TESTFILES) $(CLASSES) Makefile build.xml $(EXAMPY) examples/machines.test $(wildcard COPYING*)
#log4j2.properties 

jas.jar: $(FILES) $(TESTFILES) Makefile build.xml $(EXAMPY) #log4j2.properties 
	$(JDK)/jar -cf jas.jar $(TOJAR)
	cp jas.jar $(LIBPATH)/
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


testp:
	find examples -name "*.py"|grep -v jas.py |grep -v sdexam.py |grep -v plot|grep -v versuch|sort|xargs -L 1 echo "time jython $(SOPTS)" | awk '{ printf "echo %s\n", $$0; printf "%s\n", $$0 }' > ./all_jython.sh
	time bash -i all_jython.sh 2>&1 | tee tjy.out
	-egrep '(Error|ERROR|File|Exception)' tjy.out

testr:
	find examples -name "*.rb"|grep -v jas.rb |grep -v sdexam.rb |grep -v versuch|sort|xargs -L 1 echo "time jruby $(SOPTS) -I." | awk '{ printf "echo %s\n", $$0; printf "%s\n", $$0 }' > ./all_jruby.sh
	time bash -i ./all_jruby.sh 2>&1 | tee tjr.out
	-egrep -i '(error|__file__|exception|failed)' tjr.out

tests:
	ant test 2>&1 | tee t.out
	ant exam 2>&1 | tee e.out
	make edu.jas.application.RunGB cl="seq  examples/trinks6.jas"   2>&1 | tee tr.out
	make edu.jas.application.RunGB cl="seq+ examples/trinks6.jas"   2>&1 | tee -a tr.out
	make edu.jas.application.RunGB cl="par  examples/trinks6.jas 4" 2>&1 | tee -a tr.out
	make edu.jas.application.RunGB cl="par+ examples/trinks6.jas 4" 2>&1 | tee -a tr.out
	cd jython; make tests 2>&1 | tee jsr.out
	cd jython; make exam  2>&1 | tee -a jsr.out
	cd mpj; make tests 2>&1 | tee mpj.out
	#cd mpi; make tests 2>&1 | tee mpi.out
	cd jlinalg_adapter; make tests 2>&1 | tee ja.out
	cd commons-math_adapter; make tests 2>&1 | tee ja.out
	echo "--------------------"
	-grep FAIL t.out
	-grep Exception e.out | grep -v GCDProxy | grep -v GBProxy
	-egrep '(Exception|Usage|Error|ERROR)' tr.out
	-egrep '(Error|ERROR|File|Exception)' tjy.out
	-egrep -i '(error|__file__|exception|failed)' tjr.out
	-egrep -i '(Error|File|Exception)' jython/jsr.out mpj/mpj.out jlinalg_adapter/ja.out
	#-grep -i error mpi/mpi.out

metrics:
	ant jdepend
	../java/javancss-32.53/bin/javancss -all -recursive -out test/javanccs-`date +%Y-%m-%d`.out src

findbugs:
	cd ~/java/findbugs; bin/fb analyze -sortByClass -medium -html -nested:false /home/kredel/jas/edu/jas/ > jas.findbugs-report.medium_`date +"%Y-%m-%d"`_.html

findbugs-low:
	cd ~/java/findbugs; bin/fb analyze -sortByClass -low -html -nested:false /home/kredel/jas/edu/jas/ > jas.findbugs-report.low_`date +"%Y-%m-%d"`_.html


#svn copy file:///$(SVNREPO)/jas/trunk file:///$(SVNREPO)/jas/tags/$(VERSION)

#SVNREV=svnlook youngest $(SVNREPO)/jas
#SVNDATE=svnlook date $(SVNREPO)/jas
# new numbering in git:
#SVNDATE=git log -r HEAD --date=iso .|grep Date:|head -1|awk '{print $2}'
# Jan 2008 SVNSRT=1584 
# Jun 2008 SVNSRT=1882
# Sep 2008 SVNSRT=2118r
# jan 2009 SVNSRT=2288
# jun 2009 SVNSRT=2668
# jan 2010 SVNSRT=2978
# jun 2010 SVNSRT=3188
# jan 2011 SVNSRT=3458
# jul 2011 SVNSRT=3688
# dec 2011 SVNSRT=3838
# jul 2012 SVNSRT=4008
# aug 2013 SVNSRT=4588
# jan 2014 SVNSRT=4742
# jan 2015 SVNSRT=5055
# jan 2018 SVNSRT=5787
# jan 2019 SVNSRT=5966
# jan 2020 SVNSRT=5990
SVNREV=200
GITREV=$(SVNREV)
GITSINCE=2021-01-01
#GITSINCE=2019-01-01
#GITSINCE=2016-03-26

# git archive --format=tar --prefix=$(VERSION)/ HEAD | (cd ~/jas-versions/ && tar -xf -)
#svn export --quiet file:///$(SVNREPO)/jas/trunk ~/jas-versions/$(VERSION)
#svn log -v -r HEAD:$(SVNSRT) file:///$(SVNREPO)/jas/trunk src trc examples jython mpj mpi jlinalg_adapter commons-math_adapter > ~/jas-versions/$(VERSION)/doc/git_change.log


export:
	rm -rf ~/jas-versions/$(VERSION)
	git archive --format=tar --prefix=$(VERSION)/ HEAD | (cd ~/jas-versions/ && tar -xf -)
	cd ~/jas-versions/$(VERSION); jas_dosed $(VERSION) $(GITREV) doc/download.html $(DEBVERSION)
	cd ~/jas-versions/$(VERSION); jas_dosed $(VERSION) $(GITREV) doc/Dockerfile $(DEBVERSION)
	git log -r --since $(GITSINCE) --name-status --date=iso . src trc examples jython mpj mpi jlinalg_adapter commons-math_adapter > ~/jas-versions/$(VERSION)/doc/git_change.log
	cd ~/jas-versions/; jar -cfM $(VERSION).$(GITREV)-src.zip $(VERSION)/
	cd ~/jas-versions/$(VERSION)/; ant compile > ant_compile.out
	cd ~/jas-versions/$(VERSION)/; jar -cfm ../$(VERSION).$(GITREV)-bin.jar GBManifest.MF edu/ COPYING* #log4j2.properties
	cd ~/jas-versions/$(VERSION)/; jar -uf ../$(VERSION).$(GITREV)-bin.jar -C ~/jas-versions/$(VERSION)/examples jas.rb -C ~/jas-versions/$(VERSION)/examples jas.py
	jar -uf ~/jas-versions/$(VERSION).$(GITREV)-bin.jar -C ~/jas/NOTES COPYING.APACHE-2.0 -C ~/jas/NOTES COPYING.apache.jas
	cd ~/jas-versions/$(VERSION)/; ant doc > ant_doc.out
	cd ~/jas-versions/$(VERSION)/; epydoc -v -o doc/jython -n "Python to JAS" -u ../../index.html examples/jas.py examples/basic_sigbased_gb.py examples/sdjas.py > epydoc.out
	cd ~/jas-versions/$(VERSION)/; jrdoc -o doc/jruby -U -N -t "Ruby to JAS" examples/jas.rb examples/sdjas.rb > rdoc.out 2>&1
	cd ~/jas-versions/$(VERSION)/; ant test > ant_test.out
	cd ~/jas-versions/$(VERSION)/; sh ./jython_tests.sh >jython_tests.out 2>&1
	cd ~/jas-versions/$(VERSION)/; sh ./jruby_tests.sh >jruby_tests.out 2>&1
	cp ~/jas-versions/$(VERSION).$(GITREV)-bin.jar $(LIBPATH)/jas.jar
	cp ~/jas-versions/$(VERSION).$(GITREV)-bin.jar ~/jas-versions/$(VERSION)/jas.jar
	cd ~/jas-versions/$(VERSION)/jython; make all doc > ~/jas-versions/$(VERSION)/make_jython.out 2>&1
	cd ~/jas-versions/$(VERSION)/mpi; make all doc > ~/jas-versions/$(VERSION)/make_mpi.out 2>&1
	cd ~/jas-versions/$(VERSION)/mpj; make all doc > ~/jas-versions/$(VERSION)/make_mpj.out 2>&1
	cd ~/jas-versions/$(VERSION)/meditor; jas_dosed $(VERSION) $(GITREV) manifest.mf
	cd ~/jas-versions/$(VERSION)/meditor; make > ~/jas-versions/$(VERSION)/make_meditor.out 2>&1
	#cd ~/jas-versions/log4j_adapter; make > ~/jas-versions/$(VERSION)/make_mylog.out
	#cp ~/java/lib/mylog.jar ~/jas-versions/$(VERSION)/
	#cd ~/jas-versions/log4j_droid_adapter; make > ~/jas-versions/$(VERSION)/make_droidlog.out
	#cp ~/java/lib/droidlog.jar ~/jas-versions/$(VERSION)/
	#cd ~/jas-versions/log4j_null_adapter; make > ~/jas-versions/$(VERSION)/make_nolog.out
	#cp ~/java/lib/nolog.jar ~/jas-versions/$(VERSION)/
	cd ~/jas-versions/$(VERSION)/jlinalg_adapter; make all doc > ~/jas-versions/$(VERSION)/make_jlinalg.out 2>&1
	cd ~/jas-versions/$(VERSION)/commons-math_adapter; make all doc > ~/jas-versions/$(VERSION)/make_commons-math.out 2>&1
	cd ~/jas-versions/$(VERSION)/; jar -cfM ../$(VERSION).$(GITREV)-doc.zip doc/ images/ *.html doc/*.html *.css doc/*.css
	mv ~/jas-versions/$(VERSION).$(GITREV)-*.jar ~/jas-versions/$(VERSION)/
	mv ~/jas-versions/$(VERSION).$(GITREV)-*.zip ~/jas-versions/$(VERSION)/
	cd ~/jas-versions/$(VERSION)/; chmod -v +r *.jar *.zip >chmod.out 2>&1

deploy:
	$(RSYNC) -e 'ssh' --delete-after --exclude=DTD --exclude=*xml ~/jas-versions/$(VERSION)/ krum:htdocs/$(VERSION)

m2-deploy:
	$(RSYNC) -e 'ssh' ~/jas-versions/tmp/$(subst jas-,,$(VERSION)).$(GITREV)/ krum:htdocs/maven-repository/de/uni-mannheim/rz/krum/$(subst -,/,$(VERSION)).$(GITREV)
#	$(RSYNC) -e 'ssh' ~/jas-versions/tmp/$(subst jas-,,$(VERSION)).$(GITREV)/ https://oss.sonatype.org/content/groups/public/de/uni-mannheim/rz/krum/$(subst -,/,$(VERSION)).$(GITREV)

git-export:
	#cd ~/jas-versions/jas-git/jas; git svn rebase > ~/jas-versions/$(VERSION)/git_svn.out
	#cd ~/jas-versions/jas-git/jas; git push -v deploy > ~/jas-versions/$(VERSION)/git_push.out
	cd ~/jas; git push -v deploy > ~/jas-versions/$(VERSION)/git_push.out

svn-export:
	cd ~/jas; git svn dcommit > ~/jas-versions/$(VERSION)/git_svn.out

git-deploy:
	$(RSYNC) -e 'ssh' --delete-after ~/jas-versions/jas-git/jas.git/ krum:htdocs/jas.git
	#cd ~/jas-versions/jas-git/jas.git; git push -v $(DRY) google >> ~/jas-versions/$(VERSION)/git_push.out
	cd ~/jas-versions/jas-git/jas.git; git push -v $(DRY) github >> ~/jas-versions/$(VERSION)/git_push.out

jas-bin.jar:
	jar -cfm $(VERSION).$(GITREV)-bin.jar GBManifest.MF edu/ COPYING* #log4j2.properties
	jar -uf  $(VERSION).$(GITREV)-bin.jar -C examples jas.rb -C examples jas.py

#jar -ufm ../$(VERSION)-gbb-bin.jar ~/java/lib/log4j.dir/META-INF/MANIFEST.MF -C ~/java	/lib/log4j.dir/ org/ 
#echo "EXAMJAS" $(EXAMJAS)
mkbench:
	jar -cfm $(VERSION)-gbb-bin.jar GBManifest.MF edu/ COPYING* #log4j2.properties
	#jar -ufe $(VERSION)-gbb-bin.jar edu.jas.application.RunMPJGB -C mpj/classes/ edu/
	jar -uf $(VERSION)-gbb-bin.jar -C mpj/classes/ edu/
	jar -uf $(VERSION)-gbb-bin.jar -C ~/java/lib/log4j.dir/ org/ 
	cd examples; jar -cf examples.jar $(EXAMJAS)

runbench:
	java -jar $(VERSION)-gbb-bin.jar seq katsura5.jas

	#echo youngest revision `svnlook youngest $(SVNREPO)/jas`
young:
	echo youngest revision `git rev-list -n1 HEAD`

subst:
	cd ~/jas-versions/$(VERSION); jas_dosed $(VERSION) $(GITREV) doc/download.html $(DEBVERSION)

svn-logs:
	svn log -v -r HEAD:$(SVNSRT) file:///$(SVNREPO)/jas/trunk src trc examples jython mpj mpi jlinalg_adapter commons-math_adapter > doc/svn_change.log

git-logs:
	git log -r --since $(GITSINCE) --name-status --date=iso . src trc examples jython mpj mpi jlinalg_adapter commons-math_adapter > doc/git_change.log


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
	find jlinalg_adapter -name "*.java" | wc -l
	find jlinalg_adapter -name "*.java" | xargs cat | wc
	find commons-math_adapter -name "*.java" | wc -l
	find commons-math_adapter -name "*.java" | xargs cat | wc
	find jython -name "*.java" | wc -l 
	find jython -name "*.java" | xargs cat | wc
	find mpi -name "*.java" | wc -l 
	find mpi -name "*.java" | xargs cat | wc
	find mpj -name "*.java" | wc -l 
	find mpj -name "*.java" | xargs cat | wc
	cat examples/jas.py | wc
	find examples -name "*.py" | grep -v jas.py | xargs cat | wc
	cat examples/jas.rb | wc
	find examples -name "*.rb" | grep -v jas.rb | xargs cat | wc

# -eof-
