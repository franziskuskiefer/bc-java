package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;

import org.bouncycastle.crypto.util.Pack;

public abstract class Nat192
{
    private static final long M = 0xFFFFFFFFL;

    public static int add(int[] x, int[] y, int[] z)
    {
        long c = 0;
        c += (x[0] & M) + (y[0] & M);
        z[0] = (int)c;
        c >>>= 32;
        c += (x[1] & M) + (y[1] & M);
        z[1] = (int)c;
        c >>>= 32;
        c += (x[2] & M) + (y[2] & M);
        z[2] = (int)c;
        c >>>= 32;
        c += (x[3] & M) + (y[3] & M);
        z[3] = (int)c;
        c >>>= 32;
        c += (x[4] & M) + (y[4] & M);
        z[4] = (int)c;
        c >>>= 32;
        c += (x[5] & M) + (y[5] & M);
        z[5] = (int)c;
        c >>>= 32;
        return (int)c;
    }

    public static int addBothTo(int[] x, int[] y, int[] z)
    {
        long c = 0;
        c += (x[0] & M) + (y[0] & M) + (z[0] & M);
        z[0] = (int)c;
        c >>>= 32;
        c += (x[1] & M) + (y[1] & M) + (z[1] & M);
        z[1] = (int)c;
        c >>>= 32;
        c += (x[2] & M) + (y[2] & M) + (z[2] & M);
        z[2] = (int)c;
        c >>>= 32;
        c += (x[3] & M) + (y[3] & M) + (z[3] & M);
        z[3] = (int)c;
        c >>>= 32;
        c += (x[4] & M) + (y[4] & M) + (z[4] & M);
        z[4] = (int)c;
        c >>>= 32;
        c += (x[5] & M) + (y[5] & M) + (z[5] & M);
        z[5] = (int)c;
        c >>>= 32;
        return (int)c;
    }

    // TODO Re-write to allow full range for x?
    public static int addDWord(long x, int[] z, int zOff)
    {
        // assert zOff <= 4;
        long c = x;
        c += (z[zOff + 0] & M);
        z[zOff + 0] = (int)c;
        c >>>= 32;
        c += (z[zOff + 1] & M);
        z[zOff + 1] = (int)c;
        c >>>= 32;
        return c == 0 ? 0 : inc(z, zOff + 2);
    }

    public static int addExt(int[] xx, int[] yy, int[] zz)
    {
        long c = 0;
        for (int i = 0; i < 12; ++i)
        {
            c += (xx[i] & M) + (yy[i] & M);
            zz[i] = (int)c;
            c >>>= 32;
        }
        return (int)c;
    }

    public static int addToExt(int[] x, int xOff, int[] zz, int zzOff)
    {
        // assert zzOff <= 6;
        long c = 0;
        c += (x[xOff + 0] & M) + (zz[zzOff + 0] & M);
        zz[zzOff + 0] = (int)c;
        c >>>= 32;
        c += (x[xOff + 1] & M) + (zz[zzOff + 1] & M);
        zz[zzOff + 1] = (int)c;
        c >>>= 32;
        c += (x[xOff + 2] & M) + (zz[zzOff + 2] & M);
        zz[zzOff + 2] = (int)c;
        c >>>= 32;
        c += (x[xOff + 3] & M) + (zz[zzOff + 3] & M);
        zz[zzOff + 3] = (int)c;
        c >>>= 32;
        c += (x[xOff + 4] & M) + (zz[zzOff + 4] & M);
        zz[zzOff + 4] = (int)c;
        c >>>= 32;
        c += (x[xOff + 5] & M) + (zz[zzOff + 5] & M);
        zz[zzOff + 5] = (int)c;
        c >>>= 32;
        return (int)c;
    }

    public static int addWordExt(int x, int[] zz, int zzOff)
    {
        // assert zzOff <= 11;
        long c = (x & M) + (zz[zzOff + 0] & M);
        zz[zzOff + 0] = (int)c;
        c >>>= 32;
        return c == 0 ? 0 : incExt(zz, zzOff + 1);
    }

    public static int[] create()
    {
        return new int[6];
    }

    public static int[] createExt()
    {
        return new int[12];
    }

    public static int dec(int[] z, int zOff)
    {
        // assert zOff <= 6;
        for (int i = zOff; i < 6; ++i)
        {
            if (--z[i] != -1)
            {
                return 0;
            }
        }
        return -1;
    }

