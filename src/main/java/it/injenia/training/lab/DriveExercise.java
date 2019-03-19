package it.injenia.training.lab;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;

public class DriveExercise {

	public static void main(String[] args) throws Exception {
	
		Drive driveService = ExerciseAuth.getDriveService();
		
		
//		InputStream fileInputStream = new FileInputStream(new java.io.File("Mojo.jpg"));
//		File uploadedFile = uploadFile(driveService, "mojo_upload.jpg", null, fileInputStream, "image/jpg");
	
//		File folder = createFolder(driveService, "my_folder");
		
//		moveFile(driveService, uploadedFile.getId(), null, folder.getId());

//		createFileUserPermissionsBatch(driveService, uploadedFile.getHeadRevisionId(), userId, "reader");
		
//		Map<String, String> properties = new HashMap<String, String>();
//		properties.put("my_prop1", "value of my prop10");
//		properties.put("my_prop2", "value of my prop20");
//		updateFileProperties(driveService, "0B27aXb3HKbGgNDliN0JmUGtHRm8", properties);
		
//		getFiles(driveService, "QUERY TODO", null);
	}
		
	public static File uploadFile(Drive driveService, String name, String parentid, InputStream fileData, String contentType) throws IOException {
		
		File fileMetadata = new File();
		fileMetadata.setName(name);
		if(parentid != null) {
			fileMetadata.setParents(Collections.singletonList(parentid));
		}
				
		File file = driveService.files().create(fileMetadata, new InputStreamContent(contentType, fileData))
		    .execute();
				
		return file;
	}
	
	public static File createFolder(Drive driveService, String folderName) throws IOException {
		
		File fileMetadata = new File();
		fileMetadata.setName(folderName);
		fileMetadata.setMimeType("application/vnd.google-apps.folder");
		
		File file = driveService. //TODO
				
		return file;
	}
	
	public static void moveFile(Drive driveService, String fileId, String parentsToRemove, String parentsToAdd) throws IOException {
		
		//TODO
		
	}
	
	
	public static Permission createFileUserPermission(Drive driveService, String fileId, String userId, String role) throws IOException {
		
		Permission permission = new Permission();
		permission.setType("user");
		permission.setRole(role);
		permission.setEmailAddress(userId);
		
		permission = driveService.permissions().create(fileId, permission).setSendNotificationEmail(false).execute();
				
		return permission;
	}
	
	public static void createFileUserPermissionsBatch(Drive driveService, String fileId, String userId, String role) throws IOException {
		
		BatchRequest batchRequest = driveService.batch();

		//TODO
				
		batchRequest.execute();
		
	}
	
	public static void deletePermission(Drive driveService, String fileId, List<String> permissionsIds) throws IOException {
		
		BatchRequest batch = driveService.batch();
		
		VoidCallback callback = new VoidCallback();
		
		for(String id : permissionsIds) {
			driveService.permissions().delete(fileId, id).queue(batch, callback);;
		}
		
		batch.execute();
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
	
	public static void updateFileProperties(Drive driveService, String fileId, Map<String, String> properties) throws IOException {
		
		File file = new File();
		file.setProperties(properties);
		
		file = driveService.files().update(fileId, file).setFields("properties").execute();
		
		System.out.println(file.getProperties().toString());
	}
}
