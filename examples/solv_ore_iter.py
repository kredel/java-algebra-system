#
# jython examples for jas.
# $Id$
#

from java.lang import System

from jas import SolvableRing, SolvPolyRing, PolyRing
from jas import QQ, startLog, SRC, SRF

# Ore extension solvable polynomial example, Gomez-Torrecillas, 2003

pcz = PolyRing(QQ(),"x,y,z");
#is automatic: [one,x,y,z] = pcz.gens();

zrelations = [z, y,  y * z + x
             ];

print "zrelations: = " + str([ str(f) for f in zrelations ]);
print;

pz = SolvPolyRing(QQ(), "x,y,z", PolyRing.lex, zrelations);
print "SolvPolyRing: " + str(pz);
print;

pzq = SRF(pz);
print "SolvableQuotientRing: " + str(pzq.ring.toScript()); # + ", assoz: " + str(pzq.ring.isAssociative());
#print "gens =" + str([ str(f) for f in pzq.ring.generators() ]);
print;

pct = PolyRing(pzq,"t");
#is automatic: [one,x,y,z,t] = p.gens();

trelations = [t, y,  y * t  + y,
              t, z,  z * t - z
             ];

print "relations: = " + str([ str(f) for f in trelations ]);
print;

pt = SolvPolyRing(pzq, "t", PolyRing.lex, trelations);
print "SolvPolyRing: " + str(pt);
print;
print "gens =", [ str(f) for f in pt.gens() ];

#print "t  = " + str(t);
#print "z  = " + str(z);
zi = 1 / z;
yi = 1 / y;
xi = 1 / x;

#startLog();

a = ( t * zi ) * y;
b = t * ( zi * y );
c = a - b;
print "t * 1/z * y: ";
print "a   = " +str(a);
print "b   = " +str(b);
print "a-b = " +str(c);
print
#bm = b.monic()
#print "monic(b) = " +str(bm);
#print

#exit(0);

f1 = x**2 + y**2 + z**2 + t**2 + 1;
print "f1 = ", f1;

f2 = t * x * f1;
print "f2 = ", f2;

F = [ f1, f2 ];
print "F =", [ str(f) for f in F ];
print

I = pt.ideal( list=F );
print "SolvableIdeal: " + str(I);
print;

#exit(0);

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

#exit(0);
#startLog();

rgi = rgl.intersect(rgt);
print "leftGB intersect twosidedGB:" + str(rgi);
print;

#terminate();
