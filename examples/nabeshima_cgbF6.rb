#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Nabashima, ISSAC 2007, example F6
# integral function coefficients

r = Ring.new( "IntFunc(a, b,c, d) (x) G" );
puts "Ring: " + str(r);
puts;

ps = """
(
 ( x^4 + { a } x^3 + { b } x^2 + { c } x + { d } ),
 ( 4 x^3 + { 3 a } x^2 + { 2 b } x + { c } )
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
