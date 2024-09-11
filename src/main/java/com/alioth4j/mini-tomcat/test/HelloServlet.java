package test;

import javax.servlet.*;
import java.io.IOException;

public class HelloServlet implements Servlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        String doc = """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Test Custom Servlet</title>
                    <meta charset="utf-8">
                </head?
                <body>
                    <h1>Hello Dynamic Resource</h1>
                </body>
                </html>
                """;
        res.setCharacterEncoding("UTF-8");
        res.getWriter().println(doc);
    }

    @Override
    public String getServletInfo() {
        return "";
    }

    @Override
    public void destroy() {
    }

}
