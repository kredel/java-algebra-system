#
# jython examples for jas.
# $Id$
#

import sys;

from jas import Ring
from jas import Ideal
from jas import startLog
from jas import terminate


# Nabashima, ISSAC 2007, example F8
# integral function coefficients

r = Ring( "IntFunc(a, b,c, d) (w,z,y,x) G" );
print "Ring: " + str(r);
print;

ps = """
(
 ( { a } x^2 + { b } y ),
 ( { c } w^2 + z ),
 ( ( x - z )^2 + ( y - w)^2 ),
 ( { 2 d } x w - { 2 b } y )
) 
""";

f = r.ideal( ps );
print "Ideal: " + str(f);
print;

from edu.jas.application import PolyUtilApp;
from edu.jas.poly import PolynomialList;
from edu.jas.application import ComprehensiveGroebnerBaseSeq;  

startLog();

cofac = r.ring.coFac.coFac;
#print "cofac:", cofac;
#print;
cgb = ComprehensiveGroebnerBaseSeq( cofac );
#print "cgb:", cgb;
#print;

cl = cgb.GB( f.list );
#print "cl:", cl;
#print;
c = r.ideal( list=cl );
print "c:", c;
print;

bg = cgb.isGB( cl );
if bg:
    print "isCGB: true";
else:
    print "isCGB: false";
print;

terminate();
#------------------------------------------
sys.exit();

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
from edu.jas.application import ComprehensiveGroebnerBaseSeq;  

pr = Ring( ring=pl.ring );

pf = pr.ideal( list=pl.list );
print;
print "Ideal of product decomposition: \n" + str(pf);
print;

cofac = pl.ring.coFac;
#rgbp = RCGroebnerBasePseudoSeq( cofac );
cgb = ComprehensiveGroebnerBaseSeq( cofac );

#sys.exit();

#bg = rgbp.isGB(pl.list);
bg = cgb.isGB(pl.list);
print "isGB:", bg;
print;

#rg = rgbp.GB(pl.list);
rg = cgb.GB(pl.list);

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


#bg = rgbp.isGB(rg);
bg = cgb.isGB(rg);
print "isGB:", bg;
print;

terminate();
#sys.exit();
