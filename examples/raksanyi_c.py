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

#sys.exit();

startLog();

from edu.jas.ring import RGroebnerBasePseudoSeq;  

#print "pl.get(0)            = ", pl.get(0);
#print "pl.get(0).ring       = ", pl.get(0).ring;
#print "pl.get(0).ring.coFac = ", pl.get(0).ring.coFac;
#print;

pr = Ring( ring=pl.get(0).ring );

pf = pr.ideal( list=pl );
print "Ideal, pf: " + str(pf);
print;

cofac = pl.get(0).ring.coFac;
rgbp = RGroebnerBasePseudoSeq( cofac );
#print "rgbp                 = ", rgbp.getClass().getName();

#sys.exit();

bg = rgbp.isGB(pl);
print "isGB:", bg;
print;

rg = rgbp.GB(pl);

pg = pr.ideal( list=rg );
print "Ideal, GB: " + str(pg);
print;

bg = rgbp.isGB(rg);
print "isGB:", bg;
print;

terminate();
#sys.exit();
