#
# $Id: $
#
# Makefile for the interface of JAS to meditor
# by Raphael Jolly, Heinz kredel
#

JASPATH=/home/kredel/jas
LIBPATH=/home/kredel/java/lib
JSCLPATH=.

CLASSPATH=$(LIBPATH)/jython.jar:$(LIBPATH)/meditor.jar

compile: 
	mkdir -p classes
	cp -a $(JASPATH)/examples/jas.py classes/
	javac -g -source 1.4 -target 1.4 -classpath $(CLASSPATH) -sourcepath src -d classes src/edu/jas/editorengine/*.java

jar:
	mkdir -p classes/META-INF
	cp -a manifest.mf classes/META-INF/MANIFEST.MF
	cp -a services classes/META-INF/
	jar cMf jas-meditor.jar src -C classes .
	cp -f jas-meditor.jar $(LIBPATH)

clean:
	rm -rf classes
