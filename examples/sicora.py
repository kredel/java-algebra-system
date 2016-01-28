#
# jython examples for jas.
# $Id$
#

import sys;

from jas import PolyRing, ZZ, Order, Ideal
from jas import startLog, terminate

# sicora, e-gb example

r = PolyRing( ZZ(), "t", Order.INVLEX );
print "Ring: " + str(r);
print;

f1 = 2 * t + 1;
f2 = t**2 + 1;

F = r.ideal( "", [f1,f2] );
print "Ideal: " + str(F);
print;

E = F.eGB();
print "seq e-GB:", E;
print "is e-GB:", E.iseGB();
print;

f = t**3;
n = E.eReduction(f);
print "e-Reduction = " + str(n);
