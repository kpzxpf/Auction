package com.volzhin.auction.entity;

import com.volzhin.auction.entity.lot.Lot;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "images")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "size")
    private long size;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id", nullable = false)
    private Lot lot;

    @Column(name = "key", nullable = false, length = 50)
    private String key;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "uploaded_at", updatable = false)
    private LocalDateTime uploadedAt;
}