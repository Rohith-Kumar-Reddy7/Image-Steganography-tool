import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.imageio.ImageWriter;
import javax.imageio.ImageReader;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.awt.Graphics2D;



public class stego
{


	public BufferedImage embed(String image_path,String secret) throws Exception
	{
		File image_file=getImageFile(image_path);  //gets the image file from the given path

		BufferedImage cover_image=getBufferedImage(image_file);	//gets the BufferedImage of required image from image_file 
		
		BufferedImage new_cover_image=copyBufferedImage(cover_image); //makes a copy of the acquired BufferedImage

		byte[] cover_image_bytes=getImageBytes(new_cover_image); //gets the pixel values of new_cover_image in the form of single byte array the array is passed as reference

		byte[] secret_bytes=secret.getBytes(); //gets the bytes of the secret data
		int m=0;
		byte[][] cover_image_matrix=getImageMatrix(cover_image_bytes,new_cover_image.getHeight(),new_cover_image.getWidth());              /*This method creates a 2D matrix of pixle values of the new_cover_image by taking pixel values in byte 1D array form.In this way we can easily iterate over the image pixels in raster form.*/m=1; 
		
	
			
		embedText(secret_bytes,cover_image_matrix,m);
	
		setImageMatrix(cover_image_matrix,cover_image_bytes);
		ImageIO.write(new_cover_image, "png", new File("stego_image.png"));

		return new_cover_image;
	}

	public String decode(String image_path)
	{
		File image_file=getImageFile(image_path);
		BufferedImage secret_image=getBufferedImage(image_file);
		byte[] secret_image_bytes=getImageBytes(secret_image);
		int m=0;
		byte[][] secret_image_matrix=getImageMatrix(secret_image_bytes,secret_image.getHeight(),secret_image.getWidth());                  /*This method creates a 2D matrix of pixle values of the new_cover_image by taking pixel values in byte 1D array form.In this way we can easily iterate over the image pixels in raster form.*/m=1;

		String result=extractText(secret_image_matrix,m);

		return result;
	}

	public String binaryString(byte[] text_bytes)
	{
		StringBuffer result=new StringBuffer();
		StringBuffer imm=new StringBuffer();
		byte temp=0;
		for(int i=0;i<text_bytes.length;i++)
		{
			temp=text_bytes[i];
			for(int j=0;j<8;j++)
			{
				imm.append(temp&0x01);
				temp=(byte)(temp>>1);
			}
			imm=imm.reverse();
			
			result.append(imm);
			imm.delete(0,imm.length());
		}
		return new String(result);
	}


	public void embedLength(int length,byte[][] target_matrix)
	{
	
		int temp=length;
		byte dup=0;
		int[] index=null;
		byte[][][] length_byte_matrix=new byte[4][3][3];

		length_byte_matrix[0]=get3Matrix(target_matrix,0,0);
		length_byte_matrix[1]=get3Matrix(target_matrix,0,3);
		length_byte_matrix[2]=get3Matrix(target_matrix,0,6);
		length_byte_matrix[3]=get3Matrix(target_matrix,0,9);

		for(int i=0;i<32;i++)
		{
			index=getIndex(i);
			dup=(byte)(temp&0x01);
			length_byte_matrix[index[0]][index[1]/3][index[1]%3]=(byte)((length_byte_matrix[index[0]][index[1]/3][index[1]%3]&0xFE)|dup);
			temp=temp>>1;
		}

		set3Matrix(length_byte_matrix[0],target_matrix,0,0);
		set3Matrix(length_byte_matrix[1],target_matrix,0,3);
		set3Matrix(length_byte_matrix[2],target_matrix,0,6);
		set3Matrix(length_byte_matrix[3],target_matrix,0,9);
	}

	public void embedText(byte[] text_bytes,byte[][] image_matrix)
	{
		String bytes=binaryString(text_bytes);
		int total_bits=bytes.length();
		byte[][] sub_matrix=null;
		int count=0;
		byte dup=0;
		int length=text_bytes.length;
		StringBuffer buffer=new StringBuffer();
		String dop=buffer.toString();
		
		for(int i=31;i>=0;i--)
		{
			dup=(byte)(length&1);
			image_matrix[0][i]=(byte)((image_matrix[0][i]&0xFE)|dup);
			length=length>>1;
		}


		for(int i=1;(i<image_matrix.length)&&count<total_bits;i++)
		{
			for(int j=0;(j<image_matrix[0].length)&&count<total_bits;j++)
			{
				bytes=embedBits(image_matrix,bytes,3,count,i,j);
				count+=3;
			}
		}
	}

