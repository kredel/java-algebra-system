#
# jython examples for jas.
# $Id$
#

import sys;

from jas import PolyRing, ZZ
from jas import startLog, terminate

# hermite polynomial example
# H(0) = 1
# H(1) = 2 * x
# H(n) = 2 * x * H(n-1) - 2 * (n-1) * H(n-2)

r = PolyRing( ZZ(), "x", PolyRing.lex );
print "Ring: " + str(r);
print;

# sage like: with generators for the polynomial ring
#auto: [one,x] = r.gens();

x2 = 2 * x;

N = 10;
H = [one,x2];
for n in range(2,N+1):
    h = x2 * H[n-1] - 2 * (n-1) * H[n-2];
    H.append( h );

for n in range(0,N+1):
    print "H[%s] = %s" % (n,H[n]);

print;
