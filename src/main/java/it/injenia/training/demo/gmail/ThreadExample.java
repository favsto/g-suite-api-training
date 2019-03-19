package it.injenia.training.demo.gmail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListThreadsResponse;
import com.google.api.services.gmail.model.ModifyThreadRequest;
import com.google.api.services.gmail.model.Thread;


public class ThreadExample {
	
	public static void main(String[] args) throws IOException {
		
		Gmail gmailService = GmailService.buildGmailService("test1@injdev.com");
		
		
		ListThreadsResponse threads = listThreadsMatchingQuery(gmailService, "test1@injdev.com", null);
		
		//recupera thread
		Thread thread = getThread(gmailService, "test1@injdev.com", threads.getThreads().get(0).getId());
		
		//aggiorna thread, solo label
		modifyThread(gmailService, "test1@injdev.com", thread.getId(), Arrays.asList("Label_2960154541088848085"), null);
	}

	public static ListThreadsResponse listThreadsMatchingQuery (Gmail service, String userId, String query) throws IOException {
		
		ListThreadsResponse response = service.users().threads().list(userId).setQ(query).execute();
		
		List<Thread> threads = new ArrayList<Thread>();
		
//	    while(response.getThreads() != null) {
//	    
	    	threads.addAll(response.getThreads());
//	    	
//	    	if(response.getNextPageToken() != null) {
//	        
//	    		String pageToken = response.getNextPageToken();
//	        
//	    		response = service.users().threads().list(userId).setQ(query).setPageToken(pageToken).execute();
//	    		
//	    	} else {
//	    		break;
//	    	}
//	    }

	    for(Thread thread : threads) {
	      System.out.println(thread.toPrettyString());
	    }
	    
	    return response;
	}
	
	 public static Thread getThread(Gmail service, String userId, String threadId) throws IOException {
		
		 Thread thread = service.users().threads().get(userId, threadId).execute();
		 
		 System.out.println("Thread id: " + thread.getId());
		 System.out.println("No. of messages in this thread: " + thread.getMessages().size());
		 System.out.println(thread.toPrettyString());
		 
		 return thread;
	 }
	 
	 
	public static void modifyThread(Gmail service, String userId, String threadId, List<String> labelsToAdd, List<String> labelsToRemove) throws IOException {
		   
		ModifyThreadRequest mods = new ModifyThreadRequest()
			.setAddLabelIds(labelsToAdd)
			.setRemoveLabelIds(labelsToRemove);
		
		Thread thread = service.users().threads().modify(userId, threadId, mods).execute();

		System.out.println("Thread id: " + thread.getId());
		System.out.println(thread.toPrettyString());
	}
}
