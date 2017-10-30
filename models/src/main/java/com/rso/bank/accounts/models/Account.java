package com.rso.bank.accounts.models;

import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity(name = "account")
@NamedQueries(value =
        {
                @NamedQuery(name = "Account.getAll", query = "SELECT c FROM account c")
        })
@UuidGenerator(name = "idGenerator")
public class Account {

    @Id
    @GeneratedValue(generator = "idGenerator")
    private String id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    private double balance;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Transient
    private List<Transaction> transactions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public List<Transaction> getTransaction() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}