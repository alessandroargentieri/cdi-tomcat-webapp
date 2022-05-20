package example.cdi.services.firstlevel;

import example.cdi.services.qualifiers.PlainText;
import example.cdi.services.secondlevel.SecondLevelService;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

@ManagedBean @PlainText
public class FirstLevelServicePlain implements FirstLevelService {

    @Inject
    SecondLevelService secondLevelService;

    @Override
    public String greetz(String name, String surname) {
        return ("Hello " + secondLevelService.getComposedName(name, surname)).toUpperCase();
    }
}
