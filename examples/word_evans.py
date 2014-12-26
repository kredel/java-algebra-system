#
# jython examples for jas.
# $Id$
#

#from java.lang import System

from jas import WordRing, WordPolyRing, WordPolyIdeal
from jas import terminate, startLog
from jas import QQ, ZZ, GF, ZM

# non-commutative polynomial examples: evans example

r = WordPolyRing(QQ(),"x,y,z");
print "WordPolyRing: " + str(r);
print;

[one,x,y,z] = r.gens();
print "one = " + str(one);
print "x = " + str(x);
print "y = " + str(y);
print "z = " + str(z);
print;

f1 = x * y - z;
f2 = y * z + 2 * x + z;
f3 = y * z + x;

print "f1 = " + str(f1);
print "f2 = " + str(f2);
print "f3 = " + str(f3);
print;

F = r.ideal( list=[f1,f2,f3] );
print "F = " + str(F);
print;

startLog();

G = F.GB();
print "G = " + str(G);
print "isGB(G) = " + str(G.isGB());
print;

#exit(0);

c1 = f1 * f2;
c2 = f2 * f1;
s = c1 - c2;

print "c1 = " + str(c1);
print "c2 = " + str(c2);
print "s  = " + str(s);
print;

Fa = r.ideal( list=[f1,f2,f3,c1,c2,s] );
print "Fa = " + str(Fa);
print;

Ga = Fa.GB();
print "Ga = " + str(Ga);
print "isGB(Ga) = " + str(Ga.isGB());
#print;

#print "G = " + str(G);
print "G == Ga: " + str(cmp(G,Ga));
print "G == Ga: " + str(G.list == Ga.list);
print;

