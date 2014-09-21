#
# jython examples for jas.
# $Id$
#

import sys;
from java.lang import System

from jas import PolyRing, EF, QQ, ZZ, RF, ZM
from jas import terminate, startLog


# polynomial examples: ideal prime decomposition
# TRANSACTIONS OF THE AMERICAN MATHEMATICAL SOCIETY
# Volume 296, Number 2. August 1986
# ON THE DEPTH OF THE SYMMETRIC ALGEBRA
# J. HERZOG M. E. ROSSI AND G. VALLA

#r = PolyRing(QQ(),"x,t,z,y",PolyRing.lex);
#r = PolyRing(QQ(),"x,y,t,z",PolyRing.lex);
#r = EF(QQ()).extend("x").polynomial("y,z,t").build(); #,PolyRing.lex);
#c = PolyRing(QQ(),"t,y",PolyRing.lex);

#c = PolyRing(ZM(32003,0,True),"t",PolyRing.lex);
#c = PolyRing.new(GF(32003),"t",PolyRing.lex);
c = PolyRing(ZZ(),"t",PolyRing.lex);
r = PolyRing(RF(c),"z,y,x",PolyRing.lex);
print "Ring: " + str(r);
print;

#automatic: [one,t,z,y,x] = r.gens();
print "one = ", one;
print "x   = ", x;
print "y   = ", y;
print "z   = ", z;
print "t   = ", t;

f1 = x**3 - y**7;
f2 = x**2 * y - x * t**3 - z**6;
f3 = z**2 - t**3;
#f3 = z**19 - t**23;

print "f1 = ", f1;
print "f2 = ", f2;
print "f3 = ", f3;
print;

F = r.ideal( list=[f1,f2,f3] );
print "F = ", F;
print;

#sys.exit();

startLog();

t = System.currentTimeMillis();
P = F.radicalDecomp();
#P = F.primeDecomp();
t1 = System.currentTimeMillis() - t;
print "P = ", P;
print;
print "prime/radical decomp time =", t1, "milliseconds";
print;

print "F = ", F;
print;

#startLog();
terminate();
