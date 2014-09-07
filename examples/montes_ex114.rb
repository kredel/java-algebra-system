#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Montes JSC 2002, 33, 183-208, example 11.4
# integral function coefficients

R = PolyRing.new( PolyRing.new(QQ(),"Q2, P2, Q1, P1",PolyRing.lex), "f3, e3, f2, e2", PolyRing.lex );
puts "Ring: " + str(R);
puts;

#not automatic, since capital letters : 
one, Q2, P2, Q1, P1, f3, e3, f2, e2 = R.gens();
puts "gens: " + R.gens().each{ |f| str(f) }.join(",");
puts;

fp1 = 14 - 12 * e2 - 110 * f2 - 2 * e3 - 10 * f3 - P1;
fp2 = 2397 - 2200 * e2 + 240 * f2 - 200 * e3 + 40 * f3 - 20 * Q1;
fp3 = 16 * e2**2 - 4 * e2 * e3 - 20 * e2 * f3 + 20 * e3 * f2 + 16 * f2**2 - 4 * f2 * f3 - 12 * e2 + 110 * f2 - P2;
fp4 = 2599 * e2**2 - 400 * e2 * e3 + 80 * e2 * f3 - 80 * e3 * f2 + 2599 * f2**2 - 400 * f2 * f3 - 2200 * e2 - 240 * f2 - 20 * Q2;

puts "fp1: " + str(fp1);
puts "fp2: " + str(fp2);
puts "fp3: " + str(fp3);
puts "fp4: " + str(fp4);
puts;

F = [fp1,fp2,fp3,fp4];

puts "F: " + F.each{ |f| str(f) }.join(",");
puts;

startLog();

If = R.paramideal( "", list = F );
puts "ParamIdeal: " + str(If);
puts;

## G = If.GB();
## puts "GB: " + str(G);
## puts;

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
