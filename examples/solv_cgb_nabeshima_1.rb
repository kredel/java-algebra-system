#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# simple example for solvable comprehensive GB
# integral/rational function coefficients

rc = PolyRing.new( PolyRing.new(QQ(),"a,b",PolyRing.lex),"x1,x2,d1,d2", PolyRing.lex );
puts "commutativ Ring: " + str(rc);
puts;

rel = [d1, x1,  x1 * d1 + 1,
       d2, x2,  x2 * d2 + 1
      ];
puts "relations = [ " + rel.join(", ") { |r| r.to_s } + "]";
puts;

r = SolvPolyRing.new( PolyRing.new(QQ(),"a,b",PolyRing.lex),"x1,x2,d1,d2", PolyRing.lex, rel);
puts "Ring: " + str(r);
puts;

p1 = a * x1 * d1**2 * d2 + (a+1) * x1 * x2 * d2;
p2 = x2**2 * d2 + b * x1;
p3 = d1 * d2**2;

f = r.paramideal( "", [p1,p2,p3] );
puts "ParamIdeal: " + str(f);
puts;

#exit();
#startLog();

gs = f.CGBsystem();
puts "CGBsystem: " + str(gs);
puts;

#exit();

#bg = gs.isCGBsystem();
#puts "isCGBsystem: " + str(bg);
#puts;

#exit();
#startLog();

gs = f.CGB();
puts;
puts "CGB: " + str(gs);
puts;

#exit();
#startLog();

bg = gs.isCGB();
puts "isCGB: " + str(bg);
puts;

startLog();
terminate();
#sys.exit();

