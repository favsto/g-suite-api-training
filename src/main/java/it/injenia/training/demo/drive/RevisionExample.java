package it.injenia.training.demo.drive;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Revision;
import com.google.api.services.drive.model.RevisionList;

public class RevisionExample {

	public static void main(String[] args) throws IOException {
		
		Drive driveService = GdriveService.buildDriveService("test1@injdev.com");

		//Caricamento file
		InputStream fileInputStream = new FileInputStream(new java.io.File("Mojo.jpg"));
		File file = FileExample.uploadFile(driveService, "version1", null, fileInputStream, "image/jpg");
		
		//Ritrovamento versioni
		getFileRevisions(driveService, file.getId());
		
		//Caricamento nuova versione
		fileInputStream = new FileInputStream(new java.io.File("Mojo.jpg"));
		createNewVersion(driveService, file.getId(),  "version2", fileInputStream, "image/jpg", false)	;
		RevisionList list = getFileRevisions(driveService, file.getId());

		//Download versioni
		downloadRevision(driveService, file.getId(), list.getRevisions().get(0).getId());
		
		//dettaglio versione
		getRevision(driveService, file.getId(), list.getRevisions().get(1).getId());
		
		//elimina versione
		deleteRevision(driveService, file.getId(), list.getRevisions().get(0).getId());
		getFileRevisions(driveService, file.getId());
	}
	
	private static RevisionList getFileRevisions(Drive driveService, String fileId) throws IOException {
	
		RevisionList revisions = driveService.revisions().list(fileId).execute();
		
		if(revisions != null) {
			for(Revision revision : revisions.getRevisions()) {
				System.out.println(revision.toPrettyString());
			}
		}
		
		return revisions;
	}
	
	private static Revision getRevision(Drive driveService, String fileId, String revisionId) throws IOException {
		
		Revision revision = driveService.revisions().get(fileId, revisionId).execute();
		
		System.out.println(revision.toPrettyString());
		
		return revision;
	}
	
	private static OutputStream downloadRevision(Drive driveService, String fileId, String revisionId) throws IOException {
		
		OutputStream output = new ByteArrayOutputStream();
		
		driveService.revisions().get(fileId, revisionId).executeAndDownloadTo(output);
				
		return output;
	}
	
	private static File createNewVersion(Drive driveService, String fileId, String name, InputStream fileData, String contentType, boolean keepForever) throws IOException {
		
		File fileMetadata = new File();
		fileMetadata.setName(name);
				
		File file = driveService.files().update(fileId, fileMetadata, new InputStreamContent(contentType, fileData))
				.setKeepRevisionForever(keepForever)
				.execute();
		
		System.out.println("File ID: " + file.getId());
		
		return file;
	}
	
	private static void deleteRevision(Drive driveService, String fileId, String revisionId) throws IOException {
		
		driveService.revisions().delete(fileId, revisionId).execute();
	}
}
