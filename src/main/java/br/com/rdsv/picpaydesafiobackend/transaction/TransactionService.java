package br.com.rdsv.picpaydesafiobackend.transaction;

import br.com.rdsv.picpaydesafiobackend.authorization.AuthorizerService;
import br.com.rdsv.picpaydesafiobackend.notification.NotificationService;
import br.com.rdsv.picpaydesafiobackend.wallet.Wallet;
import br.com.rdsv.picpaydesafiobackend.wallet.WalletRepository;
import br.com.rdsv.picpaydesafiobackend.wallet.WalletType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final AuthorizerService authorizerService;
    private final NotificationService notificationService;

    public TransactionService(TransactionRepository transactionRepository, WalletRepository walletRepository, AuthorizerService authorizerService, NotificationService notificationService) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.authorizerService = authorizerService;
        this.notificationService = notificationService;
    }

    @Transactional
    public Transaction create(Transaction transaction) {
        // 1 - validar
        validate(transaction);

        // 2 - criar a transaction
        var newTransaction = transactionRepository.save(transaction);

        // 3 - debitar/depositar nas carteiras
        var walletPayer = walletRepository.findById(transaction.payer()).get();
        var walletPayee = walletRepository.findById(transaction.payee()).get();

        walletRepository.save(walletPayer.debit(transaction.value()));
        walletRepository.save(walletPayee.credit(transaction.value()));
        
        // 4 - chamar serviços externos
        authorizerService.authorize(transaction);

        // 5 - notificação
        notificationService.notify(transaction);

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

    public List<Transaction> list() {
        return transactionRepository.findAll();
    }

}
