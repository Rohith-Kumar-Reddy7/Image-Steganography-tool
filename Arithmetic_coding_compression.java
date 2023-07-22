import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.*;
import java.io.*;


public class Arithmetic_coding_compression {
    public static int[] rep_arr (String s) {       //To count the number of times the characters repeats itself in a String. (Just returns rep array)
        int[] rep_arr;          rep_arr = new int[s.length()];      int rep_count = 0;
        for (int j = 0; j < s.length(); j++) {
            for(int k = 0; k < s.length(); k++) {
                if(s.charAt(j)==s.charAt(k))
                {
                    rep_count++;
                }
            }
            rep_arr[j] = rep_count;
            rep_count = 0;
        }
        return rep_arr;
    }

    public static char[] UniqueCharString(String str) {       //To return UniqueCharacterString.
        char[] s = str.toCharArray();   int k, q = 1;
        for(int i = 1; i < str.length(); i++) {
            k = 0;
            for(int j = 0; j < i; j++) {
                if(s[i] == s[j]) {
                    k = 1; break;
                }
            }
            if ( k == 0 ) {
                s[q] = s[i];
                q++;
            }
        }
        char[] uniqueCharOnly;      uniqueCharOnly = new char[q];
        System.arraycopy(s, 0, uniqueCharOnly, 0, q);
        return uniqueCharOnly;
    }

    public static int[] UniqueCharRepArray(String str, int[] arr) {       //To return rep array of UniqueCharacterString.
        char[] s = str.toCharArray();   int k, q = 1;
        for(int i = 1; i < str.length(); i++) {
            k = 0;
            for(int j = 0; j < i; j++) {
                if(s[i] == s[j]) {
                    k = 1; break;
                }
            }
            if ( k == 0 ) {
                arr[q] = arr[i];
                q++;
            }
        }
        int[] uniqueCharRepArray;      uniqueCharRepArray = new int[q];
        System.arraycopy(arr, 0, uniqueCharRepArray, 0, q);
        return uniqueCharRepArray;
    }

    public static BigDecimal[] FreqArray(int[] arr) {
        int sum = 0;    BigDecimal[] freqArray;    freqArray = new BigDecimal[arr.length];
        for (int j : arr)
            sum += j;
        for (int j = 0; j < arr.length; j++)
            freqArray[j] = BigDecimal.valueOf((double)arr[j]/(double)sum);
        return freqArray;
    }

    public static BigDecimal[] IniFractionArr(BigDecimal[] arr) {
        for(int i = 1; i < arr.length; i++) {
            arr[i] = arr[i].add(arr[i-1]);
        }
        return arr;
    }

    public static BigDecimal[] LineFractionArr(BigDecimal[] arr, BigDecimal stValue, BigDecimal endValue) {
        BigDecimal lineLength;      BigDecimal[] newArr = new BigDecimal[arr.length];
        lineLength = endValue.subtract(stValue);
        for(int i = 0; i < arr.length; i++) {
            newArr[i] = stValue.add(arr[i].multiply(lineLength));
        }
        return newArr;
    }

    public static BigDecimal FinalOptimisedFloatingPoint(int n, BigDecimal a, BigDecimal b) {
        int i;
        for (i = 1; i <= n; i++) {
            if (!a.setScale(i, RoundingMode.FLOOR).equals(b.setScale(i, RoundingMode.FLOOR))) {
                a = a.setScale(i+1, RoundingMode.FLOOR);
                b = b.setScale(i+1, RoundingMode.FLOOR);
                BigDecimal c = a.add(b);
                c = c.divide(BigDecimal.valueOf(2));
                c = c.setScale(i+1, RoundingMode.FLOOR);
                return c;
            }
        }
        return a;
    }

    public static String ArithCodingCompress(String str, char[] u, BigDecimal[] lFArr) {
        BigDecimal stValue = new BigDecimal(0);             BigDecimal endValue = new BigDecimal(1);
        char[] s = str.toCharArray();                           BigDecimal[] lFArr1 = lFArr;
        for (char value : s) {
            for (int j = 0; j < u.length; j++) {
                if (u[j] == value) {
                    if (j == 0) {
                        endValue = lFArr[j];
                    } else if (j == u.length - 1) {
                        stValue = lFArr[j - 1];
                    } else {
                        stValue = lFArr[j - 1];
                        endValue = lFArr[j];
                    }
                    lFArr = LineFractionArr(lFArr1, stValue, endValue);
                }
            }
        }
        stValue = stValue.stripTrailingZeros();     endValue = endValue.stripTrailingZeros();       //Removing trailing zeroes for the start and end values.
//        System.out.println("The Start value and end value are: " + stValue + " " + endValue);       //Printing Start and End values.
        int n;  n = Math.min(endValue.precision(), stValue.precision());        //Assigning the minimum of (precision of start and end value) to a variable n. And printing n.
        BigDecimal c = FinalOptimisedFloatingPoint(n, stValue, endValue);
//        System.out.println(c);
        return c.toString();
    }

    public static String compress(String str) throws DataFormatException,UnsupportedEncodingException{
        String str1, str2, str3;     int[] rep_arr, uniqueCharRepArray;      char[] uniqueCharString;    BigDecimal[] freqArr, lineFractionArr;

        Deflater def = new Deflater();
        rep_arr = rep_arr(str);

        uniqueCharString = UniqueCharString(str);
        str1 = new String(uniqueCharString);

        uniqueCharRepArray = UniqueCharRepArray(str, rep_arr);

        str2 = "";
        for(int element : uniqueCharRepArray) {
            str2 = str2.concat(element + " ");
        }

        freqArr = FreqArray(uniqueCharRepArray);

        lineFractionArr = IniFractionArr(freqArr);

        str3 = ArithCodingCompress(str,uniqueCharString,lineFractionArr);

        String f_str = str1 + ",," + str2 + ",," + str3;

        def.setInput(f_str.getBytes("UTF-8"));
        def.finish();

        byte[] compString = new byte[f_str.length()*2];
        int compSize = def.deflate(compString,0,f_str.length());
//        System.out.println(compSize);
        compString = Arrays.copyOfRange(compString, 0, compSize);
       
        String cStr = Base64.getEncoder().encodeToString(compString);
        System.out.println("compltedArihtmetic coding");
        return cStr;

    }
}