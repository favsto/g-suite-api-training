package it.injenia.training.demo.drive;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

public class GdriveService {

	private static final String JSON_FILENAME = "formazione-service-account.json"; 			
	
	private static final List<String> SCOPES = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;
		{
			add(DriveScopes.DRIVE);
		}
	};

	private static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static JacksonFactory JSON_FACTORY = new JacksonFactory();
	
	
	public static Drive buildDriveService(String executionGoogleUser) throws IOException {
		Credential credential = authorize(executionGoogleUser);
		return new Drive(HTTP_TRANSPORT, JSON_FACTORY, credential);
	}
	
	public static Credential authorize(String executionGoogleUser) throws IOException  {
		
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
