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

#r = Ring( "RatFunc(a1, a2, a3, a4) (x1, x2, x3, x4) L" );
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

#rg = f.GB();
#print "GB:", rg;
#print;

from edu.jas.application import PolyUtilApp;

pl = PolyUtilApp.productDecomposition( f.list );
print;
print "product decomposition:", pl;
print;

sys.exit();

startLog();

from edu.jas.ring import RGroebnerBaseSeq;  

rg = RGroebnerBaseSeq().GB(pl);

#fp = rp.ideal( list=rg );
#print "Ideal, GB: " + str(fp);
#print;

bg = RGroebnerBaseSeq().isGB(rg);
print "isGB:", bg;
print;

terminate();
#sys.exit();
