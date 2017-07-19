package com.mattleo.finance.modules;

import com.mattleo.finance.ui.common.ActiveIntervalView;
import com.mattleo.finance.ui.overview.OverviewGraphView;
import com.mattleo.finance.ui.reports.categories.CategoriesReportView;
import com.mattleo.finance.views.AccountsView;

import dagger.Module;

@Module(
        complete = false,
        injects = {
                OverviewGraphView.class,
                AccountsView.class,
                ActiveIntervalView.class,
                CategoriesReportView.class
        }
)
public class ViewModule {
}
