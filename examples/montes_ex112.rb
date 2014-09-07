#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Montes JSC 2002, 33, 183-208, example 11.2
# integral function coefficients

r = PolyRing.new( PolyRing.new(QQ(),"f, e, d, c, b, a",PolyRing.lex), "y,x", PolyRing.lex );
#r = PolyRing.new( PolyRing.new(QQ(),"f, e, d, c, b, a",PolyRing.grad), "y,x", PolyRing.lex );
#r = PolyRing.new( PolyRing.new(QQ(),"f, e, d, c, b, a",PolyRing.lex), "y,x", PolyRing.grad );
#r = PolyRing.new( PolyRing.new(QQ(),"e, d, c, b, f, a",PolyRing.lex), "y,x", PolyRing.grad );
puts "Ring: " + str(r);
puts;

#one,e,d,c,b,f,a,y,x = r.gens();
#automatic: one,f,e,d,c,b,a,y,x = r.gens();
puts "gens: " + r.gens().each{ |f| str(f) }.join(",");
puts;

f1 = x**2 + b * y**2 + 2 * c * x * y + 2 * d * x + 2 * e * y + f; 
f2 = x + c * y + d;
f3 = b * y + c * x + e;

F = [f1,f2,f3];

puts "F: " + F.each{ |f| str(f) }.join(",");
puts;

#startLog();

If = r.paramideal( "", list = F );
puts "ParamIdeal: " + str(If);
puts;

G = If.GB();
puts "GB: " + str(G);
puts;
## sys.exit();

startLog();

GS = If.CGBsystem();
#GS = If.CGBsystem();
#GS = If.CGBsystem();
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

