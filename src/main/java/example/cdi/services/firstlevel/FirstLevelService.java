package example.cdi.services.firstlevel;

public interface FirstLevelService {
    String greetz(String name, String surname);

    default String getInstance() {
        return this.toString().split("@")[1];
    }

}
