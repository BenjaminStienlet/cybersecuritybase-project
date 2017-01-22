package sec.project.config;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sec.project.domain.Account;
import sec.project.repository.AccountRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {

        Account account = new Account("admin", passwordEncoder.encode("adminpw"),  "341616273645565",
                1, "450 Arrowhead Street, Akron, OH 44312", true);
        accountRepository.save(account);

        account = new Account("user", passwordEncoder.encode("userpw"), "375253554288425",
                1, "89 Green Dr., Orland Park, IL 60462", false);
        accountRepository.save(account);

        account = new Account("John Smith", passwordEncoder.encode("password123"), "372773498202508",
                3, "8489 Poor House Drive, Sicklerville, NJ 08081", false);
        accountRepository.save(account);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByName(username);
        if (account == null) {
            throw new UsernameNotFoundException("No such user: " + username);
        }

        return new org.springframework.security.core.userdetails.User(
                account.getName(),
                account.getPassword(),
                true,
                true,
                true,
                true,
                Arrays.asList(new SimpleGrantedAuthority("USER")));
    }
}
