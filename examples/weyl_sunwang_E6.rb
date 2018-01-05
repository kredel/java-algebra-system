#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Weyl example from Sun, Wang et al, ISSAC 2012, E6

r = PolyRing.new(QQ(),"x6,x5,x4,x3,x2,x1, d6,d5,d4,d3,d2,d1",Order::IGRLEX.blockOrder(6));
#r = PolyRing.new(QQ(),"x6,x5,x4,x3,x2,x1, d6,d5,d4,d3,d2,d1",Order::IGRLEX);
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
#rp = SolvPolyRing.new(QQ(), "x6,x5,x4,x3,x2,x1, d6,d5,d4,d3,d2,d1",Order::IGRLEX, relations);
puts "SolvPolyRing: " + str(rp);
puts;

#puts "gens =" + rp.gens().join(", ") { |r| r.to_s };

#exit(0);

ff = [ d2*d6 + x4*d2 + x6*d5 + x1*x6,
       d1*d3 + d4**2 - d6**2 + x1*d2 + x3*x5,
       d4**2 - d6**2 # not in original example
     ];

#       d5 + x1
#       d1 + x5
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


