package example.cdi.beans;

import example.cdi.services.firstlevel.FirstLevelService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

public class BeanConfigurations {

    @ApplicationScoped
    @Produces
    @Named("intruder")
    public FirstLevelService getFirstLevelService() {

        return new FirstLevelService() {

            @Override
            public String greetz(String name, String surname) {
                return "SVC - " + this.getInstance() + " - Hello!! I'm an intruder!";
            }
        };
    }
}
