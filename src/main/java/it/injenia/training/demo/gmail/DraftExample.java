package it.injenia.training.demo.gmail;

import java.io.IOException;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Draft;
import com.google.api.services.gmail.model.Message;

public class DraftExample {
	
	public static void main(String[] args) throws IOException, MessagingException {
		
		Gmail gmailService = GmailService.buildGmailService("test1@injdev.com");
		
		//Crea Draft
		MimeMessage mimeMessage = createEmail("test1@injdev.com", "test1@injdev.com", "Prova", "Questa � una prova");		
		Draft draft = createDraft(gmailService, "test1@injdev.com", mimeMessage);
		
		//Recupero Draft
		draft = getDraft(gmailService, "test1@injdev.com", draft.getId());
		
		//Aggiorno messaggio del draft
		mimeMessage = createEmail("test1@injdev.com", "test1@injdev.com", "Prova aggiornata", "Questa � una prova aggiornata");
		updateDraft(gmailService, "test1@injdev.com", draft.getId(), mimeMessage);
		
		draft = getDraft(gmailService, "test1@injdev.com", draft.getId());

		//Invio Draft
		sendDraft(gmailService, "test1@injdev.com", draft.getId());
	}
	
	//Crea messaggio MIME
	public static MimeMessage createEmail(String to, String from, String subject, String bodyText) throws MessagingException {
		
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		
		MimeMessage email = new MimeMessage(session);
		
		email.setFrom(new InternetAddress(from));
		email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
		email.setSubject(subject);
		email.setText(bodyText);
		
		return email;
	}
	
	//Crea Draft
	public static Draft createDraft(Gmail service, String userId, MimeMessage emailContent) throws MessagingException, IOException {
		
		Message message = GmailService.createMessageWithEmail(emailContent);
		
		Draft draft = new Draft();
		draft.setMessage(message);
		draft = service.users().drafts().create(userId, draft).execute();
		
		System.out.println("Draft id: " + draft.getId());
		System.out.println(draft.toPrettyString());
		return draft;
	}
	
	//Aggiorna Draft
	public static void updateDraft(Gmail service, String userId, String draftId, MimeMessage updatedEmail) throws MessagingException, IOException {
		   
		 Message updatedMessage = new Message();
		 updatedMessage.setRaw(GmailService.encodeEmail(updatedEmail));

		 Draft updatedDraft = new Draft();
		 updatedDraft.setMessage(updatedMessage);

		 updatedDraft = service.users().drafts().update(userId, draftId, updatedDraft).execute();

		 System.out.println("Draft id: " + updatedDraft.getId());
		 System.out.println(updatedDraft.toPrettyString());
	}
	
	//Ottiene draft
	public static Draft getDraft(Gmail service, String userId, String draftId) throws IOException {
		 
	    Draft draft = service.users().drafts().get(userId, draftId).execute();
	    
	    Message message = draft.getMessage();

	    System.out.println("Draft id: " + draft.getId() + "\nDraft Message:\n"
	        + message.toPrettyString());

	    return draft;
	}	
	 
	//Invia Draft
	public static void sendDraft(Gmail service, String userId, String draftId) throws IOException {
	     
		Draft draft = new Draft();
		draft.setId(draftId);

		Message message = service.users().drafts().send(userId, draft).execute();
		
		System.out.println("Draft with ID: " + draftId + " sent successfully.");
		System.out.println("Draft sent as Message with ID: " + message.getId());
	}
	 
	
}
