#
# jython examples for jas.

from jas import PolyRing, QQ, Module
#, SubModule

# module example

p = PolyRing(QQ(),"u,v,l", PolyRing.lex);
#r = Module( "Rat(u,v,l) L", cols=4 );
r = Module( "", p, cols=4 );
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
