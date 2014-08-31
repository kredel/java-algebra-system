#
# jython examples for jas.
# $Id$
#

from jas import Ring
from jas import Ideal

from edu.jas.gb import Katsura;

# katsura examples

knum = 4
tnum = 2;

k = Katsura(knum);
r = Ring( k.varList("Rat","G") );
#r = Ring.new( k.varList("Mod 23","G") );
print "Ring: " + str(r);
print;

ps = k.polyList();

f = r.ideal( ps );
print "Ideal: " + str(f);
print;

rg = f.parGB(tnum);
for th in range(tnum,0,-1):
   rg = f.parGB(th);
   #print "par Output:", rg;
   #print;

rg = f.GB();
#print "seq Output:", rg;
print;

