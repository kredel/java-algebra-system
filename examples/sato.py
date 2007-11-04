#
# jython examples for jas.
# $Id$
#

import sys;

from jas import Ring
from jas import Ideal
from jas import startLog
from jas import terminate

#startLog();

# Sato & Suzuki, ISSAC 2001, example 1
# regular ring GB
# modified, not original example

r = Ring( "IntFunc(a,b,c,d,e,f,g,h,i,j,k,l) (x,y,z) L" );
print "Ring: " + str(r);
print;

ps = """
(
 ( { 3 e } x^3 y + { 5 b } x y z - y + { 7 f } z + { i } x ),
 ( { 6 c } x y^2 z^2 - { 7 h } x + { d } y - { 8 g } + { j } z ),
 ( { a } x y z  - { b } x y + { 6 k } y - { 5 l } z - 3 ) 
) 
""";

f = r.ideal( ps );
print "Ideal: " + str(f);
print;

#o = f.optimize();
#print "optimized Ideal: " + str(o);
#print;

#o = o.optimizeCoeffQuot();
#print "optimized coeff Ideal: " + str(o);
#print;
#f = o;

from edu.jas.arith import BigRational;  
from edu.jas.poly import GenPolynomialRing;  
from edu.jas.poly import GenPolynomial;  
from edu.jas.poly import ANumRegularRing;  
from edu.jas.poly import AlgebraicNumberRing;  
from edu.jas.poly import AlgebraicNumber;  
from edu.jas.structure import Product;  
from edu.jas.structure import ProductRing;  
from edu.jas.application import PolyUtilApp;  


#GenPolynomialRing ufac;
ufac = GenPolynomialRing(BigRational(1),1);

#GenPolynomial m; original is: x^2 - x, test with complex: x^2 + 1
m = ufac.univariate(0,2);
##original##
m = m.subtract( ufac.univariate(0,1) );
##test complex#m = m.sum( ufac.getONE() );
print "m = " + str(m);

#AlgebraicNumberRing afac;
#afac = AlgebraicNumberRing(m);
afac = ANumRegularRing(m);
print "afac = " + str(afac);

#ProductRing pfac;
pfac = ProductRing( afac, 12 );
print "pfac = " + str(pfac);

#GenPolynomialRing fac; 
fac = GenPolynomialRing(pfac,3,r.ring.tord,r.ring.vars); 
print "fac = " + str(fac);

rp = Ring( ring=fac );
print "Ring: " + str(rp);
print;

np = PolyUtilApp.toANProductCoeff(fac,f.list);
print "np = " + str(np);

fp = rp.ideal( list=np );
print "Ideal: " + str(fp);
print;

startLog();

from edu.jas.ring import RGroebnerBaseSeq;  

rg = RGroebnerBaseSeq().GB(np);
fp = rp.ideal( list=rg );
print "Ideal, GB: " + str(fp);
print;

bg = RGroebnerBaseSeq().isGB(rg);
print "isGB:", bg;
print;

terminate();
#sys.exit();

