package kz.qasqir.qasqirinventory.api.model.dto;

import java.time.LocalDateTime;

public class NomenclatureDTO {
    private Long id;
    private String name;
    private String article;
    private String code;
    private String type;
    private Long categoryId;
    private String measurement;
    private String tnved;
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public NomenclatureDTO(Long id, String name, String article, String code, String type, Long categoryId, String measurement, String tnved, Long createdBy, Long updatedBy, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.article = article;
        this.code = code;
        this.type = type;
        this.categoryId = categoryId;
        this.measurement = measurement;
        this.tnved = tnved;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public String getTnved() {
        return tnved;
    }

    public void setTnved(String tnved) {
        this.tnved = tnved;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

