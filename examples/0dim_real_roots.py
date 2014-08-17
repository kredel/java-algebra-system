#
# jython examples for jas.
# $Id$
#

from java.lang import System

from jas import PolyRing
from jas import terminate
from jas import startLog

from jas import QQ

# polynomial examples: real roots over Q for zero dimensional ideals

r = PolyRing(QQ(),"x,y,z",PolyRing.lex);

print "Ring: " + str(r);
print;

#is automatic: [one,x,y,z] = r.gens();

f1 = (x**2 - 5)*(x**2 - 3)**2;
f2 = y**2 - 3;
f3 = z**3 - x * y;

print "f1 = ", f1;
print "f2 = ", f2;
print "f3 = ", f3;
print;

F = r.ideal( list=[f1,f2,f3] );

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
