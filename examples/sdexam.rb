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
gg = bb87.GB()
puts "gg = " + str(gg) 
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
gg = cz86c.GB()
puts "gg = " + str(gg) 
puts

#startLog()
terminate()

