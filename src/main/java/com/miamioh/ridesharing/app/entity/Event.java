package com.miamioh.ridesharing.app.entity;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class Event {
	
	private String requestId;
	private double longitude;
	private double latitude;
	private Date eventTime;
	private long tolerance;
	private boolean isPickup;
	private int index;
	private Date timeOfOccurance;
	private boolean isCompleted;
	
}