    public static int[] fromBigInteger(BigInteger x)
    {
        if (x.signum() < 0 || x.bitLength() > 192)
        {
            throw new IllegalArgumentException();
        }

        int[] z = create();
        int i = 0;
        while (x.signum() != 0)
        {
            z[i++] = x.intValue();
            x = x.shiftRight(32);
        }
        return z;
    }

    public static int getBit(int[] x, int bit)
    {
        if (bit == 0)
        {
            return x[0] & 1;
        }
        int w = bit >> 5;
        if (w < 0 || w >= 6)
        {
            return 0;
        }
        int b = bit & 31;
        return (x[w] >>> b) & 1;
    }

    public static boolean gte(int[] x, int[] y)
    {
        for (int i = 5; i >= 0; --i)
        {
            int x_i = x[i] ^ Integer.MIN_VALUE;
            int y_i = y[i] ^ Integer.MIN_VALUE;
            if (x_i < y_i)
                return false;
            if (x_i > y_i)
                return true;
        }
        return true;
    }

    public static boolean gteExt(int[] xx, int[] yy)
    {
        for (int i = 11; i >= 0; --i)
        {
            int xx_i = xx[i] ^ Integer.MIN_VALUE;
            int yy_i = yy[i] ^ Integer.MIN_VALUE;
            if (xx_i < yy_i)
                return false;
            if (xx_i > yy_i)
                return true;
        }
        return true;
    }

    public static int inc(int[] z, int zOff)
    {
        // assert zOff <= 6;
        for (int i = zOff; i < 6; ++i)
        {
            if (++z[i] != 0)
            {
                return 0;
            }
        }
        return 1;
    }

    public static int incExt(int[] zz, int zzOff)
    {
        // assert zzOff <= 12;
        for (int i = zzOff; i < 12; ++i)
        {
            if (++zz[i] != 0)
            {
                return 0;
            }
        }
        return 1;
    }

    public static boolean isOne(int[] x)
    {
        if (x[0] != 1)
        {
            return false;
        }
        for (int i = 1; i < 6; ++i)
        {
            if (x[i] != 0)
            {
                return false;
            }
        }
        return true;
    }

    public static boolean isZero(int[] x)
    {
        for (int i = 0; i < 6; ++i)
        {
            if (x[i] != 0)
            {
                return false;
            }
        }
        return true;
    }

    public static boolean isZeroExt(int[] xx)
    {
        for (int i = 0; i < 12; ++i)
        {
            if (xx[i] != 0)
            {
                return false;
            }
        }
        return true;
    }

    public static void mul(int[] x, int[] y, int[] zz)
    {
        long y_0 = y[0] & M;
        long y_1 = y[1] & M;
        long y_2 = y[2] & M;
        long y_3 = y[3] & M;
        long y_4 = y[4] & M;
        long y_5 = y[5] & M;

        {
            long c = 0, x_0 = x[0] & M;
            c += x_0 * y_0;
            zz[0] = (int)c;
            c >>>= 32;
            c += x_0 * y_1;
            zz[1] = (int)c;
            c >>>= 32;
            c += x_0 * y_2;
            zz[2] = (int)c;
            c >>>= 32;
            c += x_0 * y_3;
            zz[3] = (int)c;
            c >>>= 32;
            c += x_0 * y_4;
            zz[4] = (int)c;
            c >>>= 32;
            c += x_0 * y_5;
            zz[5] = (int)c;
            c >>>= 32;
            zz[6] = (int)c;
        }

        for (int i = 1; i < 6; ++i)
        {
            long c = 0, x_i = x[i] & M;
            c += x_i * y_0 + (zz[i + 0] & M);
            zz[i + 0] = (int)c;
            c >>>= 32;
            c += x_i * y_1 + (zz[i + 1] & M);
            zz[i + 1] = (int)c;
            c >>>= 32;
            c += x_i * y_2 + (zz[i + 2] & M);
            zz[i + 2] = (int)c;
            c >>>= 32;
            c += x_i * y_3 + (zz[i + 3] & M);
            zz[i + 3] = (int)c;
            c >>>= 32;
            c += x_i * y_4 + (zz[i + 4] & M);
            zz[i + 4] = (int)c;
            c >>>= 32;
            c += x_i * y_5 + (zz[i + 5] & M);
            zz[i + 5] = (int)c;
            c >>>= 32;
            zz[i + 6] = (int)c;
        }
    }

