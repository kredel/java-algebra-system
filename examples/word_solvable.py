#
# jython examples for jas.
# $Id$
#

#from java.lang import System

from jas import WordRing, WordPolyRing, WordIdeal, PolyRing, SolvPolyRing
from jas import terminate, startLog
from jas import QQ, ZZ, GF, ZM

# non-commutative polynomial examples: solvable polynomials example

#r = WordPolyRing(QQ(),"a,b,e1,e2,e3");
r = WordPolyRing(QQ(),"a,b,e,f,g");
print "WordPolyRing: " + str(r);
print;

[one,a,b,e,f,g] = r.gens();
print "one = " + str(one);
print "a = " + str(a);
print "b = " + str(b);
print "e = " + str(e);
print "f = " + str(f);
print "g = " + str(g);
print;

r1 = g * e - (e*g - e);
r2 = g * f - (f*g - f);
r3 = e * a - a * e;
r4 = e * b - b * e;
r5 = f * a - a * f;
r6 = f * b - b * f;
r7 = g * a - a * g;
r8 = g * b - b * g;

f1 = e * g**3 + f**10 - a;
f2 = e**3 * f**2 + g;
f3 = g**3 + g**2 - b;

print "r1 = " + str(r1);
print "r2 = " + str(r2);
print "r3 = " + str(r3);
print "r4 = " + str(r4);
print "r5 = " + str(r5);
print "r6 = " + str(r6);
print "r7 = " + str(r7);
print "r8 = " + str(r8);
print "f1 = " + str(f1);
print "f2 = " + str(f2);
print "f3 = " + str(f3);
print;

F = r.ideal( list=[r1,r2,r3,r4,r5,r6,r7,r8,f1,f2,f3] );
#F = r.ideal( list=[r1,r2,f1,f2,f3] );
print "F = " + str(F);
print;

startLog();

G = F.GB();
print "G = " + str(G);
print "isGB(G) = " + str(G.isGB());
print;


# now as solvable polynomials

p = PolyRing(QQ(),"a,b,e1,e2,e3");
#is automatic: [one,a,b,e1,e2,e3] = p.gens();

relations = [e3, e1, e1*e3 - e1,
             e3, e2, e2*e3 - e2];

print "relations: =", [ str(f) for f in relations ];
print;

#rp = SolvPolyRing(QQ(), "a,b,e1,e2,e3", rel=relations);
rp = SolvPolyRing(QQ(), "a,b,e1,e2,e3", PolyRing.lex, relations);
print "SolvPolyRing: " + str(rp);
print;

print "gens =", [ str(f) for f in rp.gens() ];
#[one,a,b,e1,e2,e3] = rp.gens();
#[one,I,J,K,a,b,e1,e2,e3] = rp.gens();

f1 = e1 * e3**3 + e2**10 - a;
f2 = e1**3 * e2**2 + e3;
f3 = e3**3 + e3**2 - b;

F = [ f1, f2, f3 ];
print "F =", [ str(f) for f in F ];
print

I = rp.ideal( list=F );
print "SolvableIdeal: " + str(I);
print;

rgt = I.twosidedGB();
print "seq twosided GB:" + str(rgt);
print "isTwosidedGB: " + str(rgt.isTwosidedGB());
print
print "rgt: ", [ str(f) for f in rgt.list ];
print;
