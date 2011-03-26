#
# jython examples for jas.
# $Id$
#

import sys;

from java.lang import System
from java.lang import Integer

from jas import Ring, PolyRing
from jas import QQ, DD, EF
from jas import terminate
from jas import startLog


# polynomial examples: real root tower over Q

r = EF(QQ()).realExtend("q","q^3 - 3", "[1,2]").realExtend("w", "w^2 - q", "[1,2]").realExtend("s", "s^5 - 2", "[1,2]").polynomial("x").build();

print "Ring: " + str(r);
print;

[one,q,w,s,x] = r.gens();
print "one   = " + str(one);
print "q     = " + str(q);
print "w     = " + str(w);
print "s     = " + str(s);
print "x     = " + str(x);
print;

f = x**2 - w * s;


print "f = ", f;
print;

startLog();

t = System.currentTimeMillis();
R = r.realRoots(f);
t = System.currentTimeMillis() - t;
#print "R = ", R;
print "R = ", [ a.elem.ring.getRoot() for a in R ];
print "real roots time =", t, "milliseconds";

#sys.exit();

eps = QQ(1,10) ** (DD().elem.DEFAULT_PRECISION);
print "eps = ", eps;

t = System.currentTimeMillis();
R = r.realRoots(f,eps);
t = System.currentTimeMillis() - t;
#print "R = ", [ str(r) for r in R ];
print "R = ", [ a.elem.decimalMagnitude() for a in R ];
print "real roots time =", t, "milliseconds";
print;

#startLog();
terminate();
