#
# $Id$
#
# adapted from the Symbolicdata, Sage and JAS Python code
#
# communicate with the SPARQL endpoint (http request)
#import requests
require "net/http" 
# easily handle URLs
#from os.path import split as pathsplit
#from urlparse import urlsplit
require "uri"
# for nice output of dictionaries: json.dumps(dict, indent = 4)
# mostly for debugging reasons (will be removed later)
require "json" 
# parse the sd.ini file
#from ConfigParser import SafeConfigParser
require "configparser"
# parse the xml of the resource files
#from xml.dom.minidom import parse, parseString
require "rexml/document"
# output lists nicely (there might be a better way)
#from textwrap import wrap as textwrap
# not needed
#require "examples/jas"

# Some internal helper functions that are not meant to be
# called by the user

=begin rdoc
    Converts a uri to a name or key by only taking everything
    after the last / or (if present) #.

    Examples:
        http://example.com/test             ->   test
        http://example.com/model#testedBy   ->   testedBy
=end
def _uri_to_name(uri)
    usplit = URI(uri) #urlsplit(uri)
    if usplit.fragment != nil
        return usplit.fragment
    else
        #return pathsplit(usplit.path)[-1]
        return usplit.path.split('/')[-1]
    end
end

=begin rdoc
    Formats a list l to be displayed in a tabular layout. It is
    possible to pass an integer width to the textwrap function.
    The width of the terminal window could be obtained via the
    Python console module. However, since it is not included
    in Jas, we decided not to use it. The default width that
    textwrap uses is set to 70. There might be a better way to
    do this.
=end
def _pprint(l)
    col = max(l.map { |x| x.length } ) + 3 #  [len(x) for x in l]) + 3
    padded = l.map{|x| x.ljust(col) }.join('')
    print textwrap(padded).join('\n')
end

=begin rdoc
    A quick convienience function to retrieve a single value
    of a given triple (object, predicate, ...)

    The parameter sd is a SymbolicData object that contains
    information about the SPARQL endpoint.
=end
def get_value_for_URI(sd, uri, predicate)
    result = nil
    query = "SELECT * WHERE { <#{uri}> <#{predicate}> ?x }"
    begin
        qj = SPARQL.new(sd, query)
        result = qj.json['results']['bindings'][0]['x']['value']
    rescue
        #pass 
    end
    return result
end


# Class definitions start here

=begin rdoc
    Access to the database of ideals as provided by the
    SymbolicData Project (http://symbolicdata.org).
=end
class SymbolicData

      attr_reader :_parser, :url, :sqpath, :sd, :sdhost

=begin rdoc
        The constructor parses the sd.ini file and sets up some variables.
        An optional parameter can be passed to select the SPARQL endpoint
        that should be used. The keywords for different SPARQL endpoints are
        defined in the sd.ini file.

        The default SPARQL endpoint is the one from symbolicdata.org
=end
    def initialize(sparql = 'symbolicdata.org')
        @_sparql = sparql
        @_ideals = nil
        #@_parser = SaveConfigParser.new(['sd.ini','examples/sd.ini'])
        @_parser = ConfigParser.new('examples/sd.ini') if File.file?('examples/sd.ini')
        @_parser = ConfigParser.new('sd.ini') if File.file?('sd.ini')
        @sd = @_parser['symbolicdata']['sd']
        begin
            @url = @_parser['sparql'][@_sparql]
        rescue
            raise ValueError("The SPARQL endpoint referenced by '#{@_sparql}' was not found in the sd.ini file." )
        end
        @sdhost = @_parser['DEFAULT']['sdhost']
        sh, sp, port = @url.partition(':') # hack for subst and GI host error
        @url = @sdhost + sp + port
        @sqpath = @_parser['sparql']['path']
        puts "SymbolicData() initialized"
        puts "url    = " + str(@url)
        #puts "sdhost = " + str(@sdhost)
    end

=begin rdoc
        Returns a Python list of ideals.
=end
    def get_ideals(force_reload = false)
        if @_ideals == nil or force_reload == true
            list_ideals(false, force_reload)
        end
        return @_ideals
    end

=begin rdoc
        Lists all the available ideals.
=end
    def list_ideals(output = true, force_reload = false)
        if @_ideals == nil or force_reload == true
            r = SPARQL.new(self, @_parser.get('queries', 'list_ideals'))
            ids = r.json['results']['bindings']
            @_ideals = ids.map{ |x| _uri_to_name(x['ideal']['value']) }
        end
        if output
            _pputs(@_ideals)
        end
    end

=begin rdoc
        Returns an ideal as a Jas object that is ready to be used by
        Jas.
=end
    def get_ideal(uri)
        return SD_Ideal.new(self, uri).get_ideal();
    end

=begin rdoc
        Returns an internal object that represents the SymbolicData
        database object. (See below for details)
=end
    def get_sd_ideal(uri)
        return SD_Ideal.new(self, uri)
    end

end


=begin rdoc
    This is a 'wrapper' class for SPARQL queries. A class might be
    a slight overkill. It was made with the idea, that one can store
    the query and the result together, to re-evaluate both without
    having to access the server. However, in the end this feature
    was not really needed.
=end
class SPARQL

    attr_reader :json

    def initialize(sd, query, output = 'json')
        @_sd = sd;
        @_query = query;
        @_data = {
            'query' => query,
            'output' => output
        }
        #self.response = requests.get(self._sd.url, params = self._data)
        #puts "url = " + str(self._sd.url)
        uri = URI("http://" + @_sd.url.to_s)
        #puts "uri      = " + uri.to_s
        #puts "uri.host = " + uri.host.to_s
        #puts "uri.port = " + uri.port.to_s
        Net::HTTP.start( uri.host, uri.port ) do |conn|
           #puts "conn = " + str(conn)

           #puts "query = " + str(query)
           #_path = @_sd.sqpath + "?" + URI.encode_www_form(@_data)
           uri.path = @_sd.sqpath
           uri.query = URI.encode_www_form(@_data)
           #puts "uri       = ", uri
           #puts "uri       = " + str(uri)
           #puts "uri.path  = " + uri.path.to_s
           #puts "uri.query = " + uri.query.to_s
           req = Net::HTTP::Get.new(uri.request_uri)
           #puts "req = " + str(req)
           response = conn.request( req );
           if not response.is_a?(Net::HTTPSuccess)
              puts "response = " + response.to_s + "\n"
              raise ValueError, "HTTP GET #{uri} not successful" 
           end

           #head = response.code.to_s + " " + response.msg
           #puts "head = " + str(head) + "\n"
           @text = response.body()
           #puts "body = " + str(@text)
           @json = JSON.load(@text)
           #puts "json = " + str(@json)
        end
    end

end


=begin rdoc
    This class represents a SymbolicData database object. The
    constructor takes a complete URI or a name SUBJ (the latter of which
    will be prefixed with the 'ideal' value from the sd.ini)

    Any triple of the form (SUBJ, PRED, OBJ) will yield a field PRED*
    for the SD_Ideal object with the value OBJ, where PRED* is the
    ending piece of PRED URI as defined by the function _uri_to_name()

    A SPARQL endpoint is needed. As a future improvement, it could be
    nice to directly parse an RDF in a convienient serialization.
=end
class SD_Ideal

    attr_reader :basis

=begin rdoc
        sd is a SymbolicData object, the name can be a complete URI or shortened
        name as defined by _uri_to_name(). The latter will be prefixed with the
        'ideal' value from the sd.ini. Namespaces like "sd:Wu-90" are not
        (yet) supported.

        Appart from retrieving the information from the SPARQL endpoint, the
        resource data (XML files) is needed as well. While the SPARQL endpoint
        can be substituted by another SPARQL endpoint, the links to the resource
        files are 'hard-coded' into the RDF data. The possibility to use a
        (possibly 'hand-filled') cache will be included in the next update.
=end
    def initialize(sd, name)
        @dict = {} # mimic python
        @_sd = sd
        # quick test, if the given name already is an uri
        if name[0,7] == 'http://'
            @uri = name
        else
            @uri = @_sd._parser["symbolicdata"]["ideal"] + name
        end

        @dict["hasXMLResource"] = false
        @dict["hasLengthsList"] = ''
        @dict["hasDegreeList"] = ''
        @dict["hasParameters"] = ''

        @basis = []

        # we set up the query to get all predicate values
        # of the URI/polynomial system/ideal
        query = "
            PREFIX sd: <#{@_sd.sd}>
            SELECT ?p ?o WHERE {
                <#{@uri}> ?p ?o
            }"
        #puts "query = " + query.to_s + "\n" 
        @_request = SPARQL.new(@_sd, query)

        if @_request.json['results']['bindings'].size == 0
            raise ValueError("No data found for <#{@uri}>.\nMaybe the name was misspelled or the SPARQL endpoint is unavailable.")
        end

        # append the keys to the @dict.
        for t in @_request.json['results']['bindings']
            uri = t['p']['value']
            obj = t['o']['value']
            @dict[_uri_to_name(uri)] = obj
        end

        # Next we need a resource file with the actual expressions that are
        # used to generate the ideal.
        #
        # There are four cases that need to be dealt with
        #     (1) the ideal is constructed direclty
        #         from an IntPS with related XML resource
        #     (2) the ideal is a flat variant of another
        #         ideal
        #     (3) the ideal is obtained by homogenizing
        #         another ideal
        #     (4) the ideal is obtained by parameterizing another
        #         ideal
        # Please note: While it might seem that only one of (2) and (4)
        # should be included, both are needed to map the actual history
        # of how these ideals were obtained.

        # case 1
        if @dict.include?( 'relatedPolynomialSystem' )
            link = get_value_for_URI(@_sd, @dict["relatedPolynomialSystem"], @_sd.sd+'relatedXMLResource')
            __addXMLResource(link)
            @dict["hasXMLResource"] = true
            #puts "relatedPolynomialSystem " + str(name)
        end

        # case 2
        if @dict.include?( 'flatten' )
            parent_name = @dict["flatten"]
            parent = SD_Ideal.new(@_sd, parent_name)
            @variablesCSV = @dict["hasVariables"]
            @variables = @variablesCSV.split(",").map{ |x| x.to_s.strip() }
            @basis = parent.basis
            #puts "flatten " + str(parent_name) + ", name = " + str(name)
        end

        # case 3
        if @dict.include?( 'homogenize' )
            parent_name = @dict["homogenize"]
            if @dict.include?( 'homogenizedWith' )
                hv = @dict["homogenizedWith"]
                parent = SD_Ideal.new(@_sd, parent_name)
                @variablesCSV = parent.variablesCSV + "," + hv
                @variables = parent.variables
                @variables.append(hv)
                @basis = parent.jas_homogenize(hv)
            #puts "homogenize " + str(parent_name) + ", name = " + str(name)
            end
        end

        # case 4
        if @dict.include?( 'parameterize' )
            parent_name = @dict["parameterize"]
            parent = SD_Ideal.new(@_sd, parent_name)
            @variablesCSV = @dict["hasVariables"]
            @variables = @variablesCSV.split(",").map{ |x| x.to_s.strip() }
            @basis = parent.basis
            #puts "parameterize " + str(parent_name) + ", name = " + str(name)
        end

        # now we got the variables, the parameters and
        # the strings/expressions for the polynomials
        __constructJasObject()
    end


=begin rdoc
        Return the ideal as a Jas objects.
=end
    def get_ideal()
        #return ideal(@sageBasis)
        return @jasRing.ideal("",@jasBasis)
    end

=begin rdoc
        Fill internal objects.
=end
    def __addXMLResource(link)
        #xml = requests.get(link).text
        #puts "link_xml = " + str(link)
        #url = link[0:23]
        path = link[23,link.length-23] # hack for lost domain
        #puts "url = " + str(url)
        #url = @_sd.url[:-5]
        url = URI("http://" + @_sd.sdhost.to_s)
        #puts "url = " + str(url)

        xml = nil
        Net::HTTP.start( url.host, url.port ) do |conn|
           #conn = httplib.HTTPConnection(url)
           #puts "conn = " + str(conn)
           #puts "path = " + str(path)
           url.path = path
           #puts "path = " + str(url.request_uri)
           req = Net::HTTP::Get.new(url.request_uri)
                 #conn.request("GET", path );
           response = conn.request( req );
           if not response.is_a?(Net::HTTPSuccess)
              puts "response = " + response.to_s + "\n"
              raise ValueError, "HTTP GET #{url} not successful" 
           end
           #puts "head = " + response.code.to_s + " " + response.msg + "\n"
           xml = response.body();
        end
        puts _uri_to_name(link).to_s + " = " + xml.to_s

        #xmlTree = parseString(xml)
        xmlTree = REXML::Document.new(xml)

        # Code snipped borrowed from Albert Heinle
        if xmlTree.elements.to_a("*/vars").empty? # Check, if vars are there
            raise ValueError("The given XMLString does not contain variables for the IntPS System")
        end
        if xmlTree.elements.to_a("*/basis").empty? # Check, if we have a basis
            raise ValueError("The given XMLString does not contain a basis for the IntPS System")
        end
        # -------------------- Input Check finished --------------------
        # From here, we can assume that the input is given correct
        @variablesCSV = xmlTree.elements.to_a("*/vars")[0].text
        #puts "@variablesCSV = " + @variablesCSV.to_s
        @variables = @variablesCSV.split(",").map{|x| x.to_s.strip() }
        #polynomials = xmlTree.elements.to_a("*/basis")[0]
        @basis = xmlTree.elements.to_a("*/basis/poly").map{ |poly| poly.text.to_s.strip() }
        #puts "@basis = " + @basis.to_s
    end

=begin rdoc
=end
    def __constructJasObject()
        #from jas import PolyRing, ZZ
        #require "jas"
        # set up the polynomial ring (Jas syntax)
        if @dict.include?('hasParameters') and @dict['hasParameters'] != ''
            #K = 'K.<%s> = PolynomialRing(ZZ)' % @hasParameters
            #R = K + '; R.<%s> = PolynomialRing(K)' % @hasVariables
            kk = 'PolyRing.new(ZZ(),"%s")' % @dict['hasParameters']
            rr = 'rr = PolyRing.new(%s,"%s")' % [ kk, @dict['hasVariables']]
            gens = 'one,%s,%s' % [@dict['hasParameters'], @dict['hasVariables']]
        else
            #R = 'R.<%s> = PolynomialRing(ZZ)' % (@hasVariables)
            rr = 'rr = PolyRing.new(ZZ(),"%s")' % @dict['hasVariables']
            gens = 'one,%s' % @dict['hasVariables']
        end
        # translate JAS syntax to pure Python and execute
        #exec(preparse(R))
        rr = rr + "; " + gens + " = rr.gens();"
        #puts "rr = " + str(rr)
        myb = binding
        eval(rr.to_s, myb)
        @jasRing = rr;
        #eval(gens + " = rr.gens();", myb)
        # construct polynomials in the constructed ring from
        # the polynomial expressions
        @jasBasis = []
        for ps in @basis
            #puts "ps = " + str(ps)
            if ps.is_a? String and ps.include?('^')
               ps = ps.sub('^', '**')
            end
            #exec(preparse("symbdata_ideal = %s" % ps))
            eval("symbdata_poly = %s" % ps, myb)
            @jasBasis.push(myb.eval("symbdata_poly"))
        end
        #puts "jasBasis = " + str(@jasBasis)
    end


    # the following functions will all use Jas to
    # calculate metadata

=begin rdoc
        This is the implementation of the predicate "sd:hasLengthsList".
        The lengths lists is the sorted list of the number of monomials of
        the generator of the ideal.

        Along with the output, there will also be generated a field
        FROM_JAS_hasLengthsList which can be used to later access the
        data without recalculating. The main reason for this is that the
        SymbolicData properties are converted into field, not getter
        functions. So to have some symmetry, the Jas calculations will
        end up in fields as well.
=end
    def jas_hasLengthsList()
        begin
            ll = @jasBasis.map{|x| x.size() }.sort()
            @FROM_JAS_hasLengthsList = ll.map{|x| x.to_s}.join(",") 
        rescue
            @FROM_JAS_hasLengthsList = ''
        end
        return @FROM_JAS_hasLengthsList
    end

=begin rdoc
        This is the implementation of the predicate "sd:hasDegreeList".
        The degree list is the sorted list of the degree of the generator
        of the ideal.

        Along with the output, there will also be generated a field
        FROM_JAS_hasDegreeList which can be used to later access the
        data without recalculating. The main reason for this is that the
        SymbolicData properties are converted into field, not getter
        functions. So to have some symmetry, the Jas calculations will
        end up in fields as well.
=end
    def jas_hasDegreeList()
        begin
            ll = @jasBasis.map{|x| x.degree() }.sort
            @FROM_JAS_hasDegreeList = ll.map{|x| x.to_s}.join(",")
        rescue
            @FROM_JAS_hasDegreeList = ''
        end
        return @FROM_JAS_hasDegreeList
    end

=begin rdoc
        This is the implementation of the predicate "sd:hasVariables". This
        is actually not needed.
=end
    def jas_hasVariables()
        #K = []
        #DL = map(lambda m : K.extend(map(lambda l : str(l), m.variables())), @sageBasis)
        kk = @jasRing.ring.vars
        return sorted(list(set(kk))).join(",")
    end

=begin rdoc
        Homogenize a basis, which here means actually nothing more than
        homogenizing every element of the basis.
=end
    def jas_homogenize(hv)
        homBasis = @jasBasis.map{ |x| x.homogenize(hv) }
        return homBasis
    end

end



to_skip = <<TOSKIP

TOSKIP
