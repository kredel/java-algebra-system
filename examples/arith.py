#
# jython examples for jas.
# $Id$
#

import sys;

from jas import QQ
from jas import CC

# example for rational and complex numbers
#
#

rn = QQ(1,2);
print "rn:", rn;
print "rn^2:", rn*rn;

rn = QQ((3,2));
print "rn:", rn;
print "rn^2:", rn*rn;

c = CC();
print "c:", c;
c = c.one();
print "c:", c;
c = CC((2,),(3,));
print "c:", c;
print "c^5:", c**5 + c.one();

c = CC((2,),rn);
print "c:", c;


#sys.exit();
