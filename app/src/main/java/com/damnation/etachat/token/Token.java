package com.damnation.etachat.token;

public class Token {
    private String token;
    private String id;

    public static final Token INSTANCE = new Token();

    private Token() {}

    public void setToken(String token, String id) {
        this.token = token;
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public String getId() {
        return id;
    }
}
