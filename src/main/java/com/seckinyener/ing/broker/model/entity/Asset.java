package com.seckinyener.ing.broker.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="asset", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"customer_id", "name"})
})
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="size", nullable = false)
    private BigDecimal size;

    @Column(name="usable_size", nullable = false)
    private BigDecimal usableSize;

    @Column(name="update_date", nullable = false)
    private LocalDateTime updateDate;

    @ManyToOne
    @JoinColumn(name="customer_id")
    private Customer customer;

    @PrePersist
    @PreUpdate
    public void preUpdate() {
        updateDate = LocalDateTime.now();
    }
}
