package sec.project.domain;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
public class Account extends AbstractPersistable<Long> {

    @Column(unique = true)
    private String name;
    private String password;
    private String creditCard;
    private int nrOfAttendees;
    private String address;
    private Boolean isAdmin;

    public Account() { super(); }

    public Account(String name, String password, String creditCard, int nrOfAttendees, String address, boolean isAdmin) {
        this();
        this.name = name;
        this.password = password;
        this.creditCard = creditCard;
        this.nrOfAttendees = nrOfAttendees;
        this.address = address;
        this.isAdmin = isAdmin;
    }

    public Account(String name, String password, boolean isAdmin) {
        this(name, password, "", 0, "", isAdmin);
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getCreditCard() {
        return creditCard;
    }

    public int getNrOfAttendees() {
        return nrOfAttendees;
    }

    public void setNrOfAttendees(int nrOfAttendees) {
        this.nrOfAttendees = nrOfAttendees;
    }

    public String getAddress() {
        return address;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

}
