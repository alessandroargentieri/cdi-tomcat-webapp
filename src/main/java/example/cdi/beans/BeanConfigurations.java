package example.cdi.beans;

import example.cdi.services.firstlevel.FirstLevelService;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

public class BeanConfigurations {

    @ApplicationScoped
    @Produces
    @Named("intruder")
    public FirstLevelService getFirstLevelService() {

        return new FirstLevelService() {

            @Override
            public String greetz(String name, String surname) {
                return "SVC - " + this.getInstance() + "Hello!! I'm an intruder!";
            }
        };
    }
}
