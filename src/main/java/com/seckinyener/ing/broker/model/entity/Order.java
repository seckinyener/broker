package com.seckinyener.ing.broker.model.entity;

import com.seckinyener.ing.broker.model.enumerated.SideEnum;
import com.seckinyener.ing.broker.model.enumerated.StatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="customer_order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(name="order_side", nullable = false)
    @Enumerated(EnumType.STRING)
    private SideEnum orderSide;

    @Column(name="size", nullable = false)
    private double size;

    @Column(name="price", nullable = false)
    private double price;

    @Column(name="status", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @Column(name="create_date", nullable = false)
    private LocalDateTime createDate;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "asset_name", referencedColumnName = "name"),
            @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    })
    private Asset asset;

    @PrePersist
    public void prePersist() {
        createDate = LocalDateTime.now();
    }

}

