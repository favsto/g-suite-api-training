package it.injenia.training.demo.drive;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

public class FileExample {

	public static void main(String[] args) throws IOException {
		
		Drive driveService = GdriveService.buildDriveService("test1@injdev.com");
		
		//file data
		File file = getFileMetadata(driveService, "0B27aXb3HKbGgNDliN0JmUGtHRm8", null);
//		File file = getFileMetadata(driveService, "0B27aXb3HKbGgNDliN0JmUGtHRm8", "id, name, webContentLink, webViewLink");	
//		System.out.println(file.getWebContentLink());
//		System.out.println(file.getWebViewLink());

		//carico file
		InputStream fileInputStream = new FileInputStream(new java.io.File("Mojo.jpg"));
		uploadFile(driveService, "mojo_upload.jpg", null, fileInputStream, "image/jpg");
		
		fileInputStream = new FileInputStream(new java.io.File("csv_example.csv"));
		file = uploadCsvFile(driveService, fileInputStream);
		
		//export
		ByteArrayOutputStream outputStream = (ByteArrayOutputStream) exportFileContentToPdf(driveService, file.getId());
		fileInputStream = convertToInputStream(outputStream);
		uploadFile(driveService, "my_pdf.pdf", null, fileInputStream, "application/pdf");
		
		//folder
		File folder1 = createFolder(driveService, "my_folder");
		
		fileInputStream = new FileInputStream(new java.io.File("Mojo.jpg"));
		file = uploadFile(driveService, "mojo_upload.jpg", folder1.getId(), fileInputStream, "image/jpg");
		
		//move file
		File folder2 = createFolder(driveService, "my_folder2");
		moveFile(driveService, file.getId(), folder1.getId(), folder2.getId());
		moveFile(driveService, file.getId(), null, folder1.getId());

		//add custom properties
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("my_prop1", "value of my prop10");
		properties.put("my_prop2", "value of my prop20");
		updateFileProperties(driveService, "0B27aXb3HKbGgNDliN0JmUGtHRm8", properties);
		file = getFileMetadata(driveService, "0B27aXb3HKbGgNDliN0JmUGtHRm8", "id, properties");	
		System.out.println(file.getProperties().toString());
		
		//remove custom properties
		removeFileProperties("test1@injdev.com", "0B27aXb3HKbGgNDliN0JmUGtHRm8", Arrays.asList("my_prop1")); 
		file = getFileMetadata(driveService, "0B27aXb3HKbGgNDliN0JmUGtHRm8", "id, properties");
		System.out.println(file.getProperties().toString());
	}
	
	public static File getFileMetadata(Drive driveService, String fileId, String fields) throws IOException {
		
		File file = driveService.files().get(fileId)
			.setFields(fields)
			.execute();
		
		if(fields == null) {
			System.out.println("File ID: " + file.toPrettyString());
		}
		
		return file;
	}
	
	public static File uploadFile(Drive driveService, String name, String parentid, InputStream fileData, String contentType) throws IOException {
		
		File fileMetadata = new File();
		fileMetadata.setName(name);
		if(parentid != null) {
			fileMetadata.setParents(Collections.singletonList(parentid));
		}
				
		File file = driveService.files().create(fileMetadata, new InputStreamContent(contentType, fileData))
		    .setFields("id")
		    .execute();
		
		System.out.println("File ID: " + file.getId());
		
		return file;
	}
	
	public static File uploadCsvFile(Drive driveService, InputStream fileData) throws IOException {
	
		File fileMetadata = new File();
		fileMetadata.setName("My Report");
		fileMetadata.setMimeType("application/vnd.google-apps.spreadsheet");
	
		File file = driveService.files().create(fileMetadata, new InputStreamContent("text/csv", fileData))
		    .setFields("id")
		    .execute();
		
		System.out.println("File ID: " + file.getId());
		
		return file;
	}
	
	public static OutputStream downloadFileContent(Drive driveService, String fileId) throws IOException {
		
		OutputStream outputStream = new ByteArrayOutputStream();

		driveService.files().get(fileId)
	    	.executeMediaAndDownloadTo(outputStream);
		
		return outputStream;
	}
	
	public static OutputStream exportFileContentToPdf(Drive driveService, String fileId) throws IOException {
		
		OutputStream outputStream = new ByteArrayOutputStream();
		
		driveService.files().export(fileId, "application/pdf")
		    .executeMediaAndDownloadTo(outputStream);
		
		return outputStream;
	}
	
	public static File createFolder(Drive driveService, String folderName) throws IOException {
		
		File fileMetadata = new File();
		fileMetadata.setName(folderName);
		fileMetadata.setMimeType("application/vnd.google-apps.folder");
		
		File file = driveService.files()
				.create(fileMetadata)
			    .execute();
		
		System.out.println("Folder ID: " + file.getId());
		
		return file;
	}
	
	public static void moveFile(Drive driveService, String fileId, String parentsToRemove, String parentsToAdd) throws IOException {
		
		driveService.files().update(fileId, null)
		    .setAddParents(parentsToAdd) //lista separata da ,
		    .setRemoveParents(parentsToRemove) //lista separata da ,
		    .execute();
		
	}
	
	public static void updateFileProperties(Drive driveService, String fileId, Map<String, String> properties) throws IOException {
		
		File file = new File();
		file.setProperties(properties);
		
		file = driveService.files().update(fileId, file).setFields("properties").execute();
		
		System.out.println(file.getProperties().toString());
	}
	
	//Workaround
	public static void removeFileProperties(String executionGoogleUser, String fileId, List<String> propertiesToRemoveNames) throws IOException {		
		
		String TARGET_URL = String.format("https://www.googleapis.com/drive/v3/files/%s?",fileId);
		
		Credential credential = GdriveService.authorize(executionGoogleUser);
		credential.refreshToken();
		String accessToken = credential.getAccessToken();
		
		URL serverUrl = new URL(TARGET_URL );
		URLConnection urlConnection = serverUrl.openConnection();
		HttpURLConnection httpConnection = (HttpURLConnection)urlConnection;
		
		//Workaround per utilizzo di patch
		httpConnection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
		httpConnection.setRequestMethod("POST");
		httpConnection.setRequestProperty("Content-Type", "application/json");
		
		//necessario per inviare body
		httpConnection.setDoOutput(true);

		httpConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
		
		//Genera la stringa da inserire come corpo della request
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{\"properties\": {");
		
		int i = 1;
		for(String property : propertiesToRemoveNames) {
			stringBuilder.append(" \"").append(property).append("\" : null");
			if(i != propertiesToRemoveNames.size()) {
				stringBuilder.append(", ");
			}
			i++;
		}
		stringBuilder.append("}}");
		
		//inserisce il corpo della richiesta
		BufferedWriter httpRequestBodyWriter = new BufferedWriter(new
                OutputStreamWriter(httpConnection.getOutputStream()));
		httpRequestBodyWriter.write(stringBuilder.toString());
		httpRequestBodyWriter.close();
		
		//esecuzione
		httpConnection.getResponseMessage();
	
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static InputStream convertToInputStream(ByteArrayOutputStream out) {
		
		return new ByteArrayInputStream(out.toByteArray());
	}


}



