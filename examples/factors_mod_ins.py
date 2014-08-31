#
# jython examples for jas.
# $Id$
#

import sys

from java.lang import System

from jas import PolyRing, ZM, QQ, RF, GF
from jas import terminate, startLog

# polynomial examples: factorization over Z_p, with p-th root

p = 5;
cr = PolyRing(GF(p),"u",PolyRing.lex );
print "Ring cr: " + str(cr);

#[one,u] = cr.gens();

fu = (u**2+u+1)**p;
print "fu = ", fu;
print;

t = System.currentTimeMillis();
G = cr.squarefreeFactors(fu);
t = System.currentTimeMillis() - t;
#print "G = ", G; #.toScript();
print "factor time =", t, "milliseconds";
for h, i in G.iteritems():
    print "h**i = (", h, ")**" + str(i);
    h = h**i;
print;

qcr = RF(cr);
print "Ring qcr: " + str(qcr.factory());

#not ok#r = PolyRing(cr,"x",PolyRing.lex );
r = PolyRing(qcr,"x",PolyRing.lex );
print "Ring r: " + str(r);

#qr = RF(r);
#print "Ring qr: " + str(qr.factory());
print;

#[one,u,x] = r.gens();
print "one = " + str(one);
print "u   = " + str(u);
print "x   = " + str(x);


#f = x**3 - u;
#f = (x - u)**3;
#f = (x - u**3)**3;
#f = (x - u**9)**3;

#f = x**p - u;
#f = (x - u)**p;

p2 = p * 2;
fu = (u**2+u+1)**p;
#f = x**p + 1/fu;
f = x**p + fu;
#f = x**p2 - fu * x**p - fu;
#f = x**p2 + x**p + 1;
#f = x**p2 + 1;


print "f = ", f;
print;

startLog();

t = System.currentTimeMillis();
G = r.squarefreeFactors(f);
#G = r.factorsAbsolute(f);
#G = None;
t = System.currentTimeMillis() - t;
#print "G = ", G; #.toScript();
print "factor time =", t, "milliseconds";

for h, i in G.iteritems():
    print "h**i = (", h, ")**" + str(i);
    h = h**i;
print;

gu = u**2+u+1;
#g = (x + 1/gu);
g = (x + gu);
print "g    = ", g;
g = g**p;
print "g**p = ", g;
print;

terminate();
