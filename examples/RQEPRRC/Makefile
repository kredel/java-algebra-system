# ----------------------------------------------------------------------------
# $Id$
# ----------------------------------------------------------------------------
# This file is part of MAS.
# ----------------------------------------------------------------------------
# Copyright (c) 1994 Andreas Dolzmann
# ----------------------------------------------------------------------------
# Makefile for the computation of examples of real quantifier elimination.
# ----------------------------------------------------------------------------
# $Log$
# Revision 1.1  2000/06/17 20:40:56  kredel
# Initial revision
#
# Revision 1.1.1.1  2000/06/17 20:40:56  kredel
# Clean import of MAS 1.0 from Uni Passau
#
# Revision 1.1  1994/11/29 06:24:46  dolzmann
# Examples for the real quantifier elimination with paramteric real root count.
#
# ----------------------------------------------------------------------------

IN=DavenportHeintz.in Descartes.in GonzalesVega.in binomi.in cls6.in cls7.in \
	cube.in fact.in inv.in lin2gre.in lin2gre2r.in lineq.in \
	lingre.in mcube.in mquadratic.in mquadraticgre.in quadratic.in \
	quartic.in whitney.in

OUT=DavenportHeintz.out Descartes.out GonzalesVega.out binomi.out cls6.out  \
	cls7.out cube.out fact.out inv.out lin2gre.out \
	lin2gre2r.out lineq.out lingre.out mcube.out mquadratic.out \
	mquadraticgre.out quadratic.out quartic.out whitney.out

%.out:%.in
	mas -m 32000 -f $< > $@

all: $(OUT)

clean:
	/bin/rm -f $(OUT)
