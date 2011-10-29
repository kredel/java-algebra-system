#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Montes JSC 2002, 33, 183-208, example 11.1
# integral function coefficients

r = PolyRing.new( PolyRing.new(QQ(),"c, b, a",PolyRing.lex), "z,y,x", PolyRing.lex );
puts "Ring: " + str(r);
puts;

one,c,b,a,z,y,x = r.gens();
puts "gens: " + r.gens().each{ |f| str(f) }.join(",");
puts;

f1 =     x + c * y + b * z + a;
f2 = c * x +     y + a * z + b;
f3 = b * x + a * y +     z + c;

F = [f1,f2,f3];

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

