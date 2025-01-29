package kz.qasqir.qasqirinventory.api.repository;

import kz.qasqir.qasqirinventory.api.model.entity.DocumentFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentFileRepository extends JpaRepository<DocumentFile, Long> {
    @Query(value = "INSERT INTO t_documents_files (document_id, file_name, file_data, uploaded_at) " +
            "VALUES (:documentId, :fileName, :fileData, CURRENT_TIMESTAMP) RETURNING id", nativeQuery = true)
    void saveDocumentFile(@Param("documentId") Long documentId,
                          @Param("fileName") String fileName,
                          @Param("fileData") byte[] fileData);
}
