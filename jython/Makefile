#
# $Id$
#
# Makefile for the interface of JAS to jython
# by Raphael Jolly, Heinz kredel
#

#JASPATH=$(HOME)/jas
JASPATH=..
LIBPATH=$(HOME)/java/lib

CLASSPATH=$(LIBPATH)/jython.jar

JARSPATH=$(LIBPATH)/log4j.jar:$(LIBPATH)/jas.jar

.SUFFIXES : .class .java .jar 
.PHONY    : clean

FILES=$(wildcard src/edu/jas/kern/*.java)
#echo $(FILES) $(JASPATH)/examples/jas.py
CLASSES=$(subst src,classes,$(subst java,class,$(FILES)))
#echo $(CLASSES)

all: clean jas-jython.jar

$(CLASSES): $(FILES)
	mkdir classes
	javac -g -source 1.6 -target 1.6 -classpath $(CLASSPATH) -sourcepath src -d classes src/edu/jas/kern/*.java

jas-jython.jar: $(CLASSES) $(JASPATH)/examples/jas.py services/javax.script.ScriptEngineFactory
	mkdir classes/META-INF
	mkdir classes/META-INF/services
	cp -a manifest.mf classes/META-INF/MANIFEST.MF
	cp -a services/javax.script.ScriptEngineFactory classes/META-INF/services/
	cp -a $(JASPATH)/examples/jas.py $(JASPATH)/examples/jas.pyc $(JASPATH)/examples/*.class classes
	cp -a $(JASPATH)/log4j.properties classes
	jar cfM jas-jython.jar -C classes .
	cp -f jas-jython.jar $(LIBPATH)
	cp -f jas-jython.jar ..

clean:
	rm -rf classes

tests:
	java -cp $(CLASSPATH):$(JARSPATH):jas-jython.jar edu.jas.kern.ScriptingExample
