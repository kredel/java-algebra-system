# implementation of the Staggered Linear Basis Algorithm of Gebauer and M\"oller
# "Buchberger's algorithm and Staggered Linear Bases"
# in Proceedings of SYMSAC 1986 (Waterloo/Ontario), pages 218--221
# ACM Press, 1986

# Originally from http://www.math.usm.edu/perry/Research/staggered_linear_basis.py
# small changes for JAS compatibility, changes are labled with JAS
# $Id$

# usage: staglinbasis(F) where F is a list of polynomials
# result: groebner basis of F
# example:
#   sage: R = PolynomialRing(GF(32003),x,5)
#   sage: I = sage.rings.ideal.Cyclic(R,5).homogenize()
#   sage: F = list(I.gens())
#   sage: B = staglinbasis(F)
#   >> reduction to zero: (9, 12, x1*x2*x3^3*x4^2) x2
#   >> reduction to zero: (7, 12, x1*x2*x3^3*x4^2) x2
#   >> reduction to zero: (5, 12, x1^2*x3^3*x4^2) x1
#   ... [more omitted]
#   >> 32 reductions to zero
#   sage: len(B)
#   >> 42

# minimal_pair
# used to select a minimal critical pair from a list;
# form of the list is (i,j,t) where t=lcm(lt(g[i]),lt(g[j]));
# chosen by smallest lcm
# inputs: list of pairs P
# outputs: minimal pair in P
def minimal_pair(P):
  result = P.pop()
  P.add(result)
  for p in P:
    if p[2] < result[2]:
      result = p
  return result

# spol
# compute the s-polynomial corresponding to the critical pair p
# inputs: critical pair p, basis G
# outputs: s-polynomial
def spol(p,G):
  i,j,tij = p
  R = G[i].parent() # JAS
  ti = R.monomial_quotient(tij,G[i].lm())
  tj = R.monomial_quotient(tij,G[j].lm())
  return ti*G[i] - tj*G[j]

# monid_quotient
# computes the quotient I:J of the monomial ideals I and J
# for each tI in I, and for each tJ in J, it computes lcm(tI,tJ)/tJ
# this corresponds to computing the u's such that uv is in I for each v in J
# note that the ideals must be simplified for this to work correctly
# (see below)
# inputs: lists of monomials I and J (generators of ideals)
# outputs: list of monomials that generate I:J
def monid_quotient(I,J,R): # JAS
  result = set()
  for tI in I:
    # R = tI.parent() #JAS
    for tJ in J:
      u = R.monomial_quotient(tI.lcm(tJ),tJ).lm() # JAS
      if not any(R.monomial_divides(t,u) for t in result):
        result.add(u)
  return result

# monid_add
# expands the monomial ideal I by the monomial t
# simplifies the result
# inputs: list of monomials I (generators of ideal), monomial t
# outputs: simplified I+(t)
def monid_add(I,t):
  result = set()
  R = t.parent()
  for u in I:
    if not(R.monomial_divides(t,u)) and not(R.monomial_divides(u,t)):
      result.add(u)
  if not any(R.monomial_divides(u,t) for u in result):
    result.add(t)
  return result

# simplify_monid
# simplifies the list of generators of the monomial ideal I
# by pruning redundant elements
# inputs: list of monomials I (generators of ideal)
# outputs: simplified I
def simplify_monid(I,R): # JAS
  result = set()
  for t in I:
    #R = t.parent() #JAS
    if not any(u != t and R.monomial_divides(u,t) for u in I):
      result.add(t)
  return result

# staglinbasis
# computes the Groebner Basis of a polynomial ideal using the
# Staggered Linear Basis algorithm of Gebauer-Moeller
# inputs: list of polynomials F
# outputs: Groebner basis of F
def staglinbasis(F):
  # tracks the number of zero reductions
  num_zrs = 0
  # get polynomial ring
  R = F[0].parent()
  G = list(F) # JAS
  m = len(G)
  # Z is the set of monomial used to detect principal syzygies
  # in general, Z should be simplified anytime you modify it
  Z = [set([G[j].lm() for j in xrange(i)]) for i in xrange(m)]
  Z = [simplify_monid(ZI,R) for ZI in Z] # JAS
  # initial set of critical pairs
  P = set()
  for i in xrange(m-1):
    for j in xrange(i+1,m):
      tij = G[i].lm().lcm(G[j].lm())
      tj = R.monomial_quotient(tij,G[j].lm()) # JAS
      if not any(R.monomial_divides(t,tj) for t in Z[j]):
        P.add((i,j,tij))
  # main loop
  while len(P) != 0:
    # choose the pair w/smallest lcm
    p = minimal_pair(P)
    P.remove(p)
    # compute S-polynomial, reduce it
    s = spol(p,G)
    i,j,tij = p
    r = s.reduce(G)
    if r == 0:
      print "reduction to zero:", p, R.monomial_quotient(tij,G[j].lm())
      num_zrs += 1
    else:
      # new polynomial; add to basis
      r = r.monic() # JAS #r *= r.lc()**(-1)
      G.append(r)
      # update ideals here, also below
      # need new ideal for G[-1]=r, essentially
      #   Znew = (Z[j] + t[i])):(lcm(t[i],t[j])/t[j]) + (t[1], ..., t[m])
      # where t[i] = lm(g[i])
      Znew = Z[j].copy() # JAS
      Znew.update([G[i].lm()])
      Znew = simplify_monid(Znew,R) #JAS
      tj = R.monomial_quotient(tij,G[j].lm()).lm() # JAS
      Znew = monid_quotient(Znew,[tj],R) # JAS
      Znew.update([g.lm() for g in G[:-1]])
      Znew = simplify_monid(Znew,R)
      Z.append(Znew)
      # compute new critical pairs w/new polynomial
      m = len(G) - 1
      for i in xrange(m):
        tim = G[i].lm().lcm(G[m].lm())
        tm = R.monomial_quotient(tim,G[m].lm())
        # don't add pairs detected by Z[m]
        if not any(R.monomial_divides(t,tm) for t in Z[m]):
          P.add((i,m,tim))
      # update Z[j]
      Z[j].add(R.monomial_quotient(tij,G[j].lm()).lm()) # JAS
      Z[j] = simplify_monid(Z[j],R) # JAS
      # prune pairs detected by new Z[j]
      Q = set()
      for (i,j,tij) in P:
        tj = R.monomial_quotient(tij,G[j].lm())
        if any(R.monomial_divides(t,tj) for t in Z[j]):
          Q.add((i,j,tij))
      P.difference_update(Q)
  print num_zrs, "reductions to zero"
  return G
