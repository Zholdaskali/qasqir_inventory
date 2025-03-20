package kz.qasqir.qasqirinventory.api.service;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.mapper.InventoryMapper;
import kz.qasqir.qasqirinventory.api.model.dto.DocumentDTO;
import kz.qasqir.qasqirinventory.api.model.dto.InventoryDTO;
import kz.qasqir.qasqirinventory.api.model.dto.TicketDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Document;
import kz.qasqir.qasqirinventory.api.model.request.BatchTicketRequest;
import kz.qasqir.qasqirinventory.api.model.request.BatchWriteOffRequest;
import kz.qasqir.qasqirinventory.api.model.request.TicketRequest;
import kz.qasqir.qasqirinventory.api.model.entity.Ticket;
import kz.qasqir.qasqirinventory.api.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final InventoryMapper inventoryMapper;
    private final DocumentService documentService;
    private final ProcessWriteOffService processWriteOffService;
    private final ProcessSalesAndTransferService processSalesAndTransferService;

    public TicketDTO findById(Long id) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new RuntimeException("Заявка не найдена"));
        return convertToDTO(ticket);
    }

    public List<TicketDTO> findAll() {
        return ticketRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }


    // Создание заявки
    @Transactional
    public String addBatchWriteOffTickets(BatchWriteOffRequest batchWriteOffRequest) {
        Document document = documentService.createDocument(batchWriteOffRequest.getDocumentType(), batchWriteOffRequest.getDocumentNumber(), null, null, batchWriteOffRequest.getCreatedBy());
        for (int i = 0; i < batchWriteOffRequest.getTicketRequests().size(); i++) {
            BatchTicketRequest batchTicketRequest = batchWriteOffRequest.getTicketRequests().get(i);
            createTicket(batchTicketRequest.getComment(), batchTicketRequest.getQuantity(), batchTicketRequest.getInventoryId(), batchWriteOffRequest.getCreatedBy(), batchWriteOffRequest.getDocumentType(), document.getId());
        }
        return "Групповое списание успешно выполнено";
    }

    public Ticket createTicket(String comment, BigDecimal quantity, Long inventoryId, Long createBy, String type, Long documentId) {
        Ticket ticket = new Ticket();
        ticket.setComment(comment);
        ticket.setQuantity(quantity);
        ticket.setInventoryId(inventoryId);
        ticket.setCreateBy(createBy);
        ticket.setType(type);
        ticket.setCreateAt(LocalDateTime.now());
        ticket.setDocumentId(documentId);
        ticket.setStatus("ACTIVE");
        return ticketRepository.save(ticket);
    }
    @Transactional
    public String addTicket(TicketRequest ticketRequest) {
        Document document = documentService.createDocument(ticketRequest.getDocumentType(), ticketRequest.getDocumentNumber(), null, null, ticketRequest.getCreatedBy());

        createTicket(ticketRequest.getComment(),
                ticketRequest.getQuantity(),
                ticketRequest.getInventoryId(),
                ticketRequest.getCreatedBy(),
                ticketRequest.getDocumentType(),
                document.getId());
        return "Заявка успешно подана";
    }

    // Подпись заявки
    @Transactional
    public String allowedBatchTickets(List<Long> ticketIds, Long managerId) {
        for (Long ticketId : ticketIds) {
            allowedTicket(ticketId, managerId);
        }
        return "Групповое подтверждение успешно выполнено";
    }

    @Transactional
    public String allowedTicket(Long ticketId, Long managerId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new RuntimeException("Заявка не найдена"));
        ticket.setStatus("ALLOWED");
        ticket.setManagerId(managerId);
        ticket.setManagedAt(LocalDateTime.now());
        ticketRepository.save(ticket);
        return "Заявка успешно принята";
    }


    // Выполнение заявки
    @Transactional
    public String completedBatchTickets(List<Long> ticketIds) {
        for (Long ticketId : ticketIds) {
            completedTicket(ticketId);
        }
        return "Групповое завершение успешно выполнено";
    }

    @Transactional
    public String completedTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Заявка с ID " + ticketId + " не найдена"));

        if (!"ALLOWED".equals(ticket.getStatus())) {
            throw new IllegalStateException("Заявка с ID " + ticketId + " не была одобрена. Текущий статус: " + ticket.getStatus());
        }
        ticket.setStatus("COMPLETED");

        if (ticket.getType().equals("WRITE-OFF")) {
            processWriteOffService.processTicketWriteOff(ticket);
        }
//        } else if (ticket.getType().equals("PRODUCTION")) {
//            processSalesAndTransferService.processProductionTransfer(ticket);
//        }
        ticketRepository.save(ticket);

        return "Заявка успешно закрыта";
    }

    private TicketDTO convertToDTO(Ticket ticket) {
        if (ticket == null) {
            throw new RuntimeException("Заявка не найдена");
        }

        InventoryDTO inventoryDTO = inventoryMapper.toDto(ticket.getInventory());
        if (inventoryDTO == null) {
            throw new RuntimeException("Инвентарь не найден");
        }

        DocumentDTO documentDTO = documentService.convertToDto(ticket.getDocument());

        return new TicketDTO(
                ticket.getId(),
                ticket.getType(),
                ticket.getStatus(),
                documentDTO,
                ticket.getCreatedBy() != null ? ticket.getCreatedBy().getId() : null,
                ticket.getCreatedBy() != null ? ticket.getCreatedBy().getUserName() : null,
                ticket.getCreateAt(),
                ticket.getManager() != null ? ticket.getManager().getId() : null,
                ticket.getManager() != null ? ticket.getManager().getUserName() : null,
                ticket.getManagedAt(),
                ticket.getComment(),
                inventoryDTO,
                ticket.getQuantity()
        );
    }

    public List<TicketDTO> getAllTicked(String type) {
        return ticketRepository.findAllByType(type).stream().map(this::convertToDTO).toList();
    }
}
