package com.miamioh.ridesharing.app.data.entity;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.miamioh.ridesharing.app.request.RideSharingRequest;

import lombok.Getter;
import lombok.Setter;

@RedisHash(value="RideSharingRequestHash", timeToLive=300L)
@Getter @Setter
public class RideSharingRequestHash implements Serializable{
	
	@Id
	private String requestId;
	private RideSharingRequest rideSharingRequest;

}
