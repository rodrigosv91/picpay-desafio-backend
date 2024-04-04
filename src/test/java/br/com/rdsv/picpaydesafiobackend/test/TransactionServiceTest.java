package br.com.rdsv.picpaydesafiobackend.test;

import br.com.rdsv.picpaydesafiobackend.authorization.AuthorizerService;
import br.com.rdsv.picpaydesafiobackend.notification.NotificationService;
import br.com.rdsv.picpaydesafiobackend.transaction.InvalidTransactionException;
import br.com.rdsv.picpaydesafiobackend.transaction.Transaction;
import br.com.rdsv.picpaydesafiobackend.transaction.TransactionRepository;
import br.com.rdsv.picpaydesafiobackend.transaction.TransactionService;
import br.com.rdsv.picpaydesafiobackend.wallet.Wallet;
import br.com.rdsv.picpaydesafiobackend.wallet.WalletRepository;
import br.com.rdsv.picpaydesafiobackend.wallet.WalletType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private AuthorizerService authorizerService;

    @Mock
    private NotificationService notificationService;

    @Test
    @DisplayName("Deve criar uma Transação com sucesso")
    public void testCreateTransactionSuccess() {
        var transaction = new Transaction(null, 1L, 2L, new BigDecimal(1000), null);
        var payee = new Wallet(transaction.payee(), null, null, null, null, WalletType.COMUM.getValue(),
                BigDecimal.ZERO);
        var payer = new Wallet(transaction.payer(), null, null, null, null, WalletType.COMUM.getValue(),
                new BigDecimal(1000));

        when(walletRepository.findById(transaction.payee())).thenReturn(Optional.of(payee));
        when(walletRepository.findById(transaction.payer())).thenReturn(Optional.of(payer));
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        var newTransaction = transactionService.create(transaction);

        assertEquals(transaction, newTransaction);
    }

    @ParameterizedTest
    @MethodSource("providesInvalidTransactions")
    @DisplayName("Deve falhar ao tentar criar transações com dados inválidos")
    public void testCreateInvalidTransaction(Transaction transaction) {
        assertThrows(InvalidTransactionException.class,
                () -> transactionService.create(transaction));
    }

    private static Stream<Arguments> providesInvalidTransactions() {
        var transactionLojista = new Transaction(null, 2L, 1L, new BigDecimal(1000), null);
        var transactionInsuficientBalance = new Transaction(null, 1L, 2L, new BigDecimal(1001), null);
        var transactionPayerEqualsPayee = new Transaction(null, 1L, 1L, new BigDecimal(1000), null);
        var transactionUnexistingPayee = new Transaction(null, 1L, 11L, new BigDecimal(1000), null);
        var transactionUnexistingPayer = new Transaction(null, 11L, 1L, new BigDecimal(1000), null);

        return Stream.of(
                Arguments.of(transactionLojista),
                Arguments.of(transactionInsuficientBalance),
                Arguments.of(transactionPayerEqualsPayee),
                Arguments.of(transactionUnexistingPayee),
                Arguments.of(transactionUnexistingPayer));
    }

}
