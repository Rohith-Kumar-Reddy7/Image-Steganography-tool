import javax.crypto.SecretKey;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.nio.charset.StandardCharsets;
class AESCipher{

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

	public String encrypt(String plain_text,String password,String salt) throws Exception
	{
		SecretKey key=this.generateSecretKey(password,salt);
		Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE,key,this.generateIV());

		byte[] cipher_text=cipher.doFinal(plain_text.getBytes());
		
		return Base64.getEncoder().encodeToString(cipher_text);
		//return new String(cipher_text);
	}

	public String encrypt(String plain_text,String password) throws Exception
	{

		byte dum[]=Base64.getDecoder().decode(plain_text);
		String new_str=new String(dum);
		String a=encrypt(new_str,password,"Linus_Torvalds");
		
		Deflater def2=new Deflater();
		def2.setInput(a.getBytes("UTF-8"));
		def2.finish();
		byte[] result=new byte[a.length()*2];
		int res_size=def2.deflate(result);
		result=Arrays.copyOfRange(result,0,res_size);
		String encr_str=Base64.getEncoder().encodeToString(result);
		

		System.out.println("completed encryption");
		return encr_str;
	}

	public String decrypt(String cipher_text,String password) throws Exception
	{
		byte a[]=Base64.getDecoder().decode(cipher_text);
		

		Inflater inf=new Inflater();
		inf.setInput(a);
		byte res_str_bytes[]=new byte[a.length*2];
		int res_length=inf.inflate(res_str_bytes);
		res_str_bytes=Arrays.copyOfRange(res_str_bytes,0,res_length);
		String imm=new String(res_str_bytes,"UTF-8");
		
		byte b[]=Base64.getDecoder().decode(imm);
		
		imm=new String(b);
		
		String result=decrypt(imm,password,"Linus_Torvalds");
		System.out.println("completed decryption");
		return result;
	}


	public String decrypt(String cipher_text,String password,String salt) throws Exception
	{

		SecretKey key=this.generateSecretKey(password,salt);

		Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE,key,this.generateIV());

		//byte[] plain_text=cipher.doFinal(Base64.getDecoder().decode(cipher_text));
		byte[] plain_text=cipher.doFinal(cipher_text.getBytes());

		String result= new String(Base64.getEncoder().encode(plain_text));
	
		return result;
		//return new String(plain_text);
	}


	/*static public void main(String args[]) throws Exception
	{
		String password,salt,input;
		BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));

		System.out.println("enter the password:");
		password=reader.readLine();

		//System.out.println("enter the salt:");
		//salt=reader.readLine();

		System.out.println("enter the input:");
		input=reader.readLine();

		AESCipher a=new AESCipher();

		String b=a.encrypt(input,password);
		System.out.println(b.length());
		System.out.println("the encrypted string is :\n");
		System.out.println(b+"\n");
		System.out.println("the decrypted string is :\n");
		System.out.println(a.decrypt(b,password));
	}*/
}
