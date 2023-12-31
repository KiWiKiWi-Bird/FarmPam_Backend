package com.fp.backend.system.security.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

//public class SecurityUtil {
//
//    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);
//
//    private SecurityUtil() {}
//
//
//    //시큐리티 컨텍스트에서 Authentication 객체를 꺼내와서 그걸 이용하여 username을 꺼내와서 반환시켜주는 유틸성 메소드
//    public static Optional<String> getCurrentUsername() {
//        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication == null) {
//            logger.debug("Security Context에 인증 정보가 없습니다.");
//            return Optional.empty();
//        }
//
//        String username = null;
//        if (authentication.getPrincipal() instanceof UserDetails) {
//            UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
//            username = springSecurityUser.getUsername();
//        } else if (authentication.getPrincipal() instanceof String) {
//            username = (String) authentication.getPrincipal();
//        }
//
//        return Optional.ofNullable(username);
//    }
//}