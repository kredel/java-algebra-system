#
# jython examples for jas.
# $Id$
#

import sys;

from jas import QQ
from jas import DD
from jas import SeriesRing
from jas import startLog

from edu.jas.ps import Coefficients
from edu.jas.ps import PowerSeriesMap

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
print "r1:", r1;
print;
print "r1-r1:", r1-r1;
print;

r2 = pr.random(4);
print "r2:", r2;
print;
print "r2:", r2;
print;
print "r2-r2:", r2-r2;
print;

#sys.exit();

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


def f(a):
    return a*a;

ps = pr.create(f);
print "ps:", ps;
print;


def g(a):
    return a+a;

ps1 = pr.create(g);
print "ps1:", ps1;
print;

ps2 = ps * ps1;
print "ps2:", ps2;
print;


def h(a):
    return pr.ring.coFac.fromInteger( 2*a );

ps3 = pr.create(jfunc=h);
print "ps3:", ps3;
print;

ps4 = ps3 * ps1;
print "ps4:", ps4;
print;


# does not work, since get() is not known
def k(a):
    if a > 0:
        return get(a-1).multiply( pr.ring.coFac.fromInteger( 2*a ) );
    else:
        return pr.ring.coFac.fromInteger( 2*a );

ps5 = pr.create(jfunc=k);
print "ps5:", ps5;
print;


class coeff( Coefficients ):
    def __init__(self,cofac):
        self.coFac = cofac;
    def get(self,i):
        if i == 0:
            return self.coFac.getZERO();
        else:
            if i == 1:
                return self.coFac.getONE();
            else:
                c = self.get( i-2 ).negate();
                return c.divide( self.coFac.fromInteger(i) ).divide( self.coFac.fromInteger(i-1) );

ps6 = pr.create( clazz=coeff(pr.ring.coFac) );
print "ps6:", ps6;
print;

ps7 = ps6 - s;
print "ps7:", ps7;
print;


class cosmap( PowerSeriesMap ):
    def __init__(self,cofac):
        self.coFac = cofac;
    def map(self,ps):
        return ps.negate().integrate( self.coFac.getZERO() ).integrate( self.coFac.getONE() );

ps8 = pr.fixPoint( cosmap( pr.ring.coFac ) );
print "ps8:", ps8;
print;

ps9 = ps8 - c;
print "ps9:", ps9;
print;


#sys.exit();
