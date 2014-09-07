#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Nabashima, ISSAC 2007, example F5
# integral function coefficients

r = Ring.new( "IntFunc(b, a, c) (x,y) L" );
puts "Ring: " + str(r);
puts;

ps = """
(
 ( { a } x^2 y + { b } x + y^2 ),
 ( { a } x^2 y + { b } x y ),
 ( y^2 + { b } x^2 y + { c } x y )
) 
""";

#startLog();

f = r.paramideal( ps );
puts "ParamIdeal: " + str(f);
puts;

gs = f.CGBsystem();
gs = f.CGBsystem();
gs = f.CGBsystem();
gs = f.CGBsystem();
puts "CGBsystem: " + str(gs);
puts;

#exit();

bg = gs.isCGBsystem();
if bg
    puts "isCGBsystem: true";
else
    puts "isCGBsystem: false";
end
puts;

#exit();

gs = f.CGB();
puts "CGB: " + str(gs);
puts;

bg = gs.isCGB();
if bg
    puts "isCGB: true";
else
    puts "isCGB: false";
end
puts;

terminate();
