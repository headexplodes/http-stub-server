package au.com.sensis.stubby.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * Handles operations on requets collection (eg, 'DELETE /_control/requests')
 */
@SuppressWarnings("serial")
public class RequestsServlet extends AbstractStubServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        returnJson(response, service().getRequests());
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        service().deleteRequests();
        returnOk(response);
    }

}
