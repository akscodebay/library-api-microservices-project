package com.aks.usersservice.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.aks.usersservice.pojo.Users;

@Repository
public interface UsersRepository extends CrudRepository<Users, Long> {

}