    public static long mul33AddExt(int w, int[] xx, int xxOff, int[] yy, int yyOff, int[] zz, int zzOff)
    {
        // assert x >>> 31 == 0;
        // assert xxOff <= 6;
        // assert yyOff <= 6;
        // assert zzOff <= 6;

        long c = 0, wVal = w & M;
        long xx00 = xx[xxOff + 0] & M;
        c += wVal * xx00 + (yy[yyOff + 0] & M);
        zz[zzOff + 0] = (int)c;
        c >>>= 32;
        long xx01 = xx[xxOff + 1] & M;
        c += wVal * xx01 + xx00 + (yy[yyOff + 1] & M);
        zz[zzOff + 1] = (int)c;
        c >>>= 32;
        long xx02 = xx[xxOff + 2] & M;
        c += wVal * xx02 + xx01 + (yy[yyOff + 2] & M);
        zz[zzOff + 2] = (int)c;
        c >>>= 32;
        long xx03 = xx[xxOff + 3] & M;
        c += wVal * xx03 + xx02 + (yy[yyOff + 3] & M);
        zz[zzOff + 3] = (int)c;
        c >>>= 32;
        long xx04 = xx[xxOff + 4] & M;
        c += wVal * xx04 + xx03 + (yy[yyOff + 4] & M);
        zz[zzOff + 4] = (int)c;
        c >>>= 32;
        long xx05 = xx[xxOff + 5] & M;
        c += wVal * xx05 + xx04 + (yy[yyOff + 5] & M);
        zz[zzOff + 5] = (int)c;
        c >>>= 32;
        c += xx05;
        return c;
    }

    public static int mulWordAddExt(int x, int[] yy, int yyOff, int[] zz, int zzOff)
    {
        // assert yyOff <= 6;
        // assert zzOff <= 6;
        long c = 0, xVal = x & M;
        c += xVal * (yy[yyOff + 0] & M) + (zz[zzOff + 0] & M);
        zz[zzOff + 0] = (int)c;
        c >>>= 32;
        c += xVal * (yy[yyOff + 1] & M) + (zz[zzOff + 1] & M);
        zz[zzOff + 1] = (int)c;
        c >>>= 32;
        c += xVal * (yy[yyOff + 2] & M) + (zz[zzOff + 2] & M);
        zz[zzOff + 2] = (int)c;
        c >>>= 32;
        c += xVal * (yy[yyOff + 3] & M) + (zz[zzOff + 3] & M);
        zz[zzOff + 3] = (int)c;
        c >>>= 32;
        c += xVal * (yy[yyOff + 4] & M) + (zz[zzOff + 4] & M);
        zz[zzOff + 4] = (int)c;
        c >>>= 32;
        c += xVal * (yy[yyOff + 5] & M) + (zz[zzOff + 5] & M);
        zz[zzOff + 5] = (int)c;
        c >>>= 32;
        return (int)c;
    }

    public static int mul33DWordAdd(int x, long y, int[] z, int zOff)
    {
        // assert x >>> 31 == 0;
        // assert zOff <= 2;

        long c = 0, xVal = x & M;
        long y00 = y & M;
        c += xVal * y00 + (z[zOff + 0] & M);
        z[zOff + 0] = (int)c;
        c >>>= 32;
        long y01 = y >>> 32;
        c += xVal * y01 + y00 + (z[zOff + 1] & M);
        z[zOff + 1] = (int)c;
        c >>>= 32;
        c += y01 + (z[zOff + 2] & M);
        z[zOff + 2] = (int)c;
        c >>>= 32;
        c += (z[zOff + 3] & M);
        z[zOff + 3] = (int)c;
        c >>>= 32;
        return c == 0 ? 0 : inc(z, zOff + 4);
    }

    public static int mulWordDwordAdd(int x, long y, int[] z, int zOff)
    {
        // assert zOff <= 3;
        long c = 0, xVal = x & M;
        c += xVal * (y & M) + (z[zOff + 0] & M);
        z[zOff + 0] = (int)c;
        c >>>= 32;
        c += xVal * (y >>> 32) + (z[zOff + 1] & M);
        z[zOff + 1] = (int)c;
        c >>>= 32;
        c += (z[zOff + 2] & M);
        z[zOff + 2] = (int)c;
        c >>>= 32;
        return c == 0 ? 0 : inc(z, zOff + 3);
    }

    public static int mulWordExt(int x, int[] y, int[] zz, int zzOff)
    {
        // assert zzOff <= 6;
        long c = 0, xVal = x & M;
        int i = 0;
        do
        {
            c += xVal * (y[i] & M);
            zz[zzOff + i] = (int)c;
            c >>>= 32;
        }
        while (++i < 6);
        return (int)c;
    }

