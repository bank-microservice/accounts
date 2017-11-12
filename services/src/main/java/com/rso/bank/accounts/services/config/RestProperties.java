package com.rso.bank.accounts.services.config;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@ConfigBundle("rest-properties")
public class RestProperties {

    @ConfigValue(value = "external-services.transaction-service.enabled", watch = true)
    private boolean transactionServiceEnabled;

    public boolean isTransactionServiceEnabled() {
        return transactionServiceEnabled;
    }

    public void setTransactionServiceEnabled(boolean transactionServiceEnabled) {
        this.transactionServiceEnabled = transactionServiceEnabled;
    }
}
