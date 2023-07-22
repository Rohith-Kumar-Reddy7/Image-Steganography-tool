import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.*;
import java.io.*;
import static java.lang.Integer.valueOf;

public class Arithmetic_coding_decompression {

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

    public static String Decompress(char[] u_String, BigDecimal[] lFArr, BigDecimal theNumber, int sum) {
        BigDecimal stValue = new BigDecimal(0);             BigDecimal endValue = new BigDecimal(1);
        BigDecimal[] lFArr1 = lFArr;        char[] str = new char[sum];
        for(int i = 0; i < sum; i++) {
            for(int j = 0; j < lFArr.length; j++) {
                if(theNumber.compareTo(lFArr[j]) < 0) {
                    if(j == 0) {
                        endValue = lFArr[j];
                    } else if (j == lFArr.length - 1) {
                        stValue = lFArr[j-1];
                    }
                    else {
                        stValue = lFArr[j-1];
                        endValue = lFArr[j];
                    }
                    str[i] = u_String[j];
                    lFArr = LineFractionArr(lFArr1, stValue, endValue);
                    break;
                }
            }
        }
        return new String(str);
    }

    public static String decompress(String cStr) throws DataFormatException,UnsupportedEncodingException{
        String str, str1 = "", str2 = "", str3;     BigDecimal compDecimalPoint;    int sum = 0, i = 0, j = 0;
        BigDecimal[] freqArr, lineFractionArr;  char[] ch;
        byte[] imm_result = Base64.getDecoder().decode(cStr);
        System.out.println(imm_result.length+"*"+cStr.length());

        Inflater inf = new Inflater();
       // inf.setInput(compString,0,compString.length);
      
        inf.setInput(imm_result);
        byte[] decompString = new byte[(imm_result.length * 3)];
        
        int decompSize = inf.inflate(decompString);
        decompString = Arrays.copyOfRange(decompString, 0, decompSize);
        str = new String(decompString,"UTF-8");

        inf.end();

        /*Inflater inf2=new Inflater();
        inf2.setInput(decompString);
        byte[] dup_result=new byte[decompString.length*2];
        int final_size=inf2.inflate(dup_result);
        dup_result=Arrays.copyOfRange(dup_result,0,final_size);
        String new_string=new String(dup_result,"UTF-8");*/

        for(i = 0; i < str.length(); i++) {
            if(str.charAt(i) == ',') {
                if (str.charAt(i + 1) == ',') {
                    if (str.charAt(i + 2) == ',') {
                        j = i + 1;
                        str1 = str.substring(0, j);
                        break;
                    }
                    j = i;
                    str1 = str.substring(0, j);
                    break;
                }
            }
        }

        for(i = j+2; i < str.length(); i++) {
            if(str.charAt(i) == ',') {
                if (str.charAt(i + 1) == ',') {
                    str2 = str.substring(j+2, i);
                    break;
                }
            }
        }
        int count = 0; int y = i;
        for(i = 0; i < str2.length(); i++) {
            if(str2.charAt(i) == ' ')
                count++;
        }
        int[] repArr = new int[count];
        j = 0; int k = 0;
//        System.out.println('*' + str2 + '*');
        for(i = 0; i < str2.length(); i++) {
            if(str2.charAt(i) == ' ') {
//                System.out.print(str2.substring(j, i) + '*');
                repArr[k] = valueOf(str2.substring(j, i));
                k++; j = i+1;
            }
        }

        str3 = str.substring(y+2);

//        System.out.println(str1);
//        System.out.println(str2);
//        System.out.println(str3);

        compDecimalPoint = new BigDecimal(str3);

        ch = str1.toCharArray();

        for (int p : repArr) {      // Calculating total no of char in the secret msg as sum
            sum += p;
        }

        freqArr = FreqArray(repArr);

        lineFractionArr = IniFractionArr(freqArr);
        System.out.println("completed de arithmetic coding");
        return Decompress(ch,lineFractionArr,compDecimalPoint,sum);

    }
}