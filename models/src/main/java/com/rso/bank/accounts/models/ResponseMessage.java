package com.rso.bank.accounts.models;

public class ResponseMessage {

    public String status;
    public String message;

    public ResponseMessage(String s, String m) {
        this.status = s;
        this.message = m;
    }
}
