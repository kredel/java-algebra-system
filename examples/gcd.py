#
# jython examples for jas.
# $Id$
#

from java.lang import System

from jas import Ring, PolyRing, QQ, ZZ, GF
from jas import terminate, startLog

# polynomial examples: gcd

#r = Ring( "Mod 1152921504606846883 (x,y,z) L" );
#r = Ring( "Rat(x,y,z) L" );
#r = Ring( "C(x,y,z) L" );
#r = Ring( "Z(x,y,z) L" );
r = PolyRing( QQ(), "x,y,z", PolyRing.lex );
print "Ring: " + str(r);
print;

#[one,x,y,z] = r.gens();

a = r.random();
b = r.random();
c = abs(r.random());
#c = 1; 
#a = 0;


print "a = ", a;
print "b = ", b;
print "c = ", c;
print;

ac = a * c;
bc = b * c;

print "ac = ", ac;
print "bc = ", bc;
print;

t = System.currentTimeMillis();
d = r.gcd(ac,bc);
t = System.currentTimeMillis() - t;

#d = d.monic();
print "d = " + str(d);
print "d = " + str(d.monic());

m = c % d;
## print "m = ", m;
## print;

if m.isZERO():
    print "gcd time =", t, "milliseconds,", "isGcd(c,d): true" ;
else:
    print "gcd time =", t, "milliseconds,", "isGcd(c,d): ",  str(m);
print;


#startLog();
terminate();
