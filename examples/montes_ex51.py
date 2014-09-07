#
# jython examples for jas.
# $Id$
#

import sys;

from jas import PolyRing, QQ, RF
from jas import startLog
from jas import terminate


# Montes JSC 2002, 33, 183-208, example 5.1
# integral function coefficients

r = PolyRing( PolyRing(QQ(),"a, b",PolyRing.lex), "u,z,y,x", PolyRing.lex );
print "Ring: " + str(r);
print;

#automatic: [one,a,b,u,z,y,x] = r.gens();
print "gens: ", [ str(f) for f in r.gens() ];
print;

f1 = a * x + 2 * y + 3 * z +     u - 6;
f2 = x + 3 * y - z +         2 * u - b;
f3 = 3 * x - a * y +     z         - 2;
f4 = 5 * x + 4 * y + 3 * z + 3 * u - 9;

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

#terminate();
#sys.exit();

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


