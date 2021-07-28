#
# jython examples for jas.
# $Id$
#

import sys;

from jas import PolyRing, Ideal, QQ, ZZ, EF, Mat

# matrix and polynomial examples:
# conditions for (non) commuting matrices

r = EF(QQ()).polynomial("a,b,c,d,e,f,g,h").build();
print "r = " + str(r);

#one, a, b, c, d, e, f, g, h = r.gens();
print "h = " + str(h);
print

x = Mat(r,2,2,[[a,b],[c,d]])
y = Mat(r,2,2,[[e,f],[g,h]])
print "x = " + str(x) + ", y = " + str(y) #+ ", x y = " + str(x*y)
print

com = x*y - y*x
print "commutator = " + str(com)
print

ff = r.ideal("", [ com[0][0], com[0][1], com[1][0], com[1][1] ] )
print "ff = " + str(ff)
print

gg = ff.GB();
print "gg = " + str(gg)
print

#startLog();
#terminate();
