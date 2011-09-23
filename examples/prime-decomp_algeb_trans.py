#
# jython examples for jas.
# $Id$
#

import sys

from java.lang import System

from jas import PolyRing, Ideal
from jas import QQ, AN, RF
from jas import terminate, startLog

# polynomial examples: prime/primary decomposition in Q(sqrt(2))(x)(sqrt(x))[y,z]

Q = PolyRing(QQ(),"w2",PolyRing.lex);
print "Q     = " + str(Q);
[e,a] = Q.gens();
#print "e     = " + str(e);
print "a     = " + str(a);

root = a**2 - 2;
print "root  = " + str(root);
Q2 = AN(root,field=True);
print "Q2    = " + str(Q2.factory());
[one,w2] = Q2.gens();
#print "one   = " + str(one);
#print "w2    = " + str(w2);
print;

Qp = PolyRing(Q2,"x",PolyRing.lex);
print "Qp    = " + str(Qp);
[ep,wp,ap] = Qp.gens();
#print "ep    = " + str(ep);
#print "wp    = " + str(wp);
#print "ap    = " + str(ap);
print;

Qr = RF(Qp);
print "Qr    = " + str(Qr.factory());
[er,wr,ar] = Qr.gens();
#print "er    = " + str(er);
#print "wr    = " + str(wr);
#print "ar    = " + str(ar);
print;

Qwx = PolyRing(Qr,"wx",PolyRing.lex);
print "Qwx   = " + str(Qwx);
[ewx,wwx,ax,wx] = Qwx.gens();
#print "ewx   = " + str(ewx);
print "ax    = " + str(ax);
#print "wwx   = " + str(wwx);
print "wx    = " + str(wx);
print;

rootx = wx**2 - ax;
print "rootx = " + str(rootx);
Q2x = AN(rootx,field=True);
print "Q2x   = " + str(Q2x.factory());
[ex2,w2x2,ax2,wx] = Q2x.gens();
#print "ex2   = " + str(ex2);
#print "w2x2  = " + str(w2x2);
#print "ax2   = " + str(ax2);
#print "wx    = " + str(wx);
print;


Yr = PolyRing(Q2x,"y,z",PolyRing.lex)
print "Yr    = " + str(Yr);

[e,w2,x,wx,y,z] = Yr.gens();
print "e     = " + str(e);
print "w2    = " + str(w2);
print "x     = " + str(x);
print "wx    = " + str(wx);
print "y     = " + str(y);
print "z     = " + str(z);
print;

f1 = ( y**2 - x ) * ( y**2 - 2 );
#f1 = ( y**2 - x )**3 * ( y**2 - 2 )**2;
f2 = ( z**2 - y**2 );

print "f1 = ", f1;
print "f2 = ", f2;
print;

F = Yr.ideal( list=[f1,f2] );

print "F = ", F;
print;

#sys.exit();

startLog();

t = System.currentTimeMillis();
P = F.primeDecomp();
#P = F.primaryDecomp();
t1 = System.currentTimeMillis() - t;
print "P = ", P;
print;
print "prime/primary decomp time =", t1, "milliseconds";
print;

print "F = ", F;
print;

#startLog();
terminate();

