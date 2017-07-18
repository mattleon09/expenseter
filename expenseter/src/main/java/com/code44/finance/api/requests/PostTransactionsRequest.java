package com.mattleo.finance.api.requests;

import com.mattleo.finance.api.GcmRegistration;
import com.mattleo.finance.backend.endpoint.transactions.Transactions;
import com.mattleo.finance.backend.endpoint.transactions.model.TransactionEntity;
import com.mattleo.finance.backend.endpoint.transactions.model.TransactionsBody;
import com.mattleo.finance.common.utils.Preconditions;
import com.mattleo.finance.data.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class PostTransactionsRequest extends PostRequest<TransactionsBody> {
    private final Transactions transactionsService;
    private final List<Transaction> transactions;

    public PostTransactionsRequest(GcmRegistration gcmRegistration, Transactions transactionsService, List<Transaction> transactions) {
        super(null, gcmRegistration);
        Preconditions.notNull(transactionsService, "Transactions service cannot be null.");
        Preconditions.notNull(transactions, "Transactions list cannot be null.");

        this.transactionsService = transactionsService;
        this.transactions = transactions;
    }

    @Override protected TransactionsBody createBody() {
        return new TransactionsBody();
    }

    @Override protected void onAddPostData(TransactionsBody body) {
        final List<TransactionEntity> transactionEntities = new ArrayList<>();
        for (Transaction transaction : transactions) {
//            transactionEntities.add(transaction.asEntity());
        }
        body.setTransactions(transactionEntities);
    }

    @Override protected boolean isPostDataEmpty(TransactionsBody body) {
        return body.getTransactions().isEmpty();
    }

    @Override protected void performRequest(TransactionsBody body) throws Exception {
        transactionsService.save(body);
    }
}
