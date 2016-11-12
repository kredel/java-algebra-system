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


/**
 * Integer prime factorization. Code from ALDES/SAC2 and MAS module SACPRIM.
 * 
 * @author See ALDES/SAC2 or MAS code in SACPRIM
 */

public final class PrimeInteger {


    /**
     * Digit prime generator. K and m are positive beta-integers. L is the list
     * (p(1),...,p(r)) of all prime numbers p such that m le p lt m+2*K, with
     * p(1) lt p(2) lt ... lt p(r).
     * @param mp,kp Integers.
     */
    public static List<Long> smallPrimes(long mp, int kp) {
        int k;
        long m, ms;
        ms = mp;
        if (ms <= 1) {
            ms = 1;
        }
        m = ms;
        if (m % 2 == 0) {
            m++;
            kp--;
        }
        //if (kp % 2 == 0) { 
        //    k = kp/2; 
        //} else { 
        //    k = (kp+1)/2; 
        //}
        k = kp;

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
     */
    public static SortedMap<Long, Integer> ISPD(long NL) {
        SortedMap<Long, Integer> F = new TreeMap<Long, Integer>();
        List<Long> LP;
        long QL = 0;
        long PL;
        long RL = 0;
        boolean TL;

        long ML = NL;
        LP = smallPrimes(2, 500); //SMPRM;
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
            F.put(ML, 1);
            ML = 1;
        }
        F.put(ML, 0); // hack
        return F;
    }


    /**
     * Integer factorization. n is a positive integer. F is a list (q(1),
     * q(2),...,q(h)) of the prime factors of n, q(1) le q(2) le ... le q(h),
     * with n equal to the product of the q(i).
     */
    public static SortedMap<Long, Integer> IFACT(long NL) {
        long ML, PL;
        long AL, BL, CL, MLP, RL, SL;
        SortedMap<Long, Integer> F = new TreeMap<Long, Integer>();
        SortedMap<Long, Integer> FP = null;
        // search small prime factors
        F = ISPD(NL); // , F, ML
        ML = 0; // nonsense
        for (Long m : F.keySet()) {
	    if (F.get(m) == 0) {
                //System.out.println("m = " + m);
                ML = m;
                break;
            }
        }
        if (ML == 1) {
            F.remove(ML); // hack
            return F;
        }
        if (ML != 0) {
            F.remove(ML); // hack
        }
        //System.out.println("F = " + F);
        // search medium prime factors
        AL = 1000;
        do {
            MLP = ML - 1;
            RL = (long) (new ModLong(new ModLongRing(ML), 3)).power(MLP).getVal(); //(3**MLP) mod ML; 
            //SACM.MIEXP( ML, MASSTOR.LFBETA( 3 ), MLP );
            if (RL == 1) {
                FP = IFACT(MLP);
                SL = ISPT(ML, MLP, FP);
                if (SL == 1) {
                    System.out.println("ISPT: FP = " + FP);
                    F.put(ML, 1); // = MASSTOR.COMP( ML, F );
                    //tocheck: F = MASSTOR.INV( F );
                    return F;
                }
            }
            CL = Roots.sqrt(new BigInteger(ML)).getVal().intValue(); //SACI.ISQRT( ML, CL, TL );
            //System.out.println("CL = " + CL + ", ML = " + ML + ", CL^2 = " + (CL*CL));
            BL = Math.max(5000, CL / 3);
            if (AL > BL) {
                PL = 1;
            } else {
                //System.out.println("AL = " + AL + ", BL = " + BL);
                PL = IMPDS(ML, AL, BL); //, PL, ML );
                //System.out.println("PL = " + PL);
                if (PL != 1) {
                    AL = PL;
                    F.put(PL, 1); //F = MASSTOR.COMP( PL, F );
                    ML = ML / PL;
                }
            }
        } while (PL != 1);
        AL = BL;
        BL = CL;
        //ILPDS( ML, AL, BL, PL, ML );
        PL = ILPDS(ML, AL, BL);
        if (PL != 1) {
            F.put(PL, 1);
            ML = ML / PL;
        }
        System.out.println("PL = " + PL + ", ML = " + ML);
        if (ML != 1) {
            F.put(ML, 1);
        }
        return F;
    }


