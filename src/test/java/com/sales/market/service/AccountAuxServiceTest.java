package com.sales.market.service;

import com.sales.market.model.AccountAux;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;


@SpringBootTest
class AccountAuxServiceTest {

    @Autowired
    private AccountAuxService accountAuxService;

    @Test
    public void givenBalance100WhenDebit100TwiceShouldFail() {

        assertThrows(ExecutionException.class, () -> {
            AccountAux accountAux = new AccountAux();
            accountAux.setTotalCredit(new BigDecimal("100"));
            accountAux.setTotalDebit(new BigDecimal("0"));
            accountAux.setBalance(new BigDecimal("100"));
            accountAuxService.save(accountAux);

            CompletableFuture<AccountAux> completableFutureFirstOperation =
                    CompletableFuture.supplyAsync(() -> accountAuxService.debit(accountAux.getId(),
                            new BigDecimal("100"))).orTimeout(60, TimeUnit.SECONDS);

            CompletableFuture<AccountAux> completableFutureSecondOperation =
                    CompletableFuture.supplyAsync(() -> accountAuxService.debit(accountAux.getId(),
                            new BigDecimal("100"))).orTimeout(60, TimeUnit.SECONDS);

            AccountAux finalAccount = completableFutureFirstOperation.get();
            AccountAux finalAccount2 = completableFutureSecondOperation.get();

            AccountAux accountAuxAfterOperations = accountAuxService.getById(finalAccount2.getId());

            System.out.println("id: " + accountAuxAfterOperations.getId() + ", balance: " + accountAuxAfterOperations.getBalance() + ", debit: "+ accountAuxAfterOperations.getTotalDebit() + ", credit: " + accountAuxAfterOperations.getTotalCredit());

            assertEquals(accountAuxAfterOperations.getBalance(), 0);
        });
    }

}