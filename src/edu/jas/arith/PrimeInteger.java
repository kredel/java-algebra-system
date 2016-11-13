/*
 * $Id$
 */

package edu.jas.arith;


import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;


/**
 * Integer prime factorization. Code from ALDES/SAC2 and MAS module SACPRIM.
 * 
 * @see ALDES/SAC2 or MAS code in SACPRIM
 * @author Heinz Kredel
 */

public final class PrimeInteger {


     private static final Logger logger = Logger.getLogger(PrimeInteger.class);


    /**
     * Maximal long, which can be factored by IFACT.
     * Nothing to do with SAC2.BETA.
     */
    final public static long BETA = PrimeList.getLongPrime(61, 1).longValue();


    /**
     * List of small prime numbers.
     */
    final public static List<Long> SMPRM = smallPrimes(2, 1000); 


    /**
     * List of units of Z mod 210.
     */
    final public static List<Long> UZ210 = getUZ210(); 


    /**
     * Medium prime divisor range.
     */
    final static long IMPDS_MIN = 2000; 
    final static long IMPDS_MAX = 10000; 


    /**
     * Digit prime generator. K and m are positive beta-integers. L is the list
     * (p(1),...,p(r)) of all prime numbers p such that m le p lt m+2*K, with
     * p(1) lt p(2) lt ... lt p(r).
     * @param m start integer
     * @param K number of integers
     * @return the list L of prime numbers p with p &lt; m + 2*K.
     * @see SACPRIM.DPGEN
     */
    public static List<Long> smallPrimes(long m, int K) {
        int k;
        long ms;
        ms = m;
        if (ms <= 1) {
            ms = 1;
        }
        m = ms;
        if (m % 2 == 0) {
            m++;
            K--;
        }
        //if (kp % 2 == 0) { 
        //    k = kp/2; 
        //} else { 
        //    k = (kp+1)/2; 
        //}
        k = K;

        /* init */
        long h = 2 * (k - 1);
        long m2 = m + h; // mp    
        BitSet p = new BitSet(k);
        p.set(0, k);
        //for (int i = 0; i < k; i++) { 
        //    p.set(i); 
        //}

        /* compute */
        int r, d = 0;
        int i, c = 0;
        while (true) {
            switch (c) {
            /* mark multiples of d for d=3 and d=6n-/+1 with d**2<=m2 */
            case 2:
                d += 2;
                c = 3;
                break;
            case 3:
                d += 4;
                c = 2;
                break;
            case 0:
                d = 3;
                c = 1;
                break;
            case 1:
                d = 5;
                c = 2;
                break;
            }
            if (d > (m2 / d)) {
                break;
            }
            r = (int)(m % d);
            if (r + h >= d || r == 0) {
                if (r == 0) {
                    i = 0;
                } else {
                    if (r % 2 == 0) {
                        i = d - (r / 2);
                    } else {
                        i = (d - r) / 2;
                    }
                }
                if (m <= d) {
                    i += d;
                }
                while (i < k) {
                    p.set(i, false);
                    i += d;
                }
            }
        }
        /* output */
        int l = p.cardinality(); // l = 0
        //for (i=0; i<k; i++) { 
        //    if (p.get(i)) { 
        //         l++;
        //     } 
        //}
        if (ms <= 2) {
            l++;
        }
        //if (ms <= 1) { 
        //}
        List<Long> po = new ArrayList<Long>(l);
        if (l == 0) {
            return po;
        }
        //l = 0;
        if (ms == 1) {
            //po.add(2); 
            //l++; 
            p.set(0, false);
        }
        if (ms <= 2) {
            po.add(2L);
            //l++; 
        }
        long pl = m;
        //System.out.println("pl = " + pl + " p[0] = " + p[0]);
        //System.out.println("k-1 = " + (k-1) + " p[k-1] = " + p[k-1]);
        for (i = 0; i < k; i++) {
            if (p.get(i)) {
                po.add(pl);
                //l++; 
            }
            pl += 2;
        }
        return po;
    }


