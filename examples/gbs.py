#
# jython examples for jas.
# $Id$
#

import sys;

from jas import Ring
from jas import Ideal
from jas import startLog
from jas import terminate


# ? example

#r = Ring( "Rat(t,x,y,z) L" );
#r = Ring( "Z(t,x,y,z) L" );
#r = Ring( "Mod 11(t,x,y,z) L" );
r = Ring( "Rat(t,x,y) L" );
print "Ring: " + str(r);
print;


ps = """
( 
 ( t - x - 2 y ), 
 ( x**2 + 1 ), 
 ( y**2 + 1 ) 
) 
""";


f = r.ideal( ps );
print "Ideal: " + str(f);
print;

#startLog();

rg = f.GB();
print "seq Output:", rg;
print;

terminate();

