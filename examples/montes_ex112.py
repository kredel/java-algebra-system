#
# jython examples for jas.
# $Id$
#

import sys;

from jas import PolyRing, QQ, RF
from jas import Ideal
from jas import startLog
from jas import terminate


# Montes JSC 2002, 33, 183-208, example 11.2
# integral function coefficients

r = PolyRing( PolyRing(QQ(),"f, e, d, c, b, a",PolyRing.lex), "y,x", PolyRing.lex );
print "Ring: " + str(r);
print;

[one,f,e,d,c,b,a,y,x] = r.gens();
print "gens: ", [ str(f) for f in r.gens() ];
print;

f1 = x**2 + b * y**2 + 2 * c * x * y + 2 * d * x + 2 * e * y + f; 
f2 = x + c * y + d;
f3 = b * y + c * x + e;

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

