package example.cdi.services.firstlevel;

import example.cdi.services.secondlevel.SecondLevelService;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

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
