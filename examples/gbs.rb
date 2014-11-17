#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# GB examples

#r = Ring.new( "Z(t,x,y,z) L" );
#r = Ring.new( "Mod 11(t,x,y,z) L" );
#r = Ring.new( "Rat(t,x,y) L" );
r = PolyRing.new( QQ(), "t,x,y", PolyRing.lex );
#r = PolyRing.new( ZZ(), "t,x,y", PolyRing.lex );
puts "Ring: " + str(r);
puts;


ps = """
(
 ( t - x - 2 y ),
 ( x^4 + 4 ),
 ( y^4 + 4 )
) 
""";

# ( y^4 + 4 x y^3 - 2 y^3 - 6 x y^2 + 1 y^2 + 6 x y + 2 y - 2 x + 3 ),
# ( x^2 + 1 ),
# ( y^3 - 1 )
# ( y^2 + 2 x y + 2 y + 2 x + 1 ),
# ( y^4 + 4 x - 2  y^3 - 6 x + 1  y^2 + 6 x + 2  y - 2 x + 3 ),
# ( t - x - 2 y ), 
# ( 786361/784 y^2 + 557267/392 y + 432049/784 ),
# ( x^7 + 3 y x^4 + 27/8 x^4 + 2 y x^3 + 51/7 x^3 + 801/28 y + 1041/56 )
# ( x**2 + 1 ), 
# ( y**2 + 1 ) 


f = r.ideal( ps );
puts "Ideal: " + str(f);
puts;

startLog();

rg = f.GB();
puts "seq GB:", rg;
puts;

#p = t**16 + 272 * t**12 - 7072 * t**8 + 3207424 * t**4 + 12960000;
p = t - x - 2*y;
#p = p * (y**4 + 4);
p = p.abs();

n = f.reduction(p);
puts "p = #{p}, n = #{n} "; 
puts;

n = rg.reduction(p);
puts "p = #{p}, n = #{n} "; 
puts;


nn = rg.lift(p);
ns = nn.map { |x| x.to_s }
puts "p = #{p}, ns = #{ns} "; 
puts;

puts "N * rg == p: " + str(nn.zip(rg.list).map{ |ci,ni| ci*RingElem.new(ni) }.reduce(:+) == p)

terminate();

