#
# jython examples for jas.
# $Id$
#
## \begin{PossoExample}
## \Name{Hawes2}
## \Parameters{a;b;c}
## \Variables{x;y[2];z[2]}
## \begin{Equations}
## x+2y_1z_1+3ay_1^2+5y_1^4+2cy_1 \&
## x+2y_2z_2+3ay_2^2+5y_2^4+2cy_2 \&
## 2 z_2+6ay_2+20 y_2^3+2c \&
## 3 z_1^2+y_1^2+b \&
## 3z_2^2+y_2^2+b \&
## \end{Equations}
## \end{PossoExample}


import sys;

from jas import Ring, PolyRing, RF, ZZ
from jas import Ideal
from jas import startLog
from jas import terminate

#startLog();

# Hawes & Gibson example 2
# rational function coefficients

#r = Ring( "RatFunc(a, c, b) (y2, y1, z1, z2, x) G" );
r = PolyRing( RF(PolyRing(ZZ(),"a, c, b",PolyRing.lex)), "y2, y1, z1, z2, x", PolyRing.grad );
print "Ring: " + str(r);
print;

[one,a,c,b,y2,y1,z1,z2,x] = r.gens();

p1 = x + 2 * y1 * z1 + 3 * a * y1**2 + 5 * y1**4 + 2 * c * y1;
p2 = x + 2 * y2 * z2 + 3 * a * y2**2 + 5 * y2**4 + 2 * c * y2;
p3 = 2 * z2 + 6 * a * y2 + 20 * y2**3 + 2 * c; 
p4 = 3 * z1**2 + y1**2 + b;
p5 = 3 * z2**2 + y2**2 + b; 

p6 = ( ( p5 / a ) / b ) / c;
print "p6 = ", p6;

F = [p1,p2,p3,p4,p6];

g = r.ideal( list=F );
print "Ideal: " + str(g);
print;

rg = g.GB();
rg = g.GB();
rg = g.GB();
rg = g.GB();
print "GB:", rg;
print;

bg = rg.isGB();
print "isGB:", bg;
print;

p7 = 1 / b;
print "p7 = ", p7;

p8 = p7 * b;
print "p8 = ", p8;


startLog();
terminate();
#sys.exit();

