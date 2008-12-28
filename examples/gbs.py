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
r = Ring( "Mod 11(t,x,y,z) L" );
print "Ring: " + str(r);
print;


ps = """
( 
 ( t - x - 3 * y - 5 * z ), 
 ( x**2 + 2 ), 
 ( y**2 + 5 ), 
 ( z**2 + 3 ) 
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

