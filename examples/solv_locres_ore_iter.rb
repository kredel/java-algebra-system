#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Ore extension solvable polynomial example, roots

pcz = PolyRing.new(QQ(),"x,y,z");
#is automatic: [one,x,y,z] = p.gens();

zrel = [z, y,  y * z + x ];

puts "zrel: = [" + zrel.join(", ") { |r| r.to_s } + "]";
puts;

#pz = SolvPolyRing.new(QQ(), "x,y,z", PolyRing.lex, zrel);
pz = SolvPolyRing.new(QQ(), "x,y,z", PolyRing.grad, zrel);
puts "SolvPolyRing: pz = " + str(pz);
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
puts "SolvableLocalResidueRing: pzc = " + str(pzc.toScript) + ", assoz: " + str(pzc::ring.isAssociative);
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
startLog();

rgl = ii.leftGB();
puts "seq left GB: rg1 = " + str(rgl);
puts "isLeftGB: rg1 = " + str(rgl.isLeftGB());
puts;

#terminate();
#exit(0);

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
puts "SolvableIdeal_local: iil = " + str(iil);
puts;

rgll = iil.leftGB();
puts "seq left GB: rgll = " + str(rgll);
puts "isLeftGB: rgll = " + str(rgll.isLeftGB());
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
puts "f1-f2 = " + str(f3); # + ", " + str(f3.isZERO());
puts;

iil = pt.ideal( "", [ f1, f2 ] );
puts "SolvableIdeal_local: iil = " + str(iil);
puts;

rgll = iil.leftGB();
puts "seq left GB: rgll = " + str(rgll);
puts "isLeftGB: rgll = " + str(rgll.isLeftGB());
puts;

# new

f3 = f1 * f2;
#f3 = f2 * f1;
#f3 = f2;
puts "f3    = " + str(f3)
#puts;
#f4 = f2*f2;
#puts "f4    = " + str(f4)
puts "f1    = " + str(f1)
puts "f2    = " + str(f2)

iil = pt.ideal( "", [ f2, f3 ] );
puts "SolvableIdeal_local: iil = " + str(iil);
puts;

rgll = iil.leftGB();
puts "seq left GB: rgll = " + str(rgll);
puts "isLeftGB: rgll = " + str(rgll.isLeftGB());
puts;
f5 = RingElem.new(rgll.list[0]);
puts "f5    = " + str(f5)
puts "f2-f5 = " + str(f2-f5)

#f3 = f4;
puts "f3    = " + str(f3)
puts "f2*f1 = " + str(f2*f1)
puts "f1*f2 = " + str(f1*f2)
e = f3.evaluate( -y );
puts "e     = " + str(e); 
puts;

#     ( t - z**2 ) * ( t + y )
f4c = (t**2 - z**2 * t + t * y - z**2 * y);
puts "f4c   = " + str(f4c)

t = -y;
#
# ( t**2 - ( ( z**2 - y ) ) * t - ( ( y * z**2 + 2 * x * z - y ) ) )
#
#puts "t**2     = " + str(t**2)
#puts "-2y t    = " + str(-2*y*t)
#puts "z^2*y    = " + str( z**2 * y )
f4 = (t**2 - z**2 * t + t * y - z**2 * y);
puts "f4    = " + str(f4)
puts

f4d =  ( (-y) - z**2 ) * ( (-y) + y );
puts "f4c   = " + str(f4c)
puts "f4d   = " + str(f4d)
puts;

f6 = f3 / f2;
f7 = f3 % f2;
puts "f3/f2 = " + str(f6)
puts "f3%f2 = " + str(f7)

#exit(0);

iil = pt.ideal( "", [ f2, f1*f2 ] );
puts "SolvableIdeal_local: iil = " + str(iil);
puts;

rgll = iil.leftGB();
puts "seq left GB: rgll = " + str(rgll);
puts "isLeftGB: rgll = " + str(rgll.isLeftGB());
puts;

e = f3.evaluate( z**2 );
puts "e     = " + str(e); 
#puts;

e = f3.evaluate( y-z**2 );
puts "e     = " + str(e); 
puts;

terminate();