	public String extractText(byte[][] image_matrix)
	{
		int length=0;
		byte dup=0;
		for(int i=0;i<32;i++)
		{
			dup=(byte)(image_matrix[0][i]&1);

			length=(length<<1)|dup;
			
		}
		byte[] result_bytes=null;
		int count=0,total_bits=length*8;

		StringBuffer imm=new StringBuffer();

		for(int i=1;(i<image_matrix.length)&&(count<total_bits);i++)
		{
			for(int j=0;(j<image_matrix[0].length)&&count<total_bits;j++)
			{
				imm.append(extractBits(image_matrix,length*8,3,count,i,j));
				count+=3;

			}
		}

		result_bytes=getBytesFromBits(imm.toString());
		return new String(result_bytes);
	}

	public String extractBits(byte[][] image_matrix,int length,int num_bits,int count,int x,int y)
	{
		String result="";
		String dop="";
		int dup=0;
		StringBuffer temp=new StringBuffer();
		StringBuffer buffer=new StringBuffer();


		switch(num_bits)
		{

			case 3:
				dup=image_matrix[x][y]&0x07;
				buffer.append(Integer.toString(dup,2));
				if(buffer.length()<3)
				{
					buffer=buffer.reverse();
					while(buffer.length()<3)
					{
						buffer.append("0");
					}
					buffer=buffer.reverse();
				}
				result=buffer.toString();


				break;
			case 4:

				dup=image_matrix[x][y]&0x0F;
				buffer.append(Integer.toString((int)dup,2));

				if(buffer.length()<4)
				{

					buffer=buffer.reverse();

					while(temp.length()<4)
					{
						temp.append("0");
					}
					buffer=buffer.reverse();
				}
				result=buffer.toString();
				break;

			case 5:
				dup=image_matrix[x][y]&0x1F;
				buffer.append(Integer.toString((int)dup,2));

				if(buffer.length()<5)
				{

					buffer=buffer.reverse();

					while(temp.length()<5)
					{
						temp.append("0");
					}
					buffer=buffer.reverse();
				}

				result=buffer.toString();

				break;
		}

		return result;
	}

	public String extractBits(byte[][] image_matrix,int length,int count)
	{
		StringBuffer result=new StringBuffer();
		String dup="";
		byte temp1;
		int diff,target_no;
		if(image_matrix.length==2)
		{
			temp1=(byte)(image_matrix[0][0]&0x07);
			result.append(Integer.toString((int)temp1,2));
			for(int i=0;i<image_matrix.length;i++)
			{
				for(int j=0;j<image_matrix.length;j++)
				{
					if(i!=0&&j!=0)
					{
						diff=Byte.toUnsignedInt(image_matrix[i][j])-Byte.toUnsignedInt(image_matrix[0][0]);
						diff=(diff<0)?(diff*-1):diff;
						target_no=getNumOfBits(diff);
						result.append(extractBits(image_matrix,length,target_no,count,i,j));
					}
				}
			}
		}
		else
		{
			temp1=(byte)(image_matrix[1][1]&0x07);
			result.append(Integer.toString((int)temp1,2));
			for(int i=0;i<image_matrix.length;i++)
			{
				for(int j=0;j<image_matrix.length;j++)
				{
					if(i!=1&&j!=1)
					{
						diff=Byte.toUnsignedInt(image_matrix[i][j])-Byte.toUnsignedInt(image_matrix[0][0]);
						diff=(diff<0)?(diff*-1):diff;
						target_no=getNumOfBits(diff);
						result.append(extractBits(image_matrix,length,target_no,count,i,j));
					}
				}
			}
		}
		return new String(result);
	}