    /**
     * Integer small prime divisors. n is a positive integer. F is a list of
     * primes (q(1),q(2),...,q(h)), h non-negative, q(1) le q(2) le ... lt q(h),
     * such that n is equal to m times the product of the q(i) and m is not
     * divisible by any prime in SMPRM. Either m=1 or m gt 1,000,000.
     * &gt;br /&lt;
     * In JAS F is a map and m=1 or m &gt; 4.000.000 and m is contined in F with value 0.
     * @param n integer to factor.
     * @return a map F of pairs of prime numbers and multiplicities (p,e) with p**e divides n and e maximal.
     * @see SACPRIM.ISPD
     */
    public static SortedMap<Long, Integer> ISPD(long n) {
        SortedMap<Long, Integer> F = new TreeMap<Long, Integer>();
        List<Long> LP;
        long QL = 0;
        long PL;
        long RL = 0;
        boolean TL;

        long ML = n;
        LP = SMPRM; //smallPrimes(2, 500); //SMPRM;
        TL = false;
        int i = 0;
        do {
            PL = LP.get(i);
            QL = ML / PL;
            RL = ML % PL;
            if (RL == 0) {
                Integer e = F.get(PL);
                if (e == null) {
                    e = 1;
                } else {
                    e++;
                }
                F.put(PL, e);
                ML = QL;
            } else {
                i++;
            }
            TL = (QL <= PL);
        } while (!(TL || (i >= LP.size())));
        //System.out.println("TL = " + TL + ", ML = " + ML + ", PL = " + PL + ", QL = " + QL);
        if (TL && (ML != 1)) {
            Integer e = F.get(ML);
            if (e == null) {
                e = 1;
            } else {
                e++;
            }
            F.put(ML, e);
            ML = 1;
        }
        F.put(ML, 0); // hack
        return F;
    }


    /**
     * Integer primality test. n is a positive integer. 
     * r is true, if n is prime, else false.
     * @param n integer to test.
     * @return true if n is prime, else false.
     */
    public static boolean isPrime(long n) {
        SortedMap<Long, Integer> F = IFACT(n);
        return (F.size() == 1) && F.values().contains(1);
    }


    /**
     * Test prime factorization. n is a positive integer. 
     * r is true, if n = product_i(pi**ei) and each pi is prime, else false.
     * @param n integer to test.
     * @param a map F of pairs of prime numbers (p,e) with p**e divides n.
     * @return true if n = product_i(pi**ei) and each pi is prime, else false.
     */
    public static boolean isPrimeFactorization(long NL, SortedMap<Long, Integer> F) {
        long f = 1L;
        for (Map.Entry<Long, Integer> m : F.entrySet()) {
	    long p = m.getKey();
            if (!isPrime(p)) {
                return false;
            }
            int e = m.getValue();
            long pe = java.math.BigInteger.valueOf(p).pow(e).longValue();
            f *= pe;
        }
        return NL == f;
    }


    /**
     * Test factorization. n is a positive integer. 
     * r is true, if n = product_i(pi**ei), else false.
     * @param n integer to test.
     * @param a map F of pairs of numbers (p,e) with p**e divides n.
     * @return true if n = product_i(pi**ei), else false.
     */
    public static boolean isFactorization(long n, SortedMap<Long, Integer> F) {
        long f = 1L;
        for (Map.Entry<Long, Integer> m : F.entrySet()) {
	    long p = m.getKey();
            int e = m.getValue();
            long pe = java.math.BigInteger.valueOf(p).pow(e).longValue();
            f *= pe;
        }
        return n == f;
    }


