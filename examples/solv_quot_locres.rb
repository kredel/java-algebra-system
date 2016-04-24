#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Ore extension solvable polynomial example

pcz = PolyRing.new(QQ(),"x,y,z");
#is automatic: [one,x,y,z] = p.gens();

zrel = [z, y,  y * z + x ];

puts "zrel: = [" + zrel.join(", ") { |r| r.to_s } + "]";
puts;

pz = SolvPolyRing.new(QQ(), "x,y,z", PolyRing.lex, zrel);
puts "SolvPolyRing: pz = " + str(pz);
puts;

#startLog();


f0 = SRF(pz,z + x + y + 1);
puts "f0 = " + str(f0);

f1 = SRF(pz, y*z+x+1  );
puts "f1 = " + str(f1);

f2 = f1*f0;
puts "f2 = f1*f0: " + str(f2);
puts;

fi = 1/f1;
puts "fi = " + str(fi);
fi1 = fi*f1;
f1i = f1*fi;
puts "fi*f1 = " + str(fi1);
puts "f1*fi = " + str(f1i);
puts;

f2i = f2*fi;
fi2 = fi*f2;
puts "f2*fi = " + str(f2i);
puts "fi*f2 = " + str(fi2);
puts "f2*fi == f0: " + str(f2i == f0);
puts "fi*f2 == f0: " + str(fi2 == f0);
puts;

#exit(0);

pzc = f0.ring;
puts "SolvableQuotientRing: pzc = " + str(pzc.toScript) + ", assoz: " + str(pzc::ring.isAssociative);
puts "gens =" + pzc.generators().join(", ") { |r| r.to_s };
puts;

pct = PolyRing.new(pzc,"t");
#is automatic: [one,y,z,t] = p.gens(); # 1/y, 1/z
puts "cp.gens(t) = " + pct.gens().join(", ") { |r| r.to_s };
puts

#exit(0);

trel = [t, y,  y * t + y,
        t, z,  z * t - z
       ];

puts "trel: = [" + trel.join(", ") { |r| r.to_s } + "]";
puts;

#startLog();

pt = SolvPolyRing.new(pzc, "t", PolyRing.lex, trel);
puts "SolvPolyRing: pt = " + str(pt);
puts "sp.gens(t) = " + pt.gens().join(", ") { |r| r.to_s };
#is automatic: one,y,z,t = rp.gens(); # no x?
puts "pt.isAssociative: " + str(pt.ring.isAssociative());
puts;

#exit(0);

a = t**2 + y;
b = t + y + 1;
c = z*t**2 - y * t - z;
puts "a   = " + str(a);
puts "b   = " + str(b);
puts "c   = " + str(c);
#c = c.monic();
#puts "c   = " + str(c);
puts

ff1 = [ a*c, b*c, (a+b)*c ];
puts "ff1 = [" + ff1.join(", ") { |r| r.to_s } + "]";
puts

ii = pt.ideal( "", ff1 );
puts "SolvableIdeal: ii = " + str(ii);
puts;

#exit(0);
#startLog();

rgl = ii.leftGB();
puts "seq left GB: rg1 = " + str(rgl);
puts "isLeftGB: rg1 = " + str(rgl.isLeftGB());
puts;

#p = RingElem.new(rgl.list.get(0));
p = RingElem.new(rgl.list[0]);
puts "p     = " + str(p);
puts "c     = " + str(c);
puts "c-p   = " + str(c-p);
d = c.monic();
puts "d     = " + str(d);
puts "d-p   = " + str(d-p);
puts;

#exit(0);


# new

f3 = t**2 + x**2 + y**2 + z**2 + 1;
puts "f3    = " + str(f3)

iil = pt.ideal( "", [ f3 ] );
puts "SolvableIdeal_local: iil = " + str(iil);
puts;

rgll = iil.twosidedGB();
puts "seq twosided GB: rgll = " + str(rgll);
puts "isTwosidedGB: rgll = " + str(rgll.isTwosidedGB());
puts;
f5 = RingElem.new(rgll.list[0]);
puts "f5    = " + str(f5)
#puts "f3-f5 = " + str(f3-f5)

terminate();