	public String embedBits(byte[][] image_matrix,String text_bits,int num_bits,int count,int x,int y)
	{
		int dup=0,dup1=0;
		if(count+num_bits>text_bits.length())
		{
			
			//num_bits=text_bits.length()-count;
			dup=text_bits.length()-count;
			dup1=num_bits-dup;
			if(dup1==0)
			{
			System.out.println("padded 0 secret bits");	
			}
			else if(dup1==1)
			{
				System.out.println(x+" "+y);
				text_bits=((new StringBuffer(text_bits)).append("0")).toString();
				System.out.println("padded 1 secret bits");
			}
			else if(dup1==2)
			{
				System.out.println(x+" "+y);
				text_bits=((new StringBuffer(text_bits)).append("00")).toString();
				System.out.println("padded 2 secret bits");
			}
			else if(dup1==3)
			{
				System.out.println(x+" "+y);
				text_bits=((new StringBuffer(text_bits)).append("000")).toString();
				System.out.println("padded 3 secret bits");
			}
			else if(dup1==4)
			{
				System.out.println(x+" "+y);
				text_bits=((new StringBuffer(text_bits)).append("0000")).toString();
				System.out.println("padded 3 secret bits");
			}
		}
		
		switch(num_bits)
		{
			case 3:
				String temp=text_bits.substring(count,count+3);
				dup=Integer.valueOf(text_bits.substring(count,count+3),2);
				image_matrix[x][y]=(byte)((image_matrix[x][y]&0xF8)|dup);
				break;
			case 4:
				dup=Integer.valueOf(text_bits.substring(count,count+4),2);
				image_matrix[x][y]=(byte)((image_matrix[x][y]&0xF0)|dup);
				break;
			case 5:
				dup=Integer.valueOf(text_bits.substring(count,count+4),2);
				image_matrix[x][y]=(byte)((image_matrix[x][y]&0xE0)|dup);
				break;
			default:
				System.out.println("some other error has occurred");
				break;
		}
		return text_bits;
	}
	public byte[] getBytesFromBits(String bits)
	{
		StringBuffer sb=new StringBuffer(bits);
		byte[] result=new byte[(bits.length())/8];
		int count=0;
		String dop="";

		while(sb.length()>=8)
		{
			dop=sb.substring(0,8);
			result[count++]=(byte)(Integer.parseInt(dop,2));
			sb.delete(0,8);
		}
		return result;
	}

