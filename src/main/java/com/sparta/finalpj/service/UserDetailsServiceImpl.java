package com.sparta.finalpj.service;


import com.sparta.finalpj.domain.Member;
import com.sparta.finalpj.jwt.UserDetailsImpl;
import com.sparta.finalpj.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {


// UserDetails 가 사용자의 정보를 담았다면 UserDetailsService 는 DB 에서 유저 정보를 불러온다.
// 이 메서드가 loadUserByUsername() 이다.
// 다시 말하자면 DB 에서 유저정보를 가지고와서 return 해주는 작업이다.
// @Service 를 해줘야 IoC 로 등록이 되고 그래야 loadUserByUsername() 오버라이드 사용가능
    private final MemberRepository memberRepository;


    // UserDetails return 은 어디로 가나?
    // security Session => Authentication => UserDetails(UserDetailsImpl)
    // 이렇게 return 해주면
    // security Session(내부 Authentication(내부 UserDetails)) 요롷게 쏘옥 들어간다.
    // 이러면 로그인 완료~
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> member = memberRepository.findByEmail(username);
        return member
                .map(UserDetailsImpl::new)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }
}