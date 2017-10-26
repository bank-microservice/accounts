package com.rso.bank.accounts;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("accounts")
public class AccountResource {

    @GET
    public Response getAllCustomers() {
        return Response.ok("test").build();
    }

    @GET
    @Path("{accountId}")
    public Response getAccount(@PathParam("accountId") int accountId) {
        //Account customer = Database.getCustomer(accountId);
        int accId = accountId;
        return accId != 0
                ? Response.ok(accId).build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    public Response addNewCustomer(Account account) {
        //Database.addCustomer(customer);
        return Response.noContent().build();
    }

    @DELETE
    @Path("{accountId}")
    public Response deleteCustomer(@PathParam("accountId") int accountId) {
        //Database.deleteCustomer(customerId);
        return Response.noContent().build();
    }
}
