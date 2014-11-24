#
# $Id$
#

import sdjas;
from jas import terminate, startLog, auto_inject
#startLog()

auto_inject = False
sd = sdjas.SymbolicData()
print
#sd.list_ideals()
#print
#exit();

bb87 = sd.get_ideal('Buchberger-87')
#bb87 = sd.get_ideal('Buchberger-87.Homog')
print
print "bb87 = " + str(bb87) 
print
G = bb87.GB()
print "G = " + str(G) 
print

startLog()
#terminate()
#exit();


cz86c = sd.get_ideal('Czapor-86c')
#cz86c = sd.get_ideal('Czapor-86c.Flat')
#cz86c = sd.get_ideal('Czapor-86c.Flat.Homog')
print
print "cz86c = " + str(cz86c) 
print
G = cz86c.GB()
print "G = " + str(G) 
print

#startLog()
terminate()
exit();

ids = sd.get_ideals()

for id in ids:
    print "id = " + str(id)
    if 'Curves' in id: #.find('Curves') >= 0:
       print "'Curves' = " + str(id)
       continue
    F = sd.get_ideal(id)
    print "F = " + str(F)
    G = F.GB();
    print "G = " + str(G)
