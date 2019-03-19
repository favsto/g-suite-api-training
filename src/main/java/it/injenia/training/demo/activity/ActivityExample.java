package it.injenia.training.demo.activity;

import java.io.IOException;

import com.google.api.services.driveactivity.v2.DriveActivity;
import com.google.api.services.driveactivity.v2.model.ConsolidationStrategy;
import com.google.api.services.driveactivity.v2.model.Legacy;
import com.google.api.services.driveactivity.v2.model.QueryDriveActivityRequest;
import com.google.api.services.driveactivity.v2.model.QueryDriveActivityResponse;

public class ActivityExample {

	public static void main(String[] args) throws IOException {
		
		DriveActivity driveActivityService = ActivityService.buildDriveActivityService("test1@injdev.com");

//		getItemsActivities(driveActivityService, "1REURdMaCzZC_nFI_0_QuR4j1LU0ms0cmbRIjNpnXTGY", null, true);
		
//		getFolderActivities(driveActivityService, "0B27aXb3HKbGgUDQ3blhxUEhWRGc", null, false);
		
//		getFolderActivities(driveActivityService, "root", null, true);
		
//		getItemsActivities(driveActivityService, "1REURdMaCzZC_nFI_0_QuR4j1LU0ms0cmbRIjNpnXTGY", "time >= \"2019-03-10T09:00:00+01:00\"", true);

//		getItemsActivities(driveActivityService, "1REURdMaCzZC_nFI_0_QuR4j1LU0ms0cmbRIjNpnXTGY", "detail.action_detail_case: PERMISSION_CHANGE", true);

//		getItemsActivities(driveActivityService, "1REURdMaCzZC_nFI_0_QuR4j1LU0ms0cmbRIjNpnXTGY", "detail.action_detail_case: MOVE", true);
		
		getItemsActivities(driveActivityService, "1REURdMaCzZC_nFI_0_QuR4j1LU0ms0cmbRIjNpnXTGY", "-detail.action_detail_case: PERMISSION_CHANGE", true);
		
//		getItemsActivities(driveActivityService, "1REURdMaCzZC_nFI_0_QuR4j1LU0ms0cmbRIjNpnXTGY", "detail.action_detail_case: (MOVE PERMISSION_CHANGE)", true);

		//CREATE EDIT MOVE RENAME DELETE RESTORE PERMISSION_CHANGE COMMENT REFERENCE SETTINGS_CHANGE DLP_CHANGE
	}
	
	public static QueryDriveActivityResponse getItemsActivities(DriveActivity driveActivityService, String itemId, String filter, boolean grouping) throws IOException {
		
		QueryDriveActivityRequest request = new QueryDriveActivityRequest();
		request.setItemName("items/" + itemId); //se non specificato ancestorName item/root
		
		if(grouping) {
			request.setConsolidationStrategy(new ConsolidationStrategy().setLegacy(new Legacy()));
		}
		
		request.setFilter(filter);
		
		QueryDriveActivityResponse response = driveActivityService.activity().query(request).execute();

		if(response.getActivities() != null) {
			for(com.google.api.services.driveactivity.v2.model.DriveActivity activity : response.getActivities()) {
				System.out.println(activity.toPrettyString());
			}
		}
		System.out.println(response.getNextPageToken());

		return response;
	}
	
	public static QueryDriveActivityResponse getFolderActivities(DriveActivity driveActivityService, String folderId, String filter, boolean grouping) throws IOException {
		
		QueryDriveActivityRequest request = new QueryDriveActivityRequest();
		request.setAncestorName("items/" + folderId); //se non specificato item/root
		
		if(grouping) {
			request.setConsolidationStrategy(new ConsolidationStrategy().setLegacy(new Legacy()));
		}
		
		request.setFilter(filter);
		
		QueryDriveActivityResponse response = driveActivityService.activity().query(request).execute();
		
		if(response.getActivities() != null) {
			for(com.google.api.services.driveactivity.v2.model.DriveActivity activity : response.getActivities()) {
				System.out.println(activity.toPrettyString());
			}
		}
		System.out.println(response.getNextPageToken());

		return response;
	}
}
