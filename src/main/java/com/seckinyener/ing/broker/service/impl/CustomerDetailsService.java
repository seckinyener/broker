package com.seckinyener.ing.broker.service.impl;

import com.seckinyener.ing.broker.model.entity.Customer;
import com.seckinyener.ing.broker.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.Collections;

@AllArgsConstructor
@Service
public class CustomerDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Customer customer = customerRepository.findCustomerByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new User(customer.getUsername(), customer.getPassword(), Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + customer.getRole())));
    }
}
