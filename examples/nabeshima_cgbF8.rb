#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Nabashima, ISSAC 2007, example F8
# modified, take care
# integral function coefficients

#r = Ring.new( "IntFunc(d, b, a, c) (y,x,w,z) L" );
r = Ring.new( "IntFunc(d, b, a, c) (y,x,w,z) G" );

#r = Ring.new( "IntFunc(d, b, c, a) (w,z,y,x) G" );
#r = Ring.new( "IntFunc(b, c, a) (w,x,z,y) L" );
#r = Ring.new( "IntFunc(b, c) (z,y,w,x) L" );
#r = Ring.new( "IntFunc(b) (z,y,w,x) L" );
#r = Ring.new( "IntFunc(c) (z,y,w,x) L" );
#r = Ring.new( "IntFunc(c) (z,y,w,x) G" );
puts "Ring: " + str(r);
puts;

ps = """
(
 ( { c } w^2 + z ),
 ( { a } x^2 + { b } y ),
 ( ( x - z )^2 + ( y - w)^2 ),
 ( { 2 d } x w - { 2 b } y )
) 
""";

## ( { 1 } x^2 + { 1 } y ),
## ( { 1 } w^2 + z ),
## ( { 2 } x w - { 2 1 } y )
# ( { 1 } x^2 + { b } y ),
# ( { c } w^2 + z ),
# ( { a } x^2 + { b } y ),
# ( { 2 d } x w - { 2 b } y )


#startLog();

f = r.paramideal( ps );
puts "ParamIdeal: " + str(f);
puts;

#startLog();

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
