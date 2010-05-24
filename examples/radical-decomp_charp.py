#
# jython examples for jas.
# $Id$
#

from java.lang import System
from java.lang import Integer

from jas import Ring, PolyRing
from jas import terminate
from jas import startLog

from jas import QQ, ZM

# polynomial examples: ideal radical decomposition

#r = Ring( "Rat(x) L" );
#r = Ring( "Q(x) L" );
r = PolyRing(ZM(5),"x,y,z",PolyRing.lex);

print "Ring: " + str(r);
print;

[one,x,y,z] = r.gens();

#f1 = (x**2 - 5)**2;
f1 = (y**10 - x**5)**3;
#f2 = y**6 + 2 * x * y**4 + 4 * x**2 * y**2 + 4 * x**3;
f2 = y**6 + 2 * x * y**4 + 3 * x**2 * y**2 + 4 * x**3;
f2 = f2**5;
f3 = z**10 - x**5;

f4 = (y**2 - x)**3;

print "f1 = ", f1;
print "f2 = ", f2;
print "f3 = ", f3;
print "f4 = ", f4;
print;

F = r.ideal( list=[f2,f3] );

print "F = ", F;
print;

startLog();

t = System.currentTimeMillis();
R = F.radicalDecomp();
t = System.currentTimeMillis() - t;
print "R = ", R;
print;
print "decomp time =", t, "milliseconds";
print;

print "F = ", F;
print;

#startLog();
terminate();
