#
# $Id$
#
# Makefile for the interface of JAS to meditor
# by Raphael Jolly, Heinz kredel
#

JASPATH=/home/kredel/jas
LIBPATH=/home/kredel/java/lib
JSCLPATH=.

CLASSPATH=$(LIBPATH)/jython.jar:$(LIBPATH)/meditor.jar

.SUFFIXES : .class .java .jar 
.PHONY    : clean

FILES=$(wildcard src/edu/jas/editorengine/*.java)
#echo $(FILES) $(JASPATH)/examples/jas.py
CLASSES=$(subst src,classes,$(subst java,class,$(FILES)))
#echo $(CLASSES)

all: jas-meditor.jar

$(CLASSES): $(FILES)
	mkdir -p classes
	javac -g -source 1.4 -target 1.4 -classpath $(CLASSPATH) -sourcepath src -d classes src/edu/jas/editorengine/*.java

jas-meditor.jar: $(CLASSES) $(JASPATH)/examples/jas.py manifest.mf services/jscl.editor.EngineFactory
	cp -a $(JASPATH)/examples/jas.py classes/
	mkdir -p classes/META-INF
	mkdir -p classes/META-INF/services
	cp -a manifest.mf classes/META-INF/MANIFEST.MF
	cp -a services/* classes/META-INF/services/
	jar cMf jas-meditor.jar src -C classes .
	cp -f jas-meditor.jar $(LIBPATH)
	rm -rf classes

clean:
	rm -rf classes
