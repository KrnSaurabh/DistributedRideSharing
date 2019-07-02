package com.miamioh.ridesharing.app.data.entity;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.miamioh.ridesharing.app.entity.Event;

import lombok.Getter;
import lombok.Setter;

@RedisHash(value="TempScheduledEventList", timeToLive=300L)
@Getter @Setter
public class TempScheduledEventList implements Serializable{
	
	@Id
	private String responseId;
	private String taxiId;
	private Event pickUpEvent;
	private Event dropEvent;
	
}
