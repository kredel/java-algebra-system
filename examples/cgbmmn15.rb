#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# rational function coefficients
# IP (alpha,beta,gamma,epsilon,theta,eta)
# (c3,c2,c1) /G/
#r = Ring( "IntFunc(alpha,beta,gamma,epsilon,theta,eta)(c3,c2,c1) G" );
# ( { alpha } c1 - { beta } c1**2 - { gamma } c1 c2 + { epsilon } c3 ),
# ( - { gamma } c1 c2 + { epsilon + theta } c3 - { gamma } c2 ),
# ( { gamma } c2 c3 + { eta } c2 - { epsilon + theta } c3 )

r = Ring.new( "IntFunc(a,b,g,e,t,eta)(c3,c2,c1) G" );
puts "Ring: " + str(r);
puts;

ps = """
(
 ( { a } c1 - { b } c1**2 - { g } c1 c2 + { e } c3 ),
 ( - { g } c1 c2 + { e + t } c3 - { g } c2 ),
 ( { g } c2 c3 + { eta } c2 - { e + t } c3 )
)
""";

f = r.paramideal( ps );
puts "ParamIdeal: " + str(f);
puts;

#sys.exit();

startLog();

gs = f.CGBsystem();
puts "CGBsystem: " + str(gs);
puts;

#sys.exit();

#bg = gs.isCGBsystem();
#puts "isCGBsystem: " + str(bg);
#puts;

#sys.exit();

#gs = f.CGB();
#puts "CGB: " + str(gs);
#puts;

#bg = gs.isCGB();
#puts "isCGB: " + str(bg);
#puts;

terminate();
#sys.exit();

