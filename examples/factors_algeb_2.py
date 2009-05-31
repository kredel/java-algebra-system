#
# jython examples for jas.
# $Id$
#

import sys

from java.lang import System
from java.lang import Integer

from jas import Ring
from jas import PolyRing
from jas import Ideal
from jas import QQ, AN
from jas import terminate
from jas import startLog

# polynomial examples: factorization over Q(i)(sqrt2)

Q = PolyRing(QQ(),"i",PolyRing.lex);
print "Q     = " + str(Q);
[e,a] = Q.gens();
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

Wr = PolyRing(Qi,"w2",PolyRing.lex)
print "Wr    = " + str(Wr);
[e,a,b] = Wr.gens();
print "e     = " + str(e);
print "a     = " + str(a);
print "b     = " + str(b);
w2 = b**2 - 2;
print "w2    = " + str(w2);
Qw2 = AN(w2,field=True);
print "Qw2   = " + str(Qw2.factory());
[one,i,w2] = Qw2.gens();
print "one   = " + str(one);
print "i     = " + str(i);
print "w2    = " + str(w2);
print;

Qiw2 = PolyRing(Qw2,"x",PolyRing.lex)
print "Qiw2  = " + str(Qiw2);

[one,i,w2,x] = Qiw2.gens();
print "one   = " + str(one);
print "i     = " + str(i);
print "w2    = " + str(w2);
print "x     = " + str(x);
print;

#sys.exit();

f = ( x**2 + 1 ) * ( x**2 - 2 );

print "f = ", f;
print;

startLog();

t = System.currentTimeMillis();
G = Qiw2.factors(f);
t = System.currentTimeMillis() - t;
#print "G = ", G;
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

#startLog();
terminate();
