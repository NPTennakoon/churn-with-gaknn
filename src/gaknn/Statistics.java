/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gaknn;

import gaknn.core.Attribute;
import gaknn.core.FastVector;

import java.math.*;
import java.util.*;

/**
 * @author admin
 */
public class Statistics {

    protected static final double LOGPI = 1.14472988584940017414;

    public static double Mean(double[][] attribute, int k) {
        double mean = 0.0;
        double sum = 0;
        for (int i = 0; i < attribute.length; i++) {
            //  for (int i = 0; i < ; i++) {
            sum += attribute[i][k];
//            System.out.println("att"+ attribute[i][k]);
        }
//        System.out.println("att sum"+sum);
//        System.out.println("att len"+attribute.length);

        mean = sum / attribute.length;
        return mean;
    }

    /**
     * Returns the variance of the array of double.
     */
    public static double Variance(double[][] attribute, int k) {
        double mu = Mean(attribute, k);
        double sumsq = 0.0;
        for (int i = 0; i < attribute.length; i++) {
            sumsq += (mu - attribute[i][k]) * (mu - attribute[i][k]);
        }
        return sumsq / (attribute.length - 1);
    }

    public static double STD(double[][] attribute, int k) {
        double STD = Math.sqrt(Variance(attribute, k));

        return STD;
    }

    public static double Median(double[] attribute) {
        double median = 0.0;
        Arrays.sort(attribute);
        int middle = attribute.length / 2;
        if (attribute.length % 2 == 1) {
            median = attribute[middle];
        } else {
            median = (attribute[middle - 1] + attribute[middle]) / 2.0;
        }
        return median;
    }

    public static double Count(double[][] attribute, int k, double value) {
        double count = 0.0;
        for (int i = 0; i < attribute.length; i++) {

            if (value == attribute[i][k]) {
                count = count + 1;
            }
//            System.out.println("att"+ attribute[i][k]);
        }
//        System.out.println("att sum"+sum);
//        System.out.println("att len"+attribute.length);
//        System.out.println("count bef "+k+" "+count);

//        System.out.println("count "+k+" "+count);
        if (count > 0) {
            count = count / attribute.length;
        } else {
            count = count + 1;
            int length = attribute.length;
            length = attribute.length + 1;
            count = count / length;
        }

        return count;
    }

    /**
     * Returns natural logarithm of gamma function.
     *
     * @param x the value
     * @return natural logarithm of gamma function
     */
    public static double lnGamma(double x) {

        double p, q, w, z;

        double A[] = {8.11614167470508450300E-4, -5.95061904284301438324E-4,
            7.93650340457716943945E-4, -2.77777777730099687205E-3,
            8.33333333333331927722E-2};
        double B[] = {-1.37825152569120859100E3, -3.88016315134637840924E4,
            -3.31612992738871184744E5, -1.16237097492762307383E6,
            -1.72173700820839662146E6, -8.53555664245765465627E5};
        double C[] = {
            /* 1.00000000000000000000E0, */
            -3.51815701436523470549E2, -1.70642106651881159223E4,
            -2.20528590553854454839E5, -1.13933444367982507207E6,
            -2.53252307177582951285E6, -2.01889141433532773231E6};

        if (x < -34.0) {
            q = -x;
            w = lnGamma(q);
            p = Math.floor(q);
            if (p == q) {
                throw new ArithmeticException("lnGamma: Overflow");
            }
            z = q - p;
            if (z > 0.5) {
                p += 1.0;
                z = p - q;
            }
            z = q * Math.sin(Math.PI * z);
            if (z == 0.0) {
                throw new ArithmeticException("lnGamma: Overflow");
            }
            z = LOGPI - Math.log(z) - w;
            return z;
        }

        if (x < 13.0) {
            z = 1.0;
            while (x >= 3.0) {
                x -= 1.0;
                z *= x;
            }
            while (x < 2.0) {
                if (x == 0.0) {
                    throw new ArithmeticException("lnGamma: Overflow");
                }
                z /= x;
                x += 1.0;
            }
            if (z < 0.0) {
                z = -z;
            }
            if (x == 2.0) {
                return Math.log(z);
            }
            x -= 2.0;
            p = x * polevl(x, B, 5) / p1evl(x, C, 6);
            return (Math.log(z) + p);
        }

        if (x > 2.556348e305) {
            throw new ArithmeticException("lnGamma: Overflow");
        }

        q = (x - 0.5) * Math.log(x) - x + 0.91893853320467274178;

        if (x > 1.0e8) {
            return (q);
        }

        p = 1.0 / (x * x);
        if (x >= 1000.0) {
            q += ((7.9365079365079365079365e-4 * p - 2.7777777777777777777778e-3) * p + 0.0833333333333333333333)
                    / x;
        } else {
            q += polevl(p, A, 4) / x;
        }
        return q;
    }

