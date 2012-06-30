#
# jython examples for jas.
# $Id$
#

import sys;

from jas import Ring, PolyRing, ParamIdeal, QQ
from jas import startLog
from jas import terminate


# 2 univariate polynomials of degree 2 example for comprehensive GB
# integral/rational function coefficients

#rp = PolyRing(QQ(), "a,b" );
rp = PolyRing(QQ(), "a" );
r = PolyRing( rp, "x,y,z", PolyRing.lex );
print "Ring: " + str(r);
print;

#one,a,b,x,y = r.gens();
one,a,x,y,z = r.gens();

#f1 = x * ( x**2 + a * y + b );
#f2 = x * ( y**2 + b * x + a );

f1 = ( x**2 + a * y**2 - x );
f2 = ( a * x**2 + y**2 - y );
f3 = ( x - y ) * z - 1;

print "f1 = " + str(f1);
print "f2 = " + str(f2);
print "f3 = " + str(f3);

f = r.paramideal( "", list=[f1,f2,f3] );
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

