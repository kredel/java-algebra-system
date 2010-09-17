#
# jython examples for jas.
# $Id$
#

import sys;

from jas import QQ, DD, Ring
from jas import MultiSeriesRing, MPS
from jas import startLog

from edu.jas.ps import MultiVarCoefficients
from edu.jas.ps import MultiVarPowerSeriesMap

# example for power series
#
#

#
# rational number examples
#

psr = MultiSeriesRing("Q(x,y,z)",truncate=4);
print "psr:", psr;
print;

one = psr.one();
print "one:", one;
print;

zero = psr.zero();
print "zero:", zero;
print;

r1 = psr.random(4);
print "r1:", r1;
print;
print "r1:", r1;
print;
print "r1-r1:", r1-r1;
print;

r2 = psr.random(4);
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

r4 = r1 * r2 + one;
print "r4:", r4;
print;

e = psr.exp(1);
print "e:", e;
print;

r5 = r1 * r2 + e;
print "r5:", r5;
print;

#print "psr.gens: ", [ str(i) for i in psr.gens() ];
[one,x,y,z] = psr.gens();
print "x:", x;
print "y:", y;
print "z:", z;
print;

r6 = one - y;
print "r6:", r6;
print;

r7 = one / r6;
print "r7:", r7;
print;


s = psr.sin(1);
print "s:", s;
print;

r8 = psr.gcd(y,s);
print "r8:", r8;
print;

## s1 = s.evaluate( QQ(0) );
## print "s1:", s1;
## print;

c = psr.cos(1);
print "c:", c;
print;

## c1 = c.evaluate( QQ(0) );
## print "c1:", c1;
## print;

s2c2 = s*s+c*c; # sin^2 + cos^2 = 1
print "s2c2:", s2c2;
print;

#sys.exit();

# conversion from polynomials

pr = Ring("Q(x,y,z) L");
print "pr:", pr;
print;

[one,xp,yp,zp] = pr.gens();

p1 = one;
p2 = one - yp;

ps1 = psr.fromPoly(p1);
ps2 = psr.fromPoly(p2);

# rational function as power series:
ps3 = ps1 / ps2;

print "p1:", p1;
print "p2:", p2;
print "ps1:", ps1;
print "ps2:", ps2;
print "ps3:", ps3;
print;


p1 = one * 2 + yp**3 - yp**5;
p2 = one - yp**2 + yp**4;

ps1 = psr.fromPoly(p1);
ps2 = psr.fromPoly(p2);

# rational function as power series:
ps3 = ps1 / ps2;

ps4 = ps3.integrate( QQ(1), 1 );
ps5 = ps3.differentiate(1);

print "p1:", p1;
print "p2:", p2;
print "ps1:", ps1;
print "ps2:", ps2;
print "ps3:", ps3;
print "ps4:", ps4;
print "ps5:", ps5;
print;

#sys.exit();

#
# floating point examples
#

dr = MultiSeriesRing(cofac=DD(),names=psr.ring.polyRing().getVars(),truncate=psr.ring.truncate());
print "dr:", dr;
print;

de = dr.exp(1);
print "de:", de;
print;

def f(a):
    return a.totalDeg();

ps = psr.create(f);
print "ps:", ps;
print;


def g(a):
    return a.maxDeg();

ps1 = psr.create(g);
print "ps1:", ps1;
print;

ps2 = ps * ps1;
print "ps2:", ps2;
print;


def h(a):
    return psr.ring.coFac.fromInteger( 2*a.maxDeg() );

ps3 = psr.create(jfunc=h);
print "ps3:", ps3;
print;

ps4 = ps3 * ps1;
print "ps4:", ps4;
print;


def k(a):
    if a.signum() > 0:
        r = psr.ring.coFac.fromInteger( 2*a.totalDeg() );
    else:
        r = psr.ring.coFac.getONE();
    #print "r: ", r;
    return r;

ps5 = MPS(psr.ring.coFac,psr.ring.getVars(),f=k);
print "ps5:", ps5;
print;

#sys.exit();

class coeff( MultiVarCoefficients ):
    def __init__(self,r):
        MultiVarCoefficients.__init__(self,r);
        self.coFac = r.coFac;
    def generate(self,i):
        #print "i: ", i;
        if i.signum() < 0:
            return self.coFac.getZERO();
        else:
            if i.isZERO():
                return self.coFac.getONE();
            else:
                c = self.coFac.getONE(); #self.get( i-1 ).negate();
                return c.divide( self.coFac.fromInteger(i.totalDeg()) ).divide( self.coFac.fromInteger(i.totalDeg()-1) );

ps6 = psr.create( clazz=coeff(psr.ring) );
print "ps6:", ps6;
print;

ps7 = ps6 - s;
print "ps7:", ps7;
print;

#sys.exit();

class cosmap( MultiVarPowerSeriesMap ):
    def __init__(self,cofac):
        self.coFac = cofac;
    def map(self,ps):
        return ps.negate().integrate( self.coFac.getZERO(), 1 ).integrate( self.coFac.getONE(), 1 );

ps8 = psr.fixPoint( cosmap( psr.ring.coFac ) );
print "ps8:", ps8;
print;

ps9 = ps8 - c;
print "ps9:", ps9;
print;