    /**
     * Integer medium prime divisor search. n, a and b are positive integers
     * such that a le b le n and n has no positive divisors less than a. If n
     * has a prime divisor in the closed interval from a to b then p is the
     * least such prime and q=n/p. Otherwise p=1 and q=n.
     */
    public static long IMPDS(long NL, long AL, long BL) {
        List<Long> LP, UZ210;
        long R, J1Y, RL1, RL2, RL, PL;

        RL = AL % 210;
        UZ210 = getUZ210();
        LP = UZ210;
        long ll = LP.size();
        int i = 0;
        while (RL > LP.get(i)) {
            i++; //LP = MASSTOR.RED( LP );
        }
        RL1 = LP.get(i); //MASSTOR.FIRSTi( LP );
        //J1Y = (RL1 - RL);
        PL = AL + (RL1 - RL); //SACI.ISUM( AL, MASSTOR.LFBETA( J1Y ) );
        //System.out.println("PL = " + PL + ", BL = " + BL);
        while (PL <= BL) {
            R = NL % PL; //SACI.IQR( NL, ((jas.maskern.MASSTOR_Cell)PL.val), QL, R );
            if (R == 0) {
                return PL;
            }
            i++; //LP = MASSTOR.RED( LP );
            if (i >= ll) { //LP == MASSTOR.SIL )
                LP = UZ210;
                RL2 = (RL1 - 210);
                i = 0;
            } else {
                RL2 = RL1;
            }
            RL1 = LP.get(i); //MASSTOR.FIRSTi( LP );
            J1Y = (RL1 - RL2);
            PL = PL + J1Y; //SACI.ISUM( ((jas.maskern.MASSTOR_Cell)PL.val), MASSTOR.LFBETA( J1Y ) );
        }
        PL = 1; //SACI.IONE;
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
     */
    public static long ISPT(long ML, long MLP, SortedMap<Long, Integer> F) {
        long AL, BL, QL, QL1, MLPP, PL1, PL, SL;
        List<Long> SMPRM = smallPrimes(2, 500); //SMPRM;
        List<Long> PP;

        List<Map.Entry<Long, Integer>> FP = new ArrayList<Map.Entry<Long, Integer>>(F.entrySet());
        QL1 = 1; //SACI.IONE;
        PL1 = 1;
        int i = 0;
        while (true) {
            do {
                if (i == FP.size()) { //FP == MASSTOR.SIL
                    System.out.println("SL=1: ML = " + ML + ", MLP = " + MLP);
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
                    System.out.println("SL=0: ML = " + ML + ", MLP = " + MLP);
                    SL = 0;
                    return SL;
                }
                PL = PP.get(j); // MASSTOR.FIRSTi( PP );
                j++; //PP = MASSTOR.RED( PP );
                if (PL > PL1) {
                    PL1 = PL;
                    //AL = SACM.MIEXP( ML, MASSTOR.LFBETA( PL ), MLP );
                    AL = (long) (new ModLong(new ModLongRing(ML), PL)).power(MLP).getVal(); //(PL**MLP) mod ML; 
                    if (AL != 1) {
                        System.out.println("SL=-1: ML = " + ML + ", PL = " + PL + ", MLP = " + MLP + ", AL = "
                                        + AL);
                        SL = (-1);
                        return SL;
                    }
                }
                MLPP = MLP / QL; //SACI.IQ( MLP, QL );
                //BL = SACM.MIEXP( ML, MASSTOR.LFBETA( PL ), MLPP );
                BL = (long) (new ModLong(new ModLongRing(ML), PL)).power(MLPP).getVal(); //(PL**MLPP) mod ML; 
            } while (BL == 1);
        }
    }


    /**
     * Integer large prime divisor search. n is a positive integer with no prime
     * divisors less than 17. 1 le a le b le n. A search is made for a divisor p
     * of the integer n, with a le p le b. If such a p is found then np=n/p,
     * otherwise p=1 and np=n. A modular version of fermats method is used, and
     * the search goes from a to b.
     */
    public static long ILPDS(long NL, long AL, long BL) { // return PL, NLP ignored
        long RL, J2Y, XL1, XL2, QL, XL, YL, YLP;
        List<ModLong> L = null;
        List<ModLong> LP;
        long RL1, RL2, J1Y, r, PL, TL;
        long ML = 0;
        long SL = 0;
        //SACI.IQR( NL, BL, QL, RL );
        QL = NL / BL;
        RL = NL % BL;
        XL1 = BL + QL;
        //SACI.IDQR( XL1, 2, XL1, _SL );
        SL = XL1 % 2;
        XL1 = XL1 / 2; // after SL
        if ((RL != 0) || (SL != 0)) {
            XL1 = XL1 + 1;
        }
        QL = NL / AL;
        XL2 = AL + QL;
        XL2 = XL2 / 2;
        L = FRESL(NL); //FRESL( NL, ML, L ); // ML not returned
        if (L.isEmpty()) {
            return NL;
        }
        ML = L.get(0).ring.getModul().intValue(); // sic
        // tocheck sort: L = SACSET.LBIBMS( L ); revert: L = MASSTOR.INV( L );
        Collections.sort(L);
        Collections.reverse(L);
        System.out.println("FRESL: " + L);
        r = XL2 % ML;
        LP = L;
        int i = 0;
        while (i < LP.size() && r < LP.get(i).getSymmetricVal()) {
            i++; //LP = MASSTOR.RED( LP );
        }
        if (i == LP.size()) {
            i = 0; //LP = L;
            SL = ML;
        } else {
            SL = 0;
        }
        RL1 = (long) LP.get(i).getSymmetricVal(); //MASSTOR.FIRSTi( LP );
        i++; //LP = MASSTOR.RED( LP );
        SL = ((SL + r) - RL1);
        XL = XL2 - SL;
        TL = 0;
        while (XL >= XL1) {
            J2Y = XL * XL;
            YLP = J2Y - NL;
            YL = Roots.sqrt(new BigInteger(YLP)).getVal().intValue(); // SACI.ISQRT( YLP, YL, TL );
            TL = YLP - YL * YL;
            if (TL == 0) {
                PL = XL - YL;
                //NLP.val = SACI.ISUM( XL, YL );
                return PL;
            }
            if (i < LP.size()) {
                RL2 = (long) LP.get(i).getSymmetricVal(); //MASSTOR.FIRSTi( LP );
                i++; //LP = MASSTOR.RED( LP );
                SL = (RL1 - RL2);
            } else {
                i = 0;
                RL2 = (long) LP.get(i).getSymmetricVal(); //MASSTOR.FIRSTi( L );
                i++; //LP = MASSTOR.RED( L );
                J1Y = (ML + RL1);
                SL = (J1Y - RL2);
            }
            RL1 = RL2;
            XL = XL - SL;
        }
        PL = 1; //SACI.IONE;
        // unused NLP = NL;
        return PL;
    }


    /**
     * Fermat residue list, single modulus. m is a positive beta-integer. a
     * belongs to Z(m). L is a list of the distinct b in Z(m) such that b**2-a
     * is a square in Z(m).
     */
    public static List<ModLong> FRLSM(long ML, long AL) {
        List<ModLong> Lp;
        SortedSet<ModLong> L;
        List<ModLong> S, SP;
        int MLP;
        ModLong SL, SLP, SLPP;

        ModLongRing ring = new ModLongRing(ML);
        ModLong a = ring.fromInteger(AL);
        MLP = (int) (ML / 2L);
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
            SLP = SL.subtract(a); //SACM.MDDIF( ML, SL, AL );
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
     */
    public static List<ModLong> FRESL(long NL) {
        List<ModLong> L, L1;
        List<Long> H, M;
        long AL1, AL2, AL3, AL4, BL1, HL, J1Y, J2Y, KL, KL1, ML1, ML;
        long BETA = (new BigInteger(2)).power(29).getVal().intValue() - 3;

        // modulus 2**5.
        BL1 = 0;
        AL1 = NL % 32; //SACI.IDREM( NL, 32 );
        AL2 = AL1 % 16; //MASELEM.MASREM( AL1, 16 );
        AL3 = AL2 % 8; //MASELEM.MASREM( AL2, 8 );
        AL4 = AL3 % 4; //MASELEM.MASREM( AL3, 4 );
        if (AL4 == 3) {
            ML = 4;
            if (AL3 == 3) {
                BL1 = 2;
            } else {
                BL1 = 0;
            }
        } else {
            if (AL3 == 1) {
                ML = 8;
                if (AL2 == 1) {
                    BL1 = 1;
                } else {
                    BL1 = 3;
                }
            } else {
                ML = 16;
                switch ((short) (AL1 / 8)) {
                case (short) 0: {
                    BL1 = 3;
                    break;
                }
                case (short) 1: {
                    BL1 = 7;
                    break;
                }
                case (short) 2: {
                    BL1 = 5;
                    break;
                }
                case (short) 3: {
                    BL1 = 1;
                    break;
                }
                }
            }
        }
        L = new ArrayList<ModLong>();
        ModLongRing ring = new ModLongRing(ML);
        ModLongRing ring2;
        if (ML == 4) {
            L.add(ring.fromInteger(BL1)); //.val = MASSTOR.LFBETA( BL1 );
        } else {
            J1Y = ML - BL1;
            L.add(ring.fromInteger(BL1));
            L.add(ring.fromInteger(J1Y));
            // = MASSTOR.COMPi( BL1, MASSTOR.LFBETA( J1Y ) );
        }
        KL = L.size(); //MASSTOR.LENGTH( ((jas.maskern.MASSTOR_Cell)L.val) );

        // modulus 3**3.
        AL1 = NL % 27; //SACI.IDREM( NL, 27 );
        AL2 = AL1 % 3; //MASELEM.MASREM( AL1, 3 );
        if (AL2 == 2) {
            ML1 = 3;
            ring2 = new ModLongRing(ML1);
            KL1 = 1;
            L1 = new ArrayList<ModLong>();
            L1.add(ring2.fromInteger(0)); // = MASSTOR.LFBETA( 0 );
        } else {
            ML1 = 27;
            ring2 = new ModLongRing(ML1);
            KL1 = 4;
            L1 = FRLSM(ML1, AL1);
            // ring2 == L1.get(0).ring
        }
        //L = SACM.MDLCRA( ML, ML1, L, L1 );
        L = ModLongRing.chineseRemainder(ring.getONE(), ring2.getONE(), L, L1);
        ML = (ML * ML1);
        ring = new ModLongRing(ML); // == L.get(0).ring
        KL = (KL * KL1);

        // modulus 5**2.
        AL1 = NL % 25; //SACI.IDREM( NL, 25 );
        AL2 = AL1 % 5; //MASELEM.MASREM( AL1, 5 );
        if ((AL2 == 2) || (AL2 == 3)) {
            ML1 = 5;
            ring2 = new ModLongRing(ML1);
            J1Y = (AL2 - 1);
            J2Y = (6 - AL2);
            L1 = new ArrayList<ModLong>();
            L1.add(ring2.fromInteger(J1Y));
            L1.add(ring2.fromInteger(J2Y));
            //L1 = MASSTOR.COMPi( J1Y, MASSTOR.LFBETA( J2Y ) );
            KL1 = 2;
        } else {
            ML1 = 25;
            ring2 = new ModLongRing(ML1);
            L1 = FRLSM(ML1, AL1);
            KL1 = 7;
        }
        if (ML1 >= BETA / ML) {
            return L;
        }
        //L = SACM.MDLCRA( ML, ML1, L, L1 );
        L = ModLongRing.chineseRemainder(ring.getONE(), ring2.getONE(), L, L1);
        ML = (ML * ML1);
        ring = new ModLongRing(ML);
        KL = (KL * KL1);

        // moduli 7,11,13.
        L1 = new ArrayList<ModLong>();
        M = new ArrayList<Long>(3);
        H = new ArrayList<Long>(3);
        //M = MASSTOR.COMPi( 7, MASSTOR.COMPi( 11, MASSTOR.LFBETA( 13 ) ) );
        M.add(7L);
        M.add(11L);
        M.add(13L);
        //H = MASSTOR.COMPi( 64, MASSTOR.COMPi( 48, MASSTOR.LFBETA( 0 ) ) );
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
            AL1 = NL % ML1; //SACI.IDREM( NL, ML1 );
            L1 = FRLSM(ML1, AL1);
            KL1 = L1.size(); //MASSTOR.LENGTH( L1 );
            //L = SACM.MDLCRA( ML, ML1, L, L1 );
            L = ModLongRing.chineseRemainder(ring.getONE(), ring2.getONE(), L, L1);
            ML = (ML * ML1);
            ring = new ModLongRing(ML);
            KL = (KL * KL1);
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
