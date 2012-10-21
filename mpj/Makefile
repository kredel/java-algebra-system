#
# $Id: $
#
# Makefile for the (Fast) MPJ (Express) dependend parts
# by Heinz kredel
#

#JASPATH=$(HOME)/jas
JASPATH=..
LIBPATH=$(HOME)/java/lib

FMPJ_HOME=$(HOME)/java/fastmpj
FMPJ_LIB=$(FMPJ_HOME)/lib/mpj.jar
FMPJ_RUNLIB=$(FMPJ_HOME)/lib/runtime.jar

CLASSPATH=$(LIBPATH)/log4j.jar:$(LIBPATH)/junit.jar:$(FMPJ_LIB):$(JASPATH)
#$(LIBPATH)/jas.jar

NP=2
PPN=3

FMPJ_CMD=java -cp $(FMPJ_RUNLIB) runtime.FMPJRun -cp $(CLASSPATH):jas-mpj.jar

#DOCOPTS=-package
DOCOPTS=-package -author -version -linksource
DOCCLASSES=$(CLASSPATH)
DOC=javadoc -classpath $(DOCCLASSES)


.SUFFIXES : .class .java .jar 
.PHONY    : clean

FILES=$(wildcard src/edu/jas/*/*.java)
#echo $(FILES) $(JASPATH)/examples/jas.py
CLASSES=$(subst src,classes,$(subst java,class,$(FILES)))
#echo $(CLASSES)

all: clean jas-mpj.jar doc

$(CLASSES): $(FILES) Makefile
	-mkdir classes
	javac -classpath $(CLASSPATH) -d classes src/edu/jas/*/*.java

jas-mpj.jar: $(CLASSES) 
	-mkdir classes/META-INF
	cp -a manifest.mf classes/META-INF/MANIFEST.MF
	jar cfM jas-mpj.jar -C classes .
	cp -f jas-mpj.jar $(LIBPATH)
	cp -f jas-mpj.jar ..

clean:
	rm -rf classes
	find . -name "*~" -follow -print -exec rm {} \;

MFILE=machines
EFILE=$(JASPATH)/examples/trinks6.jas

tests: jas-mpj.jar
	FMPJ_HOME=$(FMPJ_HOME) $(FMPJ_CMD) -np $(NP) -machinefile $(MFILE) -class edu.jas.application.RunMPJGB disthybmpj $(EFILE) $(NP)/$(PPN) $(MFILE)

notest: 
	FMPJ_HOME=$(FMPJ_HOME) $(FMPJ_CMD) -np $(NP) -machinefile $(MFILE) -class edu.jas.application.RunMPJGB distmpj $(EFILE) $(NP) $(MFILE)
	FMPJ_HOME=$(FMPJ_HOME) $(FMPJ_CMD) -class edu.jas.gb.HelloWorld
	FMPJ_HOME=$(FMPJ_HOME) $(FMPJ_CMD) -class edu.jas.kern.MPJEngineTest -n $(NP)
	FMPJ_HOME=$(FMPJ_HOME) $(FMPJ_CMD) -class edu.jas.util.DistHashTableMPJTest -n $(NP)

gbtest:
	FMPJ_HOME=$(FMPJ_HOME) $(FMPJ_CMD) -np $(NP) -machinefile $(MFILE) -class edu.jas.gb.GroebnerBaseDistMPJTest 

doc: $(FILES)
	$(DOC) $(DOCOPTS) -d ../doc/mpj $(FILES)
