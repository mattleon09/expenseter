package com.mattleo.finance;

import com.mattleo.finance.modules.AppModule;

final class Modules {
    private Modules() {
        // No instances.
    }

    static Object[] list(App app) {
        return new Object[]{
                new AppModule(app)
        };
    }
}