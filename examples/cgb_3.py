#
# jython examples for jas.
# $Id$
#

import sys;

from jas import ZZ, Ring, PolyRing
from jas import ParamIdeal
from jas import startLog
from jas import terminate


# 2 univariate polynomials of degree 2 example for comprehensive GB
# integral/rational function coefficients

#r = Ring( "IntFunc(a3, b3, a2, b2, a1, b1, a0, b0) (x) L" );
r = PolyRing( PolyRing(ZZ(),"(a3, b3, a2, b2, a1, b1, a0, b0)",PolyRing.lex),"(x)", PolyRing.lex );
print "Ring: " + str(r);
print;

ps = """
(
 ( { a3 } x^3 + { a2 } x^2 + { a1 } x + { a0 } ),
 ( { b3 } x^3 + { b2 } x^2 + { b1 } x + { b0 } )
) 
""";

p1 = a3 * x**3 + a2 * x**2 + a1 * x + a0;
p2 = b3 * x**3 + b2 * x**2 + b1 * x + b0;

#f = r.paramideal( ps );
f = r.paramideal( "", [p1,p2] );
print "ParamIdeal: " + str(f);
print;

#sys.exit(); # long run time

startLog();

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

gs = f.CGB();
print "CGB: " + str(gs);
print;

terminate();
sys.exit();

bg = gs.isCGB();
if bg:
    print "isCGB: true";
else:
    print "isCGB: false";
print;

terminate();

