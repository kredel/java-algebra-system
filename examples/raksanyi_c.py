#
# jython examples for jas.
# $Id$
#

import sys;

from jas import PolyRing, QQ
from jas import startLog, terminate


# Raksanyi & Walter example
# integral/rational function coefficients

#nono: r = Ring( "RatFunc(a1, a2, a3, a4) (x1, x2, x3, x4) L" );
#r = Ring( "IntFunc(a1, a2, a3, a4) (x1, x2, x3, x4) G" );
r = PolyRing( PolyRing(QQ(),"a1, a2, a3, a4",PolyRing.lex), 
              "x1, x2, x3, x4", PolyRing.grad );
print "Ring: " + str(r);
print;

ps = """
(
 ( x4 - { a4 - a2 } ),
 ( x1 + x2 + x3 + x4 - { a1 + a3 + a4 } ),
 ( x1 x3 + x1 x4 + x2 x3 + x3 x4 - { a1 a4 + a1 a3 + a3 a4 } ),
 ( x1 x3 x4 - { a1 a3 a4 } )
) 
""";

f = r.paramideal( ps );
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

gs = f.CGBsystem();
gs = f.CGBsystem();
gs = f.CGBsystem();

print "2-CGBsystem: " + str(gs);
print;

gs = f.CGB();
print "CGB: " + str(gs);
print;

bg = gs.isCGB();
if bg:
    print "isCGB: true";
else:
    print "isCGB: false";
print;

terminate();

