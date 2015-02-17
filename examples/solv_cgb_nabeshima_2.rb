#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# simple example for solvable comprehensive GB
# integral/rational function coefficients

rc = PolyRing.new( PolyRing.new(QQ(),"a,b",PolyRing.lex),"x,d", PolyRing.lex );
puts "commutativ Ring: " + str(rc);
puts;

rel = [d, x,  x * d + 1
      ];
puts "relations = [ " + rel.join(", ") { |r| r.to_s } + "]";
puts;

r = SolvPolyRing.new( PolyRing.new(QQ(),"a,b",PolyRing.lex),"x,d", PolyRing.lex, rel);
puts "Ring: " + str(r);
puts;

p1 = 2 * x * d**2 + a * x * d;
p2 = x * d**3 + b * x**2 * d - b * x;
p3 = x * d**2 - a * x;

f = r.paramideal( "", [p1,p2,p3] );
puts "ParamIdeal: " + str(f);
puts;

#exit();
#startLog();

gs = f.CGBsystem();
puts "CGBsystem: " + str(gs);
puts;

#exit();

bg = gs.isCGBsystem();
puts "isCGBsystem: " + str(bg);
puts;

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

