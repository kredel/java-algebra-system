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

f1 = x**2 + y**2 + z**2 + t**2 + 1;

puts "f1 = " +str(f1);

ff = [ f1 ];
puts "ff = [" + ff.join(", ") { |r| r.to_s } + "]";
puts

ii = rp.ideal( "", ff );
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


#startLog();

#rgi = rgl.intersect(rgt);
#puts "leftGB intersect twosidedGB: " + str(rgi);
#puts;

#startLog();

#rgtu = rgt.univariates();
#puts "univariate polynomials for twosidedGB: " + rgtu.join(", ");
#puts;

startLog();

sr = SRC(rgt,one);
puts "SolvableResidue: " + str(sr);
puts "SolvableResidue: " + str(sr-sr);
puts;

st = SRC(rgt,t-x);
puts "SolvableResidue: " + str(st);
puts "SolvableResidue: " + str(st-st);
puts "SolvableResidue: " + str(st**4+3*st);
puts;


sc = SRF(rp,one);
puts "SolvableQuotient: " + str(sc);
puts "SolvableQuotient: " + str(sc-sc);
puts;

scx = SRF(rp,x);
puts "SolvableQuotient: " + str(scx);
puts "SolvableQuotient: " + str(scx*scx);
puts;

scy = SRF(rp,y);
scyi = 1 / scy;
puts "SolvableQuotient: " + str(scy);
puts "SolvableQuotient: " + str(scyi);
puts "SolvableQuotient: " + str(scyi*scy);
puts "SolvableQuotient: " + str(scy*scyi);
puts;

sca = SRF(rp,x*y + t*z*x);
scai = 1 / sca;
puts "SolvableQuotient: " + str(sca);
puts "SolvableQuotient: " + str(scai);
puts "SolvableQuotient: " + str(scai*sca);
puts "SolvableQuotient: " + str(sca*scai);
puts;

scb = SRF(rp, z*(x-y) - t*z );
scbi = 1 / scb;
puts "SolvableQuotient: " + str(scb);
puts "SolvableQuotient: " + str(scbi);
puts "SolvableQuotient: " + str(scbi*scb);
puts "SolvableQuotient: " + str(scb*scbi);
puts;

scc = sca*scb;
scci = 1 / scc;
puts "SolvableQuotient: " + str(scc);
puts "SolvableQuotient: " + str(scci);
puts "SolvableQuotient: " + str(scci*scb);
puts "SolvableQuotient: " + str(scci*sca*scb);
puts "SolvableQuotient: " + str(sca*scb*scci);
#puts "SolvableQuotient: " + str(sca*scci*scb); # expensive
puts;

scd = scci + scb + sca;
puts "SolvableQuotient: " + str(scd);
puts "SolvableQuotient: " + str(scd-(scci+scb) == sca);
puts "SolvableQuotient: " + str(sca == scd-(scci+scb));
puts;

sce = scai + scbi;
puts "SolvableQuotient: " + str(sce);

#terminate();
#exit(0);