    public static int shiftDownBit(int[] x, int xLen, int c)
    {
        int i = xLen;
        while (--i >= 0)
        {
            int next = x[i];
            x[i] = (next >>> 1) | (c << 31);
            c = next;
        }
        return c << 31;
    }

    public static int shiftDownBit(int[] x, int c, int[] z)
    {
        int i = 6;
        while (--i >= 0)
        {
            int next = x[i];
            z[i] = (next >>> 1) | (c << 31);
            c = next;
        }
        return c << 31;
    }

    public static int shiftDownBits(int[] x, int xLen, int bits, int c)
    {
//        assert bits > 0 && bits < 32;
        int i = xLen;
        while (--i >= 0)
        {
            int next = x[i];
            x[i] = (next >>> bits) | (c << -bits);
            c = next;
        }
        return c << -bits;
    }

    public static int shiftDownWord(int[] x, int xLen, int c)
    {
        int i = xLen;
        while (--i >= 0)
        {
            int next = x[i];
            x[i] = c;
            c = next;
        }
        return c;
    }

    public static int shiftUpBit(int[] x, int xLen, int c)
    {
        for (int i = 0; i < xLen; ++i)
        {
            int next = x[i];
            x[i] = (next << 1) | (c >>> 31);
            c = next;
        }
        return c >>> 31;
    }

    public static int shiftUpBit(int[] x, int c, int[] z)
    {
        for (int i = 0; i < 6; ++i)
        {
            int next = x[i];
            z[i] = (next << 1) | (c >>> 31);
            c = next;
        }
        return c >>> 31;
    }

    public static void square(int[] x, int[] zz)
    {
        long x_0 = x[0] & M;
        long zz_1;

        {
            int c = 0, i = 5, j = 12;
            do
            {
                long xVal = (x[i--] & M);
                long p = xVal * xVal;
                zz[--j] = (c << 31) | (int)(p >>> 33);
                zz[--j] = (int)(p >>> 1);
                c = (int)p;
            }
            while (i > 0);

            {
                long p = x_0 * x_0;
                zz_1 = ((c << 31) & M) | (p >>> 33);
                zz[0] = (int)(p >>> 1);
            }
        }

        long x_1 = x[1] & M;
        long zz_2 = zz[2] & M;

        {
            zz_1 += x_1 * x_0;
            zz[1] = (int)zz_1;
            zz_2 += zz_1 >>> 32;
        }

        long x_2 = x[2] & M;
        long zz_3 = zz[3] & M;
        long zz_4 = zz[4] & M;
        {
            zz_2 += x_2 * x_0;
            zz[2] = (int)zz_2;
            zz_3 += (zz_2 >>> 32) + x_2 * x_1;
            zz_4 += zz_3 >>> 32;
            zz_3 &= M;
        }

        long x_3 = x[3] & M;
        long zz_5 = zz[5] & M;
        long zz_6 = zz[6] & M;
        {
            zz_3 += x_3 * x_0;
            zz[3] = (int)zz_3;
            zz_4 += (zz_3 >>> 32) + x_3 * x_1;
            zz_5 += (zz_4 >>> 32) + x_3 * x_2;
            zz_4 &= M;
            zz_6 += zz_5 >>> 32;
            zz_5 &= M;
        }

        long x_4 = x[4] & M;
        long zz_7 = zz[7] & M;
        long zz_8 = zz[8] & M;
        {
            zz_4 += x_4 * x_0;
            zz[4] = (int)zz_4;
            zz_5 += (zz_4 >>> 32) + x_4 * x_1;
            zz_6 += (zz_5 >>> 32) + x_4 * x_2;
            zz_5 &= M;
            zz_7 += (zz_6 >>> 32) + x_4 * x_3;
            zz_6 &= M;
            zz_8 += zz_7 >>> 32;
            zz_7 &= M;
        }

        long x_5 = x[5] & M;
        long zz_9 = zz[9] & M;
        long zz_10 = zz[10] & M;
        {
            zz_5 += x_5 * x_0;
            zz[5] = (int)zz_5;
            zz_6 += (zz_5 >>> 32) + x_5 * x_1;
            zz_7 += (zz_6 >>> 32) + x_5 * x_2;
            zz_8 += (zz_7 >>> 32) + x_5 * x_3;
            zz_9 += (zz_8 >>> 32) + x_5 * x_4;
            zz_10 += zz_9 >>> 32;
        }

        zz[6] = (int)zz_6;
        zz[7] = (int)zz_7;
        zz[8] = (int)zz_8;
        zz[9] = (int)zz_9;
        zz[10] = (int)zz_10;
        zz[11] += (int)(zz_10 >>> 32);

        shiftUpBit(zz, 12, (int)x_0 << 31);
    }

