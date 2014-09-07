#
# jython examples for jas.
# $Id$
#

from java.lang import System

from jas import Ring, PolyRing, QQ, DD, RF, GF
from jas import terminate, startLog

# polynomial examples: ideal primary decomposition

cr = PolyRing(GF(5),"c",PolyRing.lex);
print "coefficient Ring: " + str(cr);
rf = RF(cr);
print "coefficient quotient Ring: " + str(rf.ring);

r = PolyRing(rf,"x,y,z",PolyRing.lex);
print "Ring: " + str(r);
print;

#automatic: [one,c,x,y,z] = r.gens();

f1 = (x**2 - 2); #**2
f2 = (y**2 - c)**5;
f3 = (z**2 - 2 * c); #**5 

print "f1 = ", f1;
print "f2 = ", f2;
print "f3 = ", f3;
print;

F = r.ideal( list=[f1,f2,f3] );
print "F = ", F;
print;

startLog();

t = System.currentTimeMillis();
Q = F.primaryDecomp();
t = System.currentTimeMillis() - t;
print "Q = ", Q;
print;
print "primary decomp time =", t, "milliseconds";
print;

print "F = ", F;
print;

#startLog();
terminate();