    /**
     * Integer factorization. n is a positive integer. F is a list (q(1),
     * q(2),...,q(h)) of the prime factors of n, q(1) le q(2) le ... le q(h),
     * with n equal to the product of the q(i).
     * &gt;br /&lt;
     * In JAS F is a map.
     * @param n integer to factor.
     * @param a map F of pairs of numbers (p,e) with p**e divides n.
     * @see SACPRIM.IFACT
     */
    public static SortedMap<Long, Integer> IFACT(long n) {
        if (n > BETA) {
            throw new UnsupportedOperationException("IFACT only for longs less than BETA: " + BETA);
        }
        long ML, PL, AL, BL, CL, MLP, RL, SL;
        SortedMap<Long, Integer> F = new TreeMap<Long, Integer>();
        SortedMap<Long, Integer> FP = null;
        // search small prime factors
        F = ISPD(n); // , F, ML
        ML = 0L; // nonsense
        for (Long m : F.keySet()) {
	    if (F.get(m) == 0) {
                //System.out.println("m = " + m);
                ML = m;
                break;
            }
        }
        if (ML == 1L) {
            F.remove(ML); // hack
            return F;
        }
        if (ML != 0L) {
            F.remove(ML); // hack
        }
        //System.out.println("F = " + F);
        // search medium prime factors
        AL = IMPDS_MIN;
        do {
            MLP = ML - 1;
            RL = (long) (new ModLong(new ModLongRing(ML), 3)).power(MLP).getVal(); //(3**MLP) mod ML; 
            //SACM.MIEXP( ML, 3, MLP );
            if (RL == 1L) {
                FP = IFACT(MLP);
                SL = ISPT(ML, MLP, FP);
                if (SL == 1) {
                    logger.info("ISPT: FP = " + FP);
                    Integer e = F.get(ML);
                    if (e == null) {
                        e = 1;
                    } else {
                        e++;
                    }
                    F.put(ML, e); // = MASSTOR.COMP( ML, F );
                    //tocheck: F = MASSTOR.INV( F );
                    return F;
                }
            }
            CL = Roots.sqrtInt(new BigInteger(ML)).getVal().longValue(); //SACI.ISQRT( ML, CL, TL );
            //System.out.println("CL = " + CL + ", ML = " + ML + ", CL^2 = " + (CL*CL));
            BL = Math.max(IMPDS_MAX, CL / 3L);
            if (AL > BL) {
                PL = 1L;
            } else {
                logger.info("IMPDS: a = " + AL + ", b = " + BL);
                PL = IMPDS(ML, AL, BL); //, PL, ML );
                //System.out.println("PL = " + PL);
                if (PL != 1L) {
                    AL = PL;
                    Integer e = F.get(PL);
                    if (e == null) {
                        e = 1;
                    } else {
                        e++;
                    }
                    F.put(PL, e); //F = MASSTOR.COMP( PL, F );
                    ML = ML / PL;
                }
            }
        } while (PL != 1L);
        // fixme: the ILPDS should also be in the while loop, was already wrong in SAC2/Aldes and MAS
        // seems to be okay for integers smaller than beta
        AL = BL;
        BL = CL;
        //ILPDS( ML, AL, BL, PL, ML );
        logger.info("ILPDS: a = " + AL + ", b = " + BL + ", m = " + ML);
        PL = ILPDS(ML, AL, BL);
        if (PL != 1L) {
            Integer e = F.get(PL);
            if (e == null) {
                e = 1;
            } else {
                e++;
            }
            F.put(PL, e);
            ML = ML / PL;
        }
        //System.out.println("PL = " + PL + ", ML = " + ML);
        if (ML != 1L) {
            Integer e = F.get(ML);
            if (e == null) {
                e = 1;
            } else {
                e++;
            }
            F.put(ML, e);
        }
        return F;
    }


