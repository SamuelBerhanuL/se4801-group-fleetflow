package com.fleetflow.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Route — a predefined delivery path from one city to another.
 * Always originates FROM a Warehouse.
 * Example: "Addis to Hawassa" route from "Bole Warehouse"
 *
 * @ManyToOne = many routes can belong to the same warehouse
 */
@Entity
@Table(name = "routes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The warehouse this route starts from
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "origin_city", nullable = false)
    private String originCity;

    @Column(name = "destination_city", nullable = false)
    private String destinationCity;

    // Estimated hours to complete this route
    @Column(name = "estimated_hours", nullable = false)
    private Integer estimatedHours;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
