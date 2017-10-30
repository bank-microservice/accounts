package com.rso.bank.accounts.api.v1.resources;

import com.kumuluz.ee.logs.cdi.Log;
import com.rso.bank.accounts.models.Account;
import com.rso.bank.accounts.models.Transaction;
import com.rso.bank.accounts.services.AccountsBean;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;


@RequestScoped
@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Log
public class AccountsResource {

    @Inject
    private AccountsBean accountsBean;

    @Context
    protected UriInfo uriInfo;

    @GET
    public Response getAccounts() {

        List<Account> accounts = accountsBean.getAccounts();

        return Response.ok(accounts).build();
    }

    @GET
    @Path("/filtered")
    public Response getAccountsFiltered() {

        List<Account> accounts;

        accounts = accountsBean.getAccountsFilter(uriInfo);

        return Response.status(Response.Status.OK).entity(accounts).build();
    }

    @GET
    @Path("/{accountId}")
    public Response getAccount(@PathParam("accountId") String accountId) {

        Account account = accountsBean.getAccount(accountId);

        if (account == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(account).build();
    }

    @GET
    @Path("/transactions/{accountId}")
    public Response getTransactions(@PathParam("accountId") String accountId) {

        List<Transaction> transactions = accountsBean.getTransactions(accountId);

        if (transactions == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(transactions).build();
    }

    @POST
    public Response createAccount(Account account) {

        if ((account.getUsername() == null || account.getUsername().isEmpty()) || (account.getPassword() == null
                || account.getPassword().isEmpty())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            account = accountsBean.createAccount(account);
        }

        if (account.getId() != null) {
            return Response.status(Response.Status.CREATED).entity(account).build();
        } else {
            return Response.status(Response.Status.CONFLICT).entity(account).build();
        }
    }

    @PUT
    @Path("{accountId}")
    public Response putAccount(@PathParam("accountId") String accountId, Account account) {

        account = accountsBean.putAccount(accountId, account);

        if (account == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            if (account.getId() != null)
                return Response.status(Response.Status.OK).entity(account).build();
            else
                return Response.status(Response.Status.NOT_MODIFIED).build();
        }
    }

    @DELETE
    @Path("{accountId}")
    public Response deleteAccount(@PathParam("accountId") String accountId) {

        boolean deleted = accountsBean.deleteAccount(accountId);

        if (deleted) {
            return Response.status(Response.Status.GONE).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
