package com.mattleo.finance.modules;

import com.mattleo.finance.ui.CalculatorActivity;
import com.mattleo.finance.ui.CalculatorFragment;
import com.mattleo.finance.ui.SplashActivity;
import com.mattleo.finance.ui.accounts.detail.AccountActivity;
import com.mattleo.finance.ui.accounts.edit.AccountEditActivity;
import com.mattleo.finance.ui.accounts.list.AccountsActivity;
import com.mattleo.finance.ui.categories.detail.CategoryActivity;
import com.mattleo.finance.ui.categories.edit.CategoryEditActivity;
import com.mattleo.finance.ui.categories.list.CategoriesActivity;
import com.mattleo.finance.ui.common.navigation.NavigationFragment;
import com.mattleo.finance.ui.currencies.detail.CurrencyActivity;
import com.mattleo.finance.ui.currencies.edit.CurrencyEditActivity;
import com.mattleo.finance.ui.currencies.list.CurrenciesActivity;
import com.mattleo.finance.ui.dialogs.ColorDialogFragment;
import com.mattleo.finance.ui.dialogs.DatePickerDialog;
import com.mattleo.finance.ui.dialogs.DeleteDialogFragment;
import com.mattleo.finance.ui.dialogs.ListDialogFragment;
import com.mattleo.finance.ui.dialogs.TimePickerDialog;
import com.mattleo.finance.ui.dropbox.FilesActivity;
import com.mattleo.finance.ui.overview.OverviewActivity;
import com.mattleo.finance.ui.playservices.GoogleApiFragment;
import com.mattleo.finance.ui.reports.ReportsFragment;
import com.mattleo.finance.ui.reports.categories.CategoriesReportActivity;
import com.mattleo.finance.ui.settings.SettingsActivity;
import com.mattleo.finance.ui.settings.about.AboutActivity;
import com.mattleo.finance.ui.settings.data.DataActivity;
import com.mattleo.finance.ui.settings.data.DataFragment;
import com.mattleo.finance.ui.settings.data.ExportActivity;
import com.mattleo.finance.ui.settings.data.ImportActivity;
import com.mattleo.finance.ui.settings.security.LockActivity;
import com.mattleo.finance.ui.settings.security.UnlockActivity;
import com.mattleo.finance.ui.tags.detail.TagActivity;
import com.mattleo.finance.ui.tags.edit.TagEditActivity;
import com.mattleo.finance.ui.tags.list.TagsActivity;
import com.mattleo.finance.ui.transactions.detail.TransactionActivity;
import com.mattleo.finance.ui.transactions.edit.TransactionEditActivity;
import com.mattleo.finance.ui.transactions.list.TransactionsActivity;

import dagger.Module;

@Module(
        library = true,
        complete = false,
        injects = {
                SplashActivity.class,
                OverviewActivity.class,
                FilesActivity.class,
                CurrenciesActivity.class,
                CurrencyActivity.class,
                CurrencyEditActivity.class,
                AccountsActivity.class,
                AccountActivity.class,
                AccountEditActivity.class,
                TransactionsActivity.class,
                TransactionActivity.class,
                TransactionEditActivity.class,
                CategoriesActivity.class,
                CategoryActivity.class,
                CategoryEditActivity.class,
                TagsActivity.class,
                TagActivity.class,
                TagEditActivity.class,
                SettingsActivity.class,
                DataActivity.class,
                CalculatorActivity.class,
                ExportActivity.class,
                ImportActivity.class,
                AboutActivity.class,
                CategoriesReportActivity.class,
                UnlockActivity.class,
                LockActivity.class,

                NavigationFragment.class,
                DeleteDialogFragment.class,
                ListDialogFragment.class,
                CalculatorFragment.class,
                GoogleApiFragment.class,
                DataFragment.class,
                ReportsFragment.class,
                DatePickerDialog.class,
                TimePickerDialog.class,
                ColorDialogFragment.class,
        }
)
public class UIModule {
}
