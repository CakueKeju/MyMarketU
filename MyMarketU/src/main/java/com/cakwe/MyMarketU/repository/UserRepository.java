package com.cakwe.MyMarketU.repository;

import com.cakwe.MyMarketU.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  boolean existsByEmail(String email);
  boolean existsByNim(String nim); 
  Optional<User> findByEmail(String email);
  Optional<User> findByEmailAndRole_Id(String email, Integer roleId);
  long countByRole_Id(Integer roleId);
  
  // Tambahan untuk customer management
  long countByCreatedAtGreaterThanEqual(LocalDateTime date);
  long countByStatus(String status);
  List<User> findByStatus(String status);
  List<User> findByNamaLengkapContainingOrEmailContaining(String nama, String email);
}