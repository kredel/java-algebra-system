#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Nabashima, ISSAC 2007, example Ex-Fig-2
# integral function coefficients

r = Ring.new( "IntFunc(a, b, c) (x) L" );
puts "Ring: " + str(r);
puts;

ps = """
(
 ( { a } x^3 ),
 ( { b } x^2 ),
 ( { c } x )
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

