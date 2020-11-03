package filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = "*")
public class CORS implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse resp = (HttpServletResponse)response;

        String origin = req.getHeader("Origin");
        if(origin == null) origin = req.getHeader("Referer");

        resp.setHeader("Access-Control-Allow-Origin", origin);            // 允许指定域访问跨域资源
        resp.setHeader("Access-Control-Allow-Credentials", "true");       // 允许客户端携带跨域cookie，此时origin值不能为“*”，只能为指定单一域名


        String allowMethod = req.getHeader("Access-Control-Request-Method");
        String allowHeaders = req.getHeader("Access-Control-Request-Headers");
        resp.setHeader("Access-Control-Max-Age", "86400");            // 浏览器缓存预检请求结果时间,单位:秒
        resp.setHeader("Access-Control-Allow-Methods", allowMethod);  // 允许浏览器在预检请求成功之后发送的实际请求方法名
        resp.setHeader("Access-Control-Allow-Headers", allowHeaders); // 允许浏览器发送的请求消息头


        chain.doFilter(request, response);
    }
}
