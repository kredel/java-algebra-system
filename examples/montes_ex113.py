#
# jython examples for jas.
# $Id$
#

import sys;

from jas import PolyRing, QQ, RF
from jas import startLog
from jas import terminate


# Montes JSC 2002, 33, 183-208, example 11.3
# integral function coefficients

R = PolyRing( PolyRing(QQ(),"r, l, z",PolyRing.lex), "c1, c2, s1, s2", PolyRing.lex );
print "Ring: " + str(R);
print;

#automatic: [one,r,l,z,c1,c2,s1,s2] = R.gens();
print "gens: ", [ str(f) for f in r.gens() ];
print;

f1 = r - c1 + l * ( s1 * s2 - c1 * c2 );
f2 = z - s1 - l * ( s1 * c2 + s2 * c1 );
f3 = s1**2 + c1**2 - 1;
f4 = s2**2 + c2**2 - 1;

F = [f1,f2,f3,f4];

print "F: ", [ str(f) for f in F ];
print;

startLog();

If = R.paramideal( "", list = F );
print "ParamIdeal: " + str(If);
print;

## G = If.GB();
## print "GB: " + str(G);
## print;
## sys.exit();

GS = If.CGBsystem();
#GS = If.CGBsystem();
#GS = If.CGBsystem();
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
#------------------------------------------
#sys.exit();

