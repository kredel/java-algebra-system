#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

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
#not supported C(Y,X,x,y) G |2|
C(Y,X,x,y) G 
RelationTable
(
 ( y ), ( x ), ( 0i1 x y )
 ( X ), ( Y ), ( 0i1 Y X )
)
""";

r = SolvableModule.new( rsc );
#r = SolvableModule.new( rsan );
puts "SolvableModule: " + str(r);
puts;


ps = """
(
 ( ( x + 1 ), ( y ) ),
 ( ( x y ), ( 0 ) ),
 ( ( x - X ), ( x - X ) ),
 ( ( y - Y ), ( y - Y ) )
)
""";

f = SolvableSubModule.new( r, ps );
puts "SolvableSubModule: " + str(f);
puts;

#exit()

lg = f.leftGB();
puts "seq left GB: " + str(lg);
puts "is left GB: " + str(lg.isLeftGB());
puts;


tg = f.twosidedGB();
puts "seq twosided GB: " + str(tg);
puts "is twosided GB: " + str(tg.isTwosidedGB());
puts "is right GB: " + str(tg.isRightGB());
puts;

startLog();

rg = f.rightGB();
puts "seq right GB: " + str(rg);
puts "is right GB: " + str(rg.isRightGB());
puts;

exit();

rg2 = rg.rightGB();
puts "seq right GB: " + str(rg2);
puts "is right GB: " + str(rg2.isRightGB());
puts "rg == rg2: " + str(rg == rg2);
puts;

rg3 = rg2.rightGB();
puts "seq right GB: " + str(rg3);
puts "is right GB: " + str(rg3.isRightGB());
puts "rg2 == rg3: " + str(rg2 == rg3);
puts;


#startLog();
terminate();
