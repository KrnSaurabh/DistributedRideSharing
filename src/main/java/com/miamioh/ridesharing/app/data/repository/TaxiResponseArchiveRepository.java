package com.miamioh.ridesharing.app.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.miamioh.ridesharing.app.data.entity.TaxiResponseArchive;

@Repository
public interface TaxiResponseArchiveRepository extends CrudRepository<TaxiResponseArchive, String>{

}
