#
# jython examples for jas.
# $Id$
#

import sys

from java.lang import System

from jas import Ring, PolyRing
from jas import ZM, QQ, AN, RF
from jas import terminate, startLog

# polynomial examples: factorization over Z_p(x)(sqrt{3}(x))[y]

Q = PolyRing(ZM(5),"x",PolyRing.lex);
print "Q     = " + str(Q);
[e,a] = Q.gens();
#print "e     = " + str(e);
print "a     = " + str(a);

Qr = RF(Q);
print "Qr    = " + str(Qr.factory());
[er,ar] = Qr.gens();
#print "er    = " + str(er);
#print "ar    = " + str(ar);
print;

Qwx = PolyRing(Qr,"wx",PolyRing.lex);
print "Qwx   = " + str(Qwx);
[ewx,ax,wx] = Qwx.gens();
#print "ewx   = " + str(ewx);
print "ax    = " + str(ax);
print "wx    = " + str(wx);
print;

rootx = wx**3 - ax;
print "rootx = " + str(rootx);
Q2x = AN(rootx,field=True);
print "Q2x   = " + str(Q2x.factory());
[ex2,ax2,wx] = Q2x.gens();
#print "ex2   = " + str(ex2);
#print "w2x2  = " + str(w2x2);
#print "ax2   = " + str(ax2);
#print "wx    = " + str(wx);
print;

#Yr = PolyRing(Q2x,"y,z,c0,c1,c2",PolyRing.lex)
Yr = PolyRing(Q2x,"y,z",PolyRing.lex)
print "Yr    = " + str(Yr);

#[e,x,wx,y,z,c0,c1,c2] = Yr.gens();
[e,x,wx,y,z] = Yr.gens();
print "e     = " + str(e);
print "x     = " + str(x);
print "wx    = " + str(wx);
print "y     = " + str(y);
print "z     = " + str(z);
print;

#f = ( y**2 - x );
#f = ( y**2 - ( wx**2 + wx + 2 ) );

f = ( y**2 - ( wx**2 + wx + 2 ) + z**2 + y * wx );
#f = wx**3;
print "f = ", f;
print;

f = f**5;

print "f = ", f;
print;

#g = ( y**2 + ( c0 + c1 * wx + c2 * wx**2 ) );
#print "g = ", g;
#g = g**5;
#print "g = ", g;
#print;

#sys.exit();

startLog();

t = System.currentTimeMillis();
#ok: G = Yr.factors(f);
G = Yr.squarefreeFactors(f);
t = System.currentTimeMillis() - t;
print "#G = ", len(G);
#print "factor time =", t, "milliseconds";

#sys.exit();

print "f    = ", f;
g = e;
for h, i in G.iteritems():
    if i > 1:
        print "h**i = ", h, "**" + str(i);
    else:
        print "h    = ", h;
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
