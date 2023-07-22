import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.crypto.BadPaddingException;
class Stegano_GUI extends JFrame{
	private JTextField password;
	private JTextArea secret_data;
	private JPanel original_image,stego_image,signaller;
	private JButton embed,extract,choose1,choose2,clear;
	private JLabel l_password,l_secret_data,l_original_image,l_stego_image,l_original_image_1,l_stego_image_1,l_signaller;
	private JFileChooser chooser;
	private ButtonListener button_listener;

	private Steganography stego;
	private stego stego_helper;
	private String selected_file_path;
	private BufferedImage result_stego_image;	

	public Stegano_GUI()
	{
		super("STEGANOGRAPHY APPLICATION");
		password= new JTextField();
		secret_data=new JTextArea();

		password.setBackground(Color.YELLOW);
		secret_data.setBackground(Color.YELLOW);

		embed=new JButton("embed");
		extract=new JButton("extract");
		choose1=new JButton("choose");
		choose2=new JButton("choose");
		clear=new JButton("clear");

		original_image=new JPanel();
		stego_image=new JPanel();
		signaller=new JPanel();

		l_password=new JLabel("password");
		l_secret_data=new JLabel("secret data");
		l_original_image=new JLabel("original image");
		l_stego_image=new JLabel("stego image");
		l_signaller=new JLabel("no operation going on");

		l_password.setForeground(Color.RED);
		l_secret_data.setForeground(Color.RED);
		l_original_image.setForeground(Color.RED);
		l_stego_image.setForeground(Color.RED);
		l_signaller.setForeground(Color.RED);


		button_listener=new ButtonListener();


		original_image.setBackground(Color.WHITE);
		stego_image.setBackground(Color.WHITE);
		signaller.setBackground(Color.WHITE);
		l_signaller.setForeground(Color.WHITE);		

		original_image.setBounds(50,10,512,512);
		stego_image.setBounds(700,10,512,512);
		signaller.setBounds(1100,600,20,20);
		l_signaller.setBounds(1130,600,150,20);
		original_image.setLayout(null);
		stego_image.setLayout(null);
		signaller.setLayout(null);
		original_image.setForeground(Color.BLACK);
		stego_image.setForeground(Color.BLACK);


		password.setBounds(200+200,425+130,500,25);
		secret_data.setBounds(200+200,475+125,500,50);
		embed.setBounds(250+200,675,100,20);
		extract.setBounds(550+200,675,100,20);
		choose1.setBounds(570,100,100,20);
		choose2.setBounds(1220,100,100,20);
		clear.setBounds(600,675,100,20);
		l_password.setBounds(100+200,425+130,90,20);
		l_secret_data.setBounds(100+200,475+125,90,20);
		l_original_image.setBounds(250,530,100,20);
		l_stego_image.setBounds(950,530,100,20);

		add(secret_data);
		add(password);
		add(l_secret_data);
		add(l_password);
		add(embed);
		add(extract);
		add(choose1);
		add(choose2);
		add(clear);
		add(original_image);
		add(stego_image);
		add(signaller);
		add(l_signaller);
	    add(l_original_image);
		add(l_stego_image);
		choose1.addActionListener(button_listener);
		choose2.addActionListener(button_listener);
		embed.addActionListener(button_listener);
		extract.addActionListener(button_listener);
		clear.addActionListener(button_listener);


		getContentPane().setBackground(Color.BLACK);

		setLayout(null);
		setSize(1450,800);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);


