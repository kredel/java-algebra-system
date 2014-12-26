#
# jython examples for jas.
# $Id$
#

#from java.lang import System

from jas import WordRing, WordPolyRing, WordPolyIdeal
from jas import terminate, startLog
from jas import QQ, ZZ, GF, ZM

# non-commutative polynomial examples: simple test

r = WordPolyRing(QQ(),"x,y");
print "WordPolyRing: " + str(r);
print;

[one,x,y] = r.gens();
print "one = " + str(one);
print "x = " + str(x);
print "y = " + str(y);
print;

f1 = x*y - (1,10);
f2 = y*x + x + y;

print "f1 = " + str(f1);
print "f2 = " + str(f2);
print;

c1 = f1 * f2;
c2 = f2 * f1;
s = c1 - c2;

print "c1 = " + str(c1);
print "c2 = " + str(c2);
print "s  = " + str(s);
print;

F = r.ideal( list=[f1,f2] );
print "F = " + str(F);
print;

startLog();

G = F.GB();
print "G = " + str(G);
print "isGB(G) = " + str(G.isGB());
print;


F = r.ideal( list=[f1,f2,c1,c2,s] );
print "F = " + str(F);
print;

G = F.GB();
print "G = " + str(G);
print "isGB(G) = " + str(G.isGB());
print;

p = r.random(3,6,4);
print "p = " + str(p);
print;

pp = p**5;
print "pp = " + str(len(pp));
print "p == pp: " + str(p == pp);
print "pp == pp: " + str(pp == pp);
print "pp-pp == 0: " + str(pp-pp == 0);
print;

#exit(0);

ri = WordPolyRing(ZZ(),"x,y");
print "WordPolyRing: " + str(ri);
print;

[one,x,y] = ri.gens();
print "one = " + str(one);
print "x = " + str(x);
print "y = " + str(y);
print;

f1 = x*y - 10;
f2 = y*x + x + y;

print "f1 = " + str(f1);
print "f2 = " + str(f2);
print;

c1 = f1 * f2;
c2 = f2 * f1;
s = c1 - c2;

print "c1 = " + str(c1);
print "c2 = " + str(c2);
print "s  = " + str(s);
print;

Fi = ri.ideal( list=[f1,f2] );
print "Fi = " + str(Fi);
print;

#not implemented:
#Gi = Fi.GB();
#print "Gi = " + str(Gi);
#print "isGB(Gi) = " + str(Gi.isGB());
#print;

#exit(0);

rp = WordPolyRing(GF(23),"x,y");
print "WordPolyRing: " + str(rp);
print;

[one,x,y] = rp.gens();
print "one = " + str(one);
print "x = " + str(x);
print "y = " + str(y);
print;

f1 = x*y - 10;
f2 = y*x + x + y;

print "f1 = " + str(f1);
print "f2 = " + str(f2);
print;

c1 = f1 * f2;
c2 = f2 * f1;
s = c1 - c2;
s1 = 22 * y*x*x*y + x*y*y*x + 22 * y*x*y + x*y*y + x*y*x + 22 * x*x*y;

print "c1 = " + str(c1);
print "c2 = " + str(c2);
print "s  = " + str(s);
print "s1 = " + str(s1);
print "s == s1: " + str(s==s1);
print;

Fp = rp.ideal( list=[f1,f2] );
print "Fp = " + str(Fp);
print;

Gp = Fp.GB();
print "Gp = " + str(Gp);
print "isGB(Gp) = " + str(Gp.isGB());
print;
