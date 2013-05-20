#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Lie field example, Apel + Lassner, 1987

r = PolyRing.new(QQ(),"q,p");
#is automatic: [one,p,q] = p.gens();

relations = [p, q,  q * p + 1
            ];

puts "relations: = [" + relations.join(", ") { |r| r.to_s } + "]";
puts;

rp = SolvPolyRing.new(QQ(), "q,p", PolyRing.lex, relations);
puts "SolvPolyRing: " + str(rp);
puts;

puts "gens =" + rp.gens().join(", ") { |r| r.to_s };
#is automatic: one,x,y,z,t = rp.gens();

f1 = p * ( q * p - 1 );
f2 = q * p * p;
f3 = ( q * p - 1 ) * q;
f4 = q * q * p;
f5 = f2 - f1;
f6 = f3 - f4;
puts "f1 = " +str(f1);
puts "f2 = " +str(f2);
puts "f3 = " +str(f3);
puts "f4 = " +str(f4);
puts "f5 = " +str(f5);
puts "f6 = " +str(f6);
puts

ff = [ f1, f2, f3, f4 ];
#ff = [ f1 ];
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



scp = SRF(rp, p);
puts "SolvableQuotient: " + str(scp);
puts "SolvableQuotient: " + str(scp / scp);
puts;

scq = SRF(rp, q);
scqi = 1 / scq;
#scqi = SRF(rp,one) / scq;
puts "SolvableQuotient: " + str(scq);
puts "SolvableQuotient: " + str(scqi);
puts "SolvableQuotient: " + str(scqi * scq);
puts "SolvableQuotient: " + str(scq * scqi);
puts;

sca = SRF(rp, p**2, q*p+2);
scb = SRF(rp, q*p-1, q**2);

puts "SolvableQuotient: " + str(sca);
puts "SolvableQuotient: " + str(scb);
puts "SolvableQuotient: " + str(sca == scb);
puts "SolvableQuotient: " + str(sca - scb);
puts;
