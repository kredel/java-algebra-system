#
# jython examples for jas.
# $Id$
#

from jas import SolvableModule
from jas import SolvableSubModule

# Quantum plane example

rsan = """
AN[ (i) (i^2 + 1) ] (Y,X,x,y) G
RelationTable
(
 ( y ), ( x ), ( {i} x y )
 ( X ), ( Y ), ( {i} Y X )
)
""";

rsc = """
C(Y,X,x,y) G
RelationTable
(
 ( y ), ( x ), ( 0i1 x y )
 ( X ), ( Y ), ( 0i1 Y X )
)
""";

r = SolvableModule( rsc );
#r = SolvableModule( rsan );
print "SolvableModule: " + str(r);
print;


ps = """
(
 ( ( x + 1 ), ( y ) ),
 ( ( x y ), ( 0 ) ),
 ( ( x - X ), ( x - X ) ),
 ( ( y - Y ), ( y - Y ) )
)
""";
# ( ( y^2 x ) ),
# ( ( y^3 x^2 ) )
# ( ( x + 1 ) ),
# ( ( x + 1 ), ( y ) ),
# ( ( x y ), ( 0 ) )


f = SolvableSubModule( r, ps );
print "SolvableSubModule: " + str(f);
print;

#flg = f.leftGB();
#print "seq left GB Output:", flg;
#print;

ftg = f.twosidedGB();
print "seq twosided GB Output:", ftg;
print;


from edu.jas.gbmod  import SolvableSyzygyAbstract;
from edu.jas.gbmod  import ModSolvableGroebnerBase;

s = SolvableSyzygyAbstract().leftZeroRelations( ftg.mset );
#sl = ModuleList(f.pset.vars,f.pset.tord,s,f.pset.table);

print "leftSyzygy:", s;
print;

if SolvableSyzygyAbstract().isLeftZeroRelation(s,ftg.mset):
   print "is Syzygy";
else:
   print "is not Syzygy";
