#
# jython examples for jas.
# $Id$
#

import sys;

from jas import Ring, PolyRing, Ideal
from jas import QQ, ZZ
from jas import startLog

# characteristic set example Circle of Apollonius, from CLO IVA

r = PolyRing( QQ(), "u1,u2,u3,u4,x1,x2,x3,x4,x5,x6,x7,x8", PolyRing.lex );
print "Ring: " + str(r);
print;

#[one,u1,u2,u3,u4,x1,x2,x3,x4,x5,x6,x7,x8] = r.gens();

h1 = 2 * x1 - u1;
h2 = 2 * x2 - u2;
h3 = 2 * x3 - u3;
h4 = 2 * x4 - u4;
h5 = u2 * x5 + u1 * x6 - u1 * u2;
h6 = u1 * x5 - u2 * x6;
h7 = x1**2 - x2**2 - 2 * x1 * x7 + 2 * x2 * x8;
h8 = x1**2 - 2 * x1 * x7 - x3**2 + 2 * x3 * x7 - x4**2 + 2 * x4 * x8;

g = ( ( x5 - x7 )**2 + ( x6 - x8 )**2 - ( x1 - x7 )**2 - x8**2 );

L = [h1,h2,h3,h4,h5,h6,h7,h8];
#print "L = ", str(L);

f = r.ideal( list=L );
print "Ideal: " + str(f);
print;

#sys.exit();
startLog();

c = f.CS();
print "seq char set:", c;
print "is char set:", c.isCS();
print;

print "g:", g;
h = c.csReduction(g);
print "h:", h;
print;

sys.exit();

