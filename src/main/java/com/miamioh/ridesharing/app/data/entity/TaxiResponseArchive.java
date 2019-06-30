package com.miamioh.ridesharing.app.data.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@RedisHash(value = "TaxiResponseArchive")
public class TaxiResponseArchive {
	
	@Id
	private String responseId;
	private TaxiResponse taxiResponse;

}
