#
# jython examples for jas.

from jas import SolvableModule
from jas import SolvableSubModule

# Quantum plane example

rsan = """
# not with twosidedGB, because of field
AN[ (i) (i^2 + 1) ] (Y,X,x,y) G
RelationTable
(
 ( y ), ( x ), ( {i} x y )
 ( X ), ( Y ), ( {i} Y X )
)
""";

rsc = """
# not supported: C(Y,X,x,y) G |2|
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

f = SolvableSubModule( r, ps );
print "SolvableSubModule: " + str(f);
print;

flg = f.leftGB();
print "seq left GB:", flg;
print;


if flg.isLeftGB():
   print "is left GB";
else:
   print "is not left GB";



ftg = f.twosidedGB();
print "seq twosided GB:", ftg;
print;

if ftg.isLeftGB():
   print "twosided GB is left GB";
else:
   print "twosided GB is not left GB";

if ftg.isRightGB():
   print "twosided GB is right GB";
else:
   print "twosided GB is not right GB";

if ftg.isTwosidedGB():
   print "is twosided GB";
else:
   print "is not twosided GB";


from jas import startLog
startLog();

frg = f.rightGB();
print "seq right GB:", frg;
print;

if frg.isRightGB():
   print "is right GB";
else:
   print "is not right GB";
