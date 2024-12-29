package com.cakwe.MyMarketU.repository;

import com.cakwe.MyMarketU.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
   boolean existsByEmail(String email);
   boolean existsByNim(String nim); 
   Optional<User> findByEmail(String email);
   Optional<User> findByEmailAndRole_Id(String email, Integer roleId);
   
   //=====
   
   Optional<User> findByNim(String nim);

   @Query("SELECT u FROM User u WHERE u.role.id = :roleId")
   Optional<User> findFirstByRoleId(Integer roleId);
    
   @Query("SELECT u FROM User u WHERE u.role.id = :roleId")
   List<User> findAllByRoleId(Integer roleId);
   
   //=====
   
   long countByRole_Id(Integer roleId);
}