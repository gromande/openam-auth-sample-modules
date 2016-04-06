package com.groman.openam.auth;

import java.io.Serializable;
import java.security.Principal;

public class Module1Principal implements Principal, Serializable {
    
    private static final long serialVersionUID = 6643193379736433982L;
    private final String name;

    public Module1Principal(String name) {
        if (name == null) {
            throw new NullPointerException("illegal null input");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Module1Principal)) {
            return false;
        }

        Module1Principal that = (Module1Principal) o;

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
