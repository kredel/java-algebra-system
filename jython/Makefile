#
# $Id$
#
# Makefile for the interface of JAS to jython
# by Raphael Jolly, Heinz kredel
#

#JASPATH=$(HOME)/jas
JASPATH=..
LIBPATH=$(HOME)/java/lib

JYTHONPATH=$(LIBPATH)/jython.jar
JRUBYPATH=$(LIBPATH)/jruby.jar:$(HOME)/java/jruby-1.5.6/lib/ruby/1.8

CLASSPATH=$(LIBPATH)/log4j.jar:$(LIBPATH)/junit.jar:$(LIBPATH)/jas.jar

BOOTCLASSPATH6="/usr/lib64/jvm/java-1.6.0/jre/lib/rt.jar"

.SUFFIXES : .class .java .jar 
.PHONY    : clean

FILES=$(wildcard src/edu/jas/*/*.java)
#echo $(FILES) $(JASPATH)/examples/jas.py
CLASSES=$(subst src,classes,$(subst java,class,$(FILES)))
#echo $(CLASSES)

#DOCOPTS=-package
DOCOPTS=-package -author -version -linksource
DOCCLASSES=$(CLASSPATH)
DOC=javadoc -classpath $(DOCCLASSES):$(JYTHONPATH)

all: clean jas-jython.jar doc

$(CLASSES): $(FILES)
	mkdir classes
	javac -g -source 1.6 -target 1.6 -classpath $(JYTHONPATH):$(CLASSPATH) -bootclasspath $(BOOTCLASSPATH6) -sourcepath src -d classes src/edu/jas/*/*.java

jas-jython.jar: $(CLASSES) $(JASPATH)/examples/jas.py services/javax.script.ScriptEngineFactory
	mkdir classes/META-INF
	mkdir classes/META-INF/services
	cp -a manifest.mf classes/META-INF/MANIFEST.MF
	#cp -a services/javax.script.ScriptEngineFactory classes/META-INF/services/
	cp -a $(JASPATH)/examples/jas.py $(JASPATH)/examples/*.class $(JASPATH)/examples/basic_sigbased_gb.py classes
	-cp -a $(JASPATH)/examples/jas.pyc classes
	cp -a $(JASPATH)/examples/jas.rb classes
	cp -a $(JASPATH)/log4j.properties classes
	jar cfM jas-jython.jar -C classes .
	cp -f jas-jython.jar $(LIBPATH)
	cp -f jas-jython.jar ..

clean:
	rm -rf classes
	find . -name "*~" -follow -print -exec rm {} \;

tests:
	java -client -cp $(JYTHONPATH):$(JRUBYPATH):$(CLASSPATH):jas-jython.jar edu.jas.kern.ScriptingExample
	java -client -cp $(JYTHONPATH):$(JRUBYPATH):$(CLASSPATH):jas-jython.jar edu.jas.gb.GBSigBasedTest

doc: $(FILES)
	$(DOC) $(DOCOPTS) -d ../doc/jas-jython $(FILES)
