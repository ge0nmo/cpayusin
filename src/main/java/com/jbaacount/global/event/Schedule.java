package com.jbaacount.global.event;

import com.jbaacount.model.Member;
import com.jbaacount.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class Schedule
{
    private final MemberRepository memberRepository;

    @Scheduled(cron = "0 0 0 1 * ?")
    public void initializeScore()
    {
        List<Member> all = memberRepository.findAll();
        for (Member member : all)
        {
            member.initializeScore();
        }
    }
}
