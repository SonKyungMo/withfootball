package com.demo.withfootball.modules.community;

import com.demo.withfootball.modules.account.Account;
import com.demo.withfootball.modules.account.CurrentAccount;
import com.demo.withfootball.modules.community.form.CommunityDescriptionForm;
import com.demo.withfootball.modules.tag.Tag;
import com.demo.withfootball.modules.tag.TagForm;
import com.demo.withfootball.modules.tag.TagRepository;
import com.demo.withfootball.modules.tag.TagService;
import com.demo.withfootball.modules.zone.Zone;
import com.demo.withfootball.modules.zone.ZoneForm;
import com.demo.withfootball.modules.zone.ZoneRepository;
import com.demo.withfootball.modules.zone.ZoneService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/community/{path}/settings")
public class CommunitySettingsController {

    private final CommunityRepository communityRepository;
    private final CommunityService communityService;
    private final ModelMapper modelMapper;
    private final TagRepository tagRepository;
    private final ObjectMapper objectMapper;
    private final TagService tagService;
    private final ZoneRepository zoneRepository;

    @GetMapping("/description")
    public String viewCommunityDescription(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Community community = communityService.getCommunityToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(community);
        model.addAttribute(modelMapper.map(community, CommunityDescriptionForm.class));

        return "community/settings/description";
    }

    @PostMapping("/description")
    public String updateCommunityInfo(@CurrentAccount Account account, @PathVariable String path,
                                      @Valid CommunityDescriptionForm communityDescriptionForm, Errors errors,
                                      Model model, RedirectAttributes attributes) {
        Community community = communityService.getCommunityToUpdate(account, path);

        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(community);
            return "community/settings/description";
        }

        communityService.updateCommunityDescription(community, communityDescriptionForm);
        attributes.addFlashAttribute("message", "커뮤니티 소개 수정 완료");

