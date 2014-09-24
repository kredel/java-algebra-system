#
# jython examples for jas.
# $Id$
#

from java.lang import System

from jas import QQ, ZZ, GF, ZM
from jas import terminate, startLog

# integer examples: gcd

r = ZZ();
#r = QQ();
# = GF(19);
# = ZM(19*61);
print "Ring: " + str(r);
print;

a = r.random(251);
b = r.random(171);
c = abs(r.random(211));
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
d = ac.gcd(bc);
t = System.currentTimeMillis() - t;

print "d = " + str(d);

m = d % c;
## print "m = ", m;
## print;

if m.isZERO():
    print "gcd time =", t, "milliseconds,", "isGcd(c,d): true" ;
else:
    print "gcd time =", t, "milliseconds,", "isGcd(c,d): ",  str(m);
print;

#startLog();
terminate();
