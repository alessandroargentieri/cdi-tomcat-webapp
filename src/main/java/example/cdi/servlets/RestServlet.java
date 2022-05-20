package example.cdi.servlets;

import example.cdi.services.firstlevel.FirstLevelService;
import example.cdi.services.qualifiers.PlainText;
import example.cdi.services.qualifiers.ShadedText;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@WebServlet("/greetz")
public class RestServlet extends HttpServlet {

    public static final String SHADED_QUERY_PARAM = "shaded";
    public static final String NAME_QUERY_PARAM = "name";
    public static final String SURNAME_QUERY_PARAM = "surname";

    @Inject @PlainText
    FirstLevelService plainSvc;

    @Inject @ShadedText
    FirstLevelService shadedSvc;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        boolean shaded = Boolean.parseBoolean(req.getParameter(SHADED_QUERY_PARAM));
        String name = Optional.ofNullable(req.getParameter(NAME_QUERY_PARAM)).orElse("John");
        String surname = Optional.ofNullable(req.getParameter(SURNAME_QUERY_PARAM)).orElse("Doe");

        resp.getWriter().println((shaded ? shadedSvc : plainSvc).greetz(name, surname));
    }
}
