#
# jython examples for jas.
# $Id$
#

import sys;

from jas import Ring
from jas import Ideal
from jas import startLog
from jas import terminate


# ideal elimination example

r = Ring( "Rat(x,y,z) G" );
print "Ring: " + str(r);
print;

ps1 = """
(
 ( x^2 - 2 ),
 ( y^2 - 3 ),
 ( z^3 - x * y )
)
""";

F1 = r.ideal( ps1 );
print "Ideal: " + str(F1);
print;

e = Ring( "Rat(z) G" );
print "Ring: " + str(e);
print;

#startLog();

rg1 = F1.eliminateRing(e);
print "rg1 = ", rg1;
print;

rg2 = rg1.intersectRing(e);
print "rg2 = ", rg2;
print;

terminate();
#sys.exit();
