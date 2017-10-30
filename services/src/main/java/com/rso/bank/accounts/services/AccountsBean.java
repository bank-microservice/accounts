package com.rso.bank.accounts.services;


import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
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
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;


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

    private String baseUrl;

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
        baseUrl = "http://localhost:8081"; // only for demonstration
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


    public List<Transaction> getTransactions(String accountId) {

        try {
            return httpClient
                    .target(baseUrl + "/v1/transactions?where=accountId:EQ:" + accountId)
                    .request().get(new GenericType<List<Transaction>>() {
                    });
        } catch (WebApplicationException | ProcessingException e) {
            log.error(e);
            throw new InternalServerErrorException(e);
        }

    }

    public List<Transaction> getTransactionsFallback(String accountId) {
        return new ArrayList<>();
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

    public void loadTransaction(Integer n) {


    }
}
