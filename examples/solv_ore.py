#
# jython examples for jas.
# $Id$
#

from java.lang import System
from java.lang import Integer

from jas import SolvableRing, SolvPolyRing, PolyRing
from jas import QQ, startLog

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

rgr = I.rightGB();
print "seq right GB:" + str(rgr);
print "isRightGB: " + str(rgr.isRightGB());
print;

#startLog();

rgt = I.twosidedGB();
print "seq twosided GB:" + str(rgt);
print "isTwosidedGB: " + str(rgt.isTwosidedGB());
print;

#exit(0);

