#
# jython examples for jas.
# $Id$
#

import sys;

from java.lang import System
from java.lang import Integer

from jas import Ring, PolyRing
from jas import terminate
from jas import startLog

from jas import QQ, DD

# polynomial examples: real roots over Q

r = PolyRing(QQ(),"I,x,y,z",PolyRing.lex);
print "Ring: " + str(r);
print;

[one,I,x,y,z] = r.gens();

f1 = z - x - y * I;
f2 = I**2 + 1;

#f3 = z**3 - 2;
f3 = z**3 - 2*I;

print "f1 = ", f1;
print "f2 = ", f2;
print "f3 = ", f3;
print;

F = r.ideal( list=[f1,f2,f3] );

print "F = ", F;
print;

startLog();

G = F.GB();
print "G = ", G;
print;


#terminate();
#sys.exit();

r = PolyRing(QQ(),"x,y",PolyRing.lex);
print "Ring: " + str(r);
print;


[one,x,y] = r.gens();

#    y**3 - 3 * I * x * y**2 - 3 * x**2 * y + I * x**3 - 2 * I = z**3 - 2 
#fr = y**3 - 3 * x**2 * y; 
#fi = -3 * x * y**2 + x**3 - 2;

#    y**3 - 3 * I * x * y**2 - 3 * x**2 * y + I * x**3 + 2 = z**3 - 2 I
fr = y**3 - 3 * x**2 * y  - 2; 
fi = -3 * x * y**2 + x**3;

print "fr = ", fr;
print "fi = ", fi;
print;

F = r.ideal( list=[fr,fi] );
print "F = ", F;
print;

G = F.GB();
print "G = ", G;
print;

t = System.currentTimeMillis();
R = G.realRoots();
t = System.currentTimeMillis() - t;
print "R = ", R;
print;
print "real roots: ";
G.realRootsPrint()
print "real roots time =", t, "milliseconds";
print;

print "G = ", G;
print;

#startLog();
terminate();

