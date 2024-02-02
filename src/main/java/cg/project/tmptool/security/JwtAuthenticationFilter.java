package cg.project.tmptool.security;

import cg.project.tmptool.dto.User;
import cg.project.tmptool.services.UserCustomizedDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import static cg.project.tmptool.security.SecurityConfigConsts.HEADER_STRING;
import static cg.project.tmptool.security.SecurityConfigConsts.TOKEN_PREFIX;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserCustomizedDetailsService userCustomizedDetailsService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    // Spring Security过滤器的实现，它在每个HTTP请求到来时自动执行，用于解析和验证JWT令牌，如果令牌有效，则建立Spring Security的认证上下文。
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getJWTFromRequest(request);
            // getJWTFromRequest(request)方法从HTTP请求中提取JWT。
            // 通常是通过读取请求头部中的Authorization字段来完成的，其中JWT通常以Bearer <token>的形式出现。
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                // This step confirms JWT token is valid, which can work
                Long userId = jwtTokenProvider.getUserIdFromJWT(token);
                User user = userCustomizedDetailsService.loadUserById(userId);
                // 根据用户ID加载用户详情。

                // setting up the authentication for the user(for broadcast)
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        user, null, Collections.emptyList()
                );
                // 创建UsernamePasswordAuthenticationToken：这个对象代表了已认证的用户。

                // setting authentication details
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 设置认证详情，这里包括来源请求的信息。

                // binding the current authentication to the security context
                // also need getContext() for broadcasting to block to avoid other user requests
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
               // 为在后续的请求处理中，Spring Security可以识别出已认证的用户。
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security contest", ex);
        }
        // The above contents don't execute only once, but execute every time when a new request comes
        filterChain.doFilter(request, response);
    }

    private String getJWTFromRequest(HttpServletRequest request) {
        String tmpToken = request.getHeader(HEADER_STRING);

        if (StringUtils.hasText(tmpToken) && tmpToken.startsWith(TOKEN_PREFIX)) {
            // 从令牌字符串中去除前缀，仅返回令牌本身的部分。这就是实际的 JWT，可以用于之后的验证和处理。
            return tmpToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}