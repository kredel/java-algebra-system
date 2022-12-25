#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Bueso, Torrecillas, Lobillo, Castro: example 4.4.9, SAC Newsletter 1996

r = PolyRing.new(QQ(),"q");
#is automatic: one,q = r.gens();

rq = RF(r);

puts "r  = " + str(r);
puts "rq = " + str(rq);

#                     f < k < l < e
rp = PolyRing.new(rq,"f,k,l,e", PolyRing.lex);
puts "rp = " + str(rp);

relations = [ e, k,  one/(q**2) * k * e,
              e, l,  q**2 * l * e,
              k, f,  one/(q**2) * f * k,
              l, f,  q**2 * f * l,
              e, f,  f * e + (k**2 - l**2)/(q**2 - one/(q**2))
            ];
#commutative  l, k, k * l

puts "relations = [" + relations.join(", ") { |r| r.to_s } + "]";


rs = SolvPolyRing.new(rq, "f,k,l,e", PolyRing.lex, relations);
puts "rs: " + str(rs);

puts "gens = " + rs.gens().join(", ") { |r| r.to_s };


#p = (k * l)**2 - k * l - 1;
p = k * l - 1;
pl = [p];

ts = rs.ideal("", pl);
puts "ts = " + str(ts);

tsg = ts.twosidedGB();
puts "twosided GB: " + str(tsg);
puts "isTwosidedGB: " + str(tsg.isTwosidedGB());

d = tsg.dimension();
puts "d = " + str(d);

puts;
terminate();

#exit;
