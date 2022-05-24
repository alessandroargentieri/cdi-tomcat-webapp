package example.cdi.servlets;

import example.cdi.services.firstlevel.FirstLevelService;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@WebServlet("/greetz")
public class RestServlet extends HttpServlet {

    public static final String SHADED_QUERY_PARAM = "shaded";
    public static final String INTRUDER_QUERY_PARAM = "intruder";
    public static final String NAME_QUERY_PARAM = "name";
    public static final String SURNAME_QUERY_PARAM = "surname";

    @Inject
    @Named("plain")
    FirstLevelService plainSvc;

    @Inject
    @Named("shaded")
    FirstLevelService shadedSvc;

    @Inject
    @Named("intruder")
    FirstLevelService intruderSvc;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        boolean intruder = Boolean.parseBoolean(req.getParameter(INTRUDER_QUERY_PARAM));
        boolean shaded = Boolean.parseBoolean(req.getParameter(SHADED_QUERY_PARAM));

        String name = Optional.ofNullable(req.getParameter(NAME_QUERY_PARAM)).orElse("John");
        String surname = Optional.ofNullable(req.getParameter(SURNAME_QUERY_PARAM)).orElse("Doe");

        resp.getWriter().println(whichService(intruder, shaded).greetz(name, surname));
    }

    private FirstLevelService whichService(boolean intruder, boolean shaded) {
        return (intruder)
                  ? intruderSvc
                  : (shaded) ? shadedSvc : plainSvc;
    }
}
