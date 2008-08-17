#
# jython examples for jas.
# $Id$
#

import sys;

from jas import Ring
from jas import Ideal
from jas import startLog
from jas import terminate

from edu.jas.arith import BigRational

# Legendre polynomial example
# P(0) = 1
# P(1) = x
# P(n) = 1/n [ (2n-1) * x * P(n-1) - (n-1) * P(n-2) ]

r = Ring( "Q(x) L" );
print "Ring: " + str(r);
print;

# sage like: with generators for the polynomial ring
[x] = r.gens();

one = x**0;

N = 10;
P = [one,x];
for n in range(2,N):
    p = (2*n-1) * x * P[n-1] - (n-1) * P[n-2];
    r = BigRational(1,n); # no rational numbers in python
    p = p * r; # only on left side
    P.append( p );

for n in range(0,N):
    print "P[%s] = %s" % (n,P[n]);

print;

#sys.exit();
