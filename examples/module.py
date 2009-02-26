#
# jython examples for jas.

from jas import Module
from jas import SubModule

# module example

r = Module( "Rat(u,v,l) L", cols=4 );
print "Module: " + str(r);
print;

#print "gens() = ", [str(e) for e in r.gens()];
M = r.submodul( list=r.gens() );
print "M = ", M;

P = M.mset.getPolynomialList();
print "P = ", P;

print "M.isGB(): ", M.isGB();
