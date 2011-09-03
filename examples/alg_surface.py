#
# jython examples for jas.
# $Id$
#

import sys

from java.lang import System
from java.lang import Integer

from jas import Ring
from jas import PolyRing
from jas import Ideal
from jas import QQ, AN, RF, EF
from jas import terminate
from jas import startLog

# polynomial examples: 

Yr = EF(QQ()).extend("x,y").extend("z","z^2 + x^2 + y^2 - 1").build();
#print "Yr    = " + str(Yr);
#print

[one,x,y,z] = Yr.gens();
print "one   = " + str(one);
print "x     = " + str(x);
print "y     = " + str(y);
print "z     = " + str(z);
print;

f = (1+z)*(1-z); # / ( x**2 + y**2 );
print "f     = " + str(f);
print;

g = f / (1 - z); 
print "g     = " + str(g);
print;

#startLog();
terminate();
