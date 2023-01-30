package org.mega.book.springboot.config.auth;

import lombok.RequiredArgsConstructor;
import org.mega.book.springboot.config.auth.dto.OAuthAttributes;
import org.mega.book.springboot.config.auth.dto.SessionUser;
import org.mega.book.springboot.domain.user.User;
import org.mega.book.springboot.domain.user.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    // 제네릭으로 OAuth2UserRequest, OAuth2User 받을거임

    private final UserRepository userRepository;  // 쿼리문실행

    private final HttpSession httpSession; // Http 세션

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{
        // login 하겠따는 뜻 loadUser는

        //부모 implement 라서 객체 못만드는데 자식으로 객체 만듦
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService(); // 자식
        //전형적인 upCasting
        // 부모의 것을 loadUser로  재정의 해서 userRequest로 요청이 온것을 가지고 OAuth2User를 만듦
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        // DefaultOAuth2UserService의 loadUser를 받음 업캐스팅

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        System.out.println(registrationId+ "@@@@@@@@@");
        // 등록된 id 뺌

        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        System.out.println(userNameAttributeName+"@@@@@@@@@@@@@@2");
        // 이름 뻄


        OAuthAttributes attributes = OAuthAttributes
                .of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        User user = saveOrUpdate(attributes);
        // 만약의 유저가

        httpSession.setAttribute("user",new SessionUser(user));
        //httpSession -> 웹을 전반적으로 다쓴다. 전역변수처럼 쓸 수 있다.

        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                attributes.getAttributes(),attributes.getNameAttributeKey());

        //Collection -> 자료구조 최상위 리스트의 제일 위에 있는 애
    }

    private User saveOrUpdate(OAuthAttributes attributes){
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture())) // 있다면 있는거 바꿔치기
                .orElse(attributes.toEntity()); // 없다면 새로 만들던지
        return userRepository.save(user);
    }

    // 다 정리한 후에 Security Config 한테 줌

//    index.mustache Login을 누르면 oauth2/authorization/google 을 부름
//    google로 보냄 > 요청 온걸 CustomOAuth2UserService의 loadUser이 받는다>

}
