#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Raksanyi & Walter example
# rational function coefficients

r = Ring.new( "IntFunc(a1, a2, a3, a4) (x1, x2, x3, x4) L" );
puts "Ring: " + str(r);
puts;

ps = """
(
 ( x4 - { a4 - a2 } ),
 ( x1 + x2 + x3 + x4 - { a1 + a3 + a4 } ),
 ( x1 x3 + x1 x4 + x2 x3 + x3 x4 - { a1 a4 + a1 a3 + a3 a4 } ),
 ( x1 x3 x4 - { a1 a3 a4 } )
) 
""";

f = r.paramideal( ps );
puts "Ideal: " + str(f);
puts;

#startLog();

rg = f.GB();
rg = f.GB();
rg = f.GB();
puts "Ideal: " + str(rg);
puts;

bg = rg.isGB();
if bg
    puts "isGB: true";
else
    puts "isGB: false";
end
puts;

terminate();
