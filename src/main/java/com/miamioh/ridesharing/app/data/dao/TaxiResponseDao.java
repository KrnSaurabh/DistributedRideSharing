package com.miamioh.ridesharing.app.data.dao;

import java.util.Collection;
import java.util.Set;

import com.miamioh.ridesharing.app.data.entity.TaxiResponse;

public interface TaxiResponseDao {
	
	public void save(TaxiResponse response);
	public Set<String> getResponseIds(String requestID);
	public Iterable<TaxiResponse> getTaxiResponses(Collection<String> taxiResponseIds);
	public void delete(String responseId);
	public void deleteAll(Collection<TaxiResponse> taxiResponseList);
	

}
