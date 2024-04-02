package br.com.rdsv.picpaydesafiobackend.transaction;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("transaction")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
    }

    @PostMapping
    public Transaction createTransaction(@RequestBody Transaction transaction){
        return transactionService.create(transaction);
    }

    @GetMapping
    public List<Transaction> list(){
        return transactionService.list();
    }

}
