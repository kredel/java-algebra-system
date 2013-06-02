#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Weyl coefficient field example 

r = PolyRing.new(QQ(),"p1,q1");
#is automatic: [one,p1,q1] = r.gens();

relations = [q1, p1,  p1 * q1 + 1
            ];

puts "relations: = [" + relations.join(", ") { |r| r.to_s } + "]";
puts;

rp = SolvPolyRing.new(QQ(), "p1,q1", PolyRing.lex, relations);
puts "SolvPolyRing: " + str(rp);
puts;

puts "gens =" + rp.gens().join(", ") { |r| r.to_s };
#is automatic: one,,p1,q1 = rp.gens();

scp = SRF(rp);
puts "scp = " + str(scp);


r2 = PolyRing.new(scp,"p2,q2");
#is automatic: [one,p1,q1,p2,q2] = r2.gens();

relations2 = [q2, p2,  p2 * q2 + 1
             ];

puts "relations2: = [" + relations2.join(", ") { |r| r.to_s } + "]";
puts;

rp2 = SolvPolyRing.new(scp, "p2,q2", PolyRing.lex, relations2);
puts "SolvPolyRing: " + str(rp2);
puts;

puts "gens =" + rp2.gens().join(", ") { |r| r.to_s };
#is automatic: one,q1,p1,q2,p2 = rp2.gens();


f1 = p2 + p1;
f2 = q2 - q1;
puts "f1 = " +str(f1);
puts "f2 = " +str(f2);

f3 = f1 * f2;
puts "f3 = " +str(f3);

f4 = f2 * f1;
puts "f4 = " +str(f4);

ff = [ f4 , f3 ];
puts "ff = [" + ff.join(", ") { |r| r.to_s } + "]";
puts

#exit(0);

ii = rp2.ideal( "", ff );
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


urgt = rgt.univariates();
puts "univariate polynomials: " + urgt.join(", ") { |r| r.to_s };
puts;


#h = q1;
#h = q2;
#h = p2;
#h = q2 - p2;
h = q1 * q2 + p1 * p2 - p1 + q1**2 + 1;
puts "polynomial:         " + str(h);
hi = rgt.inverse(h);
puts "inverse polynomial: " + str(hi);
puts;

hhi = h * hi;
puts "h * hi: " + str(hhi);
puts "h * hi  left-mod rgt: " + str(rgt.leftReduction(hhi));
puts "h * hi right-mod rgt: " + str(rgt.rightReduction(hhi));
puts;

