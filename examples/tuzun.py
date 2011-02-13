#
# jython examples for jas.
# $Id$
#

import sys;

from jas import PolyRing, Ideal, ZZ
from jas import startLog, terminate

# tuzun, e-gb example

r = PolyRing(ZZ(), "(t)" );
print "Ring: " + str(r);

[one,t] = r.gens();
print "one: " + str(one);
print "t:   " + str(t);
print;

f1 = 2 * t + 1;
f2 = t**2 + 1; 

F = [f1,f2];
I = r.ideal( list=F );
print "Ideal: " + str(I);
print;

#startLog();

G = I.eGB();
print "seq e-GB:", G;
print "is e-GB:", G.iseGB();
print;

p = f1 + 2*f2 - f1*f2 + f1**4; 
print "p:", p;
n = G.eReduction(p);
print "n:", n;
