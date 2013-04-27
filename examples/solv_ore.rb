#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Ore extension solvable polynomial example, Gomez-Torrecillas, 2003

p = PolyRing.new(QQ(),"x,y,z,t");
#is automatic: [one,x,y,z,t] = p.gens();

relations = [z, y,  y * z + x,
             t, y,  y * t  + y,
             t, z,  z * t - z
            ];

puts "relations: = [" + relations.join(", ") { |r| r.to_s } + "]";
puts;

rp = SolvPolyRing.new(QQ(), "x,y,z,t", PolyRing.lex, relations);
puts "SolvPolyRing: " + str(rp);
puts;

puts "gens =" + rp.gens().join(", ") { |r| r.to_s };
#is automatic: one,x,y,z,t = rp.gens();

f1 = x**2 + y**2 + z**2 + t**2 + 1;

puts "f1 = " +str(f1);

ff = [ f1 ];
puts "ff = [" + ff.join(", ") { |r| r.to_s } + "]";
puts

ii = rp.ideal( "", ff );
puts "SolvableIdeal: " + str(ii);
puts;


rgl = ii.leftGB();
puts "seq left GB: " + str(rgl);
puts "isLeftGB: " + str(rgl.isLeftGB());
puts;

rgr = ii.rightGB();
puts "seq right GB: " + str(rgr);
puts "isRightGB: " + str(rgr.isRightGB());
puts;

#startLog();

rgt = ii.twosidedGB();
puts "seq twosided GB: " + str(rgt);
puts "isTwosidedGB: " + str(rgt.isTwosidedGB());
puts;


#startLog();

rgi = rgl.intersect(rgt);
puts "leftGB intersect twosidedGB: " + str(rgi);
puts;

#startLog();

rgtu = rgt.univariates();
puts "univariate polynomials for twosidedGB: " + rgtu.join(", ");
puts;


#terminate();

