#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Ore extension solvable polynomial example, modules

rp = PolyRing.new(QQ(),"x,y,z,t",PolyRing.lex);
#is automatic: [one,x,y,z,t] = rp.gens();

trel = [ z, y,  y * z + x,
         t, y,  y * t + y,
         t, z,  z * t - z
       ];

puts "trel: = [" + trel.map { |r| r.to_s }.join(", ") + "]";
puts;

rs = SolvPolyRing.new(QQ(),"x,y,z,t",PolyRing.lex,trel);

#exit(0)

f = rs.ideal("",[t**2 + z**2 + x**2 + y**2 + 1]);
puts "f: " + str(f);
tf = f.twosidedGB();
puts "t: " + str(tf);
puts;

#exit(0)

r = SolvableModule.new("",rs);
puts "SolvableModule: " + str(r);
puts;

subm = [
         [ 0, t**2 + z**2 + x**2 + y**2 + 1],
         [ x**2 + y**2,     z ]
       ];

m = SolvableSubModule.new( r, "", subm );
puts "SolvableSubModule: " + str(m);
puts;

#exit()
#startLog();

lg = m.leftGB();
puts "seq left GB: " + str(lg);
puts "is left GB: " + str(lg.isLeftGB());
puts;

tg = m.twosidedGB();
puts "seq twosided GB: " + str(tg);
puts "is twosided GB: " + str(tg.isTwosidedGB());
puts "is right GB: " + str(tg.isRightGB());
puts;

#exit()

rg = m.rightGB();
puts "seq right GB: " + str(rg);
puts "is right GB: " + str(rg.isRightGB());
puts;


# as quotients to coefficients
rq = SRF(rs);

rpq = PolyRing.new(rq,"v,w",PolyRing.lex);
puts "PolyRing: rpq = " + str(rpq);

vrel = [ v, t, t * v + x,
         w, t, t * w + y
       ];

puts "vrel: = [" + vrel.map { |r| r.to_s }.join(", ") + "]";
puts;

rsq = SolvPolyRing.new(rq,"v,w",PolyRing.lex,vrel);
puts "SolvPolyRing: rsq = " + str(rsq);
puts;


r = SolvableModule.new("",rsq);
puts "SolvableModule: " + str(r);
puts;

subm = [
         [     0, v + 1 ],
         [ w + x, w - v ]
       ];

m = SolvableSubModule.new( r, "", subm );
puts "SolvableSubModule: " + str(m);
puts;

#startLog();

lg = m.leftGB();
puts "seq left GB: " + str(lg);
puts "is left GB: " + str(lg.isLeftGB());
puts;

#exit();
#startLog();

tg = m.twosidedGB();
puts "seq twosided GB: " + str(tg);
puts "is twosided GB: " + str(tg.isTwosidedGB());
puts "is right GB: " + str(tg.isRightGB());
puts;

#exit()

rg = m.rightGB();
puts "seq right GB: " + str(rg);
puts "is right GB: " + str(rg.isRightGB());
puts;


#startLog();
terminate();
