#
# jython examples for jas.
# $Id$
#

import sys;

from jas import Ring
from jas import Ideal
from jas import startLog
from jas import terminate


# Nabashima, ISSAC 2007, example F1
# rational function coefficients

r = Ring( "IntFunc(a, b,c, d) (w,z,y,x) L" );
print "Ring: " + str(r);
print;

ps = """
(
 ( { a } x^4 y + x y^2 + { b } x ),
 ( x^3 + 2 x y ),
 ( { b } x^2 + x^2 y )
) 
""";

f = r.ideal( ps );
print "Ideal: " + str(f);
print;


from edu.jas.application import PolyUtilApp;
from edu.jas.poly import PolynomialList;

pl = PolyUtilApp.productEmptyDecomposition( f.list );
print;
print "product decomposition:", pl;
print;

sl = PolyUtilApp.productSlice( pl );
#print;
#print "product slice:", sl;
#print;

ssl = PolyUtilApp.productSliceToString( sl );
print;
print "product slice:", ssl;
print;

#sys.exit();

startLog();

from edu.jas.ring import RCGroebnerBasePseudoSeq;  

pr = Ring( ring=pl.ring );

pf = pr.ideal( list=pl.list );
print;
print "Ideal of product decomposition: \n" + str(pf);
print;

cofac = pl.ring.coFac;
rgbp = RCGroebnerBasePseudoSeq( cofac );

#sys.exit();

bg = rgbp.isGB(pl.list);
print "isGB:", bg;
print;

rg = rgbp.GB(pl.list);

pg = pr.ideal( list=rg );
print "Ideal, GB: " + str(pg);
print;

rgl = PolynomialList(pl.ring,rg);

sl = PolyUtilApp.productSlice( rgl );
#print;
#print "product slice:", sl;
#print;

ssl = PolyUtilApp.productSliceToString( sl );
print;
print "product slice:", ssl;
print;


bg = rgbp.isGB(rg);
print "isGB:", bg;
print;

terminate();
#sys.exit();
