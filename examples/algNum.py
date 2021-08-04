#
# jython examples for jas.
# $Id$
#

import sys;

from jas import PolyRing, AN, QQ, ZZ, terminate


# multiple algebraic field extensions

print "------- multiple algebraic field extensions QQ(alpha)(beta)(gamma)(delta) ---------";
r = PolyRing(QQ(),"alpha",PolyRing.lex);
print "r     = " + str(r);
e,a = r.gens();
print "e     = " + str(e);
print "a     = " + str(a);

ap = a**5 - a + 1;
print "ap    = " + str(ap);
x = r.factors(ap); 
print "x     = " + str( [ str(p)+"**"+str(e)+"," for (p,e) in x.iteritems() ] );
qs2 = AN(ap,0,len(x)==1); # and e==1
print "qs2   = " + str(qs2.factory());
one,alpha = qs2.gens();
print "one   = " + str(one);
print "alpha = " + str(alpha);

b = alpha**2 - 1;
print "b     = " + str(b);
c = 1 / b;
print "c     = " + str(c);
print "b*c   = " + str(b*c);
print

ar = PolyRing(qs2,"beta",PolyRing.lex);
print "ar    = " + str(ar);
e,a,b = ar.gens();
print "e     = " + str(e);
print "a     = " + str(a);
print "b     = " + str(b);

bp = b**2 - a;
print "bp    = " + str(bp);
x = ar.factors(bp); 
print "x     = " + str( [ str(p)+"**"+str(e)+"," for (p,e) in x.iteritems() ] );
qs3 = AN(bp,0,len(x)==1); # and e==1
print "qs3   = " + str(qs3.factory());
one,alpha,beta = qs3.gens();
print "one   = " + str(one);
print "alpha = " + str(alpha);
print "beta  = " + str(beta);

c = -alpha + beta**3;
print "c     = " + str(c);
d = 1 / c;
print "d     = " + str(d);
e = c * d;
print "e     = " + str(e);
print

br = PolyRing(qs3,"gamma",PolyRing.lex);
print "br    = " + str(br);
e,a,b,c = br.gens();
print "e     = " + str(e);
print "a     = " + str(a);
print "b     = " + str(b);
print "c     = " + str(c);

cp = c**2 - b*c - a;
print "cp    = " + str(cp);
x = br.factors(cp); 
print "x     = " + str( [ str(p)+"**"+str(e)+"," for (p,e) in x.iteritems() ] );
qs4 = AN(cp,0,len(x)==1); # and e==1
print "qs4   = " + str(qs4.factory());
one,alpha,beta,gamma = qs4.gens();
print "one   = " + str(one);
print "alpha = " + str(alpha);
print "beta  = " + str(beta);
print "gamma = " + str(gamma);

d = -alpha*gamma + beta;
print "d     = " + str(d);
e = 1 / d;
print "e     = " + str(e);
f = e * d;
print "f     = " + str(f);
print

cr = PolyRing(qs4,"delta",PolyRing.lex);
print "cr    = " + str(cr);
e,a,b,c,d = cr.gens();
print "e     = " + str(e);
print "a     = " + str(a);
print "b     = " + str(b);
print "c     = " + str(c);
print "d     = " + str(d);

dp = d**2 + c*d - b + a; # irreducible
#dp = (d**2 + b) * ( c**2 - a ); # = b * c * ( d**2 + b )
#dp = (d + b * c) * ( d + c - a**2 );
print "dp    = " + str(dp);
x = cr.factors(dp); 
print "x     = " + str( [ str(p)+"**"+str(e)+"," for (p,e) in x.iteritems() ] );
qs5 = AN(dp,0,len(x)==1); # and e==1
print "qs5   = " + str(qs5.factory());
one,alpha,beta,gamma,delta = qs5.gens();
print "one   = " + str(one);
print "alpha = " + str(alpha);
print "beta  = " + str(beta);
print "gamma = " + str(gamma);
print "delta = " + str(delta);

#e = -alpha*gamma + beta*delta; # ok
#e = alpha**3 - gamma; # 1/e does fail not because it is a unit
e = delta**2 + beta; # 1/e fails
e = delta + beta*gamma; # 1/e fails
print "e     = " + str(e);
f = 1 / e;
print "f     = " + str(f);
g = e * f;
print "g     = " + str(g);
print

f = ( ( ( ( (3,82) * alpha**4 - (12,41) * alpha**3 - (13,82) * alpha**2 - (19,82) * alpha - (15,82) ) * beta + ( (3,82) * alpha**4 - (12,41) * alpha**3 - (13,82) * alpha**2 - (19,82) * alpha - (15,82) ) ) * gamma - ( ( (21,82) * alpha**4 - (45,82) * alpha**3 - (25,41) * alpha**2 - (51,82) * alpha - (32,41) ) * beta - ( (45,82) * alpha**4 + (25,41) * alpha**3 + (51,82) * alpha**2 + (43,82) * alpha + (21,82) ) ) ) * delta - ( ( ( (39,82) * alpha**4 + (8,41) * alpha**3 - (5,82) * alpha**2 - (1,82) * alpha - (31,82) ) * beta ) * gamma + ( (11,82) * alpha**4 - (3,41) * alpha**3 + (7,82) * alpha**2 - (15,82) * alpha + (27,82) ) ) );

print "f     = " + str(f);
e = 1 / f;
print "e     = " + str(e);
g = e * f;
print "g     = " + str(g);
print

terminate();
