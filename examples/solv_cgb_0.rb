#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# simple example for solvable comprehensive GB
# integral/rational function coefficients

rc = PolyRing.new( PolyRing.new(QQ(),"u,v",PolyRing.lex),"x,y", PolyRing.lex );
puts "commutativ Ring: " + str(rc);
puts;

rel = [y, x,  x * y + 1];
puts "relations = [ " + rel.join(", ") { |r| r.to_s } + "]";
puts;

r = SolvPolyRing.new( PolyRing.new(QQ(),"u,v",PolyRing.lex),"x,y", PolyRing.lex, rel);
puts "Ring: " + str(r);
puts;

p1 = v * x * y + x;
p2 = u * y**2 + x**2;

f = r.paramideal( "", [p1,p2] );
puts "ParamIdeal: " + str(f);
puts;

#sys.exit();
#startLog();

gs = f.CGBsystem();
puts "CGBsystem: " + str(gs);
puts;

#sys.exit();

bg = gs.isCGBsystem();
puts "isCGBsystem: " + str(bg);
puts;

#exit();
#startLog();

gs = f.CGB();
puts;
puts "CGB: " + str(gs);
puts;

#startLog();

bg = gs.isCGB();
puts "isCGB: " + str(bg);
puts;

terminate();
#sys.exit();

