#
# jython examples for jas.
# $Id$
#

import sys;

from jas import Ring, PolyRing, QQ, ZZ, RingElem
from jas import startLog, terminate

# GB examples

#r = Ring( "Z(t,x,y,z) L" );
#r = Ring( "Mod 11(t,x,y,z) L" );
#r = Ring( "Rat(t,x,y) L" );
r = PolyRing( QQ(), "t,x,y", PolyRing.lex );
#r = PolyRing( ZZ(), "t,x,y", PolyRing.lex );
print "Ring: " + str(r);
print;

ps = """
(
 ( t - x - 2 y ),
 ( x^4 + 4 ),
 ( y^4 + 4 )
) 
""";

# ( y^4 + 4 x y^3 - 2 y^3 - 6 x y^2 + 1 y^2 + 6 x y + 2 y - 2 x + 3 ),
# ( x^2 + 1 ),
# ( y^3 - 1 )
# ( y^2 + 2 x y + 2 y + 2 x + 1 ),
# ( y^4 + 4 x - 2  y^3 - 6 x + 1  y^2 + 6 x + 2  y - 2 x + 3 ),
# ( t - x - 2 y ), 
# ( 786361/784 y^2 + 557267/392 y + 432049/784 ),
# ( x^7 + 3 y x^4 + 27/8 x^4 + 2 y x^3 + 51/7 x^3 + 801/28 y + 1041/56 )
# ( x**2 + 1 ), 
# ( y**2 + 1 ) 


f = r.ideal( ps );
print "Ideal: " + str(f);
print;

startLog();

rg = f.GB();
print "seq GB:", rg;
print;

#p = t**16 + 272 * t**12 - 7072 * t**8 + 3207424 * t**4 + 12960000;
p = t - x - 2*y;
#p = p * (y**4 + 4);
p = abs(p);

n = f.reduction(p);
print "p = %s, n = %s " % (p,n);
print;

n = rg.reduction(p);
print "p = %s, n = %s " % (p,n);
print;


N = rg.lift(p);
print "p = %s, N = %s " % (p,[ str(a) for a in N ]);
print;

pp = sum([ ci * RingElem(ni) for ci, ni in zip( N, rg.list ) ])
print "N * rg == p:", pp == p

#terminate();
