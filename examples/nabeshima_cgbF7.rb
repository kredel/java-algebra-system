#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Nabashima, ISSAC 2007, example F7
# integral function coefficients

r = Ring.new( "IntFunc(a, b) (z,y,x) G" );
puts "Ring: " + str(r);
puts;

ps = """
(
 ( x^3 - { a } ),
 ( y^4 - { b } ),
 ( x + y - { a } z )
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
