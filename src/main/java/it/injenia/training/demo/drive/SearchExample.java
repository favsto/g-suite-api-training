package it.injenia.training.demo.drive;

import java.io.IOException;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class SearchExample {

	public static void main(String args[]) throws IOException {
		
		Drive driveService = GdriveService.buildDriveService("test1@injdev.com");

//		getFiles(driveService, "mimeType='image/jpeg'", null);
		
//		getFiles(driveService, "name = 'Codice'", null);
//		getFiles(driveService, "name contains 'Codice'", null);
//		getFiles(driveService, "name contains 'manuale sql'", null);
//		getFiles(driveService, "name contains 'Codice' or name contains 'Deontologico'", null);

		
//		getFiles(driveService, "mimeType = 'application/vnd.google-apps.folder'", null);

//		getFiles(driveService, "fullText contains 'sql'", null);
		
//		getFiles(driveService, "'0B27aXb3HKbGgUDQ3blhxUEhWRGc' in parents", null);
		
//		getFiles(driveService, "'0B27aXb3HKbGgUDQ3blhxUEhWRGc' in parents and trashed = false", null);

//		getFiles(driveService, "modifiedTime > '2019-03-15T09:00:00'", null); //UTC
//		getFiles(driveService, "modifiedTime > '2019-03-15T09:00:00+01:00'", null); //CET

		getFiles(driveService, "properties has {key='my_prop2' and value='value of my prop20'}", null); //CET

		
	}
	
	
	public static FileList getFiles(Drive driveService, String query, String nextPageToken) throws IOException {
		
		 FileList result = driveService.files().list()
			      .setQ(query)
			      .setSpaces("drive")
//			      .setCorpora("user") //default user
//			      .setFields("nextPageToken, files(id, name)")
			      .setPageToken(nextPageToken)
			      .execute();
		 
		 if(result.getFiles() != null) {
			 for(File file : result.getFiles()) {
				 System.out.println(file.toPrettyString());
			 }
		 }
		 System.out.println(result.getNextPageToken());
		 
		 return result;
	}
	
}
