#
# jython examples for jas.
# $Id$
#

import sys;

from jas import ZZ, Ring, PolyRing
from jas import ParamIdeal
from jas import startLog
from jas import terminate


# simple example for comprehensive GB
# integral/rational function coefficients

#r = Ring( "IntFunc(u,v) (x,y) L" );
r = PolyRing( PolyRing(ZZ(),"(u,v)",PolyRing.lex),"(x,y)", PolyRing.lex );
print "Ring: " + str(r);
print;

ps = """
(
 ( { v } x y + x ),
 ( { u } y^2 + x^2 )
) 
""";

p1 = v * x * y + x;
p2 = u * y**2 + x**2;

#f = r.paramideal( ps );
f = r.paramideal( "", [p1,p2] );
print "ParamIdeal: " + str(f);
print;

#sys.exit();

#startLog();

gs = f.CGBsystem();
print "CGBsystem: " + str(gs);
print;

#sys.exit();

bg = gs.isCGBsystem();
if bg:
    print "isCGBsystem: true";
else:
    print "isCGBsystem: false";
print;

#sys.exit();

#startLog();

gs = f.CGB();
print "CGB: " + str(gs);
print;

#startLog();

bg = gs.isCGB();
if bg:
    print "isCGB: true";
else:
    print "isCGB: false";
print;

terminate();
#sys.exit();

