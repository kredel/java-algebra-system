#
# test functionality of the jython interface to jas.
# $Id$
#

# test ideal Groebner bases 
execfile("examples/trinksTest.py")

# test module Groebner bases 
execfile("examples/armbruster.py")

# test solvable Groebner bases 
execfile("examples/wa_32.py")

# test solvable module Groebner bases 
execfile("examples/solvablemodule.py")


import sys;
sys.exit();
