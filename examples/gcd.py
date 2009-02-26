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

# polynomial examples: gcd

#r = Ring( "Mod 1152921504606846883 (x,y,z) L" );
r = Ring( "Rat(x,y,z) L" );
#r = Ring( "C(x,y,z) L" );
#r = Ring( "Z(x,y,z) L" );

print "Ring: " + str(r);
print;

[one,x,y,z] = r.gens();

a = r.random();
b = r.random();
c = abs(r.random());
#c = 1; 
#a = 0;

#f = x * a + b * y**2 + one * z**7;
f = ( x + 1 ) * x**2;
#f = ( y + 1 ) * y**2;
#f = ( z + 1 ) * z**2;

## print "a = ", a;
## print "b = ", b;
## print "c = ", c;
print "f = ", f;
print;

ac = a * c;
bc = b * c;

## print "ac = ", ac;
## print "bc = ", bc;
## print;

t = System.currentTimeMillis();
F = r.squarefreeFactors(f);
t = System.currentTimeMillis() - t;
print "F:";
for g in F.keys():
    i = F[g];
    print "g = (%s)**%s" % (g,i);
print
print "factor time =", t, "milliseconds";

## d = r.gcd(g,c);
## if cmp(c,d) == 0:
##     print "gcd time =", t, "milliseconds,", "isGcd(c,d): true" ;
## else:
##     print "gcd time =", t, "milliseconds,", "isGcd(c,d): ",  cmp(c,d);
## print;

#d = g / c;
#m = g % c;
#print "d = ", d;
#print "m = ", m;
#print;

#startLog();
terminate();
