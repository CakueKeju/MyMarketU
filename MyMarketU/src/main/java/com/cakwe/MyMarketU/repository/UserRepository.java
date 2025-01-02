package com.cakwe.MyMarketU.repository;

import com.cakwe.MyMarketU.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;

public interface UserRepository extends JpaRepository<User, Long> {
   // Existing methods
   boolean existsByEmail(String email);
   boolean existsByNim(String nim); 
   Optional<User> findByEmail(String email);
   Optional<User> findByEmailAndRole_Id(String email, Integer roleId);
   Optional<User> findByNim(String nim);
   Optional<User> findById(Long id); //aaaaaaaaaaaaaaaaaaaaaaa
   
   @Query("SELECT u FROM User u WHERE u.role.id = :roleId")
   Optional<User> findFirstByRoleId(Integer roleId);
    
   @Query("SELECT u FROM User u WHERE u.role.id = :roleId")
   List<User> findAllByRoleId(Integer roleId);
   
   long countByRole_Id(Integer roleId);

   // New methods for customer management
   @Query("SELECT u FROM User u WHERE u.role.id = :roleId AND u.createdAt >= :startDate")
   List<User> findNewCustomersThisMonth(Integer roleId, LocalDateTime startDate);

   @Query("SELECT u FROM User u WHERE u.role.id = :roleId AND " +
          "(LOWER(u.namaLengkap) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
          "LOWER(u.nim) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
          "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')))")
   List<User> searchCustomers(Integer roleId, String query);

   @Query("SELECT COUNT(u) FROM User u WHERE u.role.id = :roleId AND u.createdAt >= :startDate")
   Long countNewCustomersThisMonth(Integer roleId, LocalDateTime startDate);

   // Sort methods
   @Query("SELECT u FROM User u WHERE u.role.id = :roleId ORDER BY u.namaLengkap ASC")
   List<User> findCustomersSortedByName(Integer roleId);

   @Query("SELECT u FROM User u WHERE u.role.id = :roleId ORDER BY u.createdAt DESC")
   List<User> findCustomersSortedByDateJoined(Integer roleId);
}