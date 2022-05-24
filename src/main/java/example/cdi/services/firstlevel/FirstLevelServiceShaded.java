package example.cdi.services.firstlevel;

import example.cdi.services.secondlevel.SecondLevelService;

import jakarta.annotation.ManagedBean;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@ManagedBean
@Named("shaded")
@RequestScoped
public class FirstLevelServiceShaded implements FirstLevelService {

    @Inject
    SecondLevelService secondLevelService;

    @Override
    public String greetz(String name, String surname) {
        return String.format("SVC: %s - ", this.getInstance()) + "HI " + shade(secondLevelService.getComposedName(name, surname));
    }

    private String shade(String s) {
        return s.toUpperCase()
                .replace("E", "3")
                .replace("I", "1")
                .replace("O", "0")
                .replace("A", "4")
                .replace("S", "5")
                .replace("T", "7")
                .replace("G", "6")
                .replace("Q", "9")
                .replace("Z", "2")
                .replace("B", "8");
    }

    @PostConstruct
    public void setUp() {
        System.out.println("Just initiated shadedSvc: " + this.getInstance());
    }
}
