#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# mark, d-gb diplom example

r = PolyRing.new( ZZ(), "x,y,z", PolyRing.lex );
puts "Ring: " + str(r);
puts;

ps = """
( 
 ( z + x y**2 + 4 x**2 + 1 ),
 ( y**2 z + 2 x + 1 ),
 ( x**2 z + y**2 + x )
) 
""";

f = r.ideal( ps );
puts "Ideal: " + str(f);
puts;

#startLog();

eg = f.eGB(); 
puts "seq e-GB: " + str(eg);
puts "is e-GB: " + str(eg.isGB());
puts;

dg = f.dGB(); 
puts "seq d-GB: " + str(dg);
puts "is d-GB: " + str(dg.isGB());
puts;

puts "d-GB == e-GB: " + str(eg.list.equals(dg.list));
puts "d-GB == e-GB: " + str(eg<=>dg);
puts "d-GB == e-GB: " + str(eg===dg);
puts
