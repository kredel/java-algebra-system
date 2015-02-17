#
# jython examples for jas.
# $Id$
#

import sys;

from jas import ZZ, QQ, PolyRing, SolvPolyRing
from jas import startLog
from jas import terminate

# simple example for solvable comprehensive GB
# integral/rational function coefficients

rc = PolyRing( PolyRing(QQ(),"a,b",PolyRing.lex),"x1,x2,d1,d2", PolyRing.lex );
print "commutativ Ring: " + str(rc);
print;

rel = [d1, x1,  x1 * d1 + 1,
       d2, x2,  x2 * d2 + 1
      ];
print "relations: = " + str([ str(f) for f in rel ]);
print;

r = SolvPolyRing( PolyRing(QQ(),"a,b",PolyRing.lex),"x1,x2,d1,d2", PolyRing.lex, rel);
print "Ring: " + str(r);
print;

p1 = a * x1 * d1**2 * d2 + (a+1) * x1 * x2 * d2;
p2 = x2**2 * d2 + b * x1;
p3 = d1 * d2**2;

f = r.paramideal( "", [p1,p2,p3] );
print "ParamIdeal: " + str(f);
print;

#exit();
#startLog();

gs = f.CGBsystem();
print "CGBsystem: " + str(gs);
print;

#exit();

#bg = gs.isCGBsystem();
#print "isCGBsystem: " + str(bg);
#print;

#exit();
#startLog();

gs = f.CGB();
print;
print "CGB: " + str(gs);
print;

#exit();
#startLog();

bg = gs.isCGB();
print "isCGB: " + str(bg);
print;

startLog();
terminate();
#exit();

