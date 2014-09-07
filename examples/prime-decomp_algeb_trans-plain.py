#
# jython examples for jas.
# $Id$
#

import sys

from java.lang import System

from jas import PolyRing, QQ, AN, RF
from jas import terminate, startLog

# polynomial examples: prime/primary decomposition in Q[w2,x,wx,y,z]

Q = PolyRing(QQ(),"w2,x,wx,y,z",PolyRing.lex);
print "Q     = " + str(Q);

[e,w2,x,wx,y,z] = Q.gens();
print "e     = " + str(e);
print "w2    = " + str(w2);
print "x     = " + str(x);
print "wx    = " + str(wx);
print "y     = " + str(y);
print "z     = " + str(z);
print;

w1 = w2**2 - 2;
w2 = wx**2 - x;
f1 = ( y**2 - x ) * ( y**2 - 2 );
#f1 = ( y**2 - x )**3 * ( y**2 - 2 )**2;
f2 = ( z**2 - y**2 );

print "w1 = ", w1;
print "w2 = ", w2;
print "f1 = ", f1;
print "f2 = ", f2;
print;

F = Q.ideal( list=[w1,w2,f1,f2] );
print "F = ", F;
print;

#sys.exit();

startLog();

t = System.currentTimeMillis();
P = F.primeDecomp();
#P = F.primaryDecomp();
t1 = System.currentTimeMillis() - t;
print "P = ", P;
print;
print "prime/primary decomp time =", t1, "milliseconds";
print;

print "F = ", F;
print;

#startLog();
terminate();

