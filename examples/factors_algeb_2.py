#
# jython examples for jas.
# $Id$
#

import sys

from java.lang import System

from jas import QQ, AN, Ring, PolyRing
from jas import terminate, startLog

# polynomial examples: factorization over Q(i)(sqrt2)

Q = PolyRing(QQ(),"i",PolyRing.lex);
print "Q     = " + str(Q);
#is automatic: Q.inject_variables();
print "i     = " + str(i);

imag = i**2 + 1;
print "imag  = " + str(imag);
Qi = AN(imag,field=True);
print "Qi    = " + str(Qi.factory());
#is automatic: Qi.inject_variables();
print "i     = " + str(i);
print;

Wr = PolyRing(Qi,"w2",PolyRing.lex)
print "Wr    = " + str(Wr);
#is automatic: Wr.inject_variables();
print "i     = " + str(i);
print "w2    = " + str(w2);

w2p = w2**2 - 2;
print "w2p   = " + str(w2p);
Qw2 = AN(w2p,field=True);
print "Qw2   = " + str(Qw2.factory());
#is automatic: Qw2.inject_variables();
print "i     = " + str(i);
print "w2    = " + str(w2);
print;

Qiw2 = PolyRing(Qw2,"x",PolyRing.lex)
print "Qiw2  = " + str(Qiw2);
#is automatic: Qiw2.inject_variables();
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
print;

if cmp(f,g) == 0:
    print "factor time =", t, "milliseconds,", "isFactors(f,g): true" ;
else:
    print "factor time =", t, "milliseconds,", "isFactors(f,g): ",  cmp(f,g);
print;

#startLog();
terminate();
