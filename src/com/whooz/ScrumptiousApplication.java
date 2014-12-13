package com.whooz;

import java.util.List;

import com.facebook.model.GraphPlace;
import com.facebook.model.GraphUser;
import com.parse.Parse;

import android.app.Application;
import org.acra.*;
import org.acra.annotation.*;

@ReportsCrashes(
		formUri = "https://whooz.cloudant.com/acra-whooz/_design/acra-storage/_update/report",
		reportType = org.acra.sender.HttpSender.Type.JSON,
		httpMethod = org.acra.sender.HttpSender.Method.PUT,
		formUriBasicAuthLogin = "ldonedifeneachapordstrin",
		formUriBasicAuthPassword = "8QwxAUG17NJlWhKifXfR4Koo",
		formKey = "", // This is required for backward compatibility but not used
		customReportContent = {
				ReportField.APP_VERSION_CODE,
				ReportField.APP_VERSION_NAME,
				ReportField.ANDROID_VERSION,
				ReportField.PACKAGE_NAME,
				ReportField.REPORT_ID,
				ReportField.BUILD,
				ReportField.STACK_TRACE
		},
		mode = ReportingInteractionMode.TOAST,
		resToastText = R.string.toast_crash
		)
public class ScrumptiousApplication extends Application {


	private GraphPlace selectedPlace;
	private List<GraphUser> selectedUsers;
	private String acraUsername="ldonedifeneachapordstrin";
	private String acraPass="8QwxAUG17NJlWhKifXfR4Koo";
	@Override
	public void onCreate() {
		super.onCreate();
		ACRA.init(this);

		Parse.enableLocalDatastore(this);
		Parse.initialize(this, "egR0YXXdO7qd4BPhJ6k7YYwgl72mtXNwoQht24tW",
				"C6IfZipQpioUkpOhO9XVGDsPk4Yt9tQqY6MylVNV");
	}


	public List<GraphUser> getSelectedUsers() {
		return selectedUsers;
	}

	public void setSelectedUsers(List<GraphUser> users) {
		selectedUsers = users;
	}

	public GraphPlace getSelectedPlace() {
		return selectedPlace;
	}

	public void setSelectedPlace(GraphPlace place) {
		this.selectedPlace = place;
	}

}
