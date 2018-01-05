#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Weyl example from Sun, Wang et al, ISSAC 2012, E1

r = PolyRing.new(QQ(),"x6,x5,x4,x3,x2,x1, d6,d5,d4,d3,d2,d1",Order::IGRLEX.blockOrder(6));
puts "PolynomialRing: " + str(r);
puts

relations = [d1, x1,  x1 * d1 + 1,
             d2, x2,  x2 * d2 + 1,
             d3, x3,  x3 * d3 + 1,
             d4, x4,  x4 * d4 + 1,
             d5, x5,  x5 * d5 + 1,
             d6, x6,  x6 * d6 + 1
            ];

puts "relations = [" + relations.join(", ") { |r| r.to_s } + "]";
puts;

rp = SolvPolyRing.new(QQ(), "x6,x5,x4,x3,x2,x1, d6,d5,d4,d3,d2,d1",Order::IGRLEX.blockOrder(2), relations);
puts "SolvPolyRing: " + str(rp);
puts;
#puts "gens = " + rp.gens().join(", ") { |r| r.to_s };

#exit(0);

ff = [ d1*d6 + x1*d1, d1*d3 + x3*d4 + x2, x3*d3 + x3*d5, x4*d4*d5 + x2**2] ;

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

#startLog();

rgt = ii.twosidedGB();
puts "seq twosided GB: " + str(rgt);
puts "isTwosidedGB: " + str(rgt.isTwosidedGB());
puts "isONE: " + str(rgt.isONE());
puts;


