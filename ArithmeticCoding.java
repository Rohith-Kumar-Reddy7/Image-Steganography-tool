import java.util.*;
import java.util.zip.DataFormatException;
import java.io.*;

public class ArithmeticCoding{

	public String compress(String str) throws DataFormatException,UnsupportedEncodingException
	{
		Arithmetic_coding_compression a=new Arithmetic_coding_compression();
		String result=a.compress(str);
		return result;
	}
	public String decompress(String str) throws DataFormatException,UnsupportedEncodingException
	{
		Arithmetic_coding_decompression b=new Arithmetic_coding_decompression();
		return b.decompress(str);
		
	}
	static public void main(String args[]) throws Exception
	{
		Scanner scan=new Scanner(System.in);
		ArithmeticCoding p= new ArithmeticCoding();
		System.out.println("enter the text");
		String a=scan.nextLine();
		System.out.println("The length of the original string:"+a.length());
		String result1=p.compress(a);
		System.out.println(result1.length());

		System.out.println(p.decompress(result1).length());
		
	}
}