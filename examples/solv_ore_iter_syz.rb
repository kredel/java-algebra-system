#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Ore extension solvable polynomial example, Gomez-Torrecillas, 2003


pcz = PolyRing.new(QQ(),"x,y,z,t", PolyRing.lex);
#is automatic: [one,x,y,z,t] = p.gens();

zrelations = [z, y,  y * z + x,
              t, y,  y * t + y,
              t, z,  z * t - z
             ];

puts "zrelations: = [" + zrelations.join(", ") { |r| r.to_s } + "]";
puts;

pz = SolvPolyRing.new(QQ(), "x,y,z,t", PolyRing.lex, zrelations);
puts "SolvPolyRing: " + str(pz);
puts;

pzq = SRF(pz);
puts "SolvableQuotientRing: " + str(pzq::ring.toScript); # + ", assoz: " + str(pzq::ring.isAssociative);
#puts "gens =" + pzq::ring.generators().join(", ") { |r| r.to_s };
puts;

pct = PolyRing.new(pzq,"u,v,w", PolyRing.lex);
#is automatic: [one,x,y,z,t,u,v,w] = p.gens();
puts "tgens = " + pct.gens().join(", ") { |r| r.to_s };
puts;

relations = [#w, v,  v * w - u,
             v, u,  v * u + x,
             w, y,  y * w + y,
             w, z,  z * w - z
            ];

puts "relations: = [" + relations.join(", ") { |r| r.to_s } + "]";
puts;

#startLog();

pt = SolvPolyRing.new(pzq, "u,v,w", PolyRing.lex, relations);
puts "SolvPolyRing: " + str(pt) + ", is assoz: " + str(pt.ring.isAssociative);
puts;

puts "gens = " + pt.gens().join(", ") { |r| r.to_s };
#is automatic: one,x,y,z,r,s,t = rp.gens();

#exit(0);

#f1 = u**2 + v**2 + w**2;
#f1 = v;
f1 = w**2 - t;
#puts "f1 = " + str(f1);

#f2 = t + u - x**2;
#f2 = f1 * (t - x**2);
#f2 = v**2 - v;
#f2 = v**2 - y;
#f2 = v**2 - z;
f2 = v**2 - t;
#puts "f2 = " + str(f2);

#f3 = (v - u)*f1;
#f3 = f1*(v - u);
f3 = v*f1*u;
#puts "f3 = " + str(f3);

#ff = [ f3, f3 ];
ff = [ f1, f2, f3 ];
#ff = [ f1 ];
#ff = [ u ]; # isTwoSided ideal if uv = vu
#ff = [ v ]; # isTwoSided ideal if uv = vu
#ff = [ w ]; # no twoSided ideal
puts "ff = [" + ff.join(", ") { |r| r.to_s } + "]";
puts

ii = pt.ideal( "", ff );
puts "SolvableIdeal: " + str(ii);
puts;

#syl = ii.leftSyzygy().leftGB();
#puts "left syzygy: " + str(syl);
#puts;

#syr = ii.rightSyzygy().rightGB();
#puts "right syzygy: " + str(syr);
#puts;


#exit(0);
#startLog();

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

#exit(0);
#startLog();

#rgi = rgl.intersect(rgt);
#puts "leftGB intersect twosidedGB: " + str(rgi);
#puts;
#rgi = rgl.intersect(rgr);
#puts "leftGB intersect rightGB: " + str(rgi);
#puts;

#exit(0);

#syl = rgl.leftSyzygy().leftGB();
syl = ii.leftSyzygy().leftGB();
puts "left syzygy: " + str(syl);
puts;

#syr = rgr.rightSyzygy().rightGB();
syr = ii.rightSyzygy().rightGB();
puts "right syzygy: " + str(syr);
puts;

terminate();
