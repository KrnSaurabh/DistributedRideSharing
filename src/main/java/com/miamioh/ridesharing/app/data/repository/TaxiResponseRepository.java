package com.miamioh.ridesharing.app.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.miamioh.ridesharing.app.data.entity.TaxiResponse;

@Repository
public interface TaxiResponseRepository extends CrudRepository<TaxiResponse	, String> {

}
