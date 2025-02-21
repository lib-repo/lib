package org.example.libdev.user.dto;

import java.util.Map;

public class OAuthUserInfo {
    private final Map<String, Object> attributes;

    public OAuthUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getProvider() {
        return (String) attributes.get("provider");
    }

    public String getEmail() {
        return (String) attributes.get("email");
    }

    public String getName() {
        return (String) attributes.get("name");
    }

    public String getProviderId() {
        return (String) attributes.get("id");
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}