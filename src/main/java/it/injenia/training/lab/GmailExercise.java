package it.injenia.training.lab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Draft;
import com.google.api.services.gmail.model.Message;

import it.injenia.training.demo.gmail.GmailService;


public class GmailExercise {

	
	public static void main(String[] args) throws Exception {
				
		Gmail gmailService = ExerciseAuth.getGmailService();
		String myUserId = "";
		
		//Crea Draft
//		MimeMessage mimeMessage = createEmail(myUserId, myUserId, "Prova", "Questa � una prova");		
//		Draft draft = createDraft(gmailService, myUserId, mimeMessage);
		
		//Recupero Draft
//		draft = getDraft(gmailService, myUserId, draft.getId());
		
		//Aggiorno messaggio del draft
//		mimeMessage = createEmail(myUserId, myUserId, "Prova aggiornata", "Questa � una prova aggiornata");
//		updateDraft(gmailService, myUserId, draft.getId(), mimeMessage);
		
		//Invio Draft
//		Message sendedMessage = sendDraft(gmailService, myUserId, draft.getId());
		
//		sendedMessage = getMessage(gmailService, myUserId, sendedMessage.getId(), "TODO");
		
//		getMessagesBatch(gmailService, myUserId, "TODO:IDS");
	}
	
	
	//Crea Draft
	public static Draft createDraft(Gmail service, String userId, MimeMessage emailContent) throws MessagingException, IOException {
		
		Message message = GmailService.createMessageWithEmail(emailContent);
		
		Draft draft = new Draft();
		draft.setMessage(message);
		
		draft = service.users().drafts().create(userId, draft).execute();

		return draft;
	}
	
	//Aggiorna Draft
	public static void updateDraft(Gmail service, String userId, String draftId, MimeMessage updatedEmail) throws MessagingException, IOException {
		   
		 Message updatedMessage = new Message();
		 updatedMessage.setRaw(GmailService.encodeEmail(updatedEmail));

		 Draft updatedDraft = new Draft();
		 updatedDraft.setMessage(updatedMessage);

		 updatedDraft = service.users().drafts(). ; //TODO
		 
	}
		 
	public static Draft getDraft(Gmail service, String userId, String draftId) throws IOException {
		 
		//TODO
	}
	
	//Invia Draft
	public static Message sendDraft(Gmail service, String userId, String draftId) throws IOException {
	     
		Draft draft = new Draft();
		draft.setId(draftId);

		Message message = service.users().drafts().send(userId, draft).execute();
		
		return message;
	}
	
	public static Message getMessage(Gmail service, String userId, String messageId, String fields) throws IOException {
	    
		Message message = service.users().messages()
				.get(userId, messageId) //TODO
				.execute();

	    System.out.println("Message snippet: " + message.getSnippet());

	    return message;
	}
	
	public static List<Message> getMessagesBatch(Gmail service, String userId, List<String> messagesIds) throws IOException {
		
		BatchRequest batchRequest = service.batch();
		
		MessageCallback callback = new MessageCallback();
		
		//TODO
		
		batchRequest.execute();
		
		return callback.getMessages();
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static MimeMessage createEmail(String to, String from, String subject, String bodyText) throws MessagingException {
		
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		
		MimeMessage email = new MimeMessage(session);
		
		email.setFrom(new InternetAddress(from));
		email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
		email.setSubject(subject);
		email.setText(bodyText);
		
		return email;
	}
	
	class MessageCallback extends JsonBatchCallback<Message> {

		private List<Message> messages = new ArrayList<Message>();
		
		public List<Message> getMessages() {
			return this.messages;
		}
		 
		public void onSuccess(Message message, HttpHeaders headers) throws IOException {
			
			messages.add(message);
		    System.out.println("Message snippet: " + message.getSnippet());
			
		}

		@Override
		public void onFailure(GoogleJsonError error, HttpHeaders headers) throws IOException {
			
		    System.out.println("Error: " + error.getMessage());
			
		}
		 
	 }
}
