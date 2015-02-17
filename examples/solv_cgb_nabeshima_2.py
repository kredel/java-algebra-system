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


rc = PolyRing( PolyRing(QQ(),"a,b",PolyRing.lex),"x,d", PolyRing.lex );
print "commutativ Ring: " + str(rc);
print;

rel = [d, x,  x * d + 1
      ];
print "relations: = " + str([ str(f) for f in rel ]);
print;

r = SolvPolyRing( PolyRing(QQ(),"a,b",PolyRing.lex),"x,d", PolyRing.lex, rel);
print "Ring: " + str(r);
print;

p1 = 2 * x * d**2 + a * x * d;
p2 = x * d**3 + b * x**2 * d - b * x;
p3 = x * d**2 - a * x;

f = r.paramideal( "", [p1,p2,p3] );
print "ParamIdeal: " + str(f);
print;

#exit();
#startLog();

gs = f.CGBsystem();
print "CGBsystem: " + str(gs);
print;

#exit();

bg = gs.isCGBsystem();
print "isCGBsystem: " + str(bg);
print;

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
#sys.exit();

