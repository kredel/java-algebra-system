#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Raksanyi & Walter example
# integral/rational function coefficients


r = PolyRing.new(PolyRing.new(QQ(),"a1,a2,a3,a4",PolyRing.grad),"x1,x2,x3,x4",PolyRing.lex);
puts "r  = " + str(r);

one,a1,a2,a3,a4,x1,x2,x3,x4 = r.gens();
puts "one = " + str(one);
puts "a1  = " + str(a1);
puts "a2  = " + str(a2);
puts "a3  = " + str(a3);
puts "a4  = " + str(a4);
puts "x1   = " + str(x1);
puts "x2   = " + str(x2);
puts "x3   = " + str(x3);
puts "x4   = " + str(x4);

pl = [ ( x4 - ( a4 - a2 ) ),
      ( x1 + x2 + x3 + x4 - ( a1 + a3 + a4 ) ),
      ( x1 * x3 + x1 * x4 + x2 * x3 + x3 * x4 - ( a1 * a4 + a1 * a3 + a3 * a4 ) ),
      ( x1 * x3 * x4 - ( a1 * a3 * a4 ) ) 
     ];
f = ParamIdeal.new(r,"",pl);
puts "ParamIdeal: " + str(f);

gs = f.CGBsystem();
puts "CGBsystem: " + str(gs);
puts;

puts f.CGB();

puts gs.isCGBsystem();

#rs = gs.regularRepresentation();
#puts "regular representation: " + str(rs);

rs = gs.regularRepresentationBC();
puts "boolean closed regular representation: " + str(rs);

puts rs.isRegularGB();


rsg = rs.regularGB();
puts "regular GB: " + str(rsg);

puts rsg.isRegularGB();

#ss = rsg.stringSlice();
#puts "regular string slice: " + str(ss);

startLog();
terminate();
#sys.exit();


