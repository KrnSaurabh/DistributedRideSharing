package com.miamioh.ridesharing.app.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.miamioh.ridesharing.app.constants.AppConstants;
import com.miamioh.ridesharing.app.data.dao.TaxiResponseDao;
import com.miamioh.ridesharing.app.data.entity.TaxiResponse;
import com.miamioh.ridesharing.app.data.entity.TempScheduledEventList;
import com.miamioh.ridesharing.app.data.repository.TempScheduledEventListRepository;
import com.miamioh.ridesharing.app.entity.Event;
import com.miamioh.ridesharing.app.entity.Taxi;
import com.miamioh.ridesharing.app.request.RideSharingConfirmation;
import com.miamioh.ridesharing.app.request.RideSharingConfirmationAck;
import com.miamioh.ridesharing.app.utilities.helper.TaxiUtility;

@RestController
public class TaxiResponseController {
	
	@Autowired
	private TaxiUtility taxiUtility;
	
	@Autowired
	private TaxiResponseDao taxiResponseDao;
	
	@Autowired
	private TempScheduledEventListRepository tempScheduledEventListRepository;
	
	/*@Resource(name="redisTemplate")
	private SetOperations<String, Event> setOperations;*/
	
	@Resource(name="redisTemplate")
	private ZSetOperations<String, Event> zSetOperations;
	
	@GetMapping(value = "/RideSharing/TaxiResponses/{request_id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TaxiResponse getTaxiResponsesByRequestId(@NotBlank @PathVariable(value="request_id") String requestId){
		Iterable<TaxiResponse> taxiResponses = taxiResponseDao.getTaxiResponses(taxiResponseDao.getResponseIds(requestId));
		List<TaxiResponse> taxiResponsesList = new ArrayList<>();
		taxiResponses.forEach(a -> taxiResponsesList.add(a));
		Collections.sort(taxiResponsesList, ((a,b)->{
			int result = Double.valueOf(a.getCost()).compareTo(Double.valueOf(b.getCost()));
			if(result==0) {
				result = Long.valueOf(a.getPickTimeInMinutes()).compareTo(Long.valueOf(b.getPickTimeInMinutes()));
			}
			if(result == 0) {
				result = Long.valueOf(a.getTimeToDestinationInMinutes()).compareTo(Long.valueOf(b.getTimeToDestinationInMinutes()));
			}
			if(result == 0) {
				result = Integer.valueOf(b.getAvailableSeats()).compareTo(Integer.valueOf(a.getAvailableSeats()));
			}
			return result;
		}));
		return taxiResponsesList.get(0);
	}
	
	@GetMapping(value="/RideSharing/Taxis", produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<Taxi> getAllTaxi(){
		return taxiUtility.getAllTaxi();
	}
	
	
	@PostMapping(value = "/RideSharing/RideConfirmation", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public RideSharingConfirmationAck confirmRide(@RequestBody RideSharingConfirmation rideSharingConfirmation) {
		
		RideSharingConfirmationAck ack = new RideSharingConfirmationAck();
		ack.setResponseId(rideSharingConfirmation.getResponseId());
		Taxi taxi = taxiUtility.getTaxiInstance(rideSharingConfirmation.getTaxiId());
		int noOfPassenger = taxi.getNoOfPassenger().get();
		if(rideSharingConfirmation.isConfirmed() && noOfPassenger < AppConstants.TAXI_MAX_CAPACITY) {
			
			 Optional<TempScheduledEventList> findById = tempScheduledEventListRepository.findById(rideSharingConfirmation.getResponseId());
			 findById.ifPresent(a -> {
				 //setOperations.add(rideSharingConfirmation.getTaxiId(), a.getPickUpEvent());// can be used sorted set to sort all events based on timestamp
				 // setOperations.add(rideSharingConfirmation.getTaxiId(), a.getDropEvent());
				 zSetOperations.add(rideSharingConfirmation.getTaxiId(), a.getPickUpEvent(), a.getPickUpEvent().getIndex());
				 zSetOperations.add(rideSharingConfirmation.getTaxiId(), a.getDropEvent(), a.getDropEvent().getIndex());
				 taxi.getNoOfPassenger().incrementAndGet();
			 });
			 
			 if(findById.isPresent()) {
				 ack.setAckStatus(true);
				 ack.setTaxi(taxiUtility.getTaxiInstance(rideSharingConfirmation.getTaxiId()));
				 ack.setMessage("Booking Confirmed");
			 }else {
				 ack.setAckStatus(false);
				 ack.setMessage("Timed Out");
			 }
		}else {
			ack.setAckStatus(false);
			ack.setMessage("Taxi Max capacity reached");
		}
		return ack;
	}
}