        return "redirect:/community/" + community.getEncodedPath() + "/settings/description";

    }

    @GetMapping("/banner")
    public String communityImageForm(@CurrentAccount Account account, @PathVariable String path,  Model model){
        Community community = communityService.getCommunityToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(community);

        return "community/settings/banner";
    }

    @PostMapping("/banner")
    public String communityImageSubmit(@CurrentAccount Account account, @PathVariable String path,
                                   String image, RedirectAttributes attributes) {
        Community community = communityService.getCommunityToUpdate(account, path);
        communityService.updateCommunityImage(community, image);
        attributes.addFlashAttribute("message", "스터디 이미지를 수정했습니다.");
        return "redirect:/community/" + community.getEncodedPath() + "/settings/banner";
    }

    @PostMapping("/banner/enable")
    public String enableCommunityBanner(@CurrentAccount Account account, @PathVariable String path) {
        Community community = communityService.getCommunityToUpdate(account,path);
        communityService.enableCommunityBanner(community);
        return "redirect:/community/" + community.getEncodedPath() + "/settings/banner";
    }

    @PostMapping("/banner/disable")
    public String disableCommunityBanner(@CurrentAccount Account account, @PathVariable String path) {
        Community community = communityService.getCommunityToUpdate(account, path);
        communityService.disableCommunityBanner(community);
        return "redirect:/community/" + community.getEncodedPath() + "/settings/banner";
    }

    @GetMapping("/tags")
    public String communityTagsForm(@CurrentAccount Account account, @PathVariable String path, Model model) throws JsonProcessingException {
        Community community = communityService.getCommunityToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(community);

        model.addAttribute("tags", community.getTags().stream().map(Tag::getTitle).collect(Collectors.toList()));
        List<String> allTagTitle = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTagTitle));
        return "community/settings/tags";
    }

    @PostMapping("/tags/add")
    public ResponseEntity<Tag> addTags(@CurrentAccount Account account, @PathVariable String path, @RequestBody TagForm tagForm){
        Community community = communityService.getCommunityToUpdateTag(account, path);
        Tag tag = tagService.findOrCreateNew(tagForm.getTagTitle());
        communityService.addTag(community,tag);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tags/remove")
    public ResponseEntity<Tag> removeTag(@CurrentAccount Account account, @PathVariable String path, @RequestBody TagForm tagForm){
        Community community = communityService.getCommunityToUpdateTag(account, path);
        Tag tag = tagRepository.findByTitle(tagForm.getTagTitle());
        if(tag == null){
            return ResponseEntity.badRequest().build();
        }
        communityService.removeTag(community,tag);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/zones")
    public String communityZonesForm(@CurrentAccount Account account, @PathVariable String path, Model model)
            throws JsonProcessingException {
        Community community = communityService.getCommunityToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(community);
        model.addAttribute("zones", community.getZones().stream()
                .map(Zone::toString).collect(Collectors.toList()));
        List<String> allZones = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allZones));
        return "community/settings/zones";
    }

    @PostMapping("/zones/add")
    @ResponseBody public ResponseEntity<Zone> addZone(@CurrentAccount Account account, @PathVariable String path,
                                  @RequestBody ZoneForm zoneForm) {
        Community community = communityService.getCommunityToUpdateZone(account, path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }

        communityService.addZone(community, zone);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/zones/remove")
    @ResponseBody public ResponseEntity<Zone> removeZone(@CurrentAccount Account account, @PathVariable String path,
                                     @RequestBody ZoneForm zoneForm) {
        Community community = communityService.getCommunityToUpdateZone(account, path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }

        communityService.removeZone(community, zone);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/community")
    public String communitySettingForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Community community = communityService.getCommunityToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(community);
        return "community/settings/community";
    }

    @PostMapping("/community/publish")
    public String publishCommunity(@CurrentAccount Account account, @PathVariable String path,
                               RedirectAttributes attributes) {
        Community community = communityService.getCommunityToUpdateStatus(account, path);
        communityService.publish(community);
        attributes.addFlashAttribute("message", "커뮤니티를 공개했습니다.");
        return "redirect:/community/" + community.getEncodedPath() + "/settings/community";
    }

    @PostMapping("/community/close")
    public String closeCommunity(@CurrentAccount Account account, @PathVariable String path,
                             RedirectAttributes attributes) {
        Community community= communityService.getCommunityToUpdateStatus(account, path);
        communityService.close(community);
        attributes.addFlashAttribute("message", "커뮤니티를 종료했습니다.");
        return "redirect:/community/" + community.getEncodedPath() + "/settings/community";
    }

    @PostMapping("/recruit/start")
    public String startRecruit(@CurrentAccount Account account, @PathVariable String path, Model model,
                               RedirectAttributes attributes) {
        Community community = communityService.getCommunityToUpdateStatus(account, path);
        if (!community.canUpdateRecruiting()) {
            attributes.addFlashAttribute("message", "1시간 안에 인원 모집 설정을 여러번 변경할 수 없습니다.");
            return "redirect:/community/" + community.getEncodedPath() + "/settings/community";
        }

        communityService.startRecruit(community);
        attributes.addFlashAttribute("message", "인원 모집을 시작합니다.");
        return "redirect:/community/" + community.getEncodedPath() + "/settings/community";
    }

    @PostMapping("/recruit/stop")
    public String stopRecruit(@CurrentAccount Account account, @PathVariable String path, Model model,
                              RedirectAttributes attributes) {
        Community community = communityService.getCommunityToUpdate(account, path);
        if (!community.canUpdateRecruiting()) {
            attributes.addFlashAttribute("message", "1시간 안에 인원 모집 설정을 여러번 변경할 수 없습니다.");
            return "redirect:/community/" + community.getEncodedPath() + "/settings/community";
        }

        communityService.stopRecruit(community);
        attributes.addFlashAttribute("message", "인원 모집을 종료합니다.");
        return "redirect:/community/" + community.getEncodedPath() + "/settings/community";
    }

    @PostMapping("/community/path")
    public String updateCommunityPath(@CurrentAccount Account account, @PathVariable String path, String newPath,
                                  Model model, RedirectAttributes attributes) {
        Community community = communityService.getCommunityToUpdateStatus(account, path);
        if (!communityService.isValidPath(newPath)) {
            model.addAttribute(account);
            model.addAttribute(community);
            model.addAttribute("communityPathError", "해당 커뮤니티 경로는 사용할 수 없습니다. 다른 값을 입력하세요.");
            return "community/settings/community";
        }

        communityService.updateCommunityPath(community, newPath);
        attributes.addFlashAttribute("message", "스터디 경로를 수정했습니다.");
        return "redirect:/community/" + community.getEncodedPath() + "/settings/community";
    }

    @PostMapping("/community/title")
    public String updateCommunityTitle(@CurrentAccount Account account, @PathVariable String path, String newTitle,
                                   Model model, RedirectAttributes attributes) {
        Community community = communityService.getCommunityToUpdateStatus(account, path);
        if (!communityService.isValidTitle(newTitle)) {
            model.addAttribute(account);
            model.addAttribute(community);
            model.addAttribute("communityTitleError", "스터디 이름을 다시 입력하세요.");
            return "community/settings/community";
        }

        communityService.updateCommunityTitle(community, newTitle);
        attributes.addFlashAttribute("message", "스터디 이름을 수정했습니다.");
        return "redirect:/community/" + community.getEncodedPath() + "/settings/community";
    }

    @PostMapping("/community/remove")
    public String removeCommunity(@CurrentAccount Account account, @PathVariable String path, Model model) {
       Community community = communityService.getCommunityToUpdateStatus(account, path);
        communityService.remove(community);
        return "redirect:/";
    }


}
