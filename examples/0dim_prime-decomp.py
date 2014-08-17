#
# jython examples for jas.
# $Id$
#

from java.lang import System

from jas import PolyRing
from jas import terminate
from jas import startLog

from jas import QQ

# polynomial examples: zero dimensional ideals prime and primary decomposition

r = PolyRing(QQ(),"x,y,z",PolyRing.lex);

print "Ring: " + str(r);
print;

#is automatic: [one,x,y,z] = r.gens();

f1 = (x**2 - 5)**2;
f2 = y**3 - x;
f3 = z**2 - y * x;

print "f1 = ", f1;
print "f2 = ", f2;
print "f3 = ", f3;
print;

F = r.ideal( list=[f1,f2,f3] );

print "F = ", F;
print;

startLog();

t = System.currentTimeMillis();
P = F.primeDecomp();
t1 = System.currentTimeMillis() - t;
print "P = ", P;
print;
print "prime decomp time =", t1, "milliseconds";
print;

print "F = ", F;
print;

#startLog();
terminate();