    public static int squareWordAddExt(int[] x, int xPos, int[] zz)
    {
        // assert xPos > 0 && xPos < 6;
        long c = 0, xVal = x[xPos] & M;
        int i = 0;
        do
        {
            c += xVal * (x[i] & M) + (zz[xPos + i] & M);
            zz[xPos + i] = (int)c;
            c >>>= 32;
        }
        while (++i < xPos);
        return (int)c;
    }

    public static int sub(int[] x, int[] y, int[] z)
    {
        long c = 0;
        c += (x[0] & M) - (y[0] & M);
        z[0] = (int)c;
        c >>= 32;
        c += (x[1] & M) - (y[1] & M);
        z[1] = (int)c;
        c >>= 32;
        c += (x[2] & M) - (y[2] & M);
        z[2] = (int)c;
        c >>= 32;
        c += (x[3] & M) - (y[3] & M);
        z[3] = (int)c;
        c >>= 32;
        c += (x[4] & M) - (y[4] & M);
        z[4] = (int)c;
        c >>= 32;
        c += (x[5] & M) - (y[5] & M);
        z[5] = (int)c;
        c >>= 32;
        return (int)c;
    }

    public static int subBothFrom(int[] x, int[] y, int[] z)
    {
        long c = 0;
        c += (z[0] & M) - (x[0] & M) - (y[0] & M);
        z[0] = (int)c;
        c >>= 32;
        c += (z[1] & M) - (x[1] & M) - (y[1] & M);
        z[1] = (int)c;
        c >>= 32;
        c += (z[2] & M) - (x[2] & M) - (y[2] & M);
        z[2] = (int)c;
        c >>= 32;
        c += (z[3] & M) - (x[3] & M) - (y[3] & M);
        z[3] = (int)c;
        c >>= 32;
        c += (z[4] & M) - (x[4] & M) - (y[4] & M);
        z[4] = (int)c;
        c >>= 32;
        c += (z[5] & M) - (x[5] & M) - (y[5] & M);
        z[5] = (int)c;
        c >>= 32;
        return (int)c;
    }

    // TODO Re-write to allow full range for x?
    public static int subDWord(long x, int[] z)
    {
        long c = -x;
        c += (z[0] & M);
        z[0] = (int)c;
        c >>= 32;
        c += (z[1] & M);
        z[1] = (int)c;
        c >>= 32;
        return c == 0 ? 0 : dec(z, 2);
    }

    public static int subExt(int[] xx, int[] yy, int[] zz)
    {
        long c = 0;
        for (int i = 0; i < 12; ++i)
        {
            c += (xx[i] & M) - (yy[i] & M);
            zz[i] = (int)c;
            c >>= 32;
        }
        return (int)c;
    }

    public static int subFromExt(int[] x, int xOff, int[] zz, int zzOff)
    {
        // assert zzOff <= 6;
        long c = 0;
        c += (zz[zzOff + 0] & M) - (x[xOff + 0] & M);
        zz[zzOff + 0] = (int)c;
        c >>= 32;
        c += (zz[zzOff + 1] & M) - (x[xOff + 1] & M);
        zz[zzOff + 1] = (int)c;
        c >>= 32;
        c += (zz[zzOff + 2] & M) - (x[xOff + 2] & M);
        zz[zzOff + 2] = (int)c;
        c >>= 32;
        c += (zz[zzOff + 3] & M) - (x[xOff + 3] & M);
        zz[zzOff + 3] = (int)c;
        c >>= 32;
        c += (zz[zzOff + 4] & M) - (x[xOff + 4] & M);
        zz[zzOff + 4] = (int)c;
        c >>= 32;
        c += (zz[zzOff + 5] & M) - (x[xOff + 5] & M);
        zz[zzOff + 5] = (int)c;
        c >>= 32;
        return (int)c;
    }

    public static BigInteger toBigInteger(int[] x)
    {
        byte[] bs = new byte[24];
        for (int i = 0; i < 6; ++i)
        {
            int x_i = x[i];
            if (x_i != 0)
            {
                Pack.intToBigEndian(x_i, bs, (5 - i) << 2);
            }
        }
        return new BigInteger(1, bs);
    }

    public static void zero(int[] z)
    {
        z[0] = 0;
        z[1] = 0;
        z[2] = 0;
        z[3] = 0;
        z[4] = 0;
        z[5] = 0;
    }
}
