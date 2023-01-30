package org.mega.book.springboot.config.auth;

import lombok.RequiredArgsConstructor;
import org.mega.book.springboot.config.auth.dto.SessionUser;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final HttpSession httpSession;
 //스프리에 들어가려면 설정이 있어야함. config 안에
    @Override
    public boolean supportsParameter(MethodParameter parameter){
        //가져오는 파라메터가 true 인지 false인지 확인.
        boolean isLoginUserAnnotation = parameter.getParameterAnnotation(LoginUser.class) != null;
        // 객체가 있으면 true 객체가 없으면 false
        boolean isUserClass = SessionUser.class.equals(parameter.getParameterType());
        //@Valid 적어도 자료형은 적는데 SessionUser랑 같은지 확인.
        return isLoginUserAnnotation && isUserClass;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
            throws Exception{
        return httpSession.getAttribute("user");
    }

}
