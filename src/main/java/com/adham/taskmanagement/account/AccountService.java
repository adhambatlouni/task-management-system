package com.adham.taskmanagement.account;

import com.adham.taskmanagement.common.exception.ResourceConflictException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void register(RegisterAccountRequest request) {

        String normalizedEmail =
                request.email()
                        .trim()
                        .toLowerCase(Locale.ROOT);

        if (accountRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ResourceConflictException("Email already exists");
        }

        String encodedPassword =
                passwordEncoder.encode(request.password());

        Account account =
                new Account(normalizedEmail, encodedPassword);

        accountRepository.save(account);
    }
}
