#
# jython examples for jas.
# $Id$
#

import sys;

from jas import PolyRing, ZZ, QQ, RealN, CC, ZM, RF, terminate
from edu.jas.root import RealArithUtil
from edu.jas.arith import ArithUtil

# example for rational and real algebraic numbers
#
#

# continued fractions:

r = PolyRing(QQ(),"alpha",PolyRing.lex);
print "r = " + str(r);
e,a = r.gens();
print "e     = " + str(e);
print "a     = " + str(a);
sqrt2 = a**2 - 2;
print "sqrt2 = " + str(sqrt2);
Qs2r = RealN(sqrt2,[1,2],a-1);
#Qs2r = RealN(sqrt2,[-2,-1],a+1);
print "Qs2r  = " + str(Qs2r.factory()) + " :: " + str(Qs2r.elem);
one,alpha = Qs2r.gens();
print "one   = " + str(one);
print "alpha = " + str(alpha);


cf = Qs2r.contFrac(20);
print "cf    = " + str(cf);
nb = Qs2r.contFracApprox(cf);
print "nb    = " + str(nb) + " ~= " + str(nb.elem.getDecimal());

cf = nb.contFrac(0);
print "cf    = " + str(cf);
nb = nb.contFracApprox(cf);
print "nb    = " + str(nb) + " ~= " + str(nb.elem.getDecimal());

nb = nb.contFracApprox(None);
print "nb    = " + str(nb.signum() == 0);

terminate();
#sys.exit();
