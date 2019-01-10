package com.miamioh.ridesharing.app.request;

import java.util.Date;

import com.miamioh.ridesharing.app.entity.Event;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class RideSharingRequest {
	
	private String requestID;
	private Event pickUpEvent;
	private Event dropOffEvent;
	private Date timestamp;
	
}
