#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Ore extension solvable polynomial example, root

pcz = PolyRing.new(QQ(),"x,y,z,t");
#is automatic: [one,x,y,z,t] = p.gens();

zrel = [z, y,  y * z + x,
        t, y,  y * t + y,
        t, z,  z * t - z
       ];

puts "zrel: = [" + zrel.map { |r| r.to_s }.join(", ") + "]";
puts;

pz = SolvPolyRing.new(QQ(), "x,y,z,t", PolyRing.lex, zrel);
puts "SolvPolyRing: pz = " + str(pz);
puts;

#startLog();

fl = [ t**2 + z**2 + y**2 + x**2 + 1 ];
ff = pz.ideal("",fl);
puts "ideal ff: " + str(ff);
puts;

ff = ff.twosidedGB();
puts "ideal ff: " + str(ff);
puts;

#exit(0);

f0 = SLR(ff, t + x + y + 1);
puts "f0 = " + str(f0);

f1 = SLR(ff, z**2+x+1  );
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

f3 = f0*fi;
puts "f0*fi = " + str(f3);
#exit(0);

pzc = f0.ring;
puts "SolvableLocalResidueRing: pzc = " + str(pzc.toScript) + ", assoz: " + str(pzc::ring.isAssociative);
puts "gens = " + pzc.generators().map { |r| r.toScript.to_s } .join(", ");
puts;

pct = PolyRing.new(pzc,"r");
#is automatic: [one,y,z,t,r] = p.gens(); # 1/y, 1/z
puts "cp.gens(t) = " + pct.gens().map { |r| r.to_s }.join(", ");
puts

#exit(0);

#trel = [r, y,  y * r + y,
#        r, z,  z * r - z
#       ];
#puts "trel: = [" + trel.map { |r| r.to_s }.join(", ") + "]";
#puts;

#startLog();

pt = SolvPolyRing.new(pzc, "r", PolyRing.lex); # trel
puts "SolvPolyRing: pt = " + str(pt);
puts "sp.gens(t) = " + pt.gens().map { |r| r.to_s }.join(", ");
#is automatic: one,t,r = rp.gens(); # no x, y, z
puts "pt.isAssociative: " + str(pt.ring.isAssociative());
puts;

#exit(0);

# new
#fr = r**2 + 1;
#fr = r**3 - t;
fr = r**3 + t;
puts "fr = " + str(fr); 
puts
iil = pt.ideal( "", [ fr ] );
puts "SolvableIdeal_local: iil = " + str(iil);
puts;

rgll = iil.twosidedGB();
puts "seq twosided GB: rgll = " + str(rgll);
puts "isTwosidedGB: rgll = " + str(rgll.isTwosidedGB());
puts;

#exit(0)

e = fr.evaluate( t );
puts "e = " + str(e); 
#puts;


fp = (r-t);
puts "fp = " + str(fp); 
puts;

#frp = fp*(r+t)**2;
frp = fr/fp;
puts "frp = " + str(frp); 
puts "frp*fp = " + str(frp*fp); 
puts "frp*fp-fr = " + str(frp*fp-fr); 
puts "frp*fp == fr: " + str((frp*fp)==fr); 
puts;

puts "frp * fp = " + str(frp*fp); 
puts "fr / fp = " + str(fr/fp); 
puts "fr % fp = " + str(fr%fp); 
puts


#exit(0)

#startLog();
# not needed:

ft = SLR(rgll, t);
puts "ft = " + str(ft);
#puts;
puts "ft**2 + 1 = " + str(ft**2+1);
puts "ft**3 + t = " + str(ft**3+ft);
puts;

rf = SLR(rgll, r);
puts "rf = " + str(rf);
#puts;
#puts "rf**2 + 1 = " + str(rf**2+1);
pr = rf**3+ft;
puts "rf**3 + ft = " + str(pr);
puts;

#puts "(rf-ft)*(rf+ft)**2 = " + str((rf-ft)*(rf+ft)**2);
#puts;

puts "(r**3+t)/(r-t) = " + str(pr/(rf-ft));
puts;


puts "SolvableLocalResidueRing: " + str(rf.ring.toScript) + ", assoz: " + str(rf.ring::ring.isAssociative);
puts "gens = " + rf.ring.generators().map { |r| r.toScript.to_s }.join(", ");
puts;

terminate();
