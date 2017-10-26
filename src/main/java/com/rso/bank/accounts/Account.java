package com.rso.bank.accounts;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "accounts")
@NamedQueries({
        @NamedQuery(
                name = "Account.findAccounts",
                query = "SELECT acc " +
                        "FROM Account acc"
        )
})
public class Account implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "accountBalance")
    private double accountBalance;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(double balance) {
        this.accountBalance = balance;
    }
}
