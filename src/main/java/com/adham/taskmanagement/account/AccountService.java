package com.adham.taskmanagement.account;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(
            AccountRepository accountRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterAccountRequest request) {

        String normalizedEmail =
                request.email()
                        .trim()
                        .toLowerCase(Locale.ROOT);

        if (accountRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already exists"
            );
        }

        String encodedPassword =
                passwordEncoder.encode(request.password());

        Account account =
                new Account(normalizedEmail, encodedPassword);

        accountRepository.save(account);
    }
}
