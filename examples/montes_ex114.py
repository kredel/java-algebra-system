#
# jython examples for jas.
# $Id$
#

import sys;

from jas import PolyRing, QQ, RF
from jas import Ideal
from jas import startLog
from jas import terminate


# Montes JSC 2002, 33, 183-208, example 11.4
# integral function coefficients

R = PolyRing( PolyRing(QQ(),"Q2, P2, Q1, P1",PolyRing.lex), "f3, e3, f2, e2", PolyRing.lex );
print "Ring: " + str(R);
print;

[one, Q2, P2, Q1, P1, f3, e3, f2, e2] = R.gens();
print "gens: ", [ str(f) for f in R.gens() ];
print;

fp1 = 14 - 12 * e2 - 110 * f2 - 2 * e3 - 10 * f3 - P1;
fp2 = 2397 - 2200 * e2 + 240 * f2 - 200 * e3 + 40 * f3 - 20 * Q1;
fp3 = 16 * e2**2 - 4 * e2 * e3 - 20 * e2 * f3 + 20 * e3 * f2 + 16 * f2**2 - 4 * f2 * f3 - 12 * e2 + 110 * f2 - P2;
fp4 = 2599 * e2**2 - 400 * e2 * e3 + 80 * e2 * f3 - 80 * e3 * f2 + 2599 * f2**2 - 400 * f2 * f3 - 2200 * e2 - 240 * f2 - 20 * Q2;

print "fp1: " + str(fp1);
print "fp2: " + str(fp2);
print "fp3: " + str(fp3);
print "fp4: " + str(fp4);
print;

F = [fp1,fp2,fp3,fp4];

print "F: ", [ str(f) for f in F ];
print;

startLog();

If = R.paramideal( "", list = F );
print "ParamIdeal: " + str(If);
print;

## G = If.GB();
## print "GB: " + str(G);
## print;

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
