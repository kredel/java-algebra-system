#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Ore extension solvable polynomial example, roots

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

#fl = [ z**2 + y, y**2 + x];
#fl = [ z**2 + y, y**2 + 2];
fl = [ z**2 + y, x ];
ff = pz.ideal("",fl);
puts "ideal ff: " + str(ff);
puts;

ff = ff.twosidedGB();
puts "ideal ff: " + str(ff);
puts;


f0 = SLR(ff,z + x + y + 1);
puts "f0 = " + str(f0);

#f1 = SLR(ff, z-y+1 );
#f1 = SLR(ff, y*z+1  );
f1 = SLR(ff, y*z+x+1  );
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
puts "SolvableLocalResidueRing: " + str(pzc.toScript) + ", assoz: " + str(pzc::ring.isAssociative);
puts "gens =" + pzc.generators().join(", ") { |r| r.to_s };
puts;

pct = PolyRing.new(pzc,"t");
#is automatic: [one,y,z,t] = p.gens(); # 1/y, 1/z

#exit(0);

trelations = [t, y,  y * t + y,
              t, z,  z * t - z
             ];

puts "trelations: = [" + trelations.join(", ") { |r| r.to_s } + "]";
puts;

#startLog();

pt = SolvPolyRing.new(pzc, "t", PolyRing.lex, trelations);
puts "SolvPolyRing: " + str(pt);
puts "sp.gens(t) = " + pt.gens().join(", ") { |r| r.to_s };
#is automatic: one,y,z,t = rp.gens(); # no x?
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

ff = [ a*c, b*c, (a+b)*c ];
puts "ff = [" + ff.join(", ") { |r| r.to_s } + "]";
puts

ii = pt.ideal( "", ff );
puts "SolvableIdeal: " + str(ii);
puts;

#exit(0);
#startLog();

rgl = ii.leftGB();
puts "seq left GB: " + str(rgl);
puts "isLeftGB: " + str(rgl.isLeftGB());
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

#no: fl = [ p, p*x ]; # x non existent
#no: fl = [ p, p*z ];
#bad: fl = [ p, p*t, p*p ];
#bad: fl = [ p, p*p ];
#fl = [ p, p*t ];
#fl = [ p ];
#fl = [ t*p, (t*t+1)*p, (t*t-t)*p ];
fl = [ t*c, (t*t+1)*c, (t*t-t)*c ];
puts "fl = [" + fl.join(", ") { |r| r.to_s } + "]";
puts

iil = pt.ideal( "", fl );
puts "SolvableIdeal_local: " + str(iil);
puts;

rgll = iil.leftGB();
puts "seq left GB: " + str(rgll);
puts "isLeftGB: " + str(rgll.isLeftGB());
puts;

#exit(0);

#q = RingElem.new(rgll.list.get(0));
q = RingElem.new(rgll.list[0]);
puts "p     = " + str(p);
puts "q     = " + str(q);
puts "q-p   = " + str(q-p);
puts "c     = " + str(c);
puts "q-c   = " + str(q-c);
puts "d     = " + str(d);
puts "q-d   = " + str(q-d);
puts;

f1 = t - z**2;
puts "f1    = " + str(f1);
#fn = x; # undefined/wrong variable, since == 0
f2 = t + y;
puts "f2    = " + str(f2); 
f3 = f1 - f2;
puts "f3    = " + str(f3) + ", " + str(f3.isZERO());
puts;

iil = pt.ideal( "", [ f1, f2 ] );
puts "SolvableIdeal_local: " + str(iil);
puts;

rgll = iil.leftGB();
puts "seq left GB: " + str(rgll);
puts "isLeftGB: " + str(rgll.isLeftGB());
puts;
