#
# jython examples for jas.
# $Id$
#

#import sys;

from jas import Ring, PolyRing, Ideal
from jas import ZZ
from jas import startLog

# mark, e-gb and d-gb example to compare with hermit normal form

r = PolyRing( ZZ(), "x4,x3,x2,x1", PolyRing.lex );
print "Ring: " + str(r);
print;

[one,x4,x3,x2,x1] = r.gens();

f1 = x1 + 2 * x2 + 3 * x3 + 4 * x4 + 3;
f2 =      3 * x2 + 2 * x3 +     x4 + 2;
f3 =               3 * x3 + 5 * x4 + 1;
f4 =                        5 * x4 + 4;

L = [f1,f2,f3,f4];
#print "L = ", str(L);

f = r.ideal( list=L );
print "Ideal: " + str(f);
print;

#startLog();

g = f.eGB();
print "seq e-GB:", g;
print "is e-GB:", g.iseGB();
print;

#sys.exit();

d = f.dGB();
print "seq d-GB:", d;
print "is d-GB:", d.isdGB();
print;

#startLog();

print "d-GB == e-GB:", g.pset.equals(d.pset);
