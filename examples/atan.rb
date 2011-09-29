#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# elementary integration atan examples

r = PolyRing.new(QQ(),"x",PolyRing.lex);
puts "r = " + str(r);
rf = RF(r);
puts "rf = " + str(rf.factory());
one,x = rf.gens();
puts "one   = " + str(one);
puts "x     = " + str(x);
puts;

#f = 1 / ( 1 + x**2 );

#f = 1 / ( x**2 - 2 );                                                                                             
#f = 1 / ( x**3 - 2 );                                                                                             
#f = ( x + 3 ) / ( x**2- 3 * x - 40 );                                                                             

f = ( x**7 - 24 * x**4 - 4 * x**2 + 8 * x - 8 ) / ( x**8 + 6 * x**6 + 12 * x**4 + 8 * x**2 );                     

puts "f = " + str(f);
puts;

#sys.exit();                                                                                                       

#startLog();                                                                                                       

t = System.currentTimeMillis();
e1 = r.integrate(f);
t = System.currentTimeMillis() - t;
puts "e1 = " + str(e1);
puts "integration time = " + str(t) + " milliseconds";
puts

t = System.currentTimeMillis();
e2 = f.integrate();
t = System.currentTimeMillis() - t;
puts "e2 = " + str(e2);
puts "integration time = " + str(t) + " milliseconds";
puts

#startLog();                                                                                                       
terminate();

#sys.exit();
