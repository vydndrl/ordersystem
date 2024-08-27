package beyond.ordersystem.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.security.sasl.AuthenticationException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



@Component
@Slf4j
public class JwtAuthFilter extends GenericFilter {

    @Value("${jwt.secretKey}")
    private String secretKey;
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        String bearerToken = ((HttpServletRequest)request).getHeader("Authorization");
        try {

            if (bearerToken != null){

                // token은 관례적으로 Bearer로 시작하는 문구를 넣어서 요청
                if(!bearerToken.substring(0, 7).equals("Bearer ")){
                    throw new AuthenticationServiceException("Bearer 형식이 아닙니다.");
                }
                String token = bearerToken.substring(7);

                // token 검증 및 claims(사용자 정보) 추출
                // token 생성시에 사용한 secret키값을 넣어 토큰 검증에 사용
                // * claims = 페이로드 값. 서버가 필요한 정보는 페이로드에서 꺼냄
                Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();

                // Authentication 객체(인증 정보가 들어감) 생성(UserDetails 객체도 필요)
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + claims.get("role")));
                UserDetails userDetails = new User(claims.getSubject(), "", authorities);
                // * Authentication : 전역적으로 사용하기 위해 Authentication 객체를 생성. 이 사용자가 누구인지 전역적으로 가져다 쓰기
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

            }
            // filterchain에서 그 다음 filtering으로 넘어가도록 하는 메서드
            chain.doFilter(request, response);

        } catch (Exception e){
            log.error(e.getMessage());
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpServletResponse.setContentType("application/json");
            httpServletResponse.getWriter().write("token error");
        }
    }
}
