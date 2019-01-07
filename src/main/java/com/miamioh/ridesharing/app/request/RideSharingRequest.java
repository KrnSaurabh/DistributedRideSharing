package com.miamioh.ridesharing.app.request;

import java.util.Date;

import com.miamioh.ridesharing.app.entity.Event;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RideSharingRequest {
	
	private String requestID;
	private Event pickUpEvent;
	private Event dropOffEvent;
	private Date timestamp;
	
}
