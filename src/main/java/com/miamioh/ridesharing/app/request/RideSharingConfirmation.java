package com.miamioh.ridesharing.app.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RideSharingConfirmation {
	
	private String requestId;
	private String responseId;
	private String taxiId;
	private boolean isConfirmed;

}
