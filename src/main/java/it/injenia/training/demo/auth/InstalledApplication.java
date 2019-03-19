package it.injenia.training.demo.auth;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class InstalledApplication {
	/**
	 * Be sure to specify the name of your application. If the application name is
	 * {@code null} or blank, the application will log a warning. Suggested format
	 * is "MyCompany-ProductName/1.0".
	 */
	private static final String APPLICATION_NAME = "Installed Application Demo";

	/** Directory to store user credentials. */
	private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"),
			".store/installed_demo");

	/**
	 * Global instance of the {@link DataStoreFactory}. The best practice is to make
	 * it a single globally shared instance across your application.
	 */
	private static FileDataStoreFactory dataStoreFactory;

	/** Global instance of the HTTP transport. */
	private static HttpTransport httpTransport;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	/** This is a Drive example, thus this is the Driver APIs wrapper */
	public static Drive drive;
	
	/**
	 * Utility: creates a @Credential object with all I need in order to call Drive APIs 
	 * @return credetials object
	 * @throws Exception if something goes wrong
	 */
	private static Credential authorize() throws Exception {
		
		// load client secrets
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
				new InputStreamReader(InstalledApplication.class.getResourceAsStream("/client_secret.json")));
		if (clientSecrets.getDetails().getClientId().startsWith("Enter")
				|| clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
			System.out.println("Enter Client ID and Secret from https://code.google.com/apis/console/?api=drive "
					+ "into /src/main/resources/client_secrets.json");
			System.exit(1);
		}
		
		// set up authorization code flow
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY,
				clientSecrets, Collections.singleton(DriveScopes.DRIVE)).setDataStoreFactory(dataStoreFactory).build();
		
		// authorize
		return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
	}

	/**
	 * This is a simple demo about Drive APIs
	 * 
	 * @param driveService the service wrapper authorized instance
	 * @param query the term to search for
	 * @param nextPageToken pagination next token
	 * @return a list of files
	 * @throws IOException if something goes wrong
	 */
	public static FileList getFiles(Drive driveService, String query, String nextPageToken) throws IOException {

		FileList result = driveService.files().list().setQ(query).setSpaces("drive")
				.setPageToken(nextPageToken).execute();

		if (result.getFiles() != null) {
			for (File file : result.getFiles()) {
				System.out.println(file.toPrettyString());
			}
		}
		System.out.println(result.getNextPageToken());

		return result;
	}

	
	public static void main(String[] args) {
		try {
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

			// Installed Application authorization
			Credential credential = authorize();

			// set up global Plus instance
			drive = new Drive.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME)
					.build();

			// run commands
			FileList list = getFiles(drive, "name = 'Codice'", null);
			
			System.out.println("Items retrieved: " + list.size());

			// success!
			return;
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
		}
		System.exit(1);
	}

}
