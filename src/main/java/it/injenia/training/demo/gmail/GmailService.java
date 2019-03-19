package it.injenia.training.demo.gmail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;

public class GmailService {

	private static final String JSON_FILENAME = "formazione-service-account.json"; 			
	
	private static final List<String> SCOPES = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;
		{
			add(GmailScopes.MAIL_GOOGLE_COM);
		}
	};

	private static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static JacksonFactory JSON_FACTORY = new JacksonFactory();
	
	public static MimeMessage getMimeMessage(Gmail service, String userId, String messageId) throws IOException, MessagingException {
	    Message message = service.users().messages().get(userId, messageId).setFormat("raw").execute();

	    byte[] emailBytes = Base64.decodeBase64(message.getRaw());

	    Properties props = new Properties();
	    Session session = Session.getDefaultInstance(props, null);

	    MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));

	    return email;
	}
	
	//Crea Message Gmail
	public static Message createMessageWithEmail(MimeMessage emailContent) throws MessagingException, IOException {
            
        String encodedEmail = encodeEmail(emailContent);
        
        Message message = new Message();
        message.setRaw(encodedEmail);
        
        return message;
	}
	
	public static MimeMessage createEmailWithAttachment(String to, String from, String subject, String bodyText, File file) throws MessagingException, IOException {
		
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
			
		MimeMessage email = new MimeMessage(session);
			
		email.setFrom(new InternetAddress(from));
		email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
		email.setSubject(subject);
			
		Multipart multipart = new MimeMultipart();

		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setContent(bodyText, "text/plain");
		multipart.addBodyPart(mimeBodyPart);
			
		DataSource source = new FileDataSource(file);	
		mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setDataHandler(new DataHandler(source));
		mimeBodyPart.setFileName(file.getName());
		multipart.addBodyPart(mimeBodyPart);
				
		email.setContent(multipart);
			
		return email;
	}
	
	public static String encodeEmail(MimeMessage email) throws MessagingException, IOException {
	    
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    email.writeTo(baos);
	    return Base64.encodeBase64URLSafeString(baos.toString().getBytes());
	}
	
	public static Gmail buildGmailService(String executionGoogleUser) throws IOException {
		Credential credential = authorize(executionGoogleUser);
		return new Gmail(HTTP_TRANSPORT, JSON_FACTORY, credential);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////


	
	private static Credential authorize(String executionGoogleUser) throws IOException  {
		
		GoogleCredential jsonCredential = GoogleCredential.fromStream(new FileInputStream(new java.io.File(JSON_FILENAME)), HTTP_TRANSPORT, JSON_FACTORY).createScoped(SCOPES);
		
		return new GoogleCredential.Builder()
		.setTransport(jsonCredential.getTransport())
		.setJsonFactory(jsonCredential.getJsonFactory())
		.setServiceAccountId(jsonCredential.getServiceAccountId())
		.setServiceAccountUser(executionGoogleUser)
		.setServiceAccountScopes(jsonCredential.getServiceAccountScopes())
		.setServiceAccountPrivateKey(jsonCredential.getServiceAccountPrivateKey())
		.build();
	}
	
}
