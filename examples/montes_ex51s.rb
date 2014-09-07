#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Montes JSC 2002, 33, 183-208, example 5.1, simplified
# integral function coefficients

r = PolyRing.new( PolyRing.new(QQ(),"a, b",PolyRing.lex), "u,z,y,x", PolyRing.lex );
puts "Ring: " + str(r);
puts;

#automatic: one,a,b,u,z,y,x = r.gens();
puts "gens: " + r.gens().each{ |f| str(f) }.join(",");
puts;

f1 = 756 * x - 39 * a * b - 4 * b - 155 - 117 * a + ( 117 * a + 51 ) * u;
f2 = 189 * y + 6 * a * b - 107 - 43 * b + 18 * a - ( 18 * a - 123 ) * u;
f3 = 756 * z - 1439 + 236 * b + 99 * a + 33 * a * b - ( 99 * a - 15 ) * u;
f4 = ( 9 * a**2 - 30 * a + 21 ) * u - 9 * a**2 - 3 * a**2 * b + 11 * a * b + 22 * a - 49 + 28 * b;

F = [f1,f2,f3,f4];

puts "F: " + F.each{ |f| str(f) }.join(",");
puts;

#startLog();

If = r.paramideal( "", list = F );
puts "ParamIdeal: " + str(If);
puts;

## G = If.GB();
## puts "GB: " + str(G);
## puts;
## sys.exit();

GS = If.CGBsystem();
GS = If.CGBsystem();
GS = If.CGBsystem();
puts "CGBsystem: " + str(GS);
puts;

bg = GS.isCGBsystem();
if bg
    puts "isCGBsystem: true";
else
    puts "isCGBsystem: false";
end
puts;

terminate();
exit();

CG = If.CGB();
puts "CGB: " + str(CG);
puts;

bg = CG.isCGB();
if bg
    puts "isCGB: true";
else
    puts "isCGB: false";
end
puts;

terminate();
#------------------------------------------
#exit();

