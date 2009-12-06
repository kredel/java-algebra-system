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

.SUFFIXES : .class .java .jar 
.PHONY    : clean

FILES=$(wildcard src/edu/jas/kern/*.java)
#echo $(FILES) $(JASPATH)/examples/jas.py
CLASSES=$(subst src,classes,$(subst java,class,$(FILES)))
#echo $(CLASSES)

all: jas-jython.jar

$(CLASSES): $(FILES)
	mkdir classes
	javac -g -source 1.6 -target 1.6 -classpath $(CLASSPATH) -sourcepath src -d classes src/edu/jas/kern/*.java

jas-jython.jar: $(CLASSES) $(JASPATH)/examples/jas.py services/javax.script.ScriptEngineFactory
	mkdir classes/META-INF
	mkdir classes/META-INF/services
	cp -a manifest.mf classes/META-INF/MANIFEST.MF
	cp -a services/javax.script.ScriptEngineFactory classes/META-INF/services/
	jar cfM jas-jython.jar -C classes .
	jar uf jas-jython.jar -C $(JASPATH)/examples jas.py
	cp -f jas-jython.jar $(LIBPATH)
	cp -f jas-jython.jar ..
	rm -rf classes

clean:
	rm -rf classes
