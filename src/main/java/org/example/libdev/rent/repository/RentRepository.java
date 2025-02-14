package org.example.libdev.rent.repository;

import org.example.libdev.rent.entity.Rent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentRepository extends JpaRepository<Rent, Long> {

//    List<Rent> findByUser_UserId(Long userId);
}
