#
# jython examples for jas.
# $Id$
#

import sys;

from jas import PolyRing, QQ, RF
from jas import Ideal
from jas import startLog
from jas import terminate


# Suzuki and Sato, ISSAC 2006, example 1
# integral function coefficients

#r = PolyRing( QQ(),"b,a,z,y,x", PolyRing.lex );
#r = PolyRing( RF( PolyRing(QQ(),"a, b",PolyRing.lex) ), "z,y,x", PolyRing.lex );

r = PolyRing( PolyRing(QQ(),"b, a",PolyRing.lex), "z,y,x", PolyRing.lex );
print "Ring: " + str(r);
print;

[one,b,a,z,y,x] = r.gens();
print "gens: ", [ str(f) for f in r.gens() ];
print;

#f1 = x**2 - a;
#f2 = y**3 - b;

f1 = x**3 - a;
f2 = y**4 - b;
f3 = x + y - z;

F = [f1,f2,f3];

print "F: ", [ str(f) for f in F ];
print;

startLog();

## If = r.ideal( "", list = F );
## print "Ideal: " + str(If);
## print;

## G = If.GB();
## print "GB: " + str(G);
## print;

## E = G.eliminateRing( PolyRing(QQ(),"b, a",PolyRing.lex) );
## print "E: " + str(E);
## print;

## sys.exit();

If = r.paramideal( "", list = F );
print "ParamIdeal: " + str(If);
print;

G = If.GB();
print "GB: " + str(G);
print;

terminate();
sys.exit();

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
#------------------------------------------
#sys.exit();

