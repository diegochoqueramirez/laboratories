package com.sales.market.service;

import com.sales.market.model.AccountAux;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;


@SpringBootTest
class AccountAuxServiceTest {

    @Autowired
    private AccountAuxService accountAuxService;

    @Test
    public void givenBalance100WhenDebit100TwiceShouldFail() {
        AccountAux accountAux = new AccountAux();
        accountAux.setTotalCredit(new BigDecimal("100"));
        accountAux.setTotalDebit(new BigDecimal("0"));
        accountAux.setBalance(new BigDecimal("100"));

        AccountAux finalAccountAux = accountAuxService.save(accountAux);
        System.out.println("Numero de registros " + accountAuxService.count());

        CompletableFuture<AccountAux> completableFutureFirstOperation =
                CompletableFuture.supplyAsync(() -> accountAuxService.debit(finalAccountAux.getId(),
                        new BigDecimal("100"))).orTimeout(60, TimeUnit.SECONDS);

        CompletableFuture<AccountAux> completableFutureSecondOperation =
                CompletableFuture.supplyAsync(() -> accountAuxService.debit(finalAccountAux.getId(),
                        new BigDecimal("100"))).orTimeout(60, TimeUnit.SECONDS);

        AccountAux accountAuxAfterOperations = accountAuxService.getById(finalAccountAux.getId());

        System.out.println("id: " + accountAuxAfterOperations.getId() + ", balance: " + accountAuxAfterOperations.getBalance() + ", debit: "+ accountAuxAfterOperations.getTotalDebit() + ", credit: " + accountAuxAfterOperations.getTotalCredit());
        assertEquals(accountAuxAfterOperations.getBalance().intValue(), 0);
    }

}