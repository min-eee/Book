package org.mega.book.springboot.config;

import lombok.RequiredArgsConstructor;
import org.mega.book.springboot.config.auth.LoginUserArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {
// WebMvcConfigure 어노테이션 configure 파일이 되었음.
    private final LoginUserArgumentResolver loginUserArgumentResolver;
    //객체 가져옴

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers){
        //매개변수를 추가한다 List로 받아옴  Spring 쪽에서 관리하고 있는데 내가 추가한다.
        argumentResolvers.add(loginUserArgumentResolver);
    }
}
