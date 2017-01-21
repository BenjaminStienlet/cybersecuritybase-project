package sec.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.Account;
import sec.project.repository.AccountRepository;

@Controller
public class SignupController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String loadForm() {
        return "form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String submitForm(Model model, @RequestParam String name, @RequestParam String password,
                             @RequestParam String creditcard, @RequestParam int count, @RequestParam String address) {
        Account account = accountRepository.findByName(name);
        if (account != null) {
            model.addAttribute("invalidUsername", true);
            return "form";
        }
        accountRepository.save(new Account(name, passwordEncoder.encode(password), creditcard, count, address, false));
        model.addAttribute("signedUp", true);
        return "redirect:/login";
    }

}
