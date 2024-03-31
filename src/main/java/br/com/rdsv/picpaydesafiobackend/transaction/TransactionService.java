package br.com.rdsv.picpaydesafiobackend.transaction;

import br.com.rdsv.picpaydesafiobackend.exceptions.InvalidTransactionException;
import br.com.rdsv.picpaydesafiobackend.wallet.Wallet;
import br.com.rdsv.picpaydesafiobackend.wallet.WalletRepository;
import br.com.rdsv.picpaydesafiobackend.wallet.WalletType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    public TransactionService(TransactionRepository transactionRepository, WalletRepository walletRepository) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
    }

    @Transactional
    public Transaction create(Transaction transaction) {
        // 1 - validar
        validate(transaction);

        // 2 - criar a transaction
        var newTransaction = transactionRepository.save(transaction);

        // 3 - debitar da carteira
        var wallet = walletRepository.findById(transaction.payer()).get();
        walletRepository.save(wallet.debit(transaction.value()));
        
        // 4 - chamar serviços externos

        return newTransaction;
    }

    // the payer has a common wallet
    // the payer has enough balance
    // the payer is not the payee
    private void validate(Transaction transaction) {
        walletRepository.findById(transaction.payee())
                .map(payee -> walletRepository.findById(transaction.payer())
                        .map(payer -> isTransactionValid(transaction, payer) ? transaction : null)
                        .orElseThrow(() -> new InvalidTransactionException("Invalid Transaction - %s".formatted(transaction))))
                .orElseThrow(() -> new InvalidTransactionException("Invalid Transaction - %s".formatted(transaction)));
    }

    private boolean isTransactionValid(Transaction transaction, Wallet payer) {
        return  isCommonWallet(payer) &&
                hasEnoughBalance(transaction, payer) &&
                isPayerNoThePayee(transaction, payer);
    }

    private boolean isCommonWallet(Wallet payer) {
        return payer.type() == WalletType.COMUM.getValue();
    }

    private boolean hasEnoughBalance(Transaction transaction, Wallet payer) {
        return payer.balance().compareTo(transaction.value()) >= 0;
    }

    private boolean isPayerNoThePayee(Transaction transaction, Wallet payer) {
        return !payer.id().equals(transaction.payee());
    }


    //validate 2
//    private void validate(Transaction transaction) {
//        Wallet payerWallet = findWalletByIdOrThrow(transaction.payer(), "Payer wallet not found");
//        Wallet payeeWallet = findWalletByIdOrThrow(transaction.payee(), "Payee wallet not found");
//
//        if (!isTransactionValid(transaction, payerWallet)) {
//            throw new InvalidTransactionException("Invalid Transaction - " + transaction);
//        }
//    }
//
//    private Wallet findWalletByIdOrThrow(Long walletId, String errorMessage) {
//        return walletRepository.findById(walletId)
//                .orElseThrow(() -> new InvalidTransactionException(errorMessage));
//    }
}
