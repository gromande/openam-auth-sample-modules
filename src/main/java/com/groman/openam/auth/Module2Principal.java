package com.groman.openam.auth;

import java.io.Serializable;
import java.security.Principal;

public class Module2Principal implements Principal, Serializable {
    
    private static final long serialVersionUID = 5777765102978412641L;
    private final String name;

    public Module2Principal(String name) {
        if (name == null) {
            throw new NullPointerException("Invalid null principal name");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Module2Principal)) {
            return false;
        }

        Module2Principal that = (Module2Principal) o;

        if (this.getName().equals(that.getName())) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
