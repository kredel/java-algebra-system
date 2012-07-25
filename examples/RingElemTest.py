#
# jython unit tests for jas.
# $Id$
#

from java.lang import System
from java.lang import Integer

from jas import PolyRing, ZZ, QQ, ZM, GF
from jas import terminate
from jas import startLog

import unittest

# some unit tests: 

class RingElemTest (unittest.TestCase):

    def testRingZZ(self):
        r = PolyRing( ZZ(), "(t,x)", PolyRing.lex );
        self.assertEqual(str(r),'PolyRing(ZZ(),"t,x",PolyRing.lex)');
        [one,x,t] = r.gens();
        self.assertTrue(one.isONE());
        self.assertTrue(len(x)==1);
        self.assertTrue(len(t)==1);
        #
        f = 11 * x**4 - 13 * t * x**2 - 11 * x**2 + 2 * t**2 + 11 * t;
        f = f**2 + f + 3;
        #print "f = " + str(f);
        self.assertEqual(str(f),'( 4 * x**4 - 52 * t**2 * x**3 + 44 * x**3 + 213 * t**4 * x**2 - 330 * t**2 * x**2 + 123 * x**2 - 286 * t**6 * x + 528 * t**4 * x - 255 * t**2 * x + 11 * x + 121 * t**8 - 242 * t**6 + 132 * t**4 - 11 * t**2 + 3 )');
        #end

    def testRingQQ(self):
        r = PolyRing( QQ(), "(t,x)", PolyRing.lex );
        self.assertEqual(str(r),'PolyRing(QQ(),"t,x",PolyRing.lex)');
        [one,x,t] = r.gens();
        self.assertTrue(one.isONE());
        self.assertTrue(len(x)==1);
        self.assertTrue(len(t)==1);
        #
        f = 11 * x**4 - 13 * t * x**2 - 11 * x**2 + 2 * t**2 + 11 * t;
        f = f**2 + f + 3;
        #print "f = " + str(f);
        self.assertEqual(str(f),'( 4 * x**4 - 52 * t**2 * x**3 + 44 * x**3 + 213 * t**4 * x**2 - 330 * t**2 * x**2 + 123 * x**2 - 286 * t**6 * x + 528 * t**4 * x - 255 * t**2 * x + 11 * x + 121 * t**8 - 242 * t**6 + 132 * t**4 - 11 * t**2 + 3 )');
        #end

    def testRingZM(self):
        r = PolyRing( GF(17), "(t,x)", PolyRing.lex );
        self.assertEqual(str(r),'PolyRing(GF(17),"t,x",PolyRing.lex)');
        [one,x,t] = r.gens();
        self.assertTrue(one.isONE());
        self.assertTrue(len(x)==1);
        self.assertTrue(len(t)==1);
        #
        f = 11 * x**4 - 13 * t * x**2 - 11 * x**2 + 2 * t**2 + 11 * t;
        f = f**2 + f + 3;
        #print "f = " + str(f);
        self.assertEqual(str(f),'( 4 * x**4 + 16 * t**2 * x**3 + 10 * x**3 + 9 * t**4 * x**2 + 10 * t**2 * x**2 + 4 * x**2 + 3 * t**6 * x + t**4 * x + 11 * x + 2 * t**8 + 13 * t**6 + 13 * t**4 + 6 * t**2 + 3 )');
        #end

if __name__ == '__main__':
    #print str(__name__) + ": " + str(sys.modules[__name__])
    unittest.main()
