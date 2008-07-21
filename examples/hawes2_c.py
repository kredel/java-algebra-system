#
# jython examples for jas.
# $Id$
#
## \begin{PossoExample}
## \Name{Hawes2}
## \Parameters{a;b;c}
## \Variables{x;y[2];z[2]}
## \begin{Equations}
## x+2y_1z_1+3ay_1^2+5y_1^4+2cy_1 \&
## x+2y_2z_2+3ay_2^2+5y_2^4+2cy_2 \&
## 2 z_2+6ay_2+20 y_2^3+2c \&
## 3 z_1^2+y_1^2+b \&
## 3z_2^2+y_2^2+b \&
## \end{Equations}
## \end{PossoExample}


import sys;

from jas import Ring
from jas import Ideal
from jas import startLog
from jas import terminate

#startLog();

# Hawes & Gibson example 2
# rational function coefficients

r = Ring( "IntFunc(a, c, b) (y2, y1, z1, z2, x) G" );
print "Ring: " + str(r);
print;

ps = """
(
 ( x + 2 y1 z1 + { 3 a } y1^2 + 5 y1^4 + { 2 c } y1 ),
 ( x + 2 y2 z2 + { 3 a } y2^2 + 5 y2^4 + { 2 c } y2 ), 
 ( 2 z2 + { 6 a } y2 + 20 y2^3 + { 2 c } ), 
 ( 3 z1^2 + y1^2 + { b } ), 
 ( 3 z2^2 + y2^2 + { b } ) 
) 
""";

#startLog();

f = r.paramideal( ps );
print "ParamIdeal: " + str(f);
print;

gs = f.CGBsystem();
print "CGBsystem: " + str(gs);
print;

bg = gs.isCGBsystem();
if bg:
    print "isCGBsystem: true";
else:
    print "isCGBsystem: false";
print;

sys.exit();

gs = f.CGB();
print "CGB: " + str(gs);
print;

bg = gs.isCGB();
if bg:
    print "isCGB: true";
else:
    print "isCGB: false";
print;

sys.exit();


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


from edu.jas.ring import GroebnerBasePseudoSeq;
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

#startLog();

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

