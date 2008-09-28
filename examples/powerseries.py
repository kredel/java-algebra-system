#
# jython examples for jas.
# $Id$
#

import sys;

from jas import SeriesRing
from jas import startLog

# example for power series
#
#

pr = SeriesRing("Q(y)");
print "pr:", pr;
print;

one = pr.one();
print "one:", one;
print;

zero = pr.zero();
print "zero:", zero;
print;

r1 = pr.random(4);
print "r1:", r1;
print;

r2 = pr.random(4);
print "r2:", r2;
print;

r3 = r1 + r2;
print "r3:", r3;
print;

r4 = r1 * r2;
print "r4:", r4;
print;


#sys.exit();
