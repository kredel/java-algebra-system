#
# $Id$
#
# adapted from the Symbolicdata and Sage code
#
# communicate with the SPARQL endpoint (http request)
#import requests
import httplib, urllib
# easily handle URLs
from os.path import split as pathsplit
from urlparse import urlsplit
# for nice output of dictionaries: json.dumps(dict, indent = 4)
# mostly for debugging reasons (will be removed later)
# for old jython versions it must be loaded from an 
# external library jyson-1.0.2.jar
#import json 
from com.xhaus.jyson import JysonCodec as json
# parse the sd.ini file
from ConfigParser import SafeConfigParser
# parse the xml of the resource files
from xml.dom.minidom import parse, parseString
# output lists nicely (there might be a better way)
from textwrap import wrap as textwrap
# not needed
#from jas import *

# Some internal helper functions that are not meant to be
# called by the user
def _uri_to_name(uri):
    """
    Converts a uri to a name or key by only taking everything
    after the last / or (if present) #.

    Examples:
        http://example.com/test             ->   test
        http://example.com/model#testedBy   ->   testedBy
    """
    usplit = urlsplit(uri)
    if usplit.fragment != '':
        return usplit.fragment
    else:
        return pathsplit(usplit.path)[-1]

def _pprint(l):
    """
    Formats a list l to be displayed in a tabular layout. It is
    possible to pass an integer width to the textwrap function.
    The width of the terminal window could be obtained via the
    Python console module. However, since it is not included
    in Jas, we decided not to use it. The default width that
    textwrap uses is set to 70. There might be a better way to
    do this.
    """
    col = max([len(x) for x in l]) + 3
    padded = ''.join([x.ljust(col) for x in l])
    print '\n'.join(textwrap(padded))

def get_value_for_URI(sd, URI, predicate):
    """
    A quick convienience function to retrieve a single value
    of a given triple (object, predicate, ...)

    The parameter sd is a SymbolicData object that contains
    information about the SPARQL endpoint.
    """
    result = None
    query = '''SELECT * WHERE { <%s> <%s> ?x }''' % (URI, predicate)
    try:
        result = SPARQL(sd, query).json['results']['bindings'][0]['x']['value']
    except:
        pass
    return result



# Class definitions start here
class SymbolicData:
    """
    Access to the database of ideals as provided by the
    SymbolicData Project (http://symbolicdata.org).
    """

    def __init__(self, sparql = 'symbolicdata.org'):
        """
        The constructor parses the sd.ini file and sets up some variables.
        An optional parameter can be passed to select the SPARQL endpoint
        that should be used. The keywords for different SPARQL endpoints are
        defined in the sd.ini file.

        The default SPARQL endpoint is the one from symbolicdata.org
        """
        self._sparql = sparql
        self._ideals = None
        self._parser = SafeConfigParser()
        self._parser.read(['sd.ini','examples/sd.ini'])
        self.sd = self._parser.get('symbolicdata', 'sd')
        try:
            self.url = self._parser.get('sparql', self._sparql)
        except:
            raise ValueError("The SPARQL endpoint referenced by '%s' was not found in the sd.ini file." % self._sparql)
        self.sdhost = self._parser.get('DEFAULT', 'sdhost')
        self.sqpath = self._parser.get('sparql', 'path')
        #print "SymbolicData() initialized"
        #print "url    = " + str(self.url)
        #print "sdhost = " + str(self.sdhost)

    def get_ideals(self, force_reload = False):
        """
        Returns a Python list of ideals.
        """
        if self._ideals == None or force_reload == True:
            self.list_ideals(False, force_reload)
        return self._ideals

    def list_ideals(self, output = True, force_reload = False):
        """
        Lists all the available ideals.
        """
        if self._ideals == None or force_reload == True:
            r = SPARQL(self, self._parser.get('queries', 'list_ideals'))
            self._ideals = [_uri_to_name(x['ideal']['value']) for
                      x in r.json['results']['bindings']]
        if output:
            _pprint(self._ideals)

    def get_ideal(self, uri):
        """
        Returns an ideal as a Jas object that is ready to be used by
        Jas.
        """
        return SD_Ideal(self, uri).get_ideal();

    def get_sd_ideal(self, uri):
        """
        Returns an internal object that represents the SymbolicData
        database object. (See below for details)
        """
        return SD_Ideal(self, uri)



class SPARQL:
    """
    This is a 'wrapper' class for SPARQL queries. A class might be
    a slight overkill. It was made with the idea, that one can store
    the query and the result together, to re-evaluate both without
    having to access the server. However, in the end this feature
    was not really needed.
    """
    def __init__(self, sd, query, output = 'json'):
        self._sd = sd;
        self._query = query;
        self._data = {
            'output' : output,
            'query' : query
        }
        #self.response = requests.get(self._sd.url, params = self._data)
        #print "url = " + str(self._sd.url)
        conn = httplib.HTTPConnection(self._sd.url)
        #print "conn = " + str(conn)

        #print "query = " + str(query)
        _path = self._sd.sqpath + "?" + urllib.urlencode(self._data)
        #print "path = " + str(_path)
        conn.request("GET", _path );
        self.response = conn.getresponse();
        if self.response.status != 200:
           print self.response.status, self.response.reason, "\n"
           raise IOError, "HTTP GET %s not successful" % _path

        self.head = self.response.msg
        #print "head = " + str(self.head)
        self.text = self.response.read()
        #print "body = " + str(self.text)
        self.json = json.loads(self.text)
        #print "json = " + str(self.json)
        conn.close()


