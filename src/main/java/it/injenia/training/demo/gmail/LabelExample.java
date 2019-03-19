package it.injenia.training.demo.gmail;

import java.io.IOException;
import java.util.List;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;

public class LabelExample {

	public static void main(String[] args) throws IOException {
	
		Gmail gmailService = GmailService.buildGmailService("test1@injdev.com");
		
		//lista label
		listLabels(gmailService, "test1@injdev.com");
		
		//creazione label
		Label newLabel = createLabel(gmailService, "test1@injdev.com", "My new label");
		listLabels(gmailService, "test1@injdev.com");
		
		//Aggiornamento label
		updateLabel(gmailService, "test1@injdev.com", newLabel.getId(), "My new Label updated", true, true);
	
		//recupero dettaglio label
		newLabel = getLabel(gmailService, "test1@injdev.com", newLabel.getId());
		
		//eliminazione label
		deleteLabel(gmailService, "test1@injdev.com", newLabel.getId());
		listLabels(gmailService, "test1@injdev.com");

	}
	
	public static ListLabelsResponse listLabels(Gmail service, String userId) throws IOException {
	    
		ListLabelsResponse response = service.users().labels()
				.list(userId).execute();
		
	    List<Label> labels = response.getLabels();
	    for (Label label : labels) {
	    	System.out.println(label.toPrettyString());
	    }
	    
	    return response;
	}
	
	
	public static Label getLabel(Gmail service, String userId, String labelId) throws IOException {
		
		    Label label = service.users().labels().get(userId, labelId).execute();

		    System.out.println("Label " + label.getName() + " retrieved.");
		    System.out.println(label.toPrettyString());
		    
		    return label;
	}
	
	public static Label createLabel(Gmail service, String userId, String newLabelName) throws IOException {
		
		Label label = new Label().setName(newLabelName)
				.setMessageListVisibility("show") 
		        .setLabelListVisibility("labelShow");
		
		label = service.users().labels().create(userId, label).execute();

		System.out.println("Label id: " + label.getId());
		System.out.println(label.toPrettyString());

		return label;
	}
	
	public static void updateLabel(Gmail service, String userId, String labelId, String newLabelName, boolean showInMessageList, boolean showInLabelList) throws IOException {
		    
		String messageListVisibility = showInMessageList ? "show" : "hide";
		String labelListVisibility = showInLabelList ? "labelShow" : "labelHide";
		
		Label newLabel = new Label()
				.setId(labelId)
				.setName(newLabelName)
		        .setMessageListVisibility(messageListVisibility) 
		        .setLabelListVisibility(labelListVisibility);
		
		newLabel = service.users().labels().update(userId, labelId, newLabel).execute();

		System.out.println("Label id: " + newLabel.getId());
		System.out.println(newLabel.toPrettyString());
	}
	
	public static void deleteLabel(Gmail service, String userId, String labelId) throws IOException{
	   
		service.users().labels().delete(userId, labelId).execute();
	    
		System.out.println("Label with id: " + labelId + " deleted successfully.");
	}
}
