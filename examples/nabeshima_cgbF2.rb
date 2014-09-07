#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Nabashima, ISSAC 2007, example F2
# integral function coefficients

r = Ring.new( "IntFunc(b, a) (x,y) L" );
puts "Ring: " + str(r);
puts;

ps = """
(
 ( { a } x^2 y^3 + { b } y + y ),
 ( x^2 y^2 + x y + 2 ),
 ( { a } x^2 + { b } y + 2 )
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

#sys.exit();

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
