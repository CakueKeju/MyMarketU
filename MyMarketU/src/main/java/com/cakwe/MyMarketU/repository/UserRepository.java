package com.cakwe.MyMarketU.repository;

import com.cakwe.MyMarketU.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
   boolean existsByEmail(String email);
   boolean existsByNim(String nim); 
   Optional<User> findByEmail(String email);
   Optional<User> findByEmailAndRole_Id(String email, Integer roleId);
   long countByRole_Id(Integer roleId);
}