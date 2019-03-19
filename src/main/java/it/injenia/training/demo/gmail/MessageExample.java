package it.injenia.training.demo.gmail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

public class MessageExample {
	
	public static void main(String[] args) throws IOException {
	
		Gmail gmailService = GmailService.buildGmailService("test1@injdev.com");
		
		
		listMessages(gmailService, "test1@injdev.com", null, null);
		
		//Page token
		listMessages(gmailService, "test1@injdev.com", null, "16183129361307267520");

		//query
		listMessages(gmailService, "test1@injdev.com", "from:test2@injdev.com and injenia", null);
		
		//Recupera dettaglio
		getMessage(gmailService, "test1@injdev.com", "1619a3d5b23985a8");

		//batch
		getMessagesBatch(gmailService, "test1@injdev.com", Arrays.asList("1619a3d5b23985a8", "1619dea4179ecb90", "1619e02fa94649fa"));
	}

	public static ListMessagesResponse listMessages(Gmail service, String userId, String query, String pageToken) throws IOException {
		
		ListMessagesResponse response = service.users().messages().list(userId)
				.setQ(query)
				.setPageToken(pageToken)
				.execute();

		if(response.getMessages() != null) {
		    for (Message message : response.getMessages()) {
		    	System.out.println(message.toPrettyString());
		    }
		}

	    System.out.println("Page Token:" + response.getNextPageToken());

		return response;
	}
	
	public static Message getMessage(Gmail service, String userId, String messageId) throws IOException {
	    
		Message message = service.users().messages().get(userId, messageId).execute();

	    System.out.println("Message snippet: " + message.getSnippet());

	    return message;
	}
	
	public static List<Message> getMessagesBatch(Gmail service, String userId, List<String> messagesIds) throws IOException {
		
		BatchRequest batchRequest = service.batch();
		
		MessageCallback callback = new MessageCallback();
		
		for(String id : messagesIds) {
			
			service.users().messages().get(userId, id).queue(batchRequest, callback);
		}
		
		batchRequest.execute();
		
		return callback.getMessages();
	}
	
	public static Message sendMessage(Gmail service, String userId, MimeMessage emailContent) throws MessagingException, IOException {
		
		Message message = GmailService.createMessageWithEmail(emailContent);
		
		message = service.users().messages().send(userId, message).execute();
	
		System.out.println("Message id: " + message.getId());
		System.out.println(message.toPrettyString());
		
		return message;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////

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

