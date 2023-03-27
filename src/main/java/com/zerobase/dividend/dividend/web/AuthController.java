package com.zerobase.dividend.dividend.web;

import com.zerobase.dividend.dividend.model.Auth;
import com.zerobase.dividend.dividend.persistence.entity.MemberEntity;
import com.zerobase.dividend.dividend.security.TokenProvider;
import com.zerobase.dividend.dividend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody Auth.SignUp request) {
        // 회원가입을 위한 API
        MemberEntity memberEntity = memberService.register(request);
        return ResponseEntity.ok(memberEntity);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody Auth.SignIn request) {
        // 로그인을 위한 API
        MemberEntity member = memberService.authenticate(request);
        String token = tokenProvider.generateToken(member.getUsername(), member.getRoles());
        log.info("user login ->" + request.getUsername());
        return ResponseEntity.ok(token);
    }

}
