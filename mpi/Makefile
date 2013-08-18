#
# $Id: $
#
# Makefile for the Java Open MPI dependend parts
# by Heinz kredel
#

#JASPATH=$(HOME)/jas
JASPATH=..
LIBPATH=$(HOME)/java/lib

#MPILIB=$(MPI_HOME)/lib/mpi.jar
MPILIB=$(LIBPATH)/mpi.jar
CLASSPATH=$(LIBPATH)/log4j.jar:$(LIBPATH)/junit.jar:$(MPILIB):$(JASPATH)
#$(LIBPATH)/jas.jar

NP=2
PPN=3

MPI_CMD=mpirun -np $(NP) -bynode -display-map -verbose java -cp $(CLASSPATH):jas-mpi.jar

#DOCOPTS=-package
DOCOPTS=-package -author -version -linksource
DOCCLASSES=$(CLASSPATH)
DOC=javadoc -classpath $(DOCCLASSES)

# --- syncing ----------
DRY=--dry-run
DELETE=
RSYNC=rsync -e ssh -avuz $(DRY) $(DELETE) --exclude=*~ --exclude=*.log* --exclude=*.out* --exclude=*.txt* --exclude=.svn --exclude=classes --exclude=.git*


.SUFFIXES : .class .java .jar 
.PHONY    : clean

FILES=$(wildcard src/edu/jas/*/*.java)
#echo $(FILES) $(JASPATH)/examples/jas.py
CLASSES=$(subst src,classes,$(subst java,class,$(FILES)))
#echo $(CLASSES)

all: jas-mpi.jar 

$(CLASSES): $(FILES) Makefile
	-mkdir classes
	javac -classpath $(CLASSPATH) -d classes src/edu/jas/*/*.java

jas-mpi.jar: $(CLASSES) 
	-mkdir classes/META-INF
	cp -a manifest.mf classes/META-INF/MANIFEST.MF
	jar cfM jas-mpi.jar -C classes .
	cp -f jas-mpi.jar $(LIBPATH)
	cp -f jas-mpi.jar ..

clean:
	rm -rf classes
	find . -name "*~" -follow -print -exec rm {} \;

MFILE=machines
EFILE=$(JASPATH)/examples/trinks6.jas

tests: jas-mpi.jar
#	$(MPI_CMD) -Xmx1000M edu.jas.application.RunMPIGB distmpi $(EFILE) $(NP)/$(PPN) $(MFILE)
	$(MPI_CMD) -Xmx1000M edu.jas.application.RunMPIGB disthybmpi $(EFILE) $(NP)/$(PPN) $(MFILE)

doc: $(FILES)
	$(DOC) $(DOCOPTS) -d ../doc/mpi $(FILES)


BWDIR=java/jas-2.5/mpi

home:
	$(RSYNC) bwgrid:$(BWDIR)/src/     src

bwgrid:
	$(RSYNC) ./                       bwgrid:$(BWDIR)/