	public void embedText(byte[] text_bytes,byte[][] image_matrix,int mode) throws Exception
	{

		if(mode==0)
		{
			int x=0,y=12,i=0,j=0;
			int spl_x=0,spl_y=0;
			byte[][] sub_matrix=null;
			int count=0;
			int difference=0;
			int diff1=0,diff2=0,diff3;
			int ak=0,ai=0,ai1=0,ai11=0;
			String bytes=binaryString(text_bytes);
			int n_bits=0,total_bits=bytes.length();
			
			embedLength(text_bytes.length,image_matrix);


			for(i=0;(i<(image_matrix.length))&&(count<(text_bytes.length*8));i=i+3)
			{

				if(i==510)
				{
					if(spl_y>=512)
					{
						break;
					}
					sub_matrix=get2Matrix(image_matrix,i,spl_y);

					diff1=Byte.toUnsignedInt(sub_matrix[0][0]);
					bytes=embedBits(sub_matrix,bytes,3,count,0,0);
					count+=3;
					diff2=Byte.toUnsignedInt(sub_matrix[0][0])-diff1;
					if(diff2>=5)
					{
						sub_matrix[0][0]=(byte)(diff1+8);
					}	
					else if(diff2<=-5)
					{
						sub_matrix[0][0]=(byte)(diff1-8);
					}
					else
					{
						sub_matrix[0][0]=(byte)diff1;
					}

					for(int k=0;(k<2)&&(count<(text_bytes.length*8));k++)
					{
						for(int l=0;(l<2)&&(count<(text_bytes.length*8));l++)
						{
							if(k!=0&&l!=0)
							{

								difference=Byte.toUnsignedInt(sub_matrix[k][l])-Byte.toUnsignedInt(sub_matrix[0][0]);
								difference=(difference<0)?(difference*(-1)):difference;
								n_bits=getNumOfBits(difference);

								//optimization for non-base pixels

								ai=Byte.toUnsignedInt(sub_matrix[k][l]);
								
								bytes=embedBits(sub_matrix,bytes,n_bits,count,k,l);
								count+=n_bits;
								ai1=Byte.toUnsignedInt(sub_matrix[k][l]);
								
								diff1=ai1+(int)Math.pow(2,n_bits);
								diff3=ai1-(int)Math.pow(2,n_bits);

								ak=(sub_matrix[k][l]>>((int)Math.pow(2,n_bits)-1))&0x01;

							
								if(ak==0&&((diff1>=0)&&(diff1<=255)))
								{
									diff2=(Byte.toUnsignedInt(sub_matrix[k][l])+(int)Math.pow(2,n_bits));
								}
								else if(ak==1&&((diff3>=0)&&(diff3<=255)))
								{
									diff2=(Byte.toUnsignedInt(sub_matrix[k][l])-(int)Math.pow(2,n_bits));
								}
								else{
									diff2=ai1;
								}


								ai11=diff2;

								if((Math.abs(ai-ai1)<Math.abs(ai-ai11))&& (ai1>=0&&ai1<=255))
								{
								
										sub_matrix[k][l]=(byte)ai1;
									
								}
								else
								{
								
										sub_matrix[k][l]=(byte)ai11;
								}
							}
						}
					}
					set2Matrix(sub_matrix,image_matrix,i,spl_y);
					i=i-3;
					//System.out.print(spl_y+" ");
					spl_y+=2;
				}
				else
				{

					for(j=y;(j<(image_matrix[0].length))&&(count<(text_bytes.length*8));j=j+3)
					{
						if(j==510)
						{
							sub_matrix=get2Matrix(image_matrix,spl_x,j);

							diff1=Byte.toUnsignedInt(sub_matrix[0][0]);
							bytes=embedBits(sub_matrix,bytes,3,count,0,0);
							count+=3;
							diff2=Byte.toUnsignedInt(sub_matrix[0][0])-diff1;

							if(diff2>=5)
							{
								sub_matrix[0][0]=(byte)(diff1+8);
							}	
							else if(diff2<=-5)
							{
								sub_matrix[0][0]=(byte)(diff1-8);
							}
							else
							{
								sub_matrix[0][0]=(byte)diff1;
							}


							for(int k=0;(k<2)&&(count<(text_bytes.length*8));k++)
							{
								for(int l=0;(l<2)&&(count<(text_bytes.length*8));l++)
								{
									if(k!=0&&l!=0)
									{
									
										difference=Byte.toUnsignedInt(sub_matrix[k][l])-Byte.toUnsignedInt(sub_matrix[0][0]);
										difference=(difference<0)?(difference*(-1)):difference;
										n_bits=getNumOfBits(difference);

										//optimization for non base pixel
										ai=Byte.toUnsignedInt(sub_matrix[k][l]);
										

										bytes=embedBits(sub_matrix,bytes,n_bits,count,k,l);
										count+=n_bits;

										ai1=Byte.toUnsignedInt(sub_matrix[k][l]);
								
										diff1=ai1+(int)Math.pow(2,n_bits);
										diff3=ai1-(int)Math.pow(2,n_bits);

										ak=((sub_matrix[k][l]>>((int)Math.pow(2,n_bits)-1))&0x01);
									
										if(ak==0&&((diff1>=0)&&(diff1<=255)))
										{
											diff2=(sub_matrix[k][l]+(int)Math.pow(2,n_bits));
										}
										else if(ak==1&&((diff3>=0)&&(diff3<=255)))
										{
											diff2=(sub_matrix[k][l]-(int)Math.pow(2,n_bits));
										}
										else
										{
											diff2=ai1;
										}

										ai11=diff2;

										if((Math.abs(ai-ai1)<Math.abs(ai-ai11))&& (ai1>=0&&ai1<=255))
										{
											
												sub_matrix[k][l]=(byte)ai1;
											
										}
										else
										{
												sub_matrix[k][l]=(byte)ai1;
											
										}
									}
								}
							}
							set2Matrix(sub_matrix,image_matrix,spl_x,j);
							spl_x+=2;
						}
						else
						{

							sub_matrix=get3Matrix(image_matrix,i,j);

							diff1=Byte.toUnsignedInt(sub_matrix[1][1]);
							bytes=embedBits(sub_matrix,bytes,3,count,1,1);
							count+=3;
							diff2=Byte.toUnsignedInt(sub_matrix[1][1])-diff1;

							if(diff2>=5)
							{
								sub_matrix[1][1]=(byte)(diff1+8);
							}	
							else if(diff2<=-5)
							{
								sub_matrix[1][1]=(byte)(diff1-8);
							}
							else
							{
								sub_matrix[1][1]=(byte)diff1;
							}

							for(int k=0;(k<3)&&(count<(text_bytes.length*8));k++)
							{
								for(int l=0;(l<3)&&count<(text_bytes.length*8);l++)
								{

									if(k!=1&&l!=1)
									{
									

										difference=Byte.toUnsignedInt(sub_matrix[k][l])-Byte.toUnsignedInt(sub_matrix[1][1]);
										difference=(difference<0)?(difference*(-1)):(difference);
										n_bits=getNumOfBits(difference);
										//optimization for the non  base pixel

										ai=Byte.toUnsignedInt(sub_matrix[k][l]);

										
										
										bytes=embedBits(sub_matrix,bytes,n_bits,count,k,l);
										count+=n_bits;
										ai1=Byte.toUnsignedInt(sub_matrix[k][l]);

										//System.out.println(ai1+" "+n_bits+" "+count+" "+i+" "+j+" "+k+" "+l);

										diff1=ai1+(int)Math.pow(2,n_bits);
										diff3=ai1-(int)Math.pow(2,n_bits);

										ak=(sub_matrix[k][l]>>((int)Math.pow(2,n_bits)-1))&0x01;
										
											if(ak==0&&((diff1>=0)&&(diff1<=255)))
											{
												diff2=(Byte.toUnsignedInt(sub_matrix[k][l])+(int)Math.pow(2,n_bits));
											}
											else if(ak==1&&((diff3>=0)&&(diff3<=255)))
											{
												diff2=(Byte.toUnsignedInt(sub_matrix[k][l])-(int)Math.pow(2,n_bits));
											}
											else
											{
												diff2=ai1;
											}

										ai11=diff2;

										if((Math.abs(ai-ai1)<Math.abs(ai-ai11))&& (ai1>=0&&ai1<=255))
										{
											
												sub_matrix[k][l]=(byte)ai1;
											
										}
										else
										{
										
												sub_matrix[k][l]=(byte)ai1;
											
										}
									}
								}
							}
							set3Matrix(sub_matrix,image_matrix,i,j);
							y=0;
						}
					}
				}
			}
		}
		else
		{
			embedText(text_bytes,image_matrix);
		}
	}



	
	public int getNumOfBits(int a)
	{
		int result=0;
		if(a>=0&&a<=15)
		{
			result=3;
		}
		else if(a>=16&&a<=31)
		{
	
			result=4;
		}
		else if(a>=32&&a<=63)
		{
			result=5;
		}
		else if(a>=64&&a<=127)
		{
			result=5;
		}
		else if(a>=128&&a<=255)
		{
			result=5;
		}

		return result;
	}



