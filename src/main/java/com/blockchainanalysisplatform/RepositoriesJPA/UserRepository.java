package com.blockchainanalysisplatform.RepositoriesJPA;

import com.blockchainanalysisplatform.Data.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface UserRepository extends CrudRepository<User,Long> { //CrudRepository<User,Long> {


    Optional<User> findByEmailOrUsername(String email, String username);

}
