package com.iny.side.account.mock;

import com.iny.side.users.domain.entity.Account;
import com.iny.side.users.domain.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class FakeUserRepository implements UserRepository {

    private final AtomicLong autoGeneratedId = new AtomicLong(0);
    private final List<Account> data = new ArrayList<>();

    @Override
    public Optional<Account> findByUsername(String username) {
        return data.stream()
                .filter(account -> Objects.equals(account.getUsername(), username))
                .findFirst();
    }

    @Override
    public Boolean existsByUsername(String username) {
        return data.stream()
                .anyMatch(account -> Objects.equals(account.getUsername(), username));
    }

    @Override
    public Account save(Account account) {
        if (account.getId() == null || account.getId() == 0) {
            Account savedAccount = Account.builder()
                    .id(autoGeneratedId.incrementAndGet())
                    .username(account.getUsername())
                    .password(account.getPassword())
                    .name(account.getName())
                    .role(account.getRole())
                    .build();

            data.add(savedAccount);
            return savedAccount;
        }
        data.removeIf(item -> Objects.equals(item.getId(), account.getId()));
        data.add(account);
        return account;
    }
}
