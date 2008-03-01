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
# integral/rational function coefficients

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

#startLog();

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

cpl = PolyUtilApp.productSlice( rgl, 0 );
cplist = cpl.list;
bg = cgb.isCGB(0,cplist);
print "isCGB:", bg;
print;

cpl = PolyUtilApp.productSlicesUnion( rgl );
cplist = cpl.list;
bg = cgb.isCGB(0,cplist);
print "isCGB:", bg;
print;

startLog();
terminate();
#sys.exit();