	public String extractText(byte[][] image_matrix,int mode)
	{
		String result="";
		if(mode==0)
		{
			int length=extractLength(image_matrix);

			int spl_x=0,spl_y=0;
			byte[][] sub_matrix=null;
			byte[] result_bytes;
			int count=0;
			byte dup=0;
			int y=12;
			String dop="";
			result="";

			for(int i=0;(i<(image_matrix.length))&&(count<length*8);i=i+3)
			{
				if(i==510)
				{	if(spl_y>=512)
					{
						break;
					}
					sub_matrix=get2Matrix(image_matrix,i,spl_y);
					dop=extractBits(sub_matrix,length*8,count);
					count+=dop.length();
					result+=dop;
					i=i-3;
					spl_y+=2;

				}
				else
				{
					for(int j=y;(j<(image_matrix[0].length))&&(count<length);j=j+3)
					{
						if(j==510)
						{
							sub_matrix=get2Matrix(image_matrix,spl_x,j);
							dop=extractBits(sub_matrix,length*8,count);
							count+=dop.length();
							result+=dop;
							spl_x+=2;
						}
						else
						{
							sub_matrix=get3Matrix(image_matrix,i,j);
							dop=extractBits(sub_matrix,length*8,count);
							count+=dop.length();
							result+=dop;
							y=0;						
						}

					}
		
				}
			}
			result_bytes=getBytesFromBits(result);
			result=new String(result_bytes);
		}
		else
		{
			result=extractText(image_matrix);
		}
		
		return result;
	}

	public int extractLength(byte[][] image_matrix)
	{
		
		byte[][][] length_matrix=new byte[4][3][3];
		length_matrix[0]=get3Matrix(image_matrix,0,0);
		length_matrix[1]=get3Matrix(image_matrix,0,3);
		length_matrix[2]=get3Matrix(image_matrix,0,6);
		length_matrix[3]=get3Matrix(image_matrix,0,9);

		
		String dup="";
		int dummy=0;
		int[] index=null;

		for(int i=0;i<32;i++)
		{
			index=getIndex(i);
			dup=dup+(length_matrix[index[0]][index[1]/3][index[1]%3]&0X01);
		
		}	
		dup=(new StringBuffer(dup).reverse()).toString();
	
		return Integer.valueOf(dup,2);
	}

