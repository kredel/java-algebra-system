#
# $Id: $
#
# Makefile for the Apache Commons Math Adapter parts
# by Heinz Kredel
#

#JASPATH=$(HOME)/jas
JASPATH=..
LIBPATH=$(HOME)/java/lib
CMJAR=$(LIBPATH)/commons-math3-3.3.jar

CLASSPATH=$(LIBPATH)/log4j-api-2.5.jar:$(LIBPATH)/log4j-core-2.5.jar:$(LIBPATH)/junit.jar:$(CMJAR):$(JASPATH)
#CLASSPATH=$(LIBPATH)/log4j.jar:$(LIBPATH)/junit.jar:$(CMJAR):$(JASPATH)
#$(LIBPATH)/jas.jar

#DOCOPTS=-package
DOCOPTS=-package -author -version -linksource -Xdoclint:none -overview overview.html
DOCCLASSES=$(CLASSPATH)
DOC=javadoc -classpath $(DOCCLASSES)

.SUFFIXES : .class .java .jar 
.PHONY    : clean

FILES=$(wildcard src/edu/jas/commons/*/*.java)
#echo $(FILES) $(JASPATH)/examples/jas.py
CLASSES=$(subst src,classes,$(subst java,class,$(FILES)))
#echo $(CLASSES)

all: clean commons-math_adapter-bin.jar

$(CLASSES): $(FILES) Makefile
	-mkdir classes
	javac -classpath $(CLASSPATH) -d classes src/edu/jas/commons/*/*.java

#compile: $(HOME)/java/lib/commons-math_adapter.jar

commons-math_adapter-bin.jar: $(CLASSES) 
	-mkdir classes/META-INF
	cp -a manifest.mf classes/META-INF/MANIFEST.MF
	jar cfM commons-math_adapter-bin.jar -C classes .
	cp -f commons-math_adapter-bin.jar $(LIBPATH)
	cp -f commons-math_adapter-bin.jar ..
	jar cf commons-math_adapter-src.jar -C src .

tests:	
	java -cp $(CLASSPATH):commons-math_adapter-bin.jar -Xms500M -Xmx600M -verbose:gc edu.jas.commons.math.CMFieldElementTest
	java -cp $(CLASSPATH):commons-math_adapter-bin.jar -Xms500M -Xmx600M -verbose:gc edu.jas.commons.math.MatrixExamples

clean:
	-rm -rf classes
	find . -name "*~" -follow -print -exec rm {} \;
	-rm -f $(HOME)/java/lib/commons-math_adapter*.jar
	-rm -f commons-math_adapter*.jar

doc: $(FILES)
	$(DOC) $(DOCOPTS) -d ../doc/commons-math_adapter $(FILES)
	jar cf commons-math_adapter-doc.jar -C ../doc/commons-math_adapter .

#JARS=$(LIB)/junit.jar:$(LIB)/log4j.jar:$(LIB)/commons-math-2.1.jar:$(JAS):$(LIB)/jas.jar
