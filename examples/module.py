#
# jython examples for jas.

from jas import Module
from jas import SubModule

# module example

r = Module( "Rat(u,v,l) L", cols=4 );
print "Module: " + str(r);
print;

G = r.gens();
print "gens() = ", [str(e) for e in G];

L = [ e.elem.val for e in G ]
print "gens() = ", [str(e) for e in L];

M = r.submodul( list=L );
print "M = ", M;

P = M.mset.getPolynomialList();
print "P = ", P.toScript();

print "M.isGB(): ", M.isGB();
