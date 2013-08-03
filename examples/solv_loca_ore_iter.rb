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

#fl = [ z**2 + y, y**2 + x];
#fl = [ z**2 + y, y**2 + 2];
fl = [ z**2 + y, x ];
ff = pz.ideal("",fl);
puts "ideal ff: " + str(ff);
puts;

ff = ff.twosidedGB();
puts "ideal ff: " + str(ff);
puts;


f0 = SLC(ff,z + x + y + 1);
puts "f0 = " + str(f0);

#f1 = SLC(ff, z-y+1 );
#f1 = SLC(ff, y*z+1  );
f1 = SLC(ff, y*z+x+1  );
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
puts "f2*fi != f0: " + str(f2i != f0);
puts "fi*f2 == f0: " + str(fi2 == f0);
puts;

#exit(0);

pzc = f0.ring;
puts "SolvableLocalRing: " + str(pzc.toScript) + ", assoz: " + str(pzc::ring.isAssociative);
puts "gens =" + pzc.generators().join(", ") { |r| r.to_s };
puts;

pct = PolyRing.new(pzc,"t,u");
#is automatic: [one,y,z,t] = p.gens(); # 1/y, 1/z

#exit(0);

trelations = [t, y,  y * t + y,
              t, z,  z * t - z
             ];

puts "trelations: = [" + trelations.join(", ") { |r| r.to_s } + "]";
puts;

#startLog();

pt = SolvPolyRing.new(pzc, "t,u", PolyRing.lex, trelations);
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

#ff = [ a*c, b*c, (a+b)*c ];
ff = [ a*c, b*c, (a+b)*c ];
puts "ff = [" + ff.join(", ") { |r| r.to_s } + "]";
puts

ii = pt.ideal( "", ff );
puts "SolvableIdeal: " + str(ii);
puts;

#exit(0);
startLog();

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
#puts "monic(p) = " + str(p.monic());
#pp = p * p;
#puts "p*p   = " + str(pp);
#puts "p*y*z = " + str(p*y*z);
#puts "p*t   = " + str(p*t);
#puts "t*p   = " + str(t*p);
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

#exit(0);

# back to p:
#flu = [ p, 1-sigma^{-deg}(lc(p))*u ];
#flu = [ p, u-1 ];
#flu = [ c, c.lc() * u-1 ];
flu = [ c, u-1 ];
puts "flu = [" + flu.join(", ") { |r| r.to_s } + "]";
puts

lu = pt.ideal( "", flu );
puts "SolvableIdeal_local: " + str(lu);
puts;

llu = lu.leftGB();
puts "seq left GB: " + str(llu);
puts "isLeftGB: " + str(llu.isLeftGB());
puts;

s = RingElem.new(llu.list[0]);
puts "s     = " + str(s);
puts "s*z   = " + str(s*z);
puts "z*s   = " + str(z*s);
#puts "z^-1*z*s   = " + str(1/z*z*s);
#puts "z*s*z^-1   = " + str(z*s*1/z);
puts;

