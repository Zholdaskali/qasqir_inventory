package kz.qasqir.qasqirinventory.api.controller;

import kz.qasqir.qasqirinventory.api.model.entity.Nomenclature;
import kz.qasqir.qasqirinventory.api.model.request.OneC.OneCWriteOffRequest;
import kz.qasqir.qasqirinventory.api.model.request.OneC.SyncingWith1CRequest;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.defaultservice.NomenclatureService;
import kz.qasqir.qasqirinventory.api.service.mainprocessservice.ProcessOneCService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    @GetMapping("/nomenclatures/synced")
    public List<Nomenclature> getSyncedNomenclatures(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return nomenclatureService.findNomenclaturesSyncedSince(startDate, endDate);
    }

    @GetMapping("/nomenclatures/not-synced")
    public List<Nomenclature> getNotSyncedNomenclatures(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return nomenclatureService.findNomenclaturesNotSyncedSince(startDate, endDate);
    }
}
