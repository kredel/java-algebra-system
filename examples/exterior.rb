#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# exterior calculus example
# Hartley and Tuckey, 1993,
# GB in Clifford and Grassmann algebras

rs = """
# exterior calculus example:
Rat(a,b,c,f,g,h,u,v,w,x,y,z) L
RelationTable
(
 ( b ), ( a ), ( - a b ),
 ( c ), ( a ), ( - a c ),
 ( f ), ( a ), ( - a f ),
 ( g ), ( a ), ( - a g ),
 ( h ), ( a ), ( - a h ),
 ( u ), ( a ), ( - a u ),
 ( v ), ( a ), ( - a v ),
 ( w ), ( a ), ( - a w ),
 ( x ), ( a ), ( - a x ),
 ( y ), ( a ), ( - a y ),
 ( z ), ( a ), ( - a z ),

 ( c ), ( b ), ( - b c ),
 ( f ), ( b ), ( - b f ),
 ( g ), ( b ), ( - b g ),
 ( h ), ( b ), ( - b h ),
 ( u ), ( b ), ( - b u ),
 ( v ), ( b ), ( - b v ),
 ( w ), ( b ), ( - b w ),
 ( x ), ( b ), ( - b x ),
 ( y ), ( b ), ( - b y ),
 ( z ), ( b ), ( - b z ),

 ( f ), ( c ), ( - c f ),
 ( g ), ( c ), ( - c g ),
 ( h ), ( c ), ( - c h ),
 ( u ), ( c ), ( - c u ),
 ( v ), ( c ), ( - c v ),
 ( w ), ( c ), ( - c w ),
 ( x ), ( c ), ( - c x ),
 ( y ), ( c ), ( - c y ),
 ( z ), ( c ), ( - c z ),

 ( g ), ( f ), ( - f g ),
 ( h ), ( f ), ( - f h ),
 ( u ), ( f ), ( - f u ),
 ( v ), ( f ), ( - f v ),
 ( w ), ( f ), ( - f w ),
 ( x ), ( f ), ( - f x ),
 ( y ), ( f ), ( - f y ),
 ( z ), ( f ), ( - f z ),

 ( h ), ( g ), ( - g h ),
 ( u ), ( g ), ( - g u ),
 ( v ), ( g ), ( - g v ),
 ( w ), ( g ), ( - g w ),
 ( x ), ( g ), ( - g x ),
 ( y ), ( g ), ( - g y ),
 ( z ), ( g ), ( - g z ),

 ( u ), ( h ), ( - h u ),
 ( v ), ( h ), ( - h v ),
 ( w ), ( h ), ( - h w ),
 ( x ), ( h ), ( - h x ),
 ( y ), ( h ), ( - h y ),
 ( z ), ( h ), ( - h z ),

 ( v ), ( u ), ( - u v ),
 ( w ), ( u ), ( - u w ),
 ( x ), ( u ), ( - u x ),
 ( y ), ( u ), ( - u y ),
 ( z ), ( u ), ( - u z ),

 ( w ), ( v ), ( - v w ),
 ( x ), ( v ), ( - v x ),
 ( y ), ( v ), ( - v y ),
 ( z ), ( v ), ( - v z ),

 ( x ), ( w ), ( - w x ),
 ( y ), ( w ), ( - w y ),
 ( z ), ( w ), ( - w z ),

 ( y ), ( x ), ( - x y ),
 ( z ), ( x ), ( - x z ),

 ( z ), ( y ), ( - y z )
)
""";

r = SolvableRing.new( rs );
puts "SolvableRing: " + str(r);
puts;

# ( a b + c f + g h ),
# ( u v + w x + y z ),
# ( a v + w x + y z ),

ps = """
(
 ( a b + c f + g h ),
 ( u v + w x + y z ),
 ( a^2 ),
 ( b^2 ),
 ( c^2 ),
 ( f^2 ),
 ( g^2 ),
 ( h^2 ),
 ( u^2 ),
 ( v^2 ),
 ( w^2 ),
 ( x^2 ),
 ( y^2 ),
 ( z^2 )
)
""";

f = r.ideal(ps); 
puts "SolvableIdeal: " + str(f);
puts;

#startLog();

lg = f.leftGB();
puts "seq left GB: " + str(lg);
puts;

tg = f.twosidedGB();
puts "seq twosided GB: " + str(tg);
puts;

rg = f.rightGB();
puts "seq right GB: " + str(rg);
puts;

puts "left == right GB:      " + str(lg == rg);
puts "left == twosided GB:   " + str(lg == tg);
puts "two sided == right GB: " + str(tg == rg);
