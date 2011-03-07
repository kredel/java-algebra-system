#
# jython examples for jas.
# $Id$
#

import sys;

from java.lang import System
from java.lang import Integer

from jas import Ring, PolyRing
from jas import terminate, startLog, noThreads

from jas import QQ, ZM, RF, AN

# polynomial examples: ideal radical decomposition, example 8.16 in GB book, base field with p-th root

# noThreads(); # must be called very early

prime = 5;
cf = ZM(prime);
#cf = QQ();

ca = PolyRing(cf,"t",PolyRing.lex);
print "ca = " + str(ca);
[ea,ta] = ca.gens();
print "ea   = " + str(ea);
print "ta   = " + str(ta);
print;

Qpt = RF(ca);
#print Qpt.gens();

[ea2,ta2] = Qpt.gens();
print "ea2  = " + str(ea2);
print "ta2  = " + str(ta2);
print;

cr = PolyRing(Qpt,"wpt",PolyRing.lex);
print "polynomial quotient ring: " + str(cr);

[et2,t,wpt] = cr.gens();
print "et2  = " + str(et2);
print "t    = " + str(t);
print "wpt  = " + str(wpt);
print;

root = wpt**prime - ta2;
af = AN(root,field=True);
print "coefficient algebraic quotient ring: " + str(af.ring.toScript());
#print af.gens();
##xx = AN(( wpt**5 + 4 * t ),True,PolyRing(RF(PolyRing(ZM(5),"t",PolyRing.lex)),"wpt",PolyRing.lex))
##print "xx: " + str(xx.ring.toScript());

[one,t,wpt] = af.gens();
print "one  = " + str(one);
print "t    = " + str(t);
print "wpt  = " + str(wpt);
#print one,t,wpt;
print;

#sys.exit();

r = PolyRing(af,"x,y",PolyRing.lex);
print "polynomial ring: " + str(r);
#print;

[one,t,wpt,x,y] = r.gens();
#print one,t,wpt,x,y;
print "one  = " + str(one);
print "t    = " + str(t);
print "wpt  = " + str(wpt);
print "x    = " + str(x);
print "y    = " + str(y);
print;

#sys.exit();

f1 = x**prime - t;
f2 = y**prime - t;

f2 = f2**3;

f3 = (y-x);
f3 = f3**prime;

print "f1 = ", f1;
print "f2 = ", f2;
#print "f3 = ", f3;
print;

F = r.ideal( list=[f1,f2] );

print "F = ", F;
print;

startLog();

t = System.currentTimeMillis();
R = F.radicalDecomp();
#R = F.primeDecomp();
t = System.currentTimeMillis() - t;
print "R = ", R;
print;
print "decomp time =", t, "milliseconds";
print;

print "F = ", F;
print;

#startLog();
terminate();
