#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Weyl example from Sun, Wang et al, ISSAC 2012, E 

#r = PolyRing.new(QQ(),"x2,x1,d2,d1",Order::IGRLEX);
r = PolyRing.new(QQ(),"x2,x1,d2,d1",Order::IGRLEX.blockOrder(2));
#r = PolyRing.new(QQ(),"x2,x1,d2,d1");
puts "PolynomialRing: " + str(r);
#is not automatic for constants: 
one,x2,x1,d2,d1 = r.gens();

relations = [d1, x1,  x1 * d1 + 1,
             d2, x2,  x2 * d2 + 1
            ];

puts "relations = [" + relations.join(", ") { |r| r.to_s } + "]";
#puts "order = " + Order.blockOrder(Order::IGRLEX,x1.lm(),2).toScript();
puts;

rp = SolvPolyRing.new(QQ(), "x2,x1,d2,d1",Order::IGRLEX.blockOrder(2), relations);
puts "SolvPolyRing: " + str(rp);
puts;

puts "gens = " + rp.gens().join(", ") { |r| r.to_s };
#is not automatic for constants: 
one,x2,x1,d2,d1 = rp.gens();


ff = [ x1 * d1 + 1, x2 * d2, x1 * d2 + d2] ;

ii = rp.ideal( "", ff );
puts "SolvableIdeal: " + str(ii);
puts;

rgl = ii.leftGB();
puts "seq left GB: " + str(rgl);
puts "isLeftGB: " + str(rgl.isLeftGB());
puts;


#exit(0);

rgr = ii.rightGB();
puts "seq right GB: " + str(rgr);
puts "isRightGB: " + str(rgr.isRightGB());
puts;

exit(0);
#startLog();

rgt = ii.twosidedGB();
puts "seq twosided GB: " + str(rgt);
puts "isTwosidedGB: " + str(rgt.isTwosidedGB());
puts "isONE: " + str(rgt.isONE());
puts;


