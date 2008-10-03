#
# jython examples for jas.
# $Id$
#

import sys;

from jas import QQ
from jas import DD
from jas import SeriesRing
from jas import startLog

# example for power series
#
#

#
# rational number examples
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

e = pr.exp();
print "e:", e;
print;

s = pr.sin();
print "s:", s;
print;

s1 = s.evaluate( QQ(0) );
print "s1:", s1;
print;

c = pr.cos();
print "c:", c;
print;

c1 = c.evaluate( QQ(0) );
print "c1:", c1;
print;


#
# floating point examples
#

dr = SeriesRing(cofac=DD());
print "dr:", dr;
print;

de = dr.exp();
print "de:", de;
print;

d0 = de.evaluate( DD(0) );
print "d0:", d0;
print;

d1 = de.evaluate( DD(0.5) );
print "d1:", d1;
print;

d01 = de.evaluate( DD(0.000000000000000001) );
print "d01:", d01;
print;

#sys.exit();
