package example.cdi.services.secondlevel;

import jakarta.annotation.ManagedBean;

@ManagedBean
public class SecondLevelServiceImpl implements SecondLevelService {

    @Override
    public String getComposedName(String name, String surname) {
        return name + " " + surname;
    }
}
