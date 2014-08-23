#
# jython examples for jas.
# $Id$
#

import sys;

from jas import QQ, PolyRing
from jas import startLog
from jas import terminate

# rational function coefficients
# IP (alpha,beta,gamma,epsilon,theta,eta)
# (c3,c2,c1) /G/
#r = Ring( "IntFunc(alpha,beta,gamma,epsilon,theta,eta)(c3,c2,c1) G" );
# ( { alpha } c1 - { beta } c1**2 - { gamma } c1 c2 + { epsilon } c3 ),
# ( - { gamma } c1 c2 + { epsilon + theta } c3 - { gamma } c2 ),
# ( { gamma } c2 c3 + { eta } c2 - { epsilon + theta } c3 )

#r = Ring( "IntFunc(a,b,g,e,t,eta)(c3,c2,c1) G" );
r = PolyRing( PolyRing(QQ(),"(a,b,g,e,t,eta)", PolyRing.lex), "(c3,c2,c1)", PolyRing.grad );
print "Ring: " + str(r);
print;

ps = """
(
 ( { a } c1 - { b } c1**2 - { g } c1 c2 + { e } c3 ),
 ( - { g } c1 c2 + { e + t } c3 - { g } c2 ),
 ( { g } c2 c3 + { eta } c2 - { e + t } c3 )
)
""";

p1 = a * c1 -  b * c1**2 - g * c1 * c2 + e * c3;
p2 = - g * c1 * c2 + (e + t) * c3 - g * c2;
p3 = g * c2 * c3 + eta * c2 - (e + t) * c3;

#f = r.paramideal( ps );
f = r.paramideal( "", [p1,p2,p3] );
print "ParamIdeal: " + str(f);
print;

#sys.exit();

startLog();

gs = f.CGBsystem();
print "CGBsystem: " + str(gs);
print;

terminate();
sys.exit();

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

bg = gs.isCGB();
if bg:
    print "isCGB: true";
else:
    print "isCGB: false";
print;

terminate();
#sys.exit();

