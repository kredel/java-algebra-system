#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Nabashima, ISSAC 2007, example Ex-4.8
# integral function coefficients

r = Ring.new( "IntFunc(a, b, c) (y,x) L" );
puts "Ring: " + str(r);
puts;

ps = """
(
 ( { a } x^2 + { b } y^2 ),
 ( { c } x^2 + y^2 ),
 ( { 2 a } x - { 2 c } y )
) 
""";

#startLog();

f = r.paramideal( ps );
puts "ParamIdeal: " + str(f);
puts;

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
#------------------------------------------
#exit();

