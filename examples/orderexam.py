#
# jython examples for jas.
# $Id$
#

import sys;

from jas import Ring, PolyRing, QQ, ZZ, Order, Scripting
from jas import startLog, terminate

# examples: from Mathematica

# check("GroebnerBasis({x^2 + y^2 + z^2 - 1, x - z + 2, z^2 - x*y},
#                      {x, y, z})", 
#       "{12-28*z+27*z^2-12*z^3+3*z^4, -6+4*y+11*z-6*z^2+3*z^3, 2+x-z}");

Scripting.setCAS(Scripting.CAS.Math);
#Scripting.setCAS(Scripting.CAS.Sage);
#Scripting.setCAS(Scripting.CAS.Singular);

r = PolyRing(ZZ(),"x,y,z",Order.Lexicographic);
print "Ring: " + str(r);
print;

ff = [ x**2 + y**2 + z**2 - 1, 
      x - z + 2, 
      z**2 - x*y]

F = r.ideal( "", ff );
print "F = " + str(F);
print;

#startLog();

G = F.GB();
print "G = " + str(G);
print;
print "Ma: " + str( r.ideal("",[12-28*z+27*z**2-12*z**3+3*z**4,-6+4*y+11*z-6*z**2+3*z**3,2+x-z] ));
print;

# check("GroebnerBasis({x^2 + y^2 + z^2 - 1, x - z + 2, z^2 - x*y},
#                      {x, y, z}, MonomialOrder -> DegreeReverseLexicographic)",
#       "{16+23*x+12*x^2+3*x^3+4*y,3+4*x+2*x^2+y^2,-4-4*x-x^2+x*y,-2-x+z}"); 

#r = PolyRing(QQ(),"x,y,z",Order.DegreeReverseLexicographic.blockOrder(1,3));
#r = PolyRing(QQ(),"x,y,z",Order.DegreeReverseLexicographic.blockOrder(1));
#r = PolyRing(QQ(),"x,y,z",Order.DegreeReverseLexicographic.blockOrder(1, Order.DegreeLexicographic));
r = PolyRing(ZZ(),"x,y,z",Order.DegreeReverseLexicographic);
print "Ring: " + str(r);
print;

ff = [ x**2 + y**2 + z**2 - 1, 
      x - z + 2, 
      z**2 - x*y]

F = r.ideal( "", ff );
print "F = " + str(F);
print;

#startLog();

G = F.GB();
print "G = " + str(G);
print;

print "Ma: " + str(r.ideal("", [ 16+23*x+12*x**2+3*x**3+4*y, 3+4*x+2*x**2+y**2, -4-4*x-x**2+x*y, -2-x+z]) ); 
print;
