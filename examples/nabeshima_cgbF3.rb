#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Nabashima, ISSAC 2007, example F3
# integral function coefficients

r = Ring.new( "IntFunc(c, b, a, d) (x) L" );
puts "Ring: " + str(r);
puts;

ps = """
(
 ( { a } x^4 + { c } x^2 + { b } ),
 ( { b } x^3 + x^2 + 2 ),
 ( { c } x^2 + { d } x )
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

terminate();
exit();

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

#------------------------------------------
#exit();
