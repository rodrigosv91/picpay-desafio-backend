package br.com.rdsv.picpaydesafiobackend.transaction;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends ListCrudRepository<Transaction,Long> {

}
