package com.blockchainanalysisplatform.RepositoriesJPA;

import com.blockchainanalysisplatform.Data.User;
import org.springframework.data.repository.CrudRepository;


public interface UserRepository extends CrudRepository<User,Long> { //CrudRepository<User,Long> {

    User findByUsername(String username);
    User findByEmail(String email);
    User findByEmailOrUsername(String email, String username);

}