	public byte[][] get3Matrix(byte[][] image_matrix,int x,int y)
	{
		byte[][] result=new byte[3][3];
		int i=0,j=0,a=0,b=0;


			for(i=x,a=0;i<x+3;i++,a++)
			{
				for(j=y,b=0;j<y+3;j++,b++)
				{
					result[a][b]=image_matrix[i][j];
				}
			}
		return result;
	}

	public byte[][] get2Matrix(byte[][] image_matrix,int x,int y)
	{
		byte[][] result=new byte[2][2];
		
		for(int i=x,a=0;i<x+2;i++,a++)
		{
			for(int j=y,b=0;j<y+2;j++,b++)
			{
				result[a][b]=image_matrix[i][j];
			}
		}
		return result;
	}

	public void set3Matrix(byte[][] source_matrix,byte[][] target_matrix,int x,int y)
	{
		for(int i=x,a=0;i<x+3;i++,a++)
		{
			for(int j=y,b=0;j<y+3;j++,b++)
			{
					target_matrix[i][j]=(byte)(Byte.toUnsignedInt(source_matrix[a][b]));
			}
		}
	}

	public void set2Matrix(byte[][] source_matrix,byte[][] target_matrix,int x,int y)
	{
		for(int i=x,a=0;i<x+2;i++,a++)
		{
			for(int j=y,b=0;j<y+2;j++,b++)
			{
				target_matrix[i][j]=(byte)(Byte.toUnsignedInt(source_matrix[a][b]));
			}
		}
	}



	public int[] getIndex(int a)
	{
		int[]  result=new int[2];

		int x=a/9;
		int y=a%9;
		if(x>3)
		{
			result[0]=result[1]=result[2]=-1;
		}
		else
		{
			result[0]=x;
			result[1]=y;
		}
		return result;
	}
	String reverse(String a)
	{
		StringBuffer b=new StringBuffer(a);
		return new String(b.reverse());
	}

	int stringToInt(String a)
	{
		int temp=0;
		for(int i=0;i<8;i++)
		{
			if(a.charAt(i)=='0')
			{
				temp=((temp<<1)|0x00);
			}
			else
			{
				temp=((temp<<1)|0x01);
			}
		}

		return temp;
	}

	public File getImageFile(String path)
	{
		File a=null;

		try{
			a=new File(path);
		}
		catch(Exception e)
		{
			System.out.println("error while opening the image file:"+e);
		}
		return a;
	}


	public BufferedImage getBufferedImage(File file_image)
	{
		BufferedImage b=null;

		try{
			b=ImageIO.read(file_image);
		}
		catch(Exception e)
		{
			System.out.println("error while making the BufferedImage of the image file"+e);
		}

		return b;
	}

	public BufferedImage copyBufferedImage(BufferedImage old_image)
	{
		BufferedImage new_image=new BufferedImage(old_image.getWidth(),old_image.getHeight(),old_image.getType());
		Graphics2D image_graphics=new_image.createGraphics();
		image_graphics.drawRenderedImage(old_image,null);
		image_graphics.dispose();
		return new_image;
	}

	public byte[] getImageBytes(BufferedImage image)
	{
		WritableRaster raster=image.getRaster();
		DataBufferByte b=(DataBufferByte)raster.getDataBuffer();
		return b.getData();
	}


	public byte[][] getImageMatrix(byte[] a,int height,int width)
	{
		byte[][] image_bytes=new byte[height][width];
		int x=0,y=0;

		for(int i=0;i<a.length;i++)
		{
			image_bytes[x][y]=a[i];
			y++;
			if(y==width)
			{
				y=0;
				x++;
			}
		}
		return image_bytes;
	}

	public void setImageMatrix(byte[][] image_matrix,byte[] image_byte_array)
	{
		int pointer=0;

		for(int i=0;i<image_matrix.length;i++)
		{
			for(int j=0;j<image_matrix.length;j++) //I have assumed that the height and width of the image are same
			{
				image_byte_array[pointer++]=image_matrix[i][j];
			}
		}

	}


}




