package com.sitescape.team.util;

import com.sitescape.team.ConfigurationException;

public class ConfigPropertyNotFoundException extends ConfigurationException {
    public ConfigPropertyNotFoundException() {
        super();
    }
    public ConfigPropertyNotFoundException(String message) {
        super(message);
    }
    public ConfigPropertyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public ConfigPropertyNotFoundException(Throwable cause) {
        super(cause);
    }

}
