#
# jython examples for jas.
# $Id$
#

import sys;

from jas import PolyRing, QQ
from jas import startLog, terminate 


# trinks 7 example

r = PolyRing(QQ(), "B,S,T,Z,P,W", PolyRing.lex );
print "Ring: " + str(r);
print;
#one,B,S,T,Z,P,W = r.gens(); # capital letter variables automaticaly included in Python

f1 = 45 * P + 35 * S - 165 * B - 36;
f2 = 35 * P + 40 * Z + 25 * T - 27 * S;
f3 = 15 * W + 25 * S * P + 30 * Z - 18 * T - 165 * B**2;
f4 = - 9 * W + 15 * T * P + 20 * S * Z;
f5 = P * W + 2 * T * Z - 11 * B**3;
f6 = 99 * W - 11 *B * S + 3 * B**2;
f7 = 10000 * B**2 + 6600 * B + 2673;

f = r.ideal( "", [f1,f2,f3,f4,f5,f6,f7] );
print "Ideal: " + str(f);
print;

#exit()

rg = f.GB();
print "GB: " + str(rg);
print;

#startLog();

s = rg.syzygy();
print "syzygy: " + str(s);
print;

#print "is syzygy: " + str(rg.isSyzygy(s));
print "is syzygy: " + str(s.isSyzygy(rg));
print;


sg = s.GB();
print "seq module GB: sg = " + str(sg);
print

sm = sg.syzygy();
print "syzygy: sm = " + str(sm);
print;

print "is syzygy: " + str(sm.isSyzygy(sg));
print;
