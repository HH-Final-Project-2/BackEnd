package com.sparta.finalpj.repository;

import com.sparta.finalpj.domain.Member;
import com.sparta.finalpj.jwt.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {

    Optional<RefreshToken> findByMember(Member member);

}
