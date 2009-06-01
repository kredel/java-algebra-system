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

# polynomial examples: absolute factorization over Q

#r = Ring( "Rat(x) L" );
r = Ring( "Q(x,y) L" );

print "Ring: " + str(r);
print;

[one,x,y] = r.gens();

f1 = x**2 + y**2;
f2 = x**3 + y**2;
f3 = x**4 + 4;

f = f1**3 * f2**1 * f3**2;

print "f = ", f;
print;

startLog();

t = System.currentTimeMillis();
#G = r.squarefreeFactors(f);
G = r.factorsAbsolute(f);
t = System.currentTimeMillis() - t;
print "G = ", G.toScript();
print
print "factor time =", t, "milliseconds";
print

#startLog();
terminate();