    /**
     * Integer medium prime divisor search. n, a and b are positive integers
     * such that a le b le n and n has no positive divisors less than a. If n
     * has a prime divisor in the closed interval from a to b then p is the
     * least such prime and q=n/p. Otherwise p=1 and q=n.
     * @param n integer to factor.
     * @param a lower bound.
     * @param b upper bound.
     * @return p a prime factor of n, with a &le; p &le; b &lt; n.
     * @see SACPRIM.IMPDS
     */
    public static long IMPDS(long n, long a, long b) {
        List<Long> LP;
        long R, J1Y, RL1, RL2, RL, PL;

        RL = a % 210;
        //UZ210 = getUZ210();
        LP = UZ210;
        long ll = LP.size();
        int i = 0;
        while (RL > LP.get(i)) {
            i++; //LP = MASSTOR.RED( LP );
        }
        RL1 = LP.get(i); //MASSTOR.FIRSTi( LP );
        //J1Y = (RL1 - RL);
        PL = a + (RL1 - RL); //SACI.ISUM( AL, J1Y );
        //System.out.println("PL = " + PL + ", BL = " + BL);
        while (PL <= b) {
            R = n % PL; //SACI.IQR( NL, PL, QL, R );
            if (R == 0) {
                return PL;
            }
            i++; //LP = MASSTOR.RED( LP );
            if (i >= ll) { //LP == MASSTOR.SIL )
                LP = UZ210;
                RL2 = (RL1 - 210L);
                i = 0;
            } else {
                RL2 = RL1;
            }
            RL1 = LP.get(i); //MASSTOR.FIRSTi( LP );
            J1Y = (RL1 - RL2);
            PL = PL + J1Y; //SACI.ISUM( PL, J1Y );
        }
        PL = 1L; //SACI.IONE;
        //QL = NL;
        return PL;
    }


    /**
     * Integer selfridge primality test. m is an integer greater than or equal
     * to 3. mp=m-1. F is a list (q(1),q(2),...,q(k)), q(1) le q(2) le ... le
     * q(k), of the prime factors of mp, with mp equal to the product of the
     * q(i). An attempt is made to find a root of unity modulo m of order m-1.
     * If the existence of such a root is discovered then m is prime and s=1. If
     * it is discovered that no such root exists then m is not a prime and s=-1.
     * Otherwise the primality of m remains uncertain and s=0.
     * @param m integer to test.
     * @param mp integer m-1.
     * @param a map F of pairs (p,e), with a prime p, multiplicity e and with p**e divides mp.
     * @return s = -1 (not prime), 0 (unknown) or 1 (prime).
     * @see SACPRIM.ISPT
     */
    public static int ISPT(long m, long mp, SortedMap<Long, Integer> F) {
        long AL, BL, QL, QL1, MLPP, PL1, PL;
        int SL;
        //List<Long> SMPRM = smallPrimes(2, 500); //SMPRM;
        List<Long> PP;

        List<Map.Entry<Long, Integer>> FP = new ArrayList<Map.Entry<Long, Integer>>(F.entrySet());
        QL1 = 1L; //SACI.IONE;
        PL1 = 1L;
        int i = 0;
        while (true) {
            do {
                if (i == FP.size()) { //FP == MASSTOR.SIL
                    logger.info("SL=1: m = " + m);
                    SL = 1;
                    return SL;
                }
                QL = FP.get(i).getKey();
                i++; //FP = MASSTOR.RED( FP );
            } while (!(QL > QL1));
            QL1 = QL;
            PP = SMPRM;
            int j = 0;
            do {
                if (j == PP.size()) {
                    logger.info("SL=0: m = " + m);
                    SL = 0;
                    return SL;
                }
                PL = PP.get(j); // MASSTOR.FIRSTi( PP );
                j++; //PP = MASSTOR.RED( PP );
                if (PL > PL1) {
                    PL1 = PL;
                    //AL = SACM.MIEXP( ML, PL, MLP );
                    AL = (long) (new ModLong(new ModLongRing(m), PL)).power(mp).getVal(); //(PL**MLP) mod ML; 
                    if (AL != 1) {
                        logger.info("SL=-1: m = " + m);
                        SL = (-1);
                        return SL;
                    }
                }
                MLPP = mp / QL; //SACI.IQ( MLP, QL );
                //BL = SACM.MIEXP( ML, PL, MLPP );
                BL = (long) (new ModLong(new ModLongRing(m), PL)).power(MLPP).getVal(); //(PL**MLPP) mod ML; 
            } while (BL == 1L);
        }
    }


