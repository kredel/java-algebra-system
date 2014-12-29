#
# jruby examples for jas.
# $Id$
#

from java.lang import System

from jas import WordRing, WordPolyRing, WordIdeal
from jas import terminate, startLog
from jas import QQ, ZZ, GF, ZM

# non-commutative polynomial examples: C_4_1_7_X example

#r = WordPolyRing(QQ,"x4,x3,x2,x1");
#r = WordPolyRing(GF(19),"x4,x3,x2,x1");

#r = WordPolyRing(QQ(),"x1,x2,x3,x4");
#r = WordPolyRing(ZZ(),"x1,x2,x3,x4");
#r = WordPolyRing(GF(19),"x1,x2,x3,x4");
#r = WordPolyRing(GF(32003),"x1,x2,x3,x4");
r = WordPolyRing(GF(2**29-3),"x1,x2,x3,x4");
#r = WordPolyRing(GF(2**63-25),"x1,x2,x3,x4");
#r = WordPolyRing(GF(170141183460469231731687303715884105727),"x1,x2,x3,x4");
#r = WordPolyRing(GF(259117086013202627776246767922441530941818887553125427303974923161874019266586362086201209516800483406550695241733194177441689509238807017410377709597512042313066624082916353517952311186154862265604547691127595848775610568757931191017711408826252153849035830401185072116424747461823031471398340229288074545677907941037288235820705892351068433882986888616658650280927692080339605869308790500409503709875902119018371991620994002568935113136548829739112656797303241986517250116412703509705427773477972349821676443446668383119322540099648994051790241624056519054483690809616061625743042361721863339415852426431208737266591962061753535748892894599629195183082621860853400937932839420261866586142503251450773096274235376822938649407127700846077124211823080804139298087057504713825264571448379371125032081826126566649084251699453951887789613650248405739378594599444335231188280123660406262468609212150349937584782292237144339628858485938215738821232393687046160677362909315071),"x1,x2,x3,x4");
print "WordPolyRing: " + str(r);
print;


#one,x4,x3,x2,x1 = r.gens();
one,x1,x2,x3,x4 = r.gens();
print "one = " + str(one);
print "x4 = " + str(x4);
print "x3 = " + str(x3);
print "x2 = " + str(x2);
print "x1 = " + str(x1);
print;

f1 = x4*x4 - 25*x4*x2 - x1*x4    - 6*x1*x3  - 9*x1*x2 + x1*x1;
f2 = x4*x4 - 4*x4*x3  + 13*x4*x2 + 12*x4*x1 - 9*x3*x4 + 4*x3*x2  + 41*x3*x1 - 7*x1*x4 - x1*x2;
f3 = x4*x4 + 12*x4*x3 - 4*x3*x3  - 9*x3*x2  + 9*x1*x4 + x1*x1;
f4 = x4*x4 - 14*x4*x3 - 17*x4*x2 - 5*x2*x2  - 42*x1*x4;
f5 = x4*x4 - 2*x4*x3  + 2*x4*x2  - 7*x4*x1  - x2*x2   - 13*x2*x1 - 4*x1*x3  + 2*x1*x2 - x1*x1;
f6 = x4*x4 + 7*x4*x2  - 15*x4*x1 - 9*x3*x4  + 4*x2*x2 + 15*x2*x1 + x1*x2;

print "f1 = " + str(f1);
print "f2 = " + str(f2);
print "f3 = " + str(f3);
print "f4 = " + str(f4);
print "f5 = " + str(f5);
print "f6 = " + str(f6);
print

ff = r.ideal( "", [f1,f2,f3,f4,f5,f6] ); #]) #
print "ff = " + str(ff);
print;

startLog();

gg = ff.GB();
print "gg = " + str(gg);
print
print "isGB(gg) = " + str(gg.isGB());
print

#hh = gg.sum(gg);
#hh = hh.GB();
#print "hh = " + str(hh);
#print
#print "gg == hh: " + (gg === hh).to_s;
#print
#print "isGB(hh) = " + str(hh.isGB());
#print
