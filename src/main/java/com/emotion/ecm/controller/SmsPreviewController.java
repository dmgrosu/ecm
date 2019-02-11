package com.emotion.ecm.controller;

import com.emotion.ecm.model.Account;
import com.emotion.ecm.model.AppUser;
import com.emotion.ecm.model.SmsPreview;
import com.emotion.ecm.model.dto.PreviewDto;
import com.emotion.ecm.service.*;
import com.emotion.ecm.util.DateUtil;
import com.emotion.ecm.validation.AjaxResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping(value = "/preview")
public class SmsPreviewController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmsPreviewController.class);

    private SmsPreviewService smsPreviewService;
    private AppUserService userService;
    private SmsTypeService typeService;
    private SmsPriorityService priorityService;
    private AccountDataService accountDataService;
    private ContactService contactService;
    private SmppAddressService smppAddressService;
    private ExpirationTimeService expirationTimeService;

    @Autowired
    public SmsPreviewController(SmsPreviewService smsPreviewService, AppUserService userService,
                                SmsTypeService typeService, SmsPriorityService priorityService,
                                ContactService contactService, AccountDataService accountDataService,
                                SmppAddressService smppAddressService, ExpirationTimeService expirationTimeService) {
        this.smsPreviewService = smsPreviewService;
        this.userService = userService;
        this.typeService = typeService;
        this.priorityService = priorityService;
        this.contactService = contactService;
        this.accountDataService = accountDataService;
        this.smppAddressService = smppAddressService;
        this.expirationTimeService = expirationTimeService;
    }

    @GetMapping(value = "/list")
    public String showList(Model model) {
        AppUser user = userService.getAuthenticatedUser();
        List<PreviewDto> previews = smsPreviewService.getAllDtoByUserId(user.getId());
        model.addAttribute("previews", previews);
        return "preview/list";
    }

    @GetMapping(value = "/create")
    public String createPreviewForm(Model model) {

        model.addAllAttributes(createPreviewAttributes(null));

        return "preview/form";
    }

    @PostMapping(value = "/create")
    public String savePreview(@Valid @ModelAttribute(name = "preview") PreviewDto previewDto,
                              BindingResult bindingResult, Model model) {

        Optional<SmsPreview> optional = smsPreviewService.getByUserIdAndName(previewDto.getUserId(), previewDto.getName());
        if (optional.isPresent()) {
            bindingResult.rejectValue("name", "name.error", "Name is not unique");
        }

        if (bindingResult.hasErrors()) {
            model.addAllAttributes(createPreviewAttributes(previewDto));
            return "preview/form";
        }

        try {
            smsPreviewService.createNewPreview(previewDto);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        return "redirect:/preview/list";
    }

    @PostMapping(value = "/delete")
    @ResponseBody
    public AjaxResponseBody deletePreview(@RequestBody PreviewDto previewDto) {

        List<FieldError> allErrors = new ArrayList<>();
        AjaxResponseBody result = new AjaxResponseBody(true, allErrors);

        try {
            if (previewDto.getPreviewId() == 0) {
                result.setValid(false);
                allErrors.add(new FieldError("previewDto", "previewId", "preview id is 0"));
            } else {
                smsPreviewService.deleteById(previewDto.getPreviewId());
            }
        } catch (Exception e) {
            result.setValid(false);
            allErrors.add(new FieldError("previewDto", "previewId", e.getMessage()));
        }

        return result;
    }

    @PostMapping(value = "/changeStatus")
    @ResponseBody
    public AjaxResponseBody changeStatus(@RequestBody PreviewDto previewDto) {

        List<FieldError> allErrors = new ArrayList<>();
        AjaxResponseBody result = new AjaxResponseBody(true, allErrors);

        try {
            if (previewDto.getPreviewId() == 0) {
                result.setValid(false);
                allErrors.add(new FieldError("previewDto", "previewId", "preview id is 0"));
            } else {
                smsPreviewService.changeStatus(previewDto);
            }
        } catch (Exception e) {
            result.setValid(false);
            allErrors.add(new FieldError("previewDto", "previewId", e.getMessage()));
        }

        return result;
    }

    private Map<String, Object> createPreviewAttributes(PreviewDto previewDto) {

        AppUser currUser = userService.getAuthenticatedUser();
        Account account = currUser.getAccount();

        Map<String, Object> result = new HashMap<>();

        if (previewDto == null) {
            previewDto = new PreviewDto();
            previewDto.setSendDate(LocalDateTime.now());
            previewDto.setUserId(currUser.getId());
            previewDto.setTps(account.getTps());
            previewDto.setUsername(currUser.getUsername());
        }

        result.put("preview", previewDto);
        result.put("types", typeService.getAll());
        result.put("priorities", priorityService.getAll());
        result.put("accountDataList", accountDataService.getAllByUser(currUser));
        result.put("groupList", contactService.getAllGroupsByUser(currUser));
        result.put("originators", smppAddressService.getAllAddressesByAccount(account));
        result.put("availableExpTime", expirationTimeService.getAllByAccount(account));

        return result;
    }
}