    /**
     * Integer large prime divisor search. n is a positive integer with no prime
     * divisors less than 17. 1 le a le b le n. A search is made for a divisor p
     * of the integer n, with a le p le b. If such a p is found then np=n/p,
     * otherwise p=1 and np=n. A modular version of fermats method is used, and
     * the search goes from a to b.
     * @param n integer to factor.
     * @param a lower bound.
     * @param b upper bound.
     * @return p a prime factor of n, with a &le; p &le; b &lt; n.
     * @see SACPRIM.ILPDS
     */
    public static long ILPDS(long n, long a, long b) { // return PL, NLP ignored
        if (n > BETA) {
            throw new UnsupportedOperationException("ILPDS only for longs less than BETA: " + BETA);
        }
        List<ModLong> L = null;
        List<ModLong> LP;
        long RL1, RL2, J1Y, r, PL, TL;
        long RL, J2Y, XL1, XL2, QL, XL, YL, YLP;
        long ML = 0L;
        long SL = 0L;
        //SACI.IQR( NL, BL, QL, RL );
        QL = n / b;
        RL = n % b;
        XL1 = b + QL;
        //SACI.IDQR( XL1, 2, XL1, _SL );
        SL = XL1 % 2L;
        XL1 = XL1 / 2L; // after SL
        if ((RL != 0) || (SL != 0)) {
            XL1 = XL1 + 1L;
        }
        QL = n / a;
        XL2 = a + QL;
        XL2 = XL2 / 2L;
        L = FRESL(n); //FRESL( NL, ML, L ); // ML not returned
        if (L.isEmpty()) {
            return n;
        }
        ML = L.get(0).ring.getModul().longValue(); // sic
        // check is okay: sort: L = SACSET.LBIBMS( L ); revert: L = MASSTOR.INV( L );
        Collections.sort(L);
        Collections.reverse(L);
        //System.out.println("FRESL: " + L);
        r = XL2 % ML;
        LP = L;
        int i = 0;
        while (i < LP.size() && r < LP.get(i).getVal()) {
            i++; //LP = MASSTOR.RED( LP );
        }
        if (i == LP.size()) {
            i = 0; //LP = L;
            SL = ML;
        } else {
            SL = 0L;
        }
        RL1 = (long) LP.get(i).getVal(); //MASSTOR.FIRSTi( LP );
        i++; //LP = MASSTOR.RED( LP );
        SL = ((SL + r) - RL1);
        XL = XL2 - SL;
        TL = 0L;
        while (XL >= XL1) {
            J2Y = XL * XL;
            YLP = J2Y - n;
            //System.out.println("YLP = " + YLP + ", J2Y = " + J2Y);
            YL = Roots.sqrtInt(new BigInteger(YLP)).getVal().longValue(); // SACI.ISQRT( YLP, YL, TL );
            //System.out.println("YL = sqrt(YLP) = " + YL);
            TL = YLP - YL * YL;
            if (TL == 0L) {
                PL = XL - YL;
                //NLP.val = SACI.ISUM( XL, YL );
                return PL;
            }
            if (i < LP.size()) {
                RL2 = (long) LP.get(i).getVal(); //MASSTOR.FIRSTi( LP );
                i++; //LP = MASSTOR.RED( LP );
                SL = (RL1 - RL2);
            } else {
                i = 0;
                RL2 = (long) LP.get(i).getVal(); //MASSTOR.FIRSTi( L );
                i++; //LP = MASSTOR.RED( L );
                J1Y = (ML + RL1);
                SL = (J1Y - RL2);
            }
            RL1 = RL2;
            XL = XL - SL;
        }
        PL = 1L; //SACI.IONE;
        // unused NLP = NL;
        return PL;
    }


