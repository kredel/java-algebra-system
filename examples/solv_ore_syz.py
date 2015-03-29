#
# jython examples for jas.
# $Id$
#

import sys;

from jas import PolyRing, SolvPolyRing, QQ
from jas import startLog, terminate 

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

print "gens =" + str([ str(f) for f in rp.gens() ]);
#is automatic: one,x,y,z,t = rp.gens();

#f1 = x**2 + y**2 + z**2 + t**2 + 1;
#print "f1 = " +str(f1);

ff = [ x, y, z, t**2 - 1 ];
print "ff = " + str([ str(f) for f in ff ]);
print

ii = rp.ideal( "", ff );
print "SolvableIdeal: " + str(ii);
print;


rgl = ii.leftGB();
print "seq left GB: " + str(rgl);
print "isLeftGB: " + str(rgl.isLeftGB());
print;

s = rgl.leftSyzygy();
print "syzygy: " + str(s);
print;

t = rgl.isLeftSyzygy(s);
print "is syzygy: " + str(t);
print "is syzygy: " + str(s.isLeftSyzygy(rgl));
print;


rgr = ii.rightGB();
print "seq right GB: " + str(rgr);
print "isRightGB: " + str(rgl.isRightGB());
print;

sr = rgr.rightSyzygy();
print "syzygy: " + str(sr);
print;

t = rgr.isRightSyzygy(sr);
print "is syzygy: " + str(t);
print "is syzygy: " + str(sr.isRightSyzygy(rgr));
print;


# module syzy

sgl = s.leftGB();
print "seq left module GB: sgl = " + str(sgl);
print

sml = sgl.leftSyzygy();
print "left syzygy: sml = " + str(sml);
print;

print "is left syzygy: " + str(sml.isLeftSyzygy(sgl));
print;


sgr = sr.rightGB();
print "seq right module GB: sgl = " + str(sgr);
print

#startLog();
smr = sgr.rightSyzygy();
print "right syzygy: smr = " + str(smr);
print;

print "is right syzygy: " + str(smr.isRightSyzygy(sgr));
print;

