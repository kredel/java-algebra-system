#
# jython examples for jas.
# $Id$
#

import sys;

from jas import PolyRing, QQ, RF
from jas import Ideal
from jas import startLog
from jas import terminate


# Montes JSC 2002, 33, 183-208, example 11.1
# integral function coefficients

r = PolyRing( PolyRing(QQ(),"c, b, a",PolyRing.lex), "z,y,x", PolyRing.lex );
print "Ring: " + str(r);
print;

[one,c,b,a,z,y,x] = r.gens();
print "gens: ", [ str(f) for f in r.gens() ];
print;

f1 =     x + c * y + b * z + a;
f2 = c * x +     y + a * z + b;
f3 = b * x + a * y +     z + c;

F = [f1,f2,f3];

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

