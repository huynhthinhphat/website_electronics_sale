package com.tip.b18.electronicsales.services;

import com.tip.b18.electronicsales.dto.AccountDTO;

public interface GoogleAuthService {
    AccountDTO authenticateWithGoogle(String googleToken);
    boolean getGender(String gender);
}
