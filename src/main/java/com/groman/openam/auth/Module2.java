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
import com.sun.identity.shared.datastruct.CollectionHelper;
import com.sun.identity.shared.debug.Debug;

@SuppressWarnings("rawtypes")
public class Module2 extends AMLoginModule {
    // Name of the debug file
    private final static String MODULE_NAME = "Module2";
    
    //ShareState object keys
    private final static String SSN_KEY = "com.groman.ssn";
    
    private final static String SSN = "111223333";
    
    private Map sharedState;
    private String username;
    private String UUID;
    private boolean sharedStateEnabled;
    private String sharedStateBehaviorPattern;
    
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
        
        sharedStateEnabled = Boolean.valueOf(CollectionHelper.getMapAttr(
                options, ISAuthConstants.SHARED_STATE_ENABLED, "false")
                ).booleanValue();

        sharedStateBehaviorPattern = CollectionHelper.getMapAttr(options,
            ISAuthConstants.SHARED_STATE_BEHAVIOR_PATTERN,
            "tryFirstPass");
        
        loadCredentials();
        
        debug.message("Is ShareState enabled: " + sharedStateEnabled);
        debug.message("ShareState Behavior Patter: " + sharedStateBehaviorPattern);
        
        if (username == null || username.length() == 0) {
            throw new RuntimeException("Missing username");
        }

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
        
        // Get credentials
        String ssn = null;
        if ((callbacks == null || callbacks.length == 0) && sharedStateEnabled) {
            debug.message("Callbacks are empty. Trying with SharedState");
            ssn = (String) sharedState.get(SSN_KEY);
            if (!SSN.equals(ssn) && "tryFirstPass".equals(sharedStateBehaviorPattern)) {
                debug.message("Invalid SSN but it's tryFirstPass. Displaying login page");
                return LOGIN_START;
            }
        } else {
            NameCallback nc = (NameCallback) callbacks[0];
            ssn = nc != null ? nc.getName() : null;
        }
        
        //Remove hyphens
        if (ssn != null) {
            ssn = ssn.replace("-", "");
        }
        
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
            username = (String) sharedState.get(ISAuthConstants.SHARED_STATE_USERNAME);
            if (debug.messageEnabled()) {
                debug.message("loadCredentials() : Got username from shared state: " + username);
            }
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
