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

# polynomial examples: absolute factorization over Q(i)

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
print;


r = PolyRing(Qi,"x",PolyRing.lex)
print "r    = " + str(r);

[one,i,x] = r.gens();
print "one   = " + str(one);
print "i     = " + str(i);
print "x     = " + str(x);
print;


#f = x**7 - 1;
#f = x**6 + x**5 + x**4 + x**3 + x**2 + x + 1;
#f = x**2 - i;
#f = x**3 - i;
#f = x**2 + 1;
#f = x**5 - 1;
#f = x**4 + x**3 + x**2 + x + (1,);
#f = ( x**2 - i - 1 ) * ( x**2 + i + 1 );
f = ( x**2 - i ) * ( x**2 + i + 1 );

print "f = ", f;
print;

#startLog();

t = System.currentTimeMillis();
G = r.factors(f);
t = System.currentTimeMillis() - t;
#print "G = ", G;
#print "factor time =", t, "milliseconds";

f2 = one;
for h, i in G.iteritems():
    print "h**i = (", h, ")**" + str(i);
    h = h**i;
    f2 = f2*h;
#print "f2 = ", f2;
print;

if cmp(f,f2) == 0:
    print "factor time =", t, "milliseconds,", "isFactors(f,g): true" ;
else:
    print "factor time =", t, "milliseconds,", "isFactors(f,g): ",  cmp(f,f2);
print;


startLog();

t = System.currentTimeMillis();
G = r.factorsAbsolute(f);
t = System.currentTimeMillis() - t;
#print "G = ", G;
print "factor time =", t, "milliseconds";

for h, i in G.iteritems():
    print "h**i = (", h, ")**" + str(i);
print;

#sys.exit();
#startLog();

terminate();
