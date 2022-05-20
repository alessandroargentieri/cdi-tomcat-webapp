package example.cdi.services.firstlevel;

import example.cdi.services.qualifiers.ShadedText;
import example.cdi.services.secondlevel.SecondLevelService;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

@ManagedBean @ShadedText
public class FirstLevelServiceShaded implements FirstLevelService {

    @Inject
    SecondLevelService secondLevelService;

    @Override
    public String greetz(String name, String surname) {
        return "HELLO " + shade(secondLevelService.getComposedName(name, surname));
    }

    private String shade(String s) {
        return s.toUpperCase()
                .replace("E", "3")
                .replace("I", "1")
                .replace("O", "0")
                .replace("A", "4")
                .replace("S", "5")
                .replace("Z", "2")
                .replace("B", "8");
    }
}
