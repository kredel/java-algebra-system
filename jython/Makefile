#
# $Id$
#
# Makefile for the interface of JAS to jython
# by Raphael Jolly, Heinz kredel
#

#JASPATH=$(HOME)/jas
JASPATH=..
LIBPATH=$(HOME)/java/lib

JYTHONPATH=$(HOME)/java/jython/jython.jar
JRUBYPATH=$(HOME)/java/jruby/lib/jruby.jar:$(HOME)/java/jruby/lib/ruby/stdlib

LOG4JPATH=$(LIBPATH)/log4j-core-2.13.2.jar:$(LIBPATH)/log4j-api-2.13.2.jar
JUNITPATH=$(LIBPATH)/junit.jar:$(LIBPATH)/hamcrest-core.jar
CLASSPATH=$(LOG4JPATH):$(JUNITPATH):$(JASPATH):$(LIBPATH)/jas.jar

BOOTCLASSPATH6="/usr/lib64/jvm/java-1.6.0/jre/lib/rt.jar"

.SUFFIXES : .class .java .jar 
.PHONY    : clean

FILES=$(wildcard src/edu/jas/*/*.java)
#echo $(FILES) $(JASPATH)/examples/jas.py
CLASSES=$(subst src,classes,$(subst java,class,$(FILES)))
#echo $(CLASSES)

#DOCOPTS=-package
DOCOPTS=-package -author -version -linksource -overview overview.html
DOCCLASSES=$(CLASSPATH)
DOC=javadoc -classpath $(DOCCLASSES):$(JYTHONPATH)

all: clean jas-jython.jar

$(CLASSES): $(FILES)
	-mkdir classes
	javac -g -classpath $(JYTHONPATH):$(CLASSPATH) -sourcepath src -d classes src/edu/jas/*/*.java
	#javac -g -source 1.6 -target 1.6 -classpath $(JYTHONPATH):$(CLASSPATH) -bootclasspath $(BOOTCLASSPATH6) -sourcepath src -d classes src/edu/jas/*/*.java

jas-jython.jar: $(CLASSES) $(JASPATH)/examples/jas.py services/javax.script.ScriptEngineFactory
	-mkdir classes/META-INF
	-mkdir classes/META-INF/services
	-cp -a manifest.mf classes/META-INF/MANIFEST.MF
	#cp -a services/javax.script.ScriptEngineFactory classes/META-INF/services/
	-cp -a $(JASPATH)/examples/jas.py $(JASPATH)/examples/*.class $(JASPATH)/examples/basic_sigbased_gb.py classes
	#-cp -a $(JASPATH)/examples/jas.pyc classes
	-cp -a $(JASPATH)/examples/jas.rb classes
	-cp -a $(JASPATH)/log4j2.properties classes
	jar cfM jas-jython.jar -C classes .
	cp -f jas-jython.jar $(LIBPATH)
	cp -f jas-jython.jar ..

clean:
	rm -rf classes
	find . -name "*~" -follow -print -exec rm {} \;

tests: jas-jython.jar
	java -client -cp $(JYTHONPATH):$(JRUBYPATH):$(CLASSPATH):jas-jython.jar edu.jas.gb.GBSigBasedTest

exam: jas-jython.jar
	java -client -cp $(JYTHONPATH):$(JRUBYPATH):$(CLASSPATH):jas-jython.jar edu.jas.kern.ScriptingExample

doc: $(FILES)
	$(DOC) $(DOCOPTS) -d ../doc/jas-jython $(FILES)