    /**
     * Fermat residue list, single modulus. m is a positive beta-integer. a
     * belongs to Z(m). L is a list of the distinct b in Z(m) such that b**2-a
     * is a square in Z(m).
     * @param m integer to factor.
     * @param a element of Z mod m.
     * @return Lp a list of Fermat residues for modul m.
     * @see SACPRIM.FRLSM
     */
    public static List<ModLong> FRLSM(long m, long a) {
        List<ModLong> Lp;
        SortedSet<ModLong> L;
        List<ModLong> S, SP;
        int MLP;
        ModLong SL, SLP, SLPP;

        ModLongRing ring = new ModLongRing(m);
        ModLong am = ring.fromInteger(a);
        MLP = (int) (m / 2L);
        S = new ArrayList<ModLong>();
        for (int i = 0; i <= MLP; i++) {
            SL = ring.fromInteger(i);
            SL = SL.multiply(SL); //SACM.MDPROD( ML, IL, IL );
            S.add(SL); //S = MASSTOR.COMPi( SL, S );
        }
        L = new TreeSet<ModLong>();
        SP = S;
        for (int i = MLP; i >= 0; i -= 1) {
            SL = SP.get(i); //MASSTOR.FIRSTi( SP );
            // IL -= 1: SP = MASSTOR.RED( SP );
            SLP = SL.subtract(am); //SACM.MDDIF( ML, SL, AL );
            int j = S.indexOf(SLP); //SACLIST.LSRCH( SLP, S );
            if (j >= 0) { // != 0
                SLP = ring.fromInteger(i);
                L.add(SLP); // = MASSTOR.COMPi( IL, L );
                SLPP = SLP.negate(); //SACM.MDNEG( ML, IL );
                if (!SLPP.equals(SLP)) {
                    L.add(SLPP); // = MASSTOR.COMPi( ILP, L );
                }
            }
        }
        Lp = new ArrayList<ModLong>(L);
        return Lp;
    }


