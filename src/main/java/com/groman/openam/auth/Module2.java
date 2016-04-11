package com.groman.openam.auth;

import java.security.Principal;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.login.LoginException;

import com.iplanet.dpro.session.service.InternalSession;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;
import com.sun.identity.authentication.spi.AMLoginModule;
import com.sun.identity.authentication.spi.AuthLoginException;
import com.sun.identity.authentication.util.ISAuthConstants;
import com.sun.identity.shared.debug.Debug;

public class Module2 extends AMLoginModule {
    // Name of the debug file
    private final static String MODULE_NAME = "Module2";
    
    private final static String SSN = "111223333";
    
    private Map sharedState;
    private String username;
    private String UUID;
    
    private final static int LOGIN_START = 1;

    private final static Debug debug = Debug.getInstance(MODULE_NAME);

    @Override
    public Principal getPrincipal() {
        //Give preference to UUID
        return new Module2Principal(UUID != null ? UUID : username);
    }

    @Override
    public void init(Subject subject, Map sharedState, Map options) {
        if (debug.messageEnabled()) {
            debug.message("Module2::init");
        }
        this.sharedState = sharedState;
        loadCredentials();
    }

    @Override
    public int process(Callback[] callbacks, int currentState) throws LoginException {
        if (debug.messageEnabled()) {
            debug.message("init(): current state: " + currentState);
        }
        
        if (username == null || username.length() == 0) {
            throw new AuthLoginException("Missing username");
        }
        
        if (currentState != LOGIN_START) {
            throw new AuthLoginException("Invalid state"); 
        }
        
        // Get credentials from callbacks
        NameCallback nc = (NameCallback) callbacks[0];
        String ssn = nc.getName();
        ssn = ssn.replace("-", "");
        if (!SSN.equals(ssn)) {
            throw new AuthLoginException("Invalid SSN: " + ssn); 
        }
        
        if (debug.messageEnabled()) {
            debug.message("Succesful authentication for username: " + username);
        }
        
        return ISAuthConstants.LOGIN_SUCCEED;
    }
    
    private void loadCredentials() {
        //get username from previous authentication
        try {
            username = (String) sharedState.get(getUserKey());
            if (debug.messageEnabled()) {
                debug.message("loadCredentials() : Got username from shared state: " + username);
            }
            username = (String) sharedState.get(getUserKey());
            if (username == null || username.isEmpty()) {
                if (debug.messageEnabled()) {
                    debug.message("Session upgrade case");
                }
                //Session upgrade case. Need to find the user ID from the old session.
                SSOTokenManager mgr = SSOTokenManager.getInstance();
                InternalSession oldSession = getLoginState(MODULE_NAME).getOldSession();
                if (oldSession == null) {
                    throw new AuthLoginException("Unable to get old session");
                }
                SSOToken token = mgr.createSSOToken(oldSession.getID().toString());
                UUID = token.getPrincipal().getName();
                username = token.getProperty("UserToken");
                if (debug.messageEnabled()) {
                    debug.message("loadCredentials() : username from SSOToken=" + username + "; UUID=" + UUID);
                }
            }
        } catch (Exception e) {
            debug.error("loadCredentials() : Unable to get username", e);
        }
    }

}
