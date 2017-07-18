package com.mattleo.finance.backend.endpoint.body;

import com.mattleo.finance.backend.entity.AccountEntity;
import com.mattleo.finance.backend.entity.CategoryEntity;

import java.util.List;

public class AccountsBody extends EntitiesBody<CategoryEntity> {
    private final List<AccountEntity> accounts;

    public AccountsBody(List<AccountEntity> accounts, String deviceRegId) {
        super(deviceRegId);
        this.accounts = accounts;
    }

    public List<AccountEntity> getAccounts() {
        return accounts;
    }
}
