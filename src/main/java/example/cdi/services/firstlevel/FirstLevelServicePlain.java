package example.cdi.services.firstlevel;

import example.cdi.services.secondlevel.SecondLevelService;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

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
