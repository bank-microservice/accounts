package com.rso.bank.accounts.api.v1.resources;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Arrays;
import java.util.List;


@RequestScoped
@Path("/demo")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DemoResource {

    @Context
    protected UriInfo uriInfo;

    @GET
    @Path("/info")
    public Response getInfo() {

        Info info = new Info();

        return Response.status(Response.Status.OK).entity(info).build();
    }

    private class Info {

        private List<String> clani = Arrays.asList("rp0183");
        private String opis_projekta = "Nas projekt implementira aplikacijo za spletno banko.";
        private List<String> mikrostoritve = Arrays.asList("http://169.51.16.128:30696/v1/accounts/", "http://169.51.16.128:32286/v1/transactions");
        private List<String> github = Arrays.asList("https://github.com/bank-microservice/accounts", "https://github.com/bank-microservice/transactions");
        private List<String> travis = Arrays.asList("https://travis-ci.org/bank-microservice/accounts/", "https://travis-ci.org/bank-microservice/transactions/");
        private List<String> dockerhub = Arrays.asList("https://hub.docker.com/r/rokplevel/bank-accounts/", "https://hub.docker.com/r/rokplevel/bank-transactions/");

        public List<String> getClani() {
            return clani;
        }

        public String getOpisProjekta() {
            return opis_projekta;
        }

        public List<String> getMikrostoritve() {
            return mikrostoritve;
        }

        public List<String> getGithub() {
            return github;
        }

        public List<String> getTravis() {
            return travis;
        }

        public List<String> getDockerhub() {
            return dockerhub;
        }
    }

}