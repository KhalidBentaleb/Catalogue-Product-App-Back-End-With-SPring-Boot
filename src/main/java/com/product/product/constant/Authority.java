package com.product.product.constant;

public class Authority {
    public static final String[] USER_AUTHORITIES = { "User" };
    public static final String[] ADMIN_AUTHORITIES = { "user:read", "user:create", "user:update", "user:delete" };
}
