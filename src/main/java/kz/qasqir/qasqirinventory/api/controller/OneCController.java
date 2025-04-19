package kz.qasqir.qasqirinventory.api.controller;

import kz.qasqir.qasqirinventory.api.model.request.OneC.OneCWriteOffRequest;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.mainprocessservice.ProcessOneCService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/1C")
@RequiredArgsConstructor
public class OneCController {

    private final ProcessOneCService processOneCService;

    @PostMapping("/issue-requests")
    public MessageResponse<String> createIssueRequest(@RequestBody OneCWriteOffRequest oneCWriteOffRequest) {
        return MessageResponse.of(processOneCService.createIssueRequest(oneCWriteOffRequest));
    }
}
