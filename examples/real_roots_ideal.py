#
# jython examples for jas.
# $Id$
#

from java.lang import System
from java.lang import Integer

from jas import Ring, PolyRing
from jas import terminate
from jas import startLog

from jas import QQ, DD

# polynomial examples: real roots over Q for zero dimensional ideals

r = PolyRing(QQ(),"q,w,s,x",PolyRing.lex);

print "Ring: " + str(r);
print;

[one,q,w,s,x] = r.gens();

# EF(QQ()).realExtend("q","q^3 - 3", "[1,2]").realExtend("w", "w^2 - q", "[1,2]").realExtend("s", "s^5 - 2", "[1,2]").polynomial("x").build();

f1 = q**3 - 3;
f2 = w**2 - q;
#f3 = s**5 - 2;
f3 = s**3 - 2;
f4 = x**2 - w * s;

print "f1 = ", f1;
print "f2 = ", f2;
print "f3 = ", f3;
print "f4 = ", f4;
print;

F = r.ideal( list=[f1,f2,f3,f4] );

print "F = ", F;
print;

startLog();

t = System.currentTimeMillis();
R = F.realRoots();
t = System.currentTimeMillis() - t;
print "R = ", R;
print;
print "real roots = ";
F.realRootsPrint()
print "real roots time =", t, "milliseconds";
print;

print "F = ", F;
print;

#startLog();
terminate();
