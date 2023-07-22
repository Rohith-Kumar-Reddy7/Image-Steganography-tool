import javax.crypto.SecretKey;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.nio.charset.StandardCharsets;

import static java.lang.Integer.valueOf;

public class Steganography{

	public IvParameterSpec generateIV()
	{
		byte[] IV=new byte[16];
		for(int i=0;i<IV.length;i++)
		{
			if(i%2==0)
				IV[i]=0;
			else
				IV[i]=1;
		}
		return new IvParameterSpec(IV);
	}

	public SecretKey generateSecretKey(String password,String salt) throws Exception
	{

		SecretKeyFactory secret_key_factory=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		PBEKeySpec PBE_key_spec=new PBEKeySpec(password.toCharArray(),salt.getBytes(),65536,256);

		SecretKey key=new SecretKeySpec(secret_key_factory.generateSecret(PBE_key_spec).getEncoded(),"AES");
		return key;
	}



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




    public static BigDecimal[] IniFractionArr(BigDecimal[] arr) {
        for(int i = 1; i < arr.length; i++) {
            arr[i] = arr[i].add(arr[i-1]);
        }
        return arr;
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


  

     public static BigDecimal[] FreqArray(int[] arr) {
        int sum = 0;    BigDecimal[] freqArray;    freqArray = new BigDecimal[arr.length];
        for (int j : arr)
            sum += j;
        for (int j = 0; j < arr.length; j++)
            freqArray[j] = BigDecimal.valueOf((double)arr[j]/(double)sum);
        return freqArray;
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

    public static String decompress(byte[] cStr) throws DataFormatException,UnsupportedEncodingException{
        String str, str1 = "", str2 = "", str3;     BigDecimal compDecimalPoint;    int sum = 0, i = 0, j = 0;
        BigDecimal[] freqArr, lineFractionArr;  char[] ch;
       
        Inflater inf = new Inflater();
        inf.setInput(cStr);

        byte[] decompString = new byte[(cStr.length * 2)];
        int decompSize = inf.inflate(decompString);
        decompString = Arrays.copyOfRange(decompString, 0, decompSize);
        
        str = new String(decompString,"UTF-8");  //i have some suspicion on this line

        inf.end();

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


        compDecimalPoint = new BigDecimal(str3);

        ch = str1.toCharArray();

        for (int p : repArr) {      // Calculating total no of char in the secret msg as sum
            sum += p;
        }

        freqArr = FreqArray(repArr);

        lineFractionArr = IniFractionArr(freqArr);
        return Decompress(ch,lineFractionArr,compDecimalPoint,sum);

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

/*     public static String compress(String str) throws DataFormatException,UnsupportedEncodingException{
   //        System.out.println(compSize);
        compString = Arrays.copyOfRange(compString, 0, compSize);
       String as_string=Base64.getEncoder().encodeToString(compString);
    }*/

	public byte[] encrypt(byte[] plain_text,String password,String salt) throws Exception
	{
		SecretKey key=this.generateSecretKey(password,salt);
		Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE,key,this.generateIV());

		byte[] cipher_text=cipher.doFinal(plain_text);
		
		return cipher_text;
		//return new String(cipher_text);
	}

	/*public byte[] encrypt(byte[] plain_text,String password) throws Exception
	{

		byte[] a=encrypt(plain_text,password,"Linus_Torvalds");
		
		Deflater def2=new Deflater();
		def2.setInput(a.getBytes("UTF-8"));
		def2.finish();
		byte[] result=new byte[a.length];
		int res_size=def2.deflate(result);
		result=Arrays.copyOfRange(result,0,res_size);

		String encr_str=Base64.getEncoder().encodeToString(result);
		

		System.out.println("completed encryption");
		return encr_str;
	}*/

	/*public String decrypt(String cipher_text,String password) throws Exception
	{
		byte a[]=Base64.getDecoder().decode(cipher_text);
		

		Inflater inf=new Inflater();
		inf.setInput(a);
		byte res_str_bytes[]=new byte[a.length*2];
		int res_length=inf.inflate(res_str_bytes);
		res_str_bytes=Arrays.copyOfRange(res_str_bytes,0,res_length);
	
		


	//	String imm=new String(res_str_bytes,"UTF-8");		
		
		byte[] result=decrypt(res_str_bytes,password,"Linus_Torvalds");
		System.out.println("completed decryption");
		return result;
	}
*/

	public byte[] decrypt(byte[] cipher_text,String password,String salt) throws Exception
	{

		SecretKey key=this.generateSecretKey(password,salt);

		Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE,key,this.generateIV());

		//byte[] plain_text=cipher.doFinal(Base64.getDecoder().decode(cipher_text));
		byte[] plain_text=cipher.doFinal(cipher_text);
	
		return plain_text;

	}

	String startCompression(String str,String password) throws Exception
	{

		String str1, str2, str3;     int[] rep_arr, uniqueCharRepArray;      char[] uniqueCharString;    BigDecimal[] freqArr, lineFractionArr;

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

        byte[] a=encrypt(f_str.getBytes(),password,"Linus_Torvalds");
		//String result=new String(Base64.getEncoder().encodeToString(a));

        String temp2=Base64.getEncoder().encodeToString(a);

        Deflater def=new Deflater();
        def.setInput(temp2.getBytes("UTF-8"));
        def.finish();

        byte[] res_bytes=new byte[temp2.length()*2];
        int res_count=def.deflate(res_bytes);
        res_bytes=Arrays.copyOfRange(res_bytes,0,res_count);
        return Base64.getEncoder().encodeToString(res_bytes);
	}	

	String startDecompression(String a,String password)throws Exception
	{
		String str, str1 = "", str2 = "", str3;     BigDecimal compDecimalPoint;    int sum = 0, i = 0, j = 0;
        BigDecimal[] freqArr, lineFractionArr;  char[] ch;
        
  

        byte[] temp=Base64.getDecoder().decode(a);
        
        Inflater inf=new Inflater();
        inf.setInput(temp);
        byte[] encrypted_bytes=new byte[temp.length*2];
        
        int count1=inf.inflate(encrypted_bytes);
        encrypted_bytes=Arrays.copyOfRange(encrypted_bytes,0,count1);

        encrypted_bytes=Base64.getDecoder().decode(encrypted_bytes);

        byte[] result=decrypt(encrypted_bytes,password,"Linus_Torvalds");

        str=new String(result);


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

        for(i = 0; i < str2.length(); i++) {
            if(str2.charAt(i) == ' ') {
                repArr[k] = valueOf(str2.substring(j, i));
                k++; j = i+1;
            }
        }

        str3 = str.substring(y+2);


        compDecimalPoint = new BigDecimal(str3);

        ch = str1.toCharArray();

        for (int p : repArr) {      
            sum += p;
        }

        freqArr = FreqArray(repArr);

        lineFractionArr = IniFractionArr(freqArr);
        System.out.println("completed de arithmetic coding");
        String ans=Decompress(ch,lineFractionArr,compDecimalPoint,sum);
        return ans;

	}	

	static public void main(String arg[]) throws Exception
	{
		Steganography c=new Steganography();
		String b="capdujlkjlkfjlksdjflkhhhh,asjdflksjdf;kljs;dlfkjs;lkdfasd123456j;askldjf;lskdjf;lsakjdf;askldfkjl;kjbejhkjhdhave bsdflkjslkdjflskdjfeenused for reversible and irreversible embeddingding.have behave been used for reversible and irreversible embeddingen used for reversibleand irreversible embeddingmethods.oo Irreversjijfble embesajdhkjhhhhadding pfdfdfsrovidesjjj higher embeddingcapacity than reversible embedding.The performance evaluation of image steganography methods is based on the parameters like: hiding capacity, visualquality/imperceptibility and security/un-detectability. However, these evaluating parameters produce opposite effectswith each other e.g. the steganographic methods designedto achieve higher hiding capacity result in visual distortionsto the steganographed images and reduced security. Thus,proper corrective/balancing measures are required to makebalance between these parameters. Qualities like high embedding capacity, un-detectability and satisfactory visual qualityare required for real applications.The simplest and most popular image steganographic technique is the LSB substitution [20]. It involves the embeddingof messages into coverimage by directly replacing the LSBs.The hiding capacity can be as high as 4 LSBs per pixel.A common weakness of LSB embedding is thatsample valuechanges asymmetrically. Through LSB embedding, visualquality may decrease and become sensitive to steganalysis [27]acks.The pixel value differencing (PVD) [19] was introduced byWu and Tsai for improving imperceptibility in stego images.In PVD method, dataembedding is done by readjusting thedifference between two pixels. The PVD based methods arevulnerable to histogram analysis and rovide low embeddingrates.Enhanced hiding capacity over PVD [19] based methods";
		System.out.println(b.length());
		String temp=c.startCompression(b,"rohith");
		System.out.println(temp);
		System.out.println(temp.length());
		String result=c.startDecompression(temp,"rohith");
		System.out.println(result);

	}
}

