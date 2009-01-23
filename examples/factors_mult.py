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
#r = Ring( "Z(x) L" );
#r = Ring( "Mod 3 (x,y,z) L" );
r = Ring( "Z(x,y,z) L" );

print "Ring: " + str(r);
print;

[x,y,z] = r.gens();
one = r.one();

a = r.random();
b = r.random();
c = abs(r.random());
#c = 1; 
#a = 0;

#f = z * ( y + 1 )**2 * ( x**2 + x + 1 )**3;
#f = z * ( y + 1 ) * ( x**2 + x + 1 );
#f = ( y + 1 ) * ( x**2 + x + 1 );
#f = ( y + z**2 ) * ( x**2 + x + 1 );

#f = x**4 * y + x**3  + z + x   + z**2 + y * z**2;
## f = x**3 + ( ( y + 2 ) * z + 2 * y + 1 ) * x**2 \
##     + ( ( y + 2 ) * z**2 + ( y**2 + 2 * y + 1 ) * z + 2 * y**2 + y ) * x \
##     + ( y + 1 ) * z**3 + ( y + 1 ) * z**2 + ( y**3 + y**2 ) * z + y**3 + y**2;

#f = ( x + y * z + y + z + 1 ) * ( x**2 + ( y + z ) * x + y**2 + z**2 );
f = ( x + y * z + y + z + 1 ) * ( x**2 + ( y + z ) * x + y**2 + 1 );


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
terminate();
