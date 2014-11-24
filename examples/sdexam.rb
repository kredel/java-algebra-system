#
# $Id$
#

require "examples/jas"
require "examples/sdjas"

startLog()

sd = SymbolicData.new()
puts
#sd.list_ideals()
#puts
#exit();

bb87 = sd.get_ideal('Buchberger-87')
#bb87 = sd.get_ideal('Buchberger-87.Homog')
puts
puts "bb87 = " + str(bb87) 
puts
G = bb87.GB()
puts "G = " + str(G) 
puts

#startLog()
#terminate()
#exit();


cz86c = sd.get_ideal('Czapor-86c')
#cz86c = sd.get_ideal('Czapor-86c.Flat')
#cz86c = sd.get_ideal('Czapor-86c.Flat.Homog')
puts
puts "cz86c = " + str(cz86c) 
puts
G = cz86c.GB()
puts "G = " + str(G) 
puts

#startLog()
terminate()

