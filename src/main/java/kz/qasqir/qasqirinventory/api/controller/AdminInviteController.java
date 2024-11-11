//package kz.qasqir.qasqirinventory.api.controller;
//
//import kz.qasqir.qasqirinventory.api.model.dto.InviteUserDTO;
//import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
//import kz.qasqir.qasqirinventory.api.repository.InviteRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/admin/invite")
//public class AdminInviteController {
//
//    private final InviteRepository inviteRepository;
//
//    @Autowired
//    public AdminInviteController(InviteRepository inviteRepository) {
//        this.inviteRepository = inviteRepository;
//    }
//
//    @GetMapping
//    public MessageResponse<List<InviteUserDTO>> getAll() {
//        return MessageResponse.of(inviteRepository.findInviteIdAndUserNameAndEmail());
//    }
//}
