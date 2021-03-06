package org.openpaas.paasta.marketplace.web.seller.controller;

import lombok.extern.slf4j.Slf4j;
import org.openpaas.paasta.marketplace.api.domain.CustomPage;
import org.openpaas.paasta.marketplace.api.domain.Profile;
import org.openpaas.paasta.marketplace.web.seller.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;


/**
 * Login 버튼이 있는 페이지 클래스
 */
@Slf4j
@Controller
@RequestMapping
public class LoginController {

    @Autowired
    ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private ProfileService profileService;

    @GetMapping(value = "/login")
    public String getLoginPage(Model model) {
        Map<String, String> oauth2AuthenticationUrls = new HashMap<>();
        Iterable<ClientRegistration> clientRegistrations = null;
        ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository).as(Iterable.class);
        if (type != ResolvableType.NONE && ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
            clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
        }

        clientRegistrations.forEach(registration -> oauth2AuthenticationUrls.put(registration.getClientName(), "/oauth2/authorization/cf"));
        model.addAttribute("urls", oauth2AuthenticationUrls);

        return "contents/login";
    }

    @GetMapping(value = "/index")
    public String getLoginInfo(Model model, OAuth2AuthenticationToken authentication, HttpSession httpSession) {
        OAuth2AuthorizedClient client = authorizedClientService
                .loadAuthorizedClient(
                        authentication.getAuthorizedClientRegistrationId(),
                        authentication.getName());

        OAuth2AccessToken accessToken = client.getAccessToken();

        OAuth2User user = authentication.getPrincipal();

        System.out.println("authentication access Token value ::: " + accessToken.getTokenValue());
        System.out.println("authentication getName ::: " + user.getAttributes().get("user_name"));

        httpSession.setAttribute("yourName", user.getAttributes().get("user_name"));
        httpSession.setAttribute("token", accessToken.getTokenValue());

        int count = 0;

        CustomPage<Profile> profileList = profileService.getProfileList("?page=0&size=10&sort=id");

        log.info(">> user :: " + user.getAttributes().get("user_name"));

        String userId = user.getAttributes().get("user_name").toString();

        for(Profile profiles : profileList) {
            if(userId.equals(profiles.getId())) {
                count++;
            }
        }
        if(count > 0){
            return "redirect:/softwares/list";       // (1) 상품 목록 페이지
        }
        return "redirect:/profiles/create";                         // (2) 프로필 등록 페이지
    }
}
