package sec.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.Account;
import sec.project.repository.AccountRepository;

@Controller
public class ProfileController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String loadForm(Authentication authentication, Model model) {
        model.addAttribute("user", accountRepository.findByName(authentication.getName()));
        return "profile";
    }

    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public String submitForm(Authentication authentication, Model model, @RequestParam int count) {
        Account account = accountRepository.findByName(authentication.getName());
        account.setNrOfAttendees(count);
        accountRepository.save(account);
        model.addAttribute("user", account);
        return "redirect:/attendees";
    }

}
