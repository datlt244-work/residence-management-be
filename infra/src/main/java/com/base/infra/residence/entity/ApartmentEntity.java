package com.base.infra.residence.entity;

import com.base.infra.employee.entity.EmployeeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "apartments")
@Getter
@Setter
@NoArgsConstructor
public class ApartmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private ZoneEntity zone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartment_type_id", nullable = false)
    private ApartmentTypeEntity apartmentType;

    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @Column(name = "hashed_code", length = 100)
    private String hashedCode;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal area;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "tax_fee", precision = 15, scale = 2)
    private BigDecimal taxFee = BigDecimal.ZERO;

    @Column(name = "furniture_status", length = 100)
    private String furnitureStatus;

    @Column(name = "legal_status", length = 100)
    private String legalStatus;

    @Column(name = "balcony_direction", length = 50)
    private String balconyDirection;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "owner_phone", length = 50)
    private String ownerPhone;

    @Column(name = "owner_contact", length = 255)
    private String ownerContact;

    @Column(length = 150)
    private String source;

    @Column(nullable = false, length = 50)
    private String status = "AVAILABLE";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private EmployeeEntity createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
