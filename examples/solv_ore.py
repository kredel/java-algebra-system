#
# jython examples for jas.
# $Id$
#

from java.lang import System
from java.lang import Integer

from jas import SolvableRing, SolvPolyRing, PolyRing
from jas import QQ, startLog, SRC, SRF

# Ore extension solvable polynomial example, Gomez-Torrecillas, 2003

p = PolyRing(QQ(),"x,y,z,t");
#is automatic: [one,x,y,z,t] = p.gens();

relations = [z, y,  y * z + x,
             t, y,  y * t  + y,
             t, z,  z * t - z
            ];

print "relations: = " + str([ str(f) for f in relations ]);
print;


rp = SolvPolyRing(QQ(), "x,y,z,t", PolyRing.lex, relations);
print "SolvPolyRing: " + str(rp);
print;
print "gens =", [ str(f) for f in rp.gens() ];


f1 = x**2 + y**2 + z**2 + t**2 + 1;

print "f1 = ", f1;

F = [ f1 ];
print "F =", [ str(f) for f in F ];
print

I = rp.ideal( list=F );
print "SolvableIdeal: " + str(I);
print;

rgl = I.leftGB();
print "seq left GB:" + str(rgl);
print "isLeftGB: " + str(rgl.isLeftGB());
print;

#rgr = I.rightGB();
#print "seq right GB:" + str(rgr);
#print "isRightGB: " + str(rgr.isRightGB());
#print;

#startLog();

rgt = I.twosidedGB();
print "seq twosided GB:" + str(rgt);
print "isTwosidedGB: " + str(rgt.isTwosidedGB());
print;

#startLog();

#rgi = rgl.intersect(rgt);
#print "leftGB intersect twosidedGB:" + str(rgi);
#print;

#startLog();

#rgtu = rgt.univariates();
#print "univariate polynomials for twosidedGB: " + str([ str(f) for f in rgtu ]);
#print;

startLog();

sr = SRC(rgt,one);
print "SolvableResidue: " + str(sr);
print "SolvableResidue: " + str(sr-sr);
print;

st = SRC(rgt,t-x);
print "SolvableResidue: " + str(st);
print "SolvableResidue: " + str(st-st);
print "SolvableResidue: " + str(st**4+3*st);
print;

#exit(0);

sc = SRF(rp,one);
print "SolvableQuotient: " + str(sc);
print "SolvableQuotient: " + str(sc-sc);
print;

scx = SRF(rp,x);
print "SolvableQuotient: " + str(scx);
print "SolvableQuotient: " + str(scx*scx);
print;

scy = SRF(rp,y);
scyi = 1 / scy;
print "SolvableQuotient: " + str(scy);
print "SolvableQuotient: " + str(scyi);
print "SolvableQuotient: " + str(scyi*scy);
print "SolvableQuotient: " + str(scy*scyi);
print;

sca = SRF(rp,x*y + t*z*x);
scai = 1 / sca;
print "SolvableQuotient: " + str(sca);
print "SolvableQuotient: " + str(scai);
print "SolvableQuotient: " + str(scai*sca);
print "SolvableQuotient: " + str(sca*scai);
print;

scb = SRF(rp, z*(x-y) - t*z );
scbi = 1 / scb;
print "SolvableQuotient: " + str(scb);
print "SolvableQuotient: " + str(scbi);
print "SolvableQuotient: " + str(scbi*scb);
print "SolvableQuotient: " + str(scb*scbi);
print;

scc = sca*scb;
scci = 1 / scc;
print "SolvableQuotient: " + str(scc);
print "SolvableQuotient: " + str(scci);
print "SolvableQuotient: " + str(scci*scb);
print "SolvableQuotient: " + str(scci*sca*scb);
print "SolvableQuotient: " + str(sca*scb*scci);
#print "SolvableQuotient: " + str(sca*scci*scb); 
print;

scd = scci + scb + sca;
print "SolvableQuotient: " + str(scd);
print "SolvableQuotient: " + str(scd-(scci+scb) == sca);
print "SolvableQuotient: " + str(sca == scd-(scci+scb));
print;

sce = scai + scbi;
print "SolvableQuotient: " + str(sce);

#exit(0);

