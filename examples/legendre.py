#
# jython examples for jas.
# $Id$
#

import sys;

from jas import Ring, PolyRing, QQ
from jas import startLog, terminate

from edu.jas.arith import BigRational

# Legendre polynomial example
# P(0) = 1
# P(1) = x
# P(n) = 1/n [ (2n-1) * x * P(n-1) - (n-1) * P(n-2) ]

r = PolyRing( QQ(), "x", PolyRing.lex );
#r = Ring( "Q(x) L" );
#r = Ring( "C(x) L" );
print "Ring: " + str(r);
print;

# sage like: with generators for the polynomial ring
#auto: [one,x] = r.gens();

N = 10;
P = [one,x];
for n in range(2,N):
    p = (2*n-1) * x * P[n-1] - (n-1) * P[n-2];
    r = (1,n); # no rational numbers in python
    #r = [(1,n)]; # no complex rational numbers in python
    #r = ((1,n),(0,1)); # no complex rational numbers in python
    p = r * p; 
    P.append( p );

for n in range(0,N):
    print "P[%s] = %s" % (n,P[n]);

print;
