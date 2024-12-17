package kz.qasqir.qasqirinventory.api.controller;

import kz.qasqir.qasqirinventory.api.model.entity.Nomenclature;
import kz.qasqir.qasqirinventory.api.model.request.NomenclatureRequest;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.NomenclatureService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/warehouse-manager")
public class StockMovementController {
    private final NomenclatureService nomenclatureService;

    public StockMovementController(NomenclatureService nomenclatureService) {
        this.nomenclatureService = nomenclatureService;
    }


}
