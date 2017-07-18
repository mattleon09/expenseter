package com.mattleo.finance.backend.endpoint.body;

import com.mattleo.finance.backend.entity.CategoryEntity;
import com.mattleo.finance.backend.entity.TransactionEntity;

import java.util.List;

public class TransactionsBody extends EntitiesBody<CategoryEntity> {
    private final List<TransactionEntity> transactions;

    public TransactionsBody(List<TransactionEntity> transactions, String deviceRegId) {
        super(deviceRegId);
        this.transactions = transactions;
    }

    public List<TransactionEntity> getTransactions() {
        return transactions;
    }
}
