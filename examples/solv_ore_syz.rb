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

#f1 = x**2 + y**2 + z**2 + t**2 + 1;
#puts "f1 = " +str(f1);

ff = [ x, y, z, t**2 - 1 ];
puts "ff = [" + ff.join(", ") { |r| r.to_s } + "]";
puts

ii = rp.ideal( "", ff );
puts "SolvableIdeal: " + str(ii);
puts;


rgl = ii.leftGB();
puts "seq left GB: " + str(rgl);
puts "isLeftGB: " + str(rgl.isLeftGB());
puts;

s = rgl.leftSyzygy();
puts "syzygy: " + str(s);
puts;

t = rgl.isLeftSyzygy(s);
puts "is syzygy: " + str(t);
puts "is syzygy: " + str(s.isLeftSyzygy(rgl));
puts;


rgr = ii.rightGB();
puts "seq right GB: " + str(rgr);
puts "isRightGB: " + str(rgl.isRightGB());
puts;

sr = rgr.rightSyzygy();
puts "syzygy: " + str(sr);
puts;

t = rgr.isRightSyzygy(sr);
puts "is syzygy: " + str(t);
puts "is syzygy: " + str(sr.isRightSyzygy(rgr));
puts;



# module syzy

sgl = s.leftGB();
puts "seq left module GB: sgl = " + str(sgl);
puts

sml = sgl.leftSyzygy();
puts "left syzygy: sml = " + str(sml);
puts;

puts "is left syzygy: " + str(sml.isLeftSyzygy(sgl));
puts;


sgr = sr.rightGB();
puts "seq right module GB: sgl = " + str(sgr);
puts

#startLog();
smr = sgr.rightSyzygy();
puts "right syzygy: smr = " + str(smr);
puts;

puts "is right syzygy: " + str(smr.isRightSyzygy(sgr));
puts;



smr = smr.rightGB();
puts "seq right module GB: sgl = " + str(smr);
puts

smr = smr.rightSyzygy();
puts "right syzygy: smr = " + str(smr);
puts;

smr = smr.rightSyzygy();
puts "right syzygy: smr = " + str(smr);
puts;
