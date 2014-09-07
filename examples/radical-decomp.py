#
# jython examples for jas.
# $Id$
#

from java.lang import System

from jas import Ring, PolyRing, QQ, DD
from jas import terminate, startLog

# polynomial examples: ideal radical decomposition

#r = Ring( "Rat(x) L" );
#r = Ring( "Q(x) L" );
r = PolyRing(QQ(),"x,y,z",PolyRing.lex);
print "Ring: " + str(r);
print;

#automatic: [one,x,y,z] = r.gens();

f1 = (x**2 - 5)**2;
f2 = (y**2 - 3)**3 * (y**2 - 5);
f3 = z**3 - x * y;

#print "f1 = ", f1;
print "f2 = ", f2;
print "f3 = ", f3;
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
