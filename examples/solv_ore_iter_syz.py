#
# jruby examples for jas.
# $Id$
#

import sys;

from jas import SolvableRing, SolvPolyRing, PolyRing, SolvableIdeal
from jas import QQ, ZZ, GF, SRF, startLog, terminate 


# Ore extension solvable polynomial example, Gomez-Torrecillas, 2003

pcz = PolyRing(QQ(),"x,y,z,t", PolyRing.lex);
#is automatic: [one,x,y,z,t] = p.gens();

zrelations = [z, y,  y * z + x,
              t, y,  y * t + y,
              t, z,  z * t - z
             ];

print "zrelations: = " + str( [ str(r) for r in zrelations ] );
print;

pz = SolvPolyRing(QQ(), "x,y,z,t", PolyRing.lex, zrelations);
print "SolvPolyRing: " + str(pz);
print;

pzq = SRF(pz);
print "SolvableQuotientRing: " + str(pzq.ring.toScript); # + ", assoz: " + str(pzq::ring.isAssociative);
#print "gens =" + str( [ str(r) for r in pzq.gens() ] );
print;

pct = PolyRing(pzq,"u,v,w", PolyRing.lex);
#is automatic: [one,x,y,z,t,u,v,w] = p.gens();
print "tgens = " + str( [ str(r) for r in pct.gens() ] );
print;

relations = [#w, v,  v * w - u,
             v, u,  v * u + x,
             w, y,  y * w + y,
             w, z,  z * w - z
            ];

print "relations: = " + str( [ str(r) for r in relations ] );
print;

#startLog();

pt = SolvPolyRing(pzq, "u,v,w", PolyRing.lex, relations);
print "SolvPolyRing: " + str(pt); # + ", is assoz: " + str(pt.ring.isAssociative);
print;

print "gens = " + str( [ str(r) for r in pt.gens() ] );
#is automatic: one,x,y,z,r,s,t = rp.gens();

#exit(0);

#f1 = u**2 + v**2 + w**2;
#f1 = v;
f1 = w**2 - t;
#print "f1 = " + str(f1);

#f2 = t + u - x**2;
#f2 = f1 * (t - x**2);
#f2 = v**2 - v;
#f2 = v**2 - y;
#f2 = v**2 - z;
f2 = v**2 - t;
#print "f2 = " + str(f2);

#f3 = (v - u)*f1;
#f3 = f1*(v - u);
f3 = v*f1*u;
#print "f3 = " + str(f3);

#ff = [ f3, f3 ];
ff = [ f1, f2, f3 ];
#ff = [ f1 ];
#ff = [ u ]; # isTwoSided ideal if uv = vu
#ff = [ v ]; # isTwoSided ideal if uv = vu
#ff = [ w ]; # no twoSided ideal
print "ff = " + str( [ str(r) for r in ff ] );
print

ii = pt.ideal( "", ff );
print "SolvableIdeal: " + str(ii);
print;

#syl = ii.leftSyzygy().leftGB();
#print "left syzygy: " + str(syl);
#print;

#syr = ii.rightSyzygy().rightGB();
#print "right syzygy: " + str(syr);
#print;


#exit(0);
#startLog();

rgl = ii.leftGB();
print "seq left GB: " + str(rgl);
print "isLeftGB: " + str(rgl.isLeftGB());
print;

rgr = ii.rightGB();
print "seq right GB: " + str(rgr);
print "isRightGB: " + str(rgr.isRightGB());
print;

#startLog();

rgt = ii.twosidedGB();
print "seq twosided GB: " + str(rgt);
print "isTwosidedGB: " + str(rgt.isTwosidedGB());
print;

#exit(0);
#startLog();

#rgi = rgl.intersect(rgt);
#print "leftGB intersect twosidedGB: " + str(rgi);
#print;
#rgi = rgl.intersect(rgr);
#print "leftGB intersect rightGB: " + str(rgi);
#print;

#exit(0);

syl = ii.leftSyzygy().leftGB();
print "left syzygy: " + str(syl);
print;

syr = ii.rightSyzygy().rightGB();
print "right syzygy: " + str(syr);
print;

terminate();