		selected_file_path=null;
		stego_helper=new stego();
		stego=new Steganography();

	}

	public void displayStegoImage() throws IOException
	{
		//original_image.removeAll();
		//original_image.repaint();
		stego_image.removeAll();
		stego_image.repaint();
		l_stego_image_1=new JLabel(new ImageIcon(result_stego_image));
		l_stego_image_1.setBounds(0,0,512,512);
		stego_image.add(l_stego_image_1);
		stego_image.setForeground(Color.BLACK);
		stego_image.repaint();
		
	}

	class ButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent ae) 
		{
			if((JButton)ae.getSource()==choose1 || (JButton)ae.getSource()==choose2)
			{

				JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));


				FileNameExtensionFilter filter=new FileNameExtensionFilter("tif image formats","png");
				fc.setFileFilter(filter);
				int retval=fc.showOpenDialog(choose1);
				signaller.setBackground(Color.WHITE);
				signaller.repaint();
				l_signaller.setText("no operation going on");

				if(retval==JFileChooser.APPROVE_OPTION)
				{
					selected_file_path=fc.getSelectedFile().getAbsolutePath();
					try
					{
						
						BufferedImage img=null;
						try{
							img=ImageIO.read(fc.getSelectedFile());
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
						
						l_original_image_1=new JLabel(new ImageIcon(img));						
						l_original_image_1.setBounds(0,0,512,512);

						if((JButton)ae.getSource()==choose1)
						{
							stego_image.removeAll();
							stego_image.repaint();
							original_image.removeAll();
							original_image.repaint();
							original_image.add(l_original_image_1);
							original_image.repaint();
						}
						
						else if((JButton)ae.getSource()==choose2)
						{

						original_image.removeAll();
						original_image.repaint();
						stego_image.removeAll();
						stego_image.repaint();
						stego_image.add(l_original_image_1);
						stego_image.repaint();

						}
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
				}
			}

			else if((JButton)ae.getSource()==embed)
			{
				String password_instance=password.getText();
				int flag=0;
				try
				{
					signaller.setBackground(Color.RED);
					signaller.repaint();
					l_signaller.setText("embedding");
					l_signaller.setForeground(Color.RED);
					String string_to_be_embedded=stego.startCompression(secret_data.getText(),password_instance);
					result_stego_image=stego_helper.embed(selected_file_path,string_to_be_embedded);
					displayStegoImage();
					flag=1;
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				if(flag==0)
				{
					signaller.setBackground(Color.RED);
					signaller.repaint();
					l_signaller.setText("Operation failed");
					l_signaller.setForeground(Color.RED);
				}
				else if(flag==1)
				{
					signaller.setBackground(Color.GREEN);
					signaller.repaint();
					l_signaller.setText("completed embedding");
					l_signaller.setForeground(Color.GREEN);

				}

			}

			else if((JButton)ae.getSource()==extract)
			{
				String password_instance=password.getText();
				String encrypted_string="",decrypted_string="",decompressed_string="";
				int flag=0;
				try
				{

					signaller.setBackground(Color.RED);
					l_signaller.setText("extraction going on");
					l_signaller.setForeground(Color.RED);
					String compressed_encrypted_string=stego_helper.decode(selected_file_path);
					secret_data.setText(stego.startDecompression(compressed_encrypted_string,password_instance));
					flag=1;
				}
				catch(BadPaddingException bp)
				{
					 JOptionPane.showMessageDialog(((JButton)ae.getSource()).getParent(), "The entered password is wrong");
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				if(flag==0)
				{
					signaller.setBackground(Color.RED);
					signaller.repaint();
					l_signaller.setText("extraction failed");
					l_signaller.setForeground(Color.RED);
				}
				else if(flag==1)
				{
					signaller.setBackground(Color.GREEN);
					l_signaller.setText("extraction completed");
					l_signaller.setForeground(Color.GREEN);
				}
			}

			else if((JButton)ae.getSource()==clear)
			{
				original_image.removeAll();
				original_image.repaint();
				stego_image.removeAll();
				stego_image.repaint();
				password.setText("");
				secret_data.setText("");
				l_signaller.setText("no operation is going on");
				l_signaller.setForeground(Color.WHITE);
				signaller.setBackground(Color.WHITE);
				signaller.repaint();

			}	
					
		}
	}

	static public void main(String args[])
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				new Stegano_GUI();
			}
		});
	}
}