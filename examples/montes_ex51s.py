#
# jython examples for jas.
# $Id$
#

import sys;

from jas import PolyRing, QQ, RF
from jas import Ideal
from jas import startLog
from jas import terminate


# Montes JSC 2002, 33, 183-208, example 5.1, simplified
# integral function coefficients

r = PolyRing( PolyRing(QQ(),"a, b",PolyRing.lex), "u,z,y,x", PolyRing.lex );
print "Ring: " + str(r);
print;

[one,a,b,u,z,y,x] = r.gens();
print "gens: ", [ str(f) for f in r.gens() ];
print;

f1 = 756 * x - 39 * a * b - 4 * b - 155 - 117 * a + ( 117 * a + 51 ) * u;
f2 = 189 * y + 6 * a * b - 107 - 43 * b + 18 * a - ( 18 * a - 123 ) * u;
f3 = 756 * z - 1439 + 236 * b + 99 * a + 33 * a * b - ( 99 * a - 15 ) * u;
f4 = ( 9 * a**2 - 30 * a + 21 ) * u - 9 * a**2 - 3 * a**2 * b + 11 * a * b + 22 * a - 49 + 28 * b;

F = [f1,f2,f3,f4];

print "F: ", [ str(f) for f in F ];
print;

#startLog();

If = r.paramideal( "", list = F );
print "ParamIdeal: " + str(If);
print;

## G = If.GB();
## print "GB: " + str(G);
## print;
## sys.exit();

GS = If.CGBsystem();
GS = If.CGBsystem();
GS = If.CGBsystem();
print "CGBsystem: " + str(GS);
print;

bg = GS.isCGBsystem();
if bg:
    print "isCGBsystem: true";
else:
    print "isCGBsystem: false";
print;

terminate();
sys.exit();

CG = If.CGB();
print "CGB: " + str(CG);
print;

bg = CG.isCGB();
if bg:
    print "isCGB: true";
else:
    print "isCGB: false";
print;

terminate();
#------------------------------------------
#sys.exit();

