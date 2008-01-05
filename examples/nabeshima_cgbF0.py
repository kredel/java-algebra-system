#
# jython examples for jas.
# $Id$
#

import sys;

from jas import Ring
from jas import Ideal
from jas import startLog
from jas import terminate


# Nabashima, ISSAC 2007, example F0
# rational function coefficients

r = Ring( "IntFunc(a, b, c) (y,x) L" );
print "Ring: " + str(r);
print;

ps = """
(
 ( { a } x^2 + { b } y^2 ),
 ( { c } x^2 + y^2 ),
 ( { 2 a } x - { 2 c } y )
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
