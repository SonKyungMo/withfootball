package com.demo.withfootball.modules.main;

import com.demo.withfootball.modules.account.Account;
import com.demo.withfootball.modules.account.AccountRepository;
import com.demo.withfootball.modules.account.CurrentAccount;
import com.demo.withfootball.modules.community.Community;
import com.demo.withfootball.modules.community.CommunityRepository;
import com.demo.withfootball.modules.event.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final CommunityRepository communityRepository;
    private final AccountRepository accountRepository;
    private final EnrollmentRepository enrollmentRepository;

    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model) {
        if (account != null) {
            Account accountLoaded = accountRepository.findAccountWithTagsAndZonesById(account.getId());
            model.addAttribute(accountLoaded);
            model.addAttribute("enrollmentList", enrollmentRepository.findByAccountAndAcceptedOrderByEnrolledAtDesc(accountLoaded, true));
            model.addAttribute("communityList", communityRepository.findByAccount(
                    accountLoaded.getTags(),
                    accountLoaded.getZones()));
            model.addAttribute("communityManagerOf",
                    communityRepository.findFirst5ByManagersContainingAndClosedOrderByPublishedDateTimeDesc(account, false));
            model.addAttribute("communityMemberOf",
                    communityRepository.findFirst5ByMembersContainingAndClosedOrderByPublishedDateTimeDesc(account, false));
            return "index-after-login";
        }

        model.addAttribute("communityList", communityRepository.findFirst9ByPublishedAndClosedOrderByMemberCountDesc(true, false));
        return "index";
    }


    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/search/community")
    public String searchCommunity(String keyword, Model model,
                              @PageableDefault(size = 9, sort = "publishedDateTime", direction = Sort.Direction.DESC)
                                      Pageable pageable) {
        Page<Community> communityPage = communityRepository.findByKeyword(keyword, pageable);
        model.addAttribute("communityPage", communityPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortProperty",
                pageable.getSort().toString().contains("publishedDateTime") ? "publishedDateTime" : "memberCount");
        return "search";
    }

}
