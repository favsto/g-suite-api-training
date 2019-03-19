package it.injenia.training.demo.drive;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.File.Capabilities;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.PermissionList;

public class PermissionExample {

	public static void main(String[] args) throws IOException {
		
		Drive driveService = GdriveService.buildDriveService("test1@injdev.com");
		
		//file capabilities e permission
		getFileCapabilities(driveService, "1REURdMaCzZC_nFI_0_QuR4j1LU0ms0cmbRIjNpnXTGY");
		getFilePermissions(driveService, "1REURdMaCzZC_nFI_0_QuR4j1LU0ms0cmbRIjNpnXTGY");
		
		//Aggiunge permesso
		Permission permission = createFileUserPermission(driveService, "1REURdMaCzZC_nFI_0_QuR4j1LU0ms0cmbRIjNpnXTGY", "test2@injdev.com", "reader");
		getFilePermissions(driveService, "1REURdMaCzZC_nFI_0_QuR4j1LU0ms0cmbRIjNpnXTGY");
		
		//aggiorna permesso
		permission = getPermission(driveService, "1REURdMaCzZC_nFI_0_QuR4j1LU0ms0cmbRIjNpnXTGY", permission.getId());
		updaterPermissionRole(driveService, "1REURdMaCzZC_nFI_0_QuR4j1LU0ms0cmbRIjNpnXTGY", permission.getId(), "writer");
		
		//elimina permesso
		permission = getPermission(driveService, "1REURdMaCzZC_nFI_0_QuR4j1LU0ms0cmbRIjNpnXTGY", permission.getId());
		deletePermission(driveService, "1REURdMaCzZC_nFI_0_QuR4j1LU0ms0cmbRIjNpnXTGY", Arrays.asList(permission.getId()));
		getFilePermissions(driveService, "1REURdMaCzZC_nFI_0_QuR4j1LU0ms0cmbRIjNpnXTGY");
	}
	
	public static Capabilities getFileCapabilities(Drive driveService, String fileId) throws IOException {
		
		Capabilities result = null;
		
		File file = driveService.files().get(fileId).setFields("capabilities").execute();
		
		if(file != null) {
			result = file.getCapabilities();
			
			System.out.println(result.toPrettyString());
		}
		
		return result; 
	}
	
	public static PermissionList getFilePermissions(Drive driveService, String fileId) throws IOException {
		
		PermissionList permissions = driveService.permissions().list(fileId).execute();
		
		if(permissions != null) {
			for(Permission permission : permissions.getPermissions()) {
				System.out.print(permission.toPrettyString()); //non mostra emailAddress o domain
			}
		}
		
		return permissions;
	}
	
	public static Permission getPermission(Drive driveService, String fileId, String permissionId) throws IOException {
		
		Permission permission = driveService.permissions().get(fileId, permissionId).execute();
		
		System.out.println(permission.toPrettyString());

		return permission;
	}
	
	public static Permission createFileUserPermission(Drive driveService, String fileId, String userId, String role) throws IOException {
		
		Permission permission = new Permission();
		permission.setType("user");
		permission.setRole(role);
		permission.setEmailAddress(userId);
		
		permission = driveService.permissions().create(fileId, permission).setSendNotificationEmail(false).execute();
		
		System.out.println("Permission id: " + permission.getId());
		
		return permission;
	}
	
	public static Permission updaterPermissionRole(Drive driveService, String fileId, String permissionId, String role) throws IOException {
		
		Permission permission = new Permission();
		permission.setRole(role);
		
		permission = driveService.permissions().update(fileId, permissionId, permission).execute();
		
		System.out.println("Permission id: " + permission.getId());

		return permission;
	}
	
	public static void deletePermission(Drive driveService, String fileId, List<String> permissionsIds) throws IOException {
	
		BatchRequest batch = driveService.batch();
		
		VoidCallback callback = new VoidCallback();
		
		for(String id : permissionsIds) {
			driveService.permissions().delete(fileId, id).queue(batch, callback);;
		}
		
		batch.execute();
	}
	
}


class VoidCallback extends JsonBatchCallback<Void> {

	public void onSuccess(Void result, HttpHeaders headers) throws IOException {
		
		
	}

	@Override
	public void onFailure(GoogleJsonError e, HttpHeaders headers) throws IOException {
		System.err.println(e.getMessage());
	}
	
}

class PermissionCallback extends JsonBatchCallback<Permission> {
	  
	public void onSuccess(Permission permission, com.google.api.client.http.HttpHeaders headers) {
		System.out.println("Permission ID: " + permission.getId());
	}

	@Override
	public void onFailure(GoogleJsonError e, com.google.api.client.http.HttpHeaders headers) {
		System.err.println(e.getMessage());
	}
}