package com.groman.openam.auth;

import java.security.Principal;
import java.util.Map;
import java.util.ResourceBundle;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;

import com.sun.identity.authentication.spi.AMLoginModule;
import com.sun.identity.authentication.spi.AuthLoginException;
import com.sun.identity.authentication.util.ISAuthConstants;
import com.sun.identity.shared.datastruct.CollectionHelper;
import com.sun.identity.shared.debug.Debug;

@SuppressWarnings("rawtypes")
public class Module1 extends AMLoginModule {
    
    //Error codes
    private final static String INVALID_USERNAME = "invalid-username";
    private final static String INVALID_PASSWORD = "invalid-password";
    
    // Name of the debug file
    private final static String MODULE_NAME = "Module1";

    // Name of the resource bundle
    private final static String RES_BUNDLE_NAME = "amAuthModule1";
    
    //ShareState object keys
    private final static String SSN_KEY = "com.groman.ssn";

    private final static String USERNAME = "demo";
    private final static String PASSWORD = "changeit";
    private final static String SSN = "111224444";

    private final static int LOGIN_START = 1;
    private final static int ERROR_STATE = 2;

    private final static Debug debug = Debug.getInstance(MODULE_NAME);

    private Map sharedState;
    private ResourceBundle bundle;
    private boolean storedCredentials = true;

    @Override
    public Principal getPrincipal() {
        return new Module1Principal(USERNAME);
    }

    @Override
    public void init(Subject subject, Map sharedState, Map options) {
        if (debug.messageEnabled()) {
            debug.message("Module1::init");
        }
        this.sharedState = sharedState;
        bundle = amCache.getResBundle(RES_BUNDLE_NAME, getLoginLocale());
            
        storedCredentials = Boolean.valueOf(CollectionHelper.getMapAttr(
            options, ISAuthConstants.STORE_SHARED_STATE_ENABLED, "true")
            ).booleanValue();
    }

    @Override
    public int process(Callback[] callbacks, int currentState) throws LoginException {
        if (debug.messageEnabled()) {
            debug.message("process(): current state: " + currentState);
        }
        
        if (currentState != LOGIN_START) {
            throw new AuthLoginException("Invalid state"); 
        }
        
        // Get credentials from callbacks
        NameCallback nc = (NameCallback) callbacks[0];
        PasswordCallback pc = (PasswordCallback) callbacks[1];
        String username = nc.getName();
        String password = new String(pc.getPassword());
        
        if (!USERNAME.equals(username)) {
            debug.error("Invalid username: " + username);
            setErrorMessage(INVALID_USERNAME);
            return ERROR_STATE;
        }
        
        if (!PASSWORD.equals(password)) {
            debug.error("Invalid password");
            setErrorMessage(INVALID_PASSWORD);
            return ERROR_STATE;
        }
        
        if (debug.messageEnabled()) {
            debug.message("Succesful authentication for username: " + username);
        }
        storeInSharedState(ISAuthConstants.SHARED_STATE_USERNAME, username);
        storeInSharedState(ISAuthConstants.SHARED_STATE_PASSWORD, password);
        storeInSharedState(SSN_KEY, SSN);
        return ISAuthConstants.LOGIN_SUCCEED;
    }
    
    private void setErrorMessage(String error_code) throws AuthLoginException {
        //Get error message from resource bundle and substitute header
        substituteHeader(ERROR_STATE, bundle.getString(error_code));
    }
    
    @SuppressWarnings("unchecked")
    private void storeInSharedState(String key, String value) {
        
        //Make sure store of credentials is enabled
        if (!storedCredentials) return;
        
        if (debug.messageEnabled()) {
            debug.message("Storing "+ key + "  in Shared State");
        }
        if (sharedState != null) {
            sharedState.put(key, value);
        } 
    }

}
