#
# jruby examples for jas.
# $Id$
#

import sys;

from jas import SolvableRing, SolvPolyRing, PolyRing, SolvableIdeal
from jas import QQ, ZZ, GF, SRF, startLog, terminate 


# Iterated Ore extension solvable polynomial example, 

rc = PolyRing(QQ(), "x,y,z,t", PolyRing.lex);
#is automatic: [one,x,y,z,t] = rc.gens();

crel = [ z, y,  y * z + x,
         t, y,  y * t + y,
         t, z,  z * t - z
       ];

print "crel: = " + str( [ str(r) for r in crel ] );
print;

rcs = SolvPolyRing(QQ(), "x,y,z,t", PolyRing.lex, crel);

#exit(0)

rm = PolyRing(rcs,"u,v,w",PolyRing.lex);
#is automatic: [one,x,y,z,t,u,v,w] = rm.gens();

mrel = [ v, u,  u * v + x,
         w, v,  v * w + y,
         w, u,  u * w - z
       ];

print "mrel: = " + str([ str(r) for r in mrel ]);
print;

rs = SolvPolyRing(rcs, "u,v,w", PolyRing.lex, mrel);

#exit(0)

ff = [ (w**2 - v * u) * v*w**2, 
       (u**3 + t) * v*w**2, 
       (v*u**2 - x*y) * v*w**2 
     ];
f = rs.ideal("", ff);
print "f: " + str(f);
print;

#exit(0)
#startLog();

lf = f.leftGB();
print "lf: " + str(lf);
print;

ff = [ (w**2 - v * u) * w, 
       (u**3 + t) * w, 
       (v*u**2 - x*y) * w 
     ];
f = rs.ideal("", ff);
print "f: " + str(f);
print;

tf = f.twosidedGB();
print "tt: " + str(tf);
print;

#exit(0)

ff = [ u * (w - v), 
       u * (v**2 + t),
       u * (v*u - x*y)
     ];
f = rs.ideal("", ff);
print "f: " + str(f);
print;

rf = f.rightGB();
print "rf: " + str(rf);
print;


#----- quotient of rsc: --------------

q = SRF(rcs);
print "q: " + str(q.ring.toScript());
print;
#one,x,xi,y,yi,z,zi,t,ti = q.gens();
#print "x: " + str(x);
#print "t: " + str(t);
#print

rm = PolyRing(q,"u,v,w",PolyRing.lex);
#is automatic: [one,x,y,z,t,u,v,w] = rm.gens();

mrel = [ v, u,  u * v + x,
         w, v,  v * w + y,
         w, u,  u * w - z
       ];

print "mrel: = " + str([ str(r) for r in mrel ]);
print;

rs = SolvPolyRing(q, "u,v,w", PolyRing.lex, mrel);
#is automatic: [one,x,y,z,t,u,v,w] = rs.gens();

print "x: " + str(x);
print "w: " + str(w);
print

#exit(0)

ff = [ (w**2 - v * u) * v*w**2, 
       (u**3 + t) * v*w**2, 
       (v*u**2 - x*y) * v*w**2 
     ];
f = rs.ideal("", ff);
print "f: " + str(f);
print;

#exit(0)
#startLog();

lf = f.leftGB();
print "lf: " + str(lf);
print;

#exit(0)

ff = [ (w**2 - v * u) * w, 
       (u**3 + t) * w, 
       (v*u**2 - x*y) * w 
     ];
f = rs.ideal("", ff);
print "f: " + str(f);
print;

tf = f.twosidedGB();
print "tt: " + str(tf);
print;

#exit(0)

ff = [ u * (w - v), 
       u * (v**2 + t),
       u * (v*u - x*y)
     ];
f = rs.ideal("", ff);
print "f: " + str(f);
print;

rf = f.rightGB();
print "rf: " + str(rf);
print;


#startLog();
terminate();
