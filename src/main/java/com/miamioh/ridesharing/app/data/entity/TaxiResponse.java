package com.miamioh.ridesharing.app.data.entity;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@RedisHash(value = "TaxiResponse", timeToLive=90L)
public class TaxiResponse implements Serializable {
	
	@Id
	private String responseId;
	private String requestId;
	private String taxiId;
	private String taxiNumber;
	private String taxiModel;
	private int availableSeats;
	private int pickUpIndex;
	private int dropIndex;
	private Long timeToDestinationInMinutes;
	private double distanceInKms;
	private double cost;
	private Long pickTimeInMinutes;
	private Double psoResponseTimeInSeconds;
	
}
