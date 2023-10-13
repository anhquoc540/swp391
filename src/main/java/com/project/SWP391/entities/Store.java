package com.project.SWP391.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.util.Set;

@Getter
@Setter

@Entity
@Table(name="stores")
public class Store {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_name")
    @Nationalized
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    @Nationalized
    private String address;

    @Column(name="status")
    private int status;



    @OneToMany(mappedBy = "store")
    private Set<Time> times;

    @OneToMany(mappedBy = "store")
    private Set<Order> orders;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany(mappedBy = "store")
    private Set<SpecialLaundry> specials;

    @OneToMany(mappedBy = "store")
    private Set<StandardLaundry> standards;

}
