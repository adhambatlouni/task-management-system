package com.adham.taskmanagement.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.adham.taskmanagement.account.Account;
import com.adham.taskmanagement.account.AccountRepository;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    public DatabaseUserDetailsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Account account = accountRepository
                .findByEmailIgnoreCase(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Account not found")
                );

        return User.builder()
                .username(account.getEmail())
                .password(account.getPassword())
                .roles("USER")
                .build();
    }
}
