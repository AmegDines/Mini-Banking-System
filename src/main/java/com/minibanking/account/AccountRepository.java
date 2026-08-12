package com.minibanking.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for the Account entity.
 * By extending JpaRepository, we get all basic CRUD operations (save, findById, findAll, delete) for free.
 * The two types in the diamond operator are: <EntityClass, PrimaryKeyClass>
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
}
