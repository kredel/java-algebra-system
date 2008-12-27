#
# jython examples for jas.
# $Id$
#

from java.lang import System
from java.lang import Integer

from jas import Ring
from jas import Ideal
from jas import terminate
from jas import startLog

# polynomial examples: factorization

#r = Ring( "Mod 1152921504606846883 (x,y,z) L" );
#r = Ring( "Rat(x,y,z) L" );
#r = Ring( "C(x,y,z) L" );
#r = Ring( "Z(x,y,z) L" );
r = Ring( "Z(x) L" );
#r = Ring( "Mod 11 (x) L" );

print "Ring: " + str(r);
print;

[x] = r.gens();
one = r.one();

a = r.random();
b = r.random();
c = abs(r.random());
#c = 1; 
#a = 0;

#f = x**15 - 1;
#f = x * ( x + 1 )**2 * ( x**2 + x + 1 )**3;
#f = x**6 - 3 * x**5 + x**4 - 3 * x**3 - x**2 - 3 * x+ 1;
#f = x**(3*11*11) + 3 * x**(2*11*11) - x**(11*11);
#f = x**(3*11*11*11) + 3 * x**(2*11*11*11) - x**(11*11*11);
#f = (x**2+1)*(x-3)*(x-5)**3;
#f = x**4 + 1;
#f = x**12 + x**9 + x**6 + x**3 + 1;
#f = x**24 - 1;
f = x**20 - 1;
#f = x**22 - 1;

print "a = ", a;
print "b = ", b;
print "c = ", c;
print "f = ", f;
print;

startLog();

t = System.currentTimeMillis();
G = f.factors();
t = System.currentTimeMillis() - t;
print "G = ", G;
#print "factor time =", t, "milliseconds";

g = one;
for h, i in G.iteritems():
    print "h**i = (", h, ")**" + str(i);
    h = h**i;
    g = g*h;
#print "g = ", g;

if cmp(f,g) == 0:
    print "factor time =", t, "milliseconds,", "isFactors(f,g): true" ;
else:
    print "factor time =", t, "milliseconds,", "isFactors(f,g): ",  cmp(f,g);
print;

#d = g / c;
#m = g % c;
#print "d = ", d;
#print "m = ", m;
#print;

#startLog();
#terminate();
