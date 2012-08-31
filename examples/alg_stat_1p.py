#
# jython examples for jas.
# $Id$
#

import sys;

from jas import Ring, PolyRing, Ideal
from jas import QQ, ZZ, RF
from jas import startLog, terminate


# example: Algebraic Statistics
# Drton, Sturmfels, Sullivant, example 2.1.3


r = PolyRing(RF(PolyRing(QQ(),"u0,u1,u2,u12",PolyRing.lex)),"l1,l2",PolyRing.grad);
print "Ring: " + str(r);
print;

print one,u0,u1,u2,u12,l1,l2;

f1 = (u1+u12)*(l1+l2+2)*(l1+1)*(l1+l2+1)\
     + (u12)*l1*(l1+1)*(l1+l2+1)\
     - (u2+u12)*l1*(l1+l2+2)*(l1+l2+1)\
     - (u0+u1+u2+u12)*l1*(l1+l2+2)*(l1+1)  ;

f2 = (u2+u12)*(l1+l2+2)*(l2+1)*(l1+l2+1)\
     + (u12)*l2*(l2+1)*(l1+l2+1)\
     - (u1+u12)*l2*(l1+l2+2)*(l1+l2+1)\
     - (u0+u1+u2+u12)*l2*(l1+l2+2)*(l2+1)  ;

print f1;
print f2;
print

#h = l1*l2*(l1+1)*(l2+1)*(l1+l2+1)*(l1+l2+2);
h = l1*l2*(l1+1);
hp = (l2+1);
hpp = (l1+l2+1)*(l1+l2+2);
print h;
print hp;
print hpp;
print

F = r.ideal(list=[f1,f2]);
print F;
print

H = r.ideal(list=[h]);
print H;
print

Hp = r.ideal(list=[hp]);
print Hp;
print

Hpp = r.ideal(list=[hpp]);
print Hpp;
print

startLog();

G = F.GB();
print G;
print

#startLog();

Q = G.sat(H);
print Q;
print

Q = Q.sat(Hp);
print Q;
print

Q = Q.sat(Hpp);
print Q;
print

D = Q.radicalDecomp();
print D;
print

#Di = Q.decomposition();
#print Di;
#print

#startLog();

terminate();
#sys.exit(); 

