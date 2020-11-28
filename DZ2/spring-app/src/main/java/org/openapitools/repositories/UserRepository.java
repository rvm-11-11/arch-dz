package org.openapitools.repositories;

import org.openapitools.model.UserEntity;
import org.springframework.data.repository.CrudRepository;

//@RepositoryRestResource(collectionResourceRel = "people", path = "people")
public interface UserRepository extends CrudRepository<UserEntity, Long> {

//    List<User> findByLastName(@Param("name") String name);

}