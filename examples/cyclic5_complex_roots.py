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

# polynomial examples: complex roots over Q for zero dimensional ideal `cyclic5'

#r = Ring( "Q(x) L" );
r = PolyRing(QQ(),"a,b,c,d,e",PolyRing.lex);

print "Ring: " + str(r);
print;

[one,a,b,c,d,e] = r.gens();

f1 = a + b + c + d + e;
f2 = a*b + b*c + c*d + d*e + e*a;
f3 = a*b*c + b*c*d + c*d*e + d*e*a + e*a*b;
f4 = a*b*c*d + b*c*d*e + c*d*e*a + d*e*a*b + e*a*b*c;
f5 = a*b*c*d*e - 1;

print "f1 = ", f1;
print "f2 = ", f2;
print "f3 = ", f3;
print "f4 = ", f4;
print "f5 = ", f5;
print;

F = r.ideal( list=[f1,f2,f3,f4,f5] );

print "F = ", F;
print;

startLog();

#G = F.GB();
#print "G = ", G;
#print;

#sys.exit();

t = System.currentTimeMillis();
R = F.complexRoots();
#R = F.realRoots();
t = System.currentTimeMillis() - t;
print;
print "R = ", R;
print;
print "complex roots: ";
F.complexRootsPrint()
print "complex roots time =", t, "milliseconds";
print;

print "F = ", F;
print;

#startLog();
terminate();
