//package kz.qasqir.qasqirinventory.api.controller;
//
//import kz.qasqir.qasqirinventory.api.model.dto.InviteUserDTO;
//import kz.qasqir.qasqirinventory.api.repository.InviteRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//
//class AdminInviteControllerTest {
//
//    private MockMvc mockMvc;
//
//    @Mock
//    private InviteRepository inviteRepository;
//
//    @InjectMocks
//    private AdminInviteController adminInviteController;
//
//    private List<InviteUserDTO> inviteUserDTOList;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        mockMvc = MockMvcBuilders.standaloneSetup(adminInviteController).build();
//
//        // Подготавливаем тестовые данные
//        InviteUserDTO invite1 = new InviteUserDTO(1L, "User1", "user1@email.com");
//        InviteUserDTO invite2 = new InviteUserDTO(2L, "User2", "user2@email.com");
//        inviteUserDTOList = Arrays.asList(invite1, invite2);
//    }
//
//    @Test
//    void testGetAllInvites() throws Exception {
//        // Мокируем поведение репозитория
//        when(inviteRepository.findInviteIdAndUserNameAndEmail()).thenReturn(inviteUserDTOList);
//
//        // Выполняем запрос и проверяем результат
//        mockMvc.perform(get("/api/v1/admin/invites")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())  // Статус HTTP 200 OK
//                .andExpect(jsonPath("$.data[0].id").value(1L))  // Проверяем первое поле в ответе
//                .andExpect(jsonPath("$.data[0].userName").value("User1"))
//                .andExpect(jsonPath("$.data[0].email").value("user1@email.com"))
//                .andExpect(jsonPath("$.data[1].id").value(2L))  // Проверяем второе поле в ответе
//                .andExpect(jsonPath("$.data[1].userName").value("User2"))
//                .andExpect(jsonPath("$.data[1].email").value("user2@email.com"));
//    }
//}
