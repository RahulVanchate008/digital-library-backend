package com.webprogramming.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.webprogramming.project.model.EtdDto;

@Repository
public interface EtdRepository extends JpaRepository<EtdDto, Integer>{

}
