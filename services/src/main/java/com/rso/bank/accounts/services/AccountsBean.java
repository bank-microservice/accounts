package com.rso.bank.accounts.services;


import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import com.rso.bank.accounts.models.ResponseMessage;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import com.rso.bank.accounts.models.Account;
import com.rso.bank.accounts.models.Transaction;
import com.rso.bank.accounts.services.config.RestProperties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RequestScoped
public class AccountsBean {

    private Logger log = LogManager.getLogger(AccountsBean.class.getName());

    @Inject
    private RestProperties restProperties;

    @Inject
    private EntityManager em;

    @Inject
    private AccountsBean accountsBean;

    private Client httpClient;

    @Inject
    @DiscoverService("bank-transactions")
    private Optional<String> transactionUrl;

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
        //baseUrl = "http://localhost:8081"; // only for demonstration
    }


    public List<Account> getAccounts() {

        TypedQuery<Account> query = em.createNamedQuery("Account.getAll", Account.class);

        return query.getResultList();

    }

    public List<Account> getAccountsFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, Account.class, queryParameters);
    }

    public Account getAccount(String accountId) {

        Account account = em.find(Account.class, accountId);

        if (account == null) {
            throw new NotFoundException();
        }

        if (restProperties.isTransactionServiceEnabled()) {
            List<Transaction> transactions = accountsBean.getTransactions(accountId);
            account.setTransactions(transactions);
        }

        return account;
    }

    public Account createAccount(Account account) {

        try {
            beginTx();
            em.persist(account);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return account;
    }

    public Account putAccount(String accountId, Account account) {

        Account c = em.find(Account.class, accountId);

        if (c == null) {
            return null;
        }

        try {
            beginTx();
            account.setId(c.getId());
            account = em.merge(account);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return account;
    }

    public boolean deleteAccount(String accountId) {

        Account account = em.find(Account.class, accountId);

        if (account != null) {
            try {
                beginTx();
                em.remove(account);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
        } else
            return false;

        return true;
    }

    public ResponseMessage sendTransaction(Transaction transaction) {

        if (transactionUrl.isPresent()) {

            try {

                Account c = em.find(Account.class, transaction.getAccountId());
                if (c == null)
                    return new ResponseMessage("Error",  "Unknown Account ID");

                if (c.getBalance() + transaction.getAmount() < 0)
                    return new ResponseMessage("Error", "Not enough account balance.");

                Response rs = httpClient
                        .target(transactionUrl.get() + "/v1/transactions/")
                        .request()
                        .post(Entity.json(transaction));

                if (rs.readEntity(Boolean.class)) { //post success
                    c.setBalance(c.getBalance() + transaction.getAmount());
                    log.info("Transaction completed!");
                    return new ResponseMessage("Success", "Transaction completed!");
                } else {
                    log.error("Account transaction process failed: " + rs.getStatusInfo().getReasonPhrase());
                    return new ResponseMessage("Error", "Transaction failed: " + rs.getStatusInfo().getReasonPhrase());
                }
            } catch (WebApplicationException | ProcessingException e) {
                log.error(e);
                throw new InternalServerErrorException(e);
            }

        } else {
            return new ResponseMessage("Error", "Transaction service not available.");
        }
    }

    @CircuitBreaker(requestVolumeThreshold = 2)
    @Fallback(fallbackMethod = "getTransactionsFallback")
    @Timeout
    public List<Transaction> getTransactions(String accountId) {

        if (transactionUrl.isPresent()) {

            try {
                return httpClient
                        .target(transactionUrl.get() + "/v1/transactions?where=accountId:EQ:" + accountId)
                        .request().get(new GenericType<List<Transaction>>() { });
            } catch (WebApplicationException | ProcessingException e) {
                log.error(e);
                throw new InternalServerErrorException(e);
            }
        }

        return new ArrayList<Transaction>();
    }

    public List<Transaction> getTransactionsFallback(String accountId) {

        List<Transaction> transactions = new ArrayList<>();

        Transaction transaction = new Transaction();

        transaction.setDescription("N/A");
        transaction.setTitle("N/A");

        transactions.add(transaction);

        return transactions;
    }


    private void beginTx() {
        if (!em.getTransaction().isActive())
            em.getTransaction().begin();
    }

    private void commitTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().commit();
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().rollback();
    }

}