class SD_Ideal:
    """
    This class represents a SymbolicData database object. The
    constructor takes a complete URI or a name SUBJ (the latter of which
    will be prefixed with the 'ideal' value from the sd.ini)

    Any triple of the form (SUBJ, PRED, OBJ) will yield a field PRED*
    for the SD_Ideal object with the value OBJ, where PRED* is the
    ending piece of PRED URI as defined by the function _uri_to_name()

    A SPARQL endpoint is needed. As a future improvement, it could be
    nice to directly parse an RDF in a convienient serialization.
    """

    def __init__(self, sd, name):
        """
        sd is a SymbolicData object, the name can be a complete URI or shortened
        name as defined by _uri_to_name(). The latter will be prefixed with the
        'ideal' value from the sd.ini. Namespaces like "sd:Wu-90" are not
        (yet) supported.

        Appart from retrieving the information from the SPARQL endpoint, the
        resource data (XML files) is needed as well. While the SPARQL endpoint
        can be substituted by another SPARQL endpoint, the links to the resource
        files are 'hard-coded' into the RDF data. The possibility to use a
        (possibly 'hand-filled') cache will be included in the next update.
        """
        self._sd = sd
        # quick test, if the given name already is an uri
        if name[:7] == 'http://':
            self.uri = name
        else:
            self.uri = "%s%s" % (self._sd._parser.get("symbolicdata", "ideal"), name)

        self.hasXMLResource = False
        self.hasLengthsList = ''
        self.hasDegreeList = ''
        self.hasParameters = ''

        # we set up the query to get all predicate values
        # of the URI/polynomial system/ideal
        query = '''
            PREFIX sd: <%s>
            SELECT ?p ?o WHERE {
                <%s> ?p ?o
            }''' % (self._sd.sd, self.uri)
        self._request = SPARQL(self._sd, query)

        if len(self._request.json['results']['bindings']) == 0:
            raise ValueError("No data found for <%s>.\nMaybe the name was misspelled or the SPARQL endpoint is unavailable." % self.uri)

        # append the keys to the self.__dict__.
        for t in self._request.json['results']['bindings']:
            uri = t['p']['value']
            obj = t['o']['value']
            self.__dict__[_uri_to_name(uri)] = obj

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
        if 'relatedPolynomialSystem' in self.__dict__.keys():
            self.__addXMLResource(get_value_for_URI(self._sd, self.relatedPolynomialSystem, self._sd.sd+'relatedXMLResource'))
            self.hasXMLResource = True
            #print "relatedPolynomialSystem " + str(name)

        # case 2
        if 'flatten' in self.__dict__.keys():
            parent_name = self.flatten
            parent = SD_Ideal(self._sd, parent_name)
            self.variablesCSV = self.hasVariables
            self.variables = map(lambda x: str(x).strip(), self.variablesCSV.rsplit(","))
            self.basis = parent.basis
            #print "flatten " + str(parent_name) + ", name = " + str(name)

        # case 3
        if 'homogenize' in self.__dict__.keys():
            parent_name = self.homogenize
            if 'homogenizedWith' in self.__dict__.keys():
                hv = self.homogenizedWith
                parent = SD_Ideal(self._sd, parent_name)
                self.variablesCSV = parent.variablesCSV + "," + hv
                self.variables = parent.variables
                self.variables.append(hv)
                self.basis = parent.jas_homogenize(hv)
            #print "homogenize " + str(parent_name) + ", name = " + str(name)

        # case 4
        if 'parameterize' in self.__dict__.keys():
            parent_name = self.parameterize
            parent = SD_Ideal(self._sd, parent_name)
            self.variablesCSV = self.hasVariables
            self.variables = map(lambda x: str(x).strip(), self.variablesCSV.rsplit(","))
            self.basis = parent.basis
            #print "parameterize " + str(parent_name) + ", name = " + str(name)

        # now we got the variables, the parameters and
        # the strings/expressions for the polynomials
        self.__constructJasObject()

    def get_ideal(self):
        """
        Return the ideal as a Jas objects.
        """
        #return ideal(self.sageBasis)
        #print "jasRing = " + str(self.jasRing)
        return self.jasRing.ideal(list=self.jasBasis)

    def __addXMLResource(self, link):
        #xml = requests.get(link).text
        #print "link = " + str(link)
        #url = link[0:23]
        path = link[23:] # hack for lost domain
        #print "url = " + str(url)
        #url = self._sd.url[:-5]
        url = self._sd.sdhost
        #print "url = " + str(url)
        conn = httplib.HTTPConnection(url)
        #print "conn = " + str(conn)
        #print "path = " + str(path)
        conn.request("GET", path );
        xml = conn.getresponse().read();
        print _uri_to_name(link) + " = " + str(xml)

        xmlTree = parseString(xml)

        # Code snipped borrowed from Albert Heinle
        if (xmlTree.getElementsByTagName("vars") == []): # Check, if vars are there
            raise IOERROR("The given XMLString does not contain variables for the IntPS System")
        if (xmlTree.getElementsByTagName("basis") == []): # Check, if we have a basis
            raise IOERROR("The given XMLString does not contain a basis for the IntPS System")
        # -------------------- Input Check finished --------------------
        # From here, we can assume that the input is given correct
        self.variablesCSV = (xmlTree.getElementsByTagName("vars")[0]).firstChild.data
        self.variables = map(lambda x: str(x).strip(), self.variablesCSV.rsplit(","))
        polynomials = xmlTree.getElementsByTagName("basis")[0]
        self.basis = map(lambda poly: str(poly.firstChild.data).strip(),polynomials.getElementsByTagName("poly"))

    def __constructJasObject(self):
        from types import StringType
        from jas import PolyRing, ZZ
        # set up the polynomial ring (Jas syntax)
        if 'hasParameters' in self.__dict__ and self.hasParameters != '':
            #K = 'K.<%s> = PolynomialRing(ZZ)' % self.hasParameters
            #R = K + '; R.<%s> = PolynomialRing(K)' % self.hasVariables
            K = 'PolyRing(ZZ(),"%s")' % self.hasParameters
            R = 'R = PolyRing(%s,"%s")' % (K, self.hasVariables)
            gens = '(one,%s,%s)' % (self.hasParameters, self.hasVariables)
        else:
            #R = 'R.<%s> = PolynomialRing(ZZ)' % (self.hasVariables)
            R = 'R = PolyRing(ZZ(),"%s")' % (self.hasVariables)
            gens = '(one,%s)' % self.hasVariables
        # translate Jas syntax to pure Python and execute
        #exec(preparse(R))
        R = R + "; " + gens + " = R.gens();"
        #print str(R)
        exec(str(R))
        print "R = " + str(R)
        self.jasRing = R;
        # construct polynomials in the constructed ring from
        # the polynomial expressions
        self.jasBasis = []
        for ps in self.basis:
            #print "ps = " + str(ps)
            if type(ps) is StringType and '^' in ps:
               ps = ps.replace('^', '**')
            #exec(preparse("symbdata_ideal = %s" % ps))
            exec("symbdata_poly = %s" % ps)
            self.jasBasis.append(symbdata_poly)
        #print "jasBasis = " + str([ str(p) for p in self.jasBasis])

    # the following functions will all use Jas to
    # calculate metadata
    def jas_hasLengthsList(self):
        """
        This is the implementation of the predicate "sd:hasLengthsList".
        The lengths lists is the sorted list of the number of monomials of
        the generator of the ideal.

        Along with the output, there will also be generated a field
        FROM_JAS_hasLengthsList which can be used to later access the
        data without recalculating. The main reason for this is that the
        SymbolicData properties are converted into field, not getter
        functions. So to have some symmetry, the Jas calculations will
        end up in fields as well.
        """
        try:
            LL = sorted(map(lambda x : len(x.monomials()), self.jasBasis))
            self.FROM_JAS_hasLengthsList = ",".join(map(lambda x: str(x), LL))
        except:
            self.FROM_JAS_hasLengthsList = ''
        return self.FROM_JAS_hasLengthsList

    def jas_hasDegreeList(self):
        """
        This is the implementation of the predicate "sd:hasDegreeList".
        The degree list is the sorted list of the degree of the generator
        of the ideal.

        Along with the output, there will also be generated a field
        FROM_JAS_hasDegreeList which can be used to later access the
        data without recalculating. The main reason for this is that the
        SymbolicData properties are converted into field, not getter
        functions. So to have some symmetry, the Jas calculations will
        end up in fields as well.
        """
        try:
            LL = sorted(map(lambda x : x.degree(), self.jasBasis))
            self.FROM_JAS_hasDegreeList = ",".join(map(lambda x: str(x), LL))
        except:
            self.FROM_JAS_hasDegreeList = ''
        return self.FROM_JAS_hasDegreeList

    def jas_hasVariables(self):
        """
        This is the implementation of the predicate "sd:hasVariables". This
        is actually not needed.
        """
        #K = []
        #DL = map(lambda m : K.extend(map(lambda l : str(l), m.variables())), self.sageBasis)
        K = self.jasRing.ring.vars
        return ",".join(sorted(list(set(K))))

    def jas_homogenize(self, hv):
        """
        Homogenize a basis, which here means actually nothing more than
        homogenizing every element of the basis.
        """
        homBasis = map(lambda x : x.homogenize(hv), self.jasBasis)
        return homBasis

