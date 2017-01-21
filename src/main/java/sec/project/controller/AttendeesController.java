package sec.project.controller;

import org.h2.jdbc.JdbcSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.Account;
import sec.project.repository.AccountRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Controller
public class AttendeesController {

    @Autowired
    private AccountRepository accountRepository;

    @PersistenceContext
    private EntityManager em;

    @RequestMapping("*")
    public String defaultMapping() {
        return "redirect:/attendees";
    }

    @RequestMapping(value = "/attendees", method = RequestMethod.GET)
    public String loadAttendees(Authentication authentication, Model model) {
        model.addAttribute("user", accountRepository.findByName(authentication.getName()));
        model.addAttribute("list", accountRepository.findAll());
        return "attendees";
    }

    @RequestMapping(value="/attendees", method = RequestMethod.POST)
    public String searchAttendees(Authentication authentication, Model model, @RequestParam String searchString) {
        List<Account> attendees;
        try {
            // sqlmap -u "http://192.168.0.185:8080/attendees/" --data="searchString=1" --cookie="JSESSIONID=73E460C1BE0C7E019BB9B7299872DE63"
            Query q = em.createNativeQuery("SELECT * FROM Account WHERE Name like '" + searchString + "'", Account.class);
            attendees = q.getResultList();
        } catch (Exception e) {
            model.addAttribute("searchError", true);
            attendees = accountRepository.findAll();
        }

        model.addAttribute("user", accountRepository.findByName(authentication.getName()));
        model.addAttribute("list", attendees);
        return "attendees";
    }

    @RequestMapping(value = "/attendees/{name}", method = RequestMethod.DELETE)
    public String delete(@PathVariable String name) {
        accountRepository.delete(accountRepository.findByName(name));
        return "redirect:/attendees";
    }

}
