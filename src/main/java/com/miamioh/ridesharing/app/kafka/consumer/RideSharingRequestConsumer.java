package com.miamioh.ridesharing.app.kafka.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.miamioh.ridesharing.app.entity.Taxi;
import com.miamioh.ridesharing.app.request.RideSharingRequest;
import com.miamioh.ridesharing.app.utilities.helper.TaxiUtility;

@Component
public class RideSharingRequestConsumer {
	
	@Autowired
	private TaxiUtility taxiUtility;
	
	@KafkaListener(topics="${kafka.topic}", containerFactory="batchFactory")
	public void consumeRideSharingRequest(RideSharingRequest rideSharingRequest, Acknowledgment ack){
		
		taxiUtility.shareRide(rideSharingRequest);
		ack.acknowledge();
	}
	
	@KafkaListener(topics="${kafka.taxi.topic}", containerFactory="registerTaxi")
	public void registerTaxi(Taxi taxi, Acknowledgment ack){
		
		taxiUtility.registerTaxi(taxi);
		ack.acknowledge();
	}
	
}
