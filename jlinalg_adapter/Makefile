#
# $Id$
#
# Makefile for the JLinAlg Adapter parts
# by Heinz kredel
#

#JASPATH=$(HOME)/jas
JASPATH=..
LIBPATH=$(HOME)/java/lib
JLINALG=$(LIBPATH)/jlinalg-api_0.6.jar

CLASSPATH=$(LIBPATH)/log4j-core-2.17.1.jar:$(LIBPATH)/log4j-api-2.17.1.jar:$(LIBPATH)/junit.jar:$(JLINALG):$(JASPATH)
#CLASSPATH=$(LIBPATH)/log4j.jar:$(LIBPATH)/junit.jar:$(JLINALG):$(JASPATH)
#$(LIBPATH)/jas.jar
#LOG4JPATH=$(LIBPATH)/log4j-core-2.13.2.jar:$(LIBPATH)/log4j-api-2.13.2.jar

#DOCOPTS=-package
DOCOPTS=-package -author -version -linksource -Xdoclint:none -overview overview.html
DOCCLASSES=$(CLASSPATH)
DOC=javadoc -classpath $(DOCCLASSES)

.SUFFIXES : .class .java .jar 
.PHONY    : clean

FILES=$(wildcard src/edu/jas/*/*.java)
#echo $(FILES) $(JASPATH)/examples/jas.py
CLASSES=$(subst src,classes,$(subst java,class,$(FILES)))
#echo $(CLASSES)

all: clean jlinalg_adapter.jar

$(CLASSES): $(FILES) Makefile
	-mkdir -p classes
	javac -classpath $(CLASSPATH) -d classes src/edu/jas/*/*.java
	-echo $(CLASSES)

#compile: $(HOME)/java/lib/jlinalg_adapter.jar

jlinalg_adapter.jar: $(CLASSES) 
	-mkdir -p classes/META-INF
	cp -a manifest.mf classes/META-INF/MANIFEST.MF
	jar cfM jlinalg_adapter.jar -C classes .
	#jar uf  jlinalg_adapter.jar -C src .
	cp -f jlinalg_adapter.jar $(LIBPATH)
	cp -f jlinalg_adapter.jar ..

#	javac -d . -cp $(JARS):. -Xlint:unchecked  edu/jas/jlinalg/*.java
#	jar -cf $(HOME)/java/lib/jlinalg_adapter.jar edu Makefile
#	#jar -tvf $(HOME)/java/lib/jlinalg_adapter.jar

tests:	
	java -cp $(CLASSPATH):jlinalg_adapter.jar -Xms500M -Xmx600M -verbose:gc edu.jas.jlinalg.JLAdapterTest
	java -cp $(CLASSPATH):jlinalg_adapter.jar -Xms500M -Xmx600M -verbose:gc edu.jas.jlinalg.MatrixExamples

clean:
	-rm -rf classes
	find . -name "*~" -follow -print -exec rm {} \;
	-rm -f jlinalg_adapter.jar

doc: $(FILES)
	$(DOC) $(DOCOPTS) -d ../doc/jlinalg_adapter $(FILES)
