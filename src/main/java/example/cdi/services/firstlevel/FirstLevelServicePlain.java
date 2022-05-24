package example.cdi.services.firstlevel;

import example.cdi.services.secondlevel.SecondLevelService;

import jakarta.annotation.ManagedBean;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@ManagedBean
@Named("plain")
@RequestScoped
public class FirstLevelServicePlain implements FirstLevelService {

    final SecondLevelService secondLevelService;

    @Inject
    public FirstLevelServicePlain(final SecondLevelService secondLevelService) {
        this.secondLevelService = secondLevelService;
    }

    @Override
    public String greetz(String name, String surname) {
        return (String.format("SVC: %s - ", this.getInstance()) + "Hi " + secondLevelService.getComposedName(name, surname)).toUpperCase();
    }

    @PostConstruct
    public void setUp() {
        System.out.println("Just initiated plainSvc: " + this.getInstance());
    }
}
