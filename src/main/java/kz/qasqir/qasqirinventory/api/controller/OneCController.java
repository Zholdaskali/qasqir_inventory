package kz.qasqir.qasqirinventory.api.controller;

import kz.qasqir.qasqirinventory.api.model.request.OneC.OneCWriteOffRequest;
import kz.qasqir.qasqirinventory.api.model.request.OneC.SyncingWith1CRequest;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.product.NomenclatureService;
import kz.qasqir.qasqirinventory.api.service.process.ProcessOneCService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/1C")
@RequiredArgsConstructor
public class OneCController {

    private final ProcessOneCService processOneCService;
    private final NomenclatureService nomenclatureService;

    @PostMapping("/issue-requests")
    public MessageResponse<String> createIssueRequest(@RequestBody OneCWriteOffRequest oneCWriteOffRequest) {
        return MessageResponse.of(processOneCService.createIssueRequest(oneCWriteOffRequest));
    }

    @PostMapping("/syncing-with-1C")
    public MessageResponse<String> syncingWith1C(@RequestBody SyncingWith1CRequest syncingWith1CRequest) {
        return MessageResponse.of(processOneCService.syncingWith1C(syncingWith1CRequest));
    }
}
