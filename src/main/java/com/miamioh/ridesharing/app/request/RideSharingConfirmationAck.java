package com.miamioh.ridesharing.app.request;

import com.miamioh.ridesharing.app.entity.Taxi;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RideSharingConfirmationAck {
	
	private String responseId;
	private boolean ackStatus;
	private Taxi taxi;
	private String message;

}
