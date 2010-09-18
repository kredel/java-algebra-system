#
# jython examples for jas.
# $Id$
#

import sys;

from jas import Ring, PolyRing, Ideal, PSIdeal, QQ, ZM
from jas import startLog, terminate

# example from CLO(UAG), 4.4

#R = PolyRing( ZM(32003), "x,y,z" );
R = PolyRing( QQ(), "x,y,z" );
print "Ring: " + str(R);
print;

[one,x,y,z] = R.gens();

f1 = x**5 - x * y**6 - z**7;
f2 = x * y + y**3 + z**3;
f3 = x**2 + y**2 - z**2;

L = [f1,f2,f3];
#print "L = ", str(L);

F = R.ideal( list=L );
print "Ideal: " + str(F);
print;

PR = R.powerseriesRing();
print "Power series ring: " + str(PR);
print;

Fp = PSIdeal(PR,L);
print "Power series ideal: " + str(Fp);
print;

startLog();

S = Fp.STD(9); # truncate at total degree 9!
#print "std: " + str(S);
print;
for p in S.list:
    print "p = ", str(p);
print;

#sys.exit();

#terminate();

