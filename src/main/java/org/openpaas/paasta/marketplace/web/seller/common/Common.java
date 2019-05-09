package org.openpaas.paasta.marketplace.web.seller.common;

import org.openpaas.paasta.marketplace.web.seller.security.SsoAuthenticationDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.client.RestTemplate;

/**
 * Properties
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-03-14
 */
public class Common {
    private static final Logger LOGGER = LoggerFactory.getLogger(Common.class);

    @Value("${market.place.api.uri}")
    public String marketApiUri;

    @Value("${cf.java.client.api.uri}")
    public String cfJavaClientApiUri;

    @Value("${cf.java.client.api.authorization.username}")
    public String cfJavaClientApiUsername;

    @Value("${cf.java.client.api.authorization.password}")
    public String cfJavaClientApiPassword;

    @Value("${cf.api.url}")
    public String apiUrl;

    @Value("${cf.uaa.oauth.info.uri}")
    public String uaaUserInfo;

    @Value("${cf.uaa.oauth.token.check.uri}")
    public String checkTokenUri;

    @Value("${cf.uaa.oauth.token.access.uri}")
    public String accessUri;

    @Value("${cf.uaa.oauth.logout.uri}")
    public String logoutUri;

    @Value("${cf.uaa.oauth.authorization.uri}")
    public String authorizationUri;

    @Value("${cf.uaa.oauth.client.id}")
    public String clientId;

    @Value("${cf.uaa.oauth.client.secret}")
    public String clientSecret;


    public String getToken() {
        try {
            SsoAuthenticationDetails user = ((SsoAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails());
            LOGGER.debug("############################# Token Expired : " + (user.getAccessToken().getExpiration().getTime() - System.currentTimeMillis()) / 1000 + " sec");
            // Token 만료 시간 비교
            if (user.getAccessToken().getExpiration().getTime() <= System.currentTimeMillis()) {
                //Rest 생성
                RestTemplate rest = new RestTemplate();
                //Token 재요청을 위한 데이터 설정
                OAuth2ProtectedResourceDetails resource = getResourceDetails(user.getUsername(), "N/A", clientId, clientSecret, accessUri);
                AccessTokenRequest accessTokenRequest = new DefaultAccessTokenRequest();
                ResourceOwnerPasswordAccessTokenProvider provider = new ResourceOwnerPasswordAccessTokenProvider();
                provider.setRequestFactory(rest.getRequestFactory());
                //Token 재요청
                OAuth2AccessToken refreshToken = provider.refreshAccessToken(resource, user.getAccessToken().getRefreshToken(), accessTokenRequest);


                //재요청으로 받은 Token 재설정
                user.setAccessToken(refreshToken);
                // session에 적용
                Authentication authentication = new UsernamePasswordAuthenticationToken(SecurityContextHolder.getContext().getAuthentication(), "N/A", SecurityContextHolder.getContext().getAuthentication().getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            String token = user.getTokenValue();
            return token;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private OAuth2ProtectedResourceDetails getResourceDetails(String username, String password, String clientId, String clientSecret, String url) {
        ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
        resource.setUsername(username);
        resource.setPassword(password);

        resource.setClientId(clientId);
        resource.setClientSecret(clientSecret);
        resource.setId(clientId);
        resource.setClientAuthenticationScheme(AuthenticationScheme.header);
        resource.setAccessTokenUri(url);

        return resource;
    }
}