#
# $Id$
#
# Makefile for the (Fast) MPJ (Express) dependend parts
# by Heinz kredel
#

#JASPATH=$(HOME)/jas
JASPATH=..
LIBPATH=$(HOME)/java/lib

FMPJ_HOME=$(HOME)/java/fastmpj
FMPJ_LIB=$(FMPJ_HOME)/lib/mpj.jar

MPJE_HOME=$(HOME)/java/mpj-v0_44
MPJE_BIN=$(MPJE_HOME)/bin

NP=2
PPN=3

CLASSPATH=$(LIBPATH)/log4j-api-2.5.jar:$(LIBPATH)/log4j-core-2.5.jar:$(LIBPATH)/junit-4.12.jar:$(FMPJ_LIB):$(JASPATH)
#$(LIBPATH)/jas.jar
CLASSPATHE=$(LIBPATH)/log4j-api-2.5.jar:$(LIBPATH)/log4j-core-2.5.jar:$(LIBPATH)/junit-4.12.jar:$(JASPATH)


#FMPJ_RUNLIB=$(FMPJ_HOME)/lib/runtime.jar
#FMPJ_CMD=java -cp $(FMPJ_RUNLIB) runtime.FMPJRun -dev niodev -cp $(CLASSPATH):jas-mpj.jar
FMPJ_CMD=$(FMPJ_HOME)/bin/fmpjrun -dev niodev -dmap -cp $(CLASSPATH):jas-mpj.jar
#FMPJ_CMD=$(FMPJ_HOME)/bin/fmpjrun -dev psmdev -dmap -cp $(CLASSPATH):jas-mpj.jar

#old MPJE_CMD=$(MPJE_HOME)/bin/mpjrun.sh -dev niodev -cp $(CLASSPATHE):jas-mpj.jar
MPJE_CMD=mpjrun.sh -dev niodev -cp $(CLASSPATHE):classes:jas-mpj.jar
#MPJE_CMD=mpjrun.sh -dev hybdev -cp "$(CLASSPATHE):classes:jas-mpj.jar"

#DOCOPTS=-package
DOCOPTS=-package -author -version -linksource -Xdoclint:none -overview overview.html
DOCCLASSES=$(CLASSPATH)
DOC=javadoc -classpath $(DOCCLASSES)

# --- syncing ----------
DRY=--dry-run
DELETE=
RSYNC=rsync -e ssh -avuz $(DRY) $(DELETE) --exclude=*~ --exclude=*.log* --exclude=*.out* --exclude=*.txt* --exclude=.svn 


.SUFFIXES : .class .java .jar 
.PHONY    : clean

FILES=$(wildcard src/edu/jas/*/*.java)
#echo $(FILES) $(JASPATH)/examples/jas.py
CLASSES=$(subst src,classes,$(subst java,class,$(FILES)))
#echo $(CLASSES)

all: jas-mpj.jar

$(CLASSES): $(FILES) Makefile
	-mkdir classes
	javac -classpath $(CLASSPATH) -d classes src/edu/jas/*/*.java
#	fmpjc -classpath $(CLASSPATH) -d classes src/edu/jas/*/*.java

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
#EFILE=$(JASPATH)/examples/ideal_one.jas
#EFILE=$(JASPATH)/examples/ideal_zero.jas
EFILE=$(JASPATH)/examples/trinks6.jas

fmpjtests: jas-mpj.jar
	FMPJ_HOME=$(FMPJ_HOME) $(FMPJ_CMD) -np $(NP) -machinefile $(MFILE) -class edu.jas.application.RunMPJGB disthybmpj $(EFILE) $(NP)/$(PPN) $(MFILE)
#	$(FMPJ_CMD) -np $(NP) -machinefile $(MFILE) -class edu.jas.application.RunMPJGB disthybmpj $(EFILE) $(NP)/$(PPN) $(MFILE)


notest: jas-mpj.jar
	FMPJ_HOME=$(FMPJ_HOME) $(FMPJ_CMD) -np $(NP) -machinefile $(MFILE) -class edu.jas.application.RunMPJGB distmpj $(EFILE) $(NP) $(MFILE)
	FMPJ_HOME=$(FMPJ_HOME) $(FMPJ_CMD) -class edu.jas.gb.HelloWorld
	FMPJ_HOME=$(FMPJ_HOME) $(FMPJ_CMD) -class edu.jas.kern.MPJEngineTest -n $(NP)
	FMPJ_HOME=$(FMPJ_HOME) $(FMPJ_CMD) -class edu.jas.util.DistHashTableMPJTest -n $(NP)


gbtest: jas-mpj.jar
	FMPJ_HOME=$(FMPJ_HOME) $(FMPJ_CMD) -np $(NP) -machinefile $(MFILE) -class edu.jas.gb.GroebnerBaseDistMPJTest 

gbhybtest: jas-mpj.jar
	FMPJ_HOME=$(FMPJ_HOME) $(FMPJ_CMD) -np $(NP) -machinefile $(MFILE) -class edu.jas.gb.GroebnerBaseDistHybridMPJTest 


tests: jas-mpj.jar 
	#export PATH=$(PATH):$(MPJE_BIN); export MPJ_HOME=$(MPJE_HOME); 
	$(MPJE_BIN)/mpjboot machines
	$(MPJE_BIN)/$(MPJE_CMD) -np $(NP) edu.jas.application.RunMPJGB disthybmpj $(EFILE) $(NP)/$(PPN) $(MFILE) nolog nocheck
	$(MPJE_BIN)/mpjhalt machines


doc: $(FILES)
	$(DOC) $(DOCOPTS) -d ../doc/mpj $(FILES)


BWDIR=java/jas-2.5/mpj

home:
	$(RSYNC) bwgrid:$(BWDIR)/src/     src

bwgrid:
	$(RSYNC) src/                     bwgrid:$(BWDIR)/src/
