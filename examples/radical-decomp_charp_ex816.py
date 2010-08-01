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

# polynomial examples: ideal radical decomposition, modified from example 8.16 in GB book

# noThreads(); # must be called very early

prime = 5;
cf = ZM(prime);
#cf = QQ();

ca = PolyRing(cf,"a",PolyRing.lex);
#print "ca = " + str(ca);
[ea,aa] = ca.gens();
print "ea   = " + str(ea);
print "aa   = " + str(aa);
print;

roota = aa**2 + 2;
print "roota = " + str(roota);
Q3a = AN(roota,field=True);
print "Q3a   = " + str(Q3a.factory());

## Q3a = RF(ca);
#print Q3a.gens();

[ea2,aa2] = Q3a.gens();
print "ea2  = " + str(ea2);
print "aa2  = " + str(aa2);
print;

#cr = PolyRing(QQ(),"t",PolyRing.lex);
cr = PolyRing(Q3a,"t",PolyRing.lex);
print "coefficient Ring: " + str(cr);
rf = RF(cr);
print "coefficient quotient Ring: " + str(rf.ring.toScript());

r = PolyRing(rf,"x,y",PolyRing.lex);
print "Ring: " + str(r);
#print;

[one,a,t,x,y] = r.gens();
#print one,a,t,x,y;
print "one = " + str(one);
print "a   = " + str(a);
print "t   = " + str(t);
print "x   = " + str(x);
print "y   = " + str(y);
print;

#sys.exit();

#f1 = x**prime - t;
#f2 = y**prime - t;
##f1 = x**4 + t;
##f2 = y**4 + t;
f1 = x**3 + t;
f2 = y**3 + t;

#f2 = f2**2;

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
#R = F.radicalDecomp();
R = F.primeDecomp();
t = System.currentTimeMillis() - t;
print "R = ", R;
print;
print "decomp time =", t, "milliseconds";
print;

print "F = ", F;
print;

#startLog();
terminate();
