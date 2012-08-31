#
# jython examples for jas.
# $Id$
#

import sys;

from jas import Ring, PolyRing, Ideal
from jas import QQ, ZZ
from jas import startLog, terminate


# example: Algebraic Statistics
# Drton, Sturmfels, Sullivant, example 2.1.3


#r = PolyRing(QQ(),"l1,l2",PolyRing.grad);
r = PolyRing(QQ(),"l1,l2",PolyRing.lex);
print "Ring: " + str(r);
print;

print one,l1,l2;

u0 = 3; u1 = 5; u2 = 7; u12 = 11;

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

h = l1*l2*(l1+1)*(l2+1)*(l1+l2+1)*(l1+l2+2);
print h;
print

F = r.ideal(list=[f1,f2]);
print F;
print

H = r.ideal(list=[h]);
print H;
print

G = F.GB();
print G;
print

#startLog();

Q = G.sat(H);
print Q;
print

#D = Q.radicalDecomp();
#print D;
#print

R = Q.realRoots();
print R;
print

print;

#startLog();

terminate();
#sys.exit(); 

