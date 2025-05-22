package com.tip.b18.electronicsales.services;

public interface PasswordService {
    String encryptPassword(String password);
    boolean matches(String password, String encodedPassword);
}
