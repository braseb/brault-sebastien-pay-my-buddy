package com.paymybuddy.app.repository;


import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.paymybuddy.app.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

	
	public Optional<User> findByEmail(String email);
	public boolean existsByEmail(String email);
	
	@Modifying
    @Query(value = "UPDATE users SET acountBalance = acountBalance + :amount WHERE email = :email", nativeQuery = true)
    int updateCapital(@Param("email") String email, @Param("amount") Double amount);
	
	
}
