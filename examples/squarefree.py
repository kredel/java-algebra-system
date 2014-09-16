#
# jython examples for jas.
# $Id$
#

from java.lang import System

from jas import PolyRing, QQ, ZM
from jas import terminate, startLog

# polynomial examples: squarefree: characteristic 0

r = PolyRing(QQ(),"x, y, z",PolyRing.lex)
print "Ring: " + str(r);
print;

#automatic: [one,x,y,z] = r.gens();

a = r.random(k=2,l=3);
b = r.random(k=2,l=3);
c = r.random(k=1,l=3);

if a.isZERO():
    a = x;
if b.isZERO():
    b = y;
if c.isZERO():
    c = z;

f = a**2 * b**3 * c;

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

startLog();
terminate();
