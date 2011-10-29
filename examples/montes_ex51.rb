#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Montes JSC 2002, 33, 183-208, example 5.1
# integral function coefficients

r = PolyRing.new( PolyRing.new(QQ(),"a, b",PolyRing.lex), "u,z,y,x", PolyRing.lex );
puts "Ring: " + str(r);
puts;

one,a,b,u,z,y,x = r.gens();
puts "gens: " + r.gens().each{ |f| str(f) }.join(",");
puts;

f1 = a * x + 2 * y + 3 * z +     u - 6;
f2 = x + 3 * y - z +         2 * u - b;
f3 = 3 * x - a * y +     z         - 2;
f4 = 5 * x + 4 * y + 3 * z + 3 * u - 9;

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

#terminate();
#exit();

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

