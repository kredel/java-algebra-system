#
# jython examples for jas.
# $Id$
#

import sys;

from java.lang import System

from jas import PolyRing, QQ, DD, AN, BigDecimal
from jas import terminate, startLog


# roots simplification

r = PolyRing( QQ(), "I", PolyRing.lex );
print "Ring: " + str(r);
#print;

#automatic: [one,I] = r.gens();
print "one   = ", one;
print "I     = ", I;

eps = QQ(1,10) ** (BigDecimal.DEFAULT_PRECISION); #-3
#eps = QQ(1,10) ** 7;
#eps = nil;
print "eps   = " + str(eps);

ip = (I**2 + 1);   # I
iq = AN( ip, 0, True);
print "iq    = " + str(iq.factory());
print

iroot = ip.algebraicRoots();
print "algebraic roots: iroot         = " + str(iroot);
print "unity algebraic roots: iroot   = " + str(iroot.rootsOfUnity());
iroot = iroot.rootRefine(eps);
print "algebraic roots refined: iroot = " + str(iroot.elem.toDecimalScript());
idroot = ip.decimalRoots(eps);
print "decimal roots: idroot          = " + str(idroot);
print


r2 = PolyRing( QQ(), "w_3_2", PolyRing.lex );
print "Ring: " + str(r2);
#print;
e,a = r2.gens();
print "e     = " + str(e);
print "a     = " + str(a);
w3p = (a**3 - 2);  # root{3}(2)
w3q = AN( w3p, 0, True);
print "w3q   = " + str(w3q.factory());
print

#w3root = RootFactory.algebraicRoots(w3p.elem);
w3root = w3p.algebraicRoots();
print "algebraic roots: w3root         = " + str(w3root);
print "unity algebraic roots: w3root   = " + str(w3root.rootsOfUnity());
w3root = w3root.rootRefine(eps);
print "algebraic roots refined: w3root = " + str(w3root.elem.toDecimalScript());
w3droot = w3p.decimalRoots(eps);
print "decimal roots: w3droot          = " + str(w3droot);
print


print "with intermediate primitive element" 
print 

#rootred = Java::EduJasApplication::RootFactory.rootReduce(iroot.elem, w3root.elem);
rootred = iroot.rootReduce(w3root);
#rootred = r.rootReduce(iroot,w3root);
print "algebraic roots: rootred       = " + str(rootred);
print "unity algebraic roots: rootred = " + str(rootred.rootsOfUnity());
# somewhat slow:
#rootred.rootRefine(eps);
#print "algebraic roots refined: rootred = " + str(rootred.toDecimalScript());
##print

#decroot = RootFactory.decimalRoots(rootred.p, eps.elem);
decroot = rootred.decimalRoots(eps);
print "decimal roots: decroot         = " + str(decroot);
print



#startLog();
terminate();
