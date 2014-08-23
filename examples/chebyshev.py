#
# jython examples for jas.
# $Id$
#

import sys;

from jas import PolyRing, ZZ
from jas import startLog

# chebyshev polynomial example
# T(0) = 1
# T(1) = x
# T(n) = 2 * x * T(n-1) - T(n-2)

#r = Ring( "Z(x) L" );
r = PolyRing(ZZ(), "(x)", PolyRing.lex );
print "Ring: " + str(r);
print;

# sage like: with generators for the polynomial ring
#is automatic: [one,x] = r.gens();

x2 = 2 * x;

N = 10;
T = [one,x];
for n in range(2,N+1):
    t = x2 * T[n-1] - T[n-2];
    T.append( t );

for n in range(0,N+1):
    print "T[%s] = %s" % (n,T[n]);

print;

#sys.exit();