    /**
     * Evaluates the given polynomial of degree <tt>N</tt> at <tt>x</tt>.
     *
     * <pre>
     *                     2          N
     * y  =  C  + C x + C x  +...+ C x
     *        0    1     2          N
     *
     * Coefficients are stored in reverse order:
     *
     * coef[0] = C  , ..., coef[N] = C  .
     *            N                   0
     * </pre>
     *
     * In the interest of speed, there are no checks for out of bounds
     * arithmetic.
     *
     * @param x argument to the polynomial.
     * @param coef the coefficients of the polynomial.
     * @param N the degree of the polynomial.
     */
    public static double polevl(double x, double coef[], int N) {

        double ans;
        ans = coef[0];

        for (int i = 1; i <= N; i++) {
            ans = ans * x + coef[i];
        }

        return ans;
    }

    /**
     * Evaluates the given polynomial of degree <tt>N</tt> at <tt>x</tt>.
     * Evaluates polynomial when coefficient of N is 1.0. Otherwise same as
     * <tt>polevl()</tt>.
     *
     * <pre>
     *                     2          N
     * y  =  C  + C x + C x  +...+ C x
     *        0    1     2          N
     *
     * Coefficients are stored in reverse order:
     *
     * coef[0] = C  , ..., coef[N] = C  .
     *            N                   0
     * </pre>
     *
     * The function <tt>p1evl()</tt> assumes that <tt>coef[N] = 1.0</tt> and is
     * omitted from the array. Its calling arguments are otherwise the same as
     * <tt>polevl()</tt>.
     * <p>
     * In the interest of speed, there are no checks for out of bounds
     * arithmetic.
     *
     * @param x argument to the polynomial.
     * @param coef the coefficients of the polynomial.
     * @param N the degree of the polynomial.
     */
    public static double p1evl(double x, double coef[], int N) {

        double ans;
        ans = x + coef[0];

        for (int i = 1; i < N; i++) {
            ans = ans * x + coef[i];
        }

        return ans;
    }

    /*
     public static List<Integer> mode(final int[] a) {
     final List<Integer> modes = new ArrayList<Integer>();
     final Map<Integer, Integer> countMap = new HashMap<Integer, Integer>();

     int max = -1;

     for (final int n : numbers) {
     int count = 0;

     if (countMap.containsKey(n)) {
     count = countMap.get(n) + 1;
     } else {
     count = 1;
     }

     countMap.put(n, count);

     if (count > max) {
     max = count;
     }
     }

     for (final Map.Entry<Integer, Integer> tuple : countMap.entrySet()) {
     if (tuple.getValue() == max) {
     modes.add(tuple.getKey());
     }
     }

     return modes;
     }
     */
    public static void main(String[] args) {
        double[][] aa = {{2.0, 33.0, 44.9, 44.9, 8.0},
        {12.0, 133.0, 144.9, 144.9, 18.0},
        {21.0, 331.0, 441.9, 441.9, 81.0}};

        // System.out.println(aa[2]);
        double d = Mean(aa, 2);
        System.out.println(d);

//        double e = Median(aa);
//        System.out.println(e);
        double f = STD(aa, 2);
        System.out.println(f);

    }
}
