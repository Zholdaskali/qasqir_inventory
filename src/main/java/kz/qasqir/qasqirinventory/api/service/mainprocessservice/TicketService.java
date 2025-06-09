package kz.qasqir.qasqirinventory.api.service.mainprocessservice;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.mapper.InventoryMapper;
import kz.qasqir.qasqirinventory.api.model.dto.DocumentDTO;
import kz.qasqir.qasqirinventory.api.model.dto.InventoryDTO;
import kz.qasqir.qasqirinventory.api.model.dto.TicketDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Document;
import kz.qasqir.qasqirinventory.api.model.request.BatchTicketRequest;
import kz.qasqir.qasqirinventory.api.model.request.BatchProcessRequest;
import kz.qasqir.qasqirinventory.api.model.entity.Ticket;
import kz.qasqir.qasqirinventory.api.model.request.DocumentFileRequest;
import kz.qasqir.qasqirinventory.api.repository.TicketRepository;
import kz.qasqir.qasqirinventory.api.service.defaultservice.DocumentFileService;
import kz.qasqir.qasqirinventory.api.service.defaultservice.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final InventoryMapper inventoryMapper;
    private final DocumentService documentService;
    private final ProcessSalesAndWriteOffAndProductionService processSalesAndWriteOffAndProductionService;
    private final DocumentFileService documentFileService;

    public TicketDTO findById(Long id) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new RuntimeException("Заявка не найдена"));
        return convertToDTO(ticket);
    }

    public List<TicketDTO> findAll() {
        return ticketRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Создание заявки
    @Transactional
    public String addBatchWriteOffTickets(BatchProcessRequest batchProcessRequest) {
        Document document = documentService.createDocument(batchProcessRequest.getDocumentType(), batchProcessRequest.getDocumentNumber(), null, batchProcessRequest.getCustomerId(), batchProcessRequest.getCreatedBy());
        for (int i = 0; i < batchProcessRequest.getTicketRequests().size(); i++) {
            BatchTicketRequest batchTicketRequest = batchProcessRequest.getTicketRequests().get(i);
            createTicket(batchTicketRequest.getComment(), batchTicketRequest.getQuantity(), batchTicketRequest.getInventoryId(), batchProcessRequest.getCreatedBy(), batchProcessRequest.getDocumentType(), document.getId());
        }
        if (batchProcessRequest.getFileData() != null) {
            byte[] fileData = Base64.getDecoder().decode(batchProcessRequest.getFileData());
            addDocumentFile(batchProcessRequest.getFileName(), fileData, document.getId());
        }
        return "Групповое списание успешно выполнено";
    }

    private void addDocumentFile(String fileName, byte[] fileData, Long documentId) {
        DocumentFileRequest documentFileRequest = new DocumentFileRequest(documentId, fileName, fileData);
        documentFileService.saveDocumentFile(documentFileRequest);
    }

    @Transactional
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

        processSalesAndWriteOffAndProductionService.processTicket(ticket);
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

    public List<TicketDTO> getAllTicked(String type, LocalDate startDate, LocalDate endDate) {
        return ticketRepository.findAllTicketsWithJoins(type, startDate.atStartOfDay(), endDate.atStartOfDay()).stream().map(this::convertToDTO).toList();
    }

    @Transactional
    public String delete(Long ticketId) {
        ticketRepository.deleteById(ticketId);
        return "Заявка успешно отменена";
    }
}
