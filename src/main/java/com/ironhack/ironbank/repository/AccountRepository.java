package com.ironhack.ironbank.repository;

import com.ironhack.ironbank.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

        @Query(value = "SELECT a.id FROM accounts a WHERE a.account_type = ?1", nativeQuery = true)
        List<Long> findAllIdsByAccountType(String accountType);

        @Query(value = "SELECT * FROM accounts a WHERE a.account_type = ?1", nativeQuery = true)
        List<Account> findAllByAccountType(String accountType);

        List<Account> findByPrimaryOwner_Id(Long accountHolderId);
        List<Account> findBySecondaryOwner_Id(Long accountHolderId);


}
