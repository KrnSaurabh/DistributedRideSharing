package com.miamioh.ridesharing.app.data.entity;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Data;

@Data
@RedisHash("TaxiHub")
public class Taxi implements Serializable{
	
	@Id
	private String taxiId;
	private String taxiNumber;
	private double longitude;
	private double latitude;
	private String model;
	private Integer noOfPassenger;
	
	public Taxi(String taxiNumber, double longitude, double latitude) {
		super();
		this.taxiNumber = taxiNumber;
		this.longitude = longitude;
		this.latitude = latitude;
		this.noOfPassenger= 0;
	}

	public Taxi() {
		
	}
	
}
