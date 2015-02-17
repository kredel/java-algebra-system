#
# jython examples for jas.
# $Id$
#

import sys;

from jas import ZZ, QQ, PolyRing, SolvPolyRing
from jas import startLog
from jas import terminate


# simple example for comprehensive GB
# integral/rational function coefficients

rc = PolyRing( PolyRing(QQ(),"(u,v)",PolyRing.lex),"(x,y)", PolyRing.lex );
print "comm Ring: " + str(rc);
print;

rel = [y, x,  x * y + 1];
print "relations: = " + str([ str(f) for f in rel ]);
print;

r = SolvPolyRing( PolyRing(QQ(),"(u,v)",PolyRing.lex),"(x,y)", PolyRing.lex, rel);
print "Ring: " + str(r);
print;


p1 = v * x * y + x;
p2 = u * y**2 + x**2;

f = r.paramideal( "", [p1,p2] );
print "ParamIdeal: " + str(f);
print;

#sys.exit();

#startLog();

gs = f.CGBsystem();
print "CGBsystem: " + str(gs);
print;

#sys.exit();

print "isCGBsystem: " + str(gs.isCGBsystem());

#sys.exit();

#startLog();

gs = f.CGB();
print "CGB: " + str(gs);
print;

#startLog();

print "isCGB: " + str(gs.isCGB());
print

terminate();
#sys.exit();
