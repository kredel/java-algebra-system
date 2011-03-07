#
# jython examples for jas.
# $Id$
#

import sys;

from java.lang import System
from java.lang import Integer

from jas import Ring
from jas import PolyRing
from jas import Ideal
from jas import QQ, AN
from jas import terminate
from jas import startLog

# polynomial examples: factorization over Q(i)

Qr = PolyRing(QQ(),"i",PolyRing.lex);
print "Qr    = " + str(Qr);
[e,a] = Qr.gens();
print "e     = " + str(e);
print "a     = " + str(a);
imag = a**2 + 1;
print "imag  = " + str(imag);
Qi = AN(imag,field=True);
print "Qi    = " + str(Qi.factory());
[one,i] = Qi.gens();
print "one   = " + str(one);
print "i     = " + str(i);
b = i**2 + 1;
print "b     = " + str(b);
c = 1 / i;
print "c     = " + str(c);
#Qix = AN(i**2 + 1);
#print "Qix   = " + str(Qix.factory());
print;

r = PolyRing(Qi,"x",PolyRing.lex)
print "r    = " + str(r);

[one,i,x] = r.gens();
print "one   = " + str(one);
print "i     = " + str(i);
print "x     = " + str(x);


#f = x**7 - 1;

f = x**6 + x**5 + x**4 + x**3 + x**2 + x + 1;

print "f = ", f;
print;

startLog();

t = System.currentTimeMillis();
G = r.factors(f);
t = System.currentTimeMillis() - t;
print "G = ", str(G);
#print "factor time =", t, "milliseconds";

## f2 = one;
## for h, i in G.iteritems():
##     print "h**i = (", h, ")**" + str(i);
##     h = h**i;
##     f2 = f2*h;
#print "f2 = ", f2;
print;

## if cmp(f,f2) == 0:
##     print "factor time =", t, "milliseconds,", "isFactors(f,g): true" ;
## else:
##     print "factor time =", t, "milliseconds,", "isFactors(f,g): ",  cmp(f,f2);
## print;



r2 = PolyRing(Qi,"a,b",PolyRing.lex)
print "r2   = " + str(r2);

[one,i,a,b] = r2.gens();
print "one   = " + str(one);
print "i     = " + str(i);
print "a     = " + str(a);
print "b     = " + str(b);

y = a + i * b;

#g = y**7 - 1;
g = y**6 + y**5 + y**4 + y**3 + y**2 + y + 1;
g = - g;

print "g = ", g;
print;


#sys.exit();

#startLog();

t = System.currentTimeMillis();
G = r2.factors(g);
t = System.currentTimeMillis() - t;
print "G = ", str(G);
#print "factor time =", t, "milliseconds";

## g2 = one;
## for h, i in G.iteritems():
##     print "h**i = (", h, ")**" + str(i);
##     h = h**i;
##     g2 = g2*h;
#print "g2 = ", g2;
print;

## if cmp(g,g2) == 0:
##     print "factor time =", t, "milliseconds,", "isFactors(f,g): true" ;
## else:
##     print "factor time =", t, "milliseconds,", "isFactors(f,g): ",  cmp(g,g2);
## print;


#sys.exit();
#startLog();

terminate();
