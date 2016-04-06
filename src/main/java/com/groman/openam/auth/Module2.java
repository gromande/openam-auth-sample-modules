package com.groman.openam.auth;

import java.security.Principal;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.login.LoginException;

import com.sun.identity.authentication.spi.AMLoginModule;

public class Module2 extends AMLoginModule {

    public Module2() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public Principal getPrincipal() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void init(Subject arg0, Map arg1, Map arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public int process(Callback[] arg0, int arg1) throws LoginException {
        // TODO Auto-generated method stub
        return 0;
    }

}
