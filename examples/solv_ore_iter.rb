#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Ore extension solvable polynomial example, Gomez-Torrecillas, 2003

##p = PolyRing.new(QQ(),"x,y");
#is automatic: [one,x,y] = p.gens();

pcz = PolyRing.new(QQ(),"x,y,z");
#is automatic: [one,x,y,z] = p.gens();

zrelations = [z, y,  y * z + x
             ];

puts "zrelations: = [" + zrelations.join(", ") { |r| r.to_s } + "]";
puts;

pz = SolvPolyRing.new(QQ(), "x,y,z", PolyRing.lex, zrelations);
puts "SolvPolyRing: " + str(pz);
puts;

pzq = SRF(pz);
puts "SolvableQuotientRing: " + str(pzq::ring.toScript); # + ", assoz: " + str(pzq::ring.isAssociative);
#puts "gens =" + pzq::ring.generators().join(", ") { |r| r.to_s };
puts;

pct = PolyRing.new(pzq,"t");
#is automatic: [one,x,y,z,t] = p.gens();

trelations = [t, y,  y * t + y,
              t, z,  z * t - z
             ];

puts "trelations: = [" + trelations.join(", ") { |r| r.to_s } + "]";
puts;

#startLog();

pt = SolvPolyRing.new(pzq, "t", PolyRing.lex, trelations);
puts "SolvPolyRing: " + str(pt);
puts;

puts "gens =" + pt.gens().join(", ") { |r| r.to_s };
#is automatic: one,x,y,z,t = rp.gens();

#puts "t  = " + str(t);
#puts "z  = " + str(z);
zi = 1 / z;
yi = 1 / y;
xi = 1 / x;

#startLog();

a = ( t * zi ) * y;
b = t * ( zi * y );
c = a - b;
puts "t * 1/z * y: ";
puts "a   = " +str(a);
puts "b   = " +str(b);
puts "a-b = " +str(c);
puts
#bm = b.monic
#puts "monic(b) = " +str(bm);
#puts

#exit(0);

f1 = x**2 + y**2 + z**2 + t**2 + 1;
puts "f1 = " + str(f1);

f2 = t * x * f1;
puts "f2 = " + str(f2);

ff = [ f1, f2 ];
puts "ff = [" + ff.join(", ") { |r| r.to_s } + "]";
puts

ii = pt.ideal( "", ff );
puts "SolvableIdeal: " + str(ii);
puts;

#exit(0);

rgl = ii.leftGB();
puts "seq left GB: " + str(rgl);
puts "isLeftGB: " + str(rgl.isLeftGB());
puts;

#rgr = ii.rightGB();
#puts "seq right GB: " + str(rgr);
#puts "isRightGB: " + str(rgr.isRightGB());
#puts;

#startLog();

rgt = ii.twosidedGB();
puts "seq twosided GB: " + str(rgt);
puts "isTwosidedGB: " + str(rgt.isTwosidedGB());
puts;

#exit(0);
#startLog();

rgi = rgl.intersect(rgt);
puts "leftGB intersect twosidedGB: " + str(rgi);
puts;

#terminate();
