#
# jython examples for jas.
# $Id$
#

import sys;

from jas import Ring
from jas import Ideal
from jas import startLog
from jas import terminate


# Nabashima, ISSAC 2007, example Ex-4.8
# integral function coefficients

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
from edu.jas.application import ComprehensiveGroebnerBaseSeq;  

startLog();

cofac = r.ring.coFac.coFac;
#print "cofac:", cofac;
#print;
cgb = ComprehensiveGroebnerBaseSeq( cofac );
#print "cgb:", cgb;
#print;

print "f.list:", f.list;
print;
cl = cgb.GB( f.list );
#print "cl:", cl;
#print;
c = r.ideal( list=cl );
print "c:", c;
print;

bg = cgb.isGB(cl);
if bg:
    print "isCGB: true";
else:
    print "isCGB: false";
print;

terminate();
#------------------------------------------
sys.exit();

from edu.jas.ring import RCGroebnerBasePseudoSeq;  

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

pr = Ring( ring=pl.ring );

pf = pr.ideal( list=pl.list );
print;
print "Ideal of product decomposition: \n" + str(pf);
print;


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

terminate();
#sys.exit();
