#
# jython examples for jas.
# $Id$
#

import sys;

from jas import Ring
from jas import Ideal
from jas import startLog
from jas import terminate

# Raksanyi & Walter example
# rational function coefficients

r = Ring( "IntFunc(a1, a2, a3, a4) (x1, x2, x3, x4) L" );
print "Ring: " + str(r);
print;

ps = """
(
 ( x4 - { a4 - a2 } ),
 ( x1 + x2 + x3 + x4 - { a1 + a3 + a4 } ),
 ( x1 x3 + x1 x4 + x2 x3 + x3 x4 - { a1 a4 + a1 a3 + a3 a4 } ),
 ( x1 x3 x4 - { a1 a3 a4 } )
) 
""";

f = r.ideal( ps );
print "Ideal: " + str(f);
print;

from edu.jas.ring import GroebnerBasePseudoSeq;

startLog();

cf = r.ring.coFac;
#rg = f.GB();
rgl = GroebnerBasePseudoSeq(cf).GB( f.list );

print "GB:", rgl;
print;

g = r.ideal( list=rgl );
print "Ideal: " + str(g);
print;

#bg = rg.isGB();
#print "isGB:", bg;
#print;

terminate();
#sys.exit();
