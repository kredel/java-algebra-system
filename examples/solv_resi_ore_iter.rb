#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Ore extension solvable polynomial example, Gomez-Torrecillas, 2003

pcz = PolyRing.new(QQ(),"x,y,z");
#is automatic: [one,x,y,z] = p.gens();

zrelations = [z, y,  y * z + x
             ];

puts "zrelations: = [" + zrelations.join(", ") { |r| r.to_s } + "]";
puts;

pz = SolvPolyRing.new(QQ(), "x,y,z", PolyRing.lex, zrelations);
puts "SolvPolyRing: " + str(pz);
puts;

#startLog();

fl = [ z**2 + y, y**2 + x];
ff = pz.ideal("",fl);
puts "ideal ff: " + str(ff);
puts;

ff = ff.twosidedGB();
puts "ideal ff: " + str(ff);
puts;


f0 = SRC(ff,z + x + y + 1);
puts "f0 = " + str(f0);

#f1 = SRC(ff, z-y+1 );
#f1 = SRC(ff, y*z+1  );
f1 = SRC(ff, y*z+x+1  );
puts "f1 = " + str(f1);

f2 = f1*f0;
puts "f2 = " + str(f2);

fi = 1/f1;
puts "fi = " + str(fi);
fi1 = fi*f1;
f1i = f1*fi;
puts "fi*f1 = " + str(fi1);
puts "f1*fi = " + str(f1i);
puts;

#exit(0);

pzc = f0.elem.ring;
puts "SolvableResidueRing: " + str(pzc.toScript); # + ", assoz: " + str(pzc::ring.isAssociative);
puts "gens =" + pzc.generators().join(", ") { |r| r.to_s };
puts;

pct = PolyRing.new(pzc,"t");
#is automatic: [one,y,z,t] = p.gens(); # no x

#exit(0);

trelations = [t, y,  y * t + y,
              t, z,  z * t - z
             ];

puts "trelations: = [" + trelations.join(", ") { |r| r.to_s } + "]";
puts;

startLog();

pt = SolvPolyRing.new(pzc, "t", PolyRing.lex, trelations);
puts "SolvPolyRing: " + str(pt);
puts;

puts "sp.gens(t) = " + pt.gens().join(", ") { |r| r.to_s };
#is automatic: one,y,z,t = rp.gens(); # no x

#exit(0);

#yi = 1 / y; # not invertible
#puts "yi   = " + str(yi);

a = t**2 + y;
b = t + y + 1;
c = t**2 - y * t - z;
puts "a   = " + str(a);
puts "b   = " + str(b);
puts "c   = " + str(c);
puts

ff = [ a*c, b*c ];
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

p = RingElem.new(rgl.list.get(0));
puts "p     = " + str(p);
puts "c-p   = " + str(c-p);
#puts "monic(p) = " + str(p.monic());
pp = p * p;
puts "p*p   = " + str(pp);
puts "p*y*z = " + str(p*y*z);
puts "p*t   = " + str(p*t);
puts "t*p   = " + str(t*p);
puts;

#no: fl = [ p, p*x ]; # x non existent
#fl = [ p, p*y ];
#no: fl = [ p, p*z ];
#fl = [ p, p*t, p*y ];
#bad: fl = [ p, p*t, p*p ];
#bad: fl = [ p, p*p ];
#fl = [ p, p*t ];
#fl = [ p, p*(t+1) ];
fl = [ p*(t*t+1), p*(t*t*t), p*(t-3) ];
#fl = [ p ];
puts "fl = [" + fl.join(", ") { |r| r.to_s } + "]";
puts

iil = pt.ideal( "", fl );
puts "SolvableIdeal_res: " + str(iil);
puts;

iiq = iil.toQuotientCoefficients(); # beware of redefined generators
puts "SolvableIdeal_quot: " + str(iiq);
puts;

#exit(0);

rgll = iiq.leftGB();
puts "seq left GB: " + str(rgll);
puts "isLeftGB: " + str(rgll.isLeftGB());
puts;

#rgr = rgl.rightGB();
#puts "seq right GB: " + str(rgr);
#puts "isRightGB: " + str(rgr.isRightGB());
#puts;

#startLog();

rgt = iiq.twosidedGB();
puts "seq twosided GB: " + str(rgt);
puts "isTwosidedGB: " + str(rgt.isTwosidedGB());
puts;

#terminate();
