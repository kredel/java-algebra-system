#
# jython examples for jas.
# $Id$
#
# modified, not original example

import sys;

from jas import Ring
from jas import Ideal
from jas import startLog
from jas import terminate

#startLog();

# Sato & Suzuki, ISSAC 2001, example 2
# regular ring GB

r = Ring( "IntFunc(p1,p2,p3,p4) (x1,x2,x3,x4,a,b,c,u1,u2,u3,u4) L" );
print "Ring: " + str(r);
print;

ps = """
(
 ( a * ( x1 x2 - 1 ) * ( x2 x3 -1 ) - ( x2 - 1 ) * ( x1 x2 x3 - 1 ) ),
 ( b * ( x2 x3 - 1 ) * ( x3 x4 -1 ) - ( x3 - 1 ) * ( x2 x3 x4 - 1 ) ),
 ( c * ( x1 x2 x3 - 1 ) * ( x3 x4 -1 ) - ( x3 - 1 ) * ( x1 x2 x3 x4 - 1 ) ),
 ( { p1 } * ( x1 - 1 ) * u1 - 1 ),
 ( { p2 } * ( x2 - 1 ) * u2 - 1 ),
 ( { p3 } * ( x3 - 1 ) * u3 - 1 ),
 ( { p4 } * ( x4 - 1 ) * u4 - 1 )
) 
""";

f = r.ideal( ps );
print "Ideal: " + str(f);
print;

#sys.exit();

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
from edu.jas.poly import AlgebraicNumberRing;  
from edu.jas.poly import AlgebraicNumber;  
from edu.jas.structure import Product;  
from edu.jas.structure import ProductRing;  
from edu.jas.application import PolyUtilApp;  


#GenPolynomialRing ufac;
ufac = GenPolynomialRing(BigRational(1),1);

#GenPolynomial m; original is: x^2 - x, test with complex: x^2 + 1
m = ufac.univariate(0,2);
##original##m = m.subtract( ufac.univariate(0,1) );
m = m.sum( ufac.getONE() );
print "m = " + str(m);

#AlgebraicNumberRing afac;
afac = AlgebraicNumberRing(m);
print "afac = " + str(afac);

#ProductRing pfac;
pfac = ProductRing( afac, 4 );
print "pfac = " + str(pfac);

#GenPolynomialRing fac; 
fac = GenPolynomialRing(pfac,11,r.ring.vars); 
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

bg = fp.isGB();
print "isGB:", bg;
print;

terminate();
#sys.exit();

