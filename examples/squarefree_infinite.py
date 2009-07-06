#
# jython examples for jas.
# $Id$
#

from java.lang import System
from java.lang import Integer

from jas import PolyRing, QQ, ZM, RF
from jas import terminate
from jas import startLog

# polynomial examples: squarefree: characteristic p, infinite

r = PolyRing( RF(PolyRing(ZM(5,field=True),"u, v",PolyRing.lex)), "x y z",PolyRing.lex);

print "r = " + str(r);
[one,u,v,x,y,z] = r.gens();
print "one   = " + str(one);
print "u     = " + str(u);
print "v     = " + str(v);
print "x     = " + str(x);
print "y     = " + str(y);
print "z     = " + str(z);

print "Ring: " + str(r);
print;

a = r.random(k=1,l=3);
b = r.random(k=1,l=3);
c = r.random(k=1,l=3);

if a.isZERO():
    a = x;
if b.isZERO():
    b = y;
if c.isZERO():
    c = z;

#f = a**2 * b**3 * c;
f = b**5 * c;

print "a = ", a;
print "b = ", b;
print "c = ", c;
print "f = ", f;
print;

t = System.currentTimeMillis();
F = r.squarefreeFactors(f);
t = System.currentTimeMillis() - t;
print "factors:";
for g in F.keys():
    i = F[g];
    print "g = %s**%s" % (g,i);
print
print "factor time =", t, "milliseconds";

#startLog();
terminate();