    /**
     * Fermat residue list. n is a positive integer with no prime divisors less
     * than 17. m is a positive beta-integer and L is an ordered list of the
     * elements of Z(m) such that if x**2-n is a square then x is congruent to a
     * (modulo m) for some a in L.
     * @param n integer to factor.
     * @return Lp a list of Fermat residues for different modules.
     * @see SACPRIM.FRESL
     */
    public static List<ModLong> FRESL(long n) {
        List<ModLong> L, L1;
        List<Long> H, M;
        long AL1, AL2, AL3, AL4, BL1, HL, J1Y, J2Y, KL, KL1, ML1, ML;
        //too large: long BETA = Long.MAX_VALUE - 1L; 

        // modulus 2**5.
        BL1 = 0L;
        AL1 = n % 32L; //SACI.IDREM( NL, 32 );
        AL2 = AL1 % 16L; //MASELEM.MASREM( AL1, 16 );
        AL3 = AL2 % 8L; //MASELEM.MASREM( AL2, 8 );
        AL4 = AL3 % 4L; //MASELEM.MASREM( AL3, 4 );
        if (AL4 == 3L) {
            ML = 4L;
            if (AL3 == 3L) {
                BL1 = 2L;
            } else {
                BL1 = 0L;
            }
        } else {
            if (AL3 == 1L) {
                ML = 8L;
                if (AL2 == 1L) {
                    BL1 = 1L;
                } else {
                    BL1 = 3L;
                }
            } else {
                ML = 16L;
                switch ((short) (AL1 / 8L)) {
                case (short) 0: {
                    BL1 = 3L;
                    break;
                }
                case (short) 1: {
                    BL1 = 7L;
                    break;
                }
                case (short) 2: {
                    BL1 = 5L;
                    break;
                }
                case (short) 3: {
                    BL1 = 1L;
                    break;
                }
                }
            }
        }
        L = new ArrayList<ModLong>();
        ModLongRing ring = new ModLongRing(ML);
        ModLongRing ring2;
        if (ML == 4L) {
            L.add(ring.fromInteger(BL1)); 
        } else {
            J1Y = ML - BL1;
            L.add(ring.fromInteger(BL1));
            L.add(ring.fromInteger(J1Y));
        }
        KL = L.size(); 

        // modulus 3**3.
        AL1 = n % 27L; //SACI.IDREM( NL, 27 );
        AL2 = AL1 % 3L; //MASELEM.MASREM( AL1, 3 );
        if (AL2 == 2L) {
            ML1 = 3L;
            ring2 = new ModLongRing(ML1);
            KL1 = 1L;
            L1 = new ArrayList<ModLong>();
            L1.add(ring2.fromInteger(0)); 
        } else {
            ML1 = 27L;
            ring2 = new ModLongRing(ML1);
            KL1 = 4L;
            L1 = FRLSM(ML1, AL1);
            // ring2 == L1.get(0).ring
        }
        //L = SACM.MDLCRA( ML, ML1, L, L1 );
        L = ModLongRing.chineseRemainder(ring.getONE(), ring2.getONE(), L, L1);
        ML = (ML * ML1);
        ring = new ModLongRing(ML); // == L.get(0).ring
        KL = (KL * KL1);
        //System.out.println("FRESL: L = " + L + ", ring = " + ring.toScript());

        // modulus 5**2.
        AL1 = n % 25L; //SACI.IDREM( NL, 25 );
        AL2 = AL1 % 5L; //MASELEM.MASREM( AL1, 5 );
        if ((AL2 == 2L) || (AL2 == 3L)) {
            ML1 = 5L;
            ring2 = new ModLongRing(ML1);
            J1Y = (AL2 - 1L);
            J2Y = (6L - AL2);
            L1 = new ArrayList<ModLong>();
            L1.add(ring2.fromInteger(J1Y));
            L1.add(ring2.fromInteger(J2Y));
            //L1 = MASSTOR.COMPi( J1Y, J2Y );
            KL1 = 2L;
        } else {
            ML1 = 25L;
            ring2 = new ModLongRing(ML1);
            L1 = FRLSM(ML1, AL1);
            KL1 = 7L;
        }
        if (ML1 >= BETA / ML) {
            return L;
        }
        //L = SACM.MDLCRA( ML, ML1, L, L1 );
        L = ModLongRing.chineseRemainder(ring.getONE(), ring2.getONE(), L, L1);
        ML = (ML * ML1);
        ring = new ModLongRing(ML);
        KL = (KL * KL1);
        //System.out.println("FRESL: L = " + L + ", ring = " + ring.toScript());

        // moduli 7,11,13.
        L1 = new ArrayList<ModLong>();
        M = new ArrayList<Long>(3);
        H = new ArrayList<Long>(3);
        //M = MASSTOR.COMPi( 7, MASSTOR.COMPi( 11, 13 ) );
        M.add(7L);
        M.add(11L);
        M.add(13L);
        //H = MASSTOR.COMPi( 64, MASSTOR.COMPi( 48, 0 ) );
        H.add(64L);
        H.add(48L);
        H.add(0L);
        int i = 0;
        while (true) {
            ML1 = M.get(i); //MASSTOR.FIRSTi( M );
            // later: M = MASSTOR.RED( M );
            if (ML1 >= BETA / ML) {
                return L;
            }
            ring2 = new ModLongRing(ML1);
            AL1 = n % ML1; //SACI.IDREM( NL, ML1 );
            L1 = FRLSM(ML1, AL1);
            KL1 = L1.size(); //MASSTOR.LENGTH( L1 );
            //L = SACM.MDLCRA( ML, ML1, L, L1 );
            L = ModLongRing.chineseRemainder(ring.getONE(), ring2.getONE(), L, L1);
            ML = (ML * ML1);
            ring = new ModLongRing(ML);
            KL = (KL * KL1);
            //System.out.println("FRESL: L = " + L + ", ring = " + ring.toScript());
            HL = H.get(i); //MASSTOR.FIRSTi( H );
            //H = MASSTOR.RED( H );
            i++;
            if (KL > HL) {
                return L;
            }
        }
        // return ?
    }


    /**
     * Compute units of Z sub 210.
     * @return list of units of Z sub 210.
     * @see SACPRIM.UZ210
     */
    public static List<Long> getUZ210() {
        List<Long> UZ = new ArrayList<Long>();
        java.math.BigInteger z210 = java.math.BigInteger.valueOf(210);
        //for (int i = 209; i >= 1; i -= 2) {
        for (long i = 1; i <= 209; i += 2) {
            if (z210.gcd(java.math.BigInteger.valueOf(i)).equals(java.math.BigInteger.ONE)) {
                UZ.add(i);
            }
        }
        return UZ;
    }

}
