package com.diegogonzalez.devsu.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/*
 * Author: Diego González
 *
 * This code is the exclusive property of the author. Any unauthorized use,
 * distribution, or modification is prohibited without the author's explicit consent.
 *
 * Disclaimer: This code is provided "as is" without any warranties of any kind,
 * either express or implied, including but not limited to warranties of merchantability
 * or fitness for a particular purpose.
 */
@Entity
@Table(
        name = "persons",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_person_identification", columnNames = {"identification"}),
                @UniqueConstraint(name = "uk_person_email", columnNames = {"email"})
        },
        indexes = {
                @Index(name = "idx_person_last_name", columnList = "lastName"),
                @Index(name = "idx_person_status", columnList = "status")
        }
)
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
public class Person implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", updatable = false, nullable = false, unique = true)
    private UUID uuid;

    @NotBlank(message = "Identification is required")
    @Column(name = "identification", length = 20, nullable = false)
    @Pattern(regexp = "^[A-Z0-9]{5,20}$", message = "Invalid identification format")
    private String identification;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Column(name = "firstName", length = 50, nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Column(name = "lastName", length = 50, nullable = false)
    private String lastName;

    @NotNull(message = "Gender is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10, nullable = false)
    private Gender gender;

    @NotNull(message = "Age is required")
    @Positive(message = "Age must be a positive number")
    @Column(name = "age")
    private Integer age;

    @Past(message = "Birth date must be in the past")
    @Column(name = "birthDate", columnDefinition = "DATE")
    private LocalDate birthDate;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    @Column(name = "address")
    private String address;

    @Email(message = "Invalid email format")
    @Column(name = "email")
    private String email;

    @ToString.Exclude
    @OneToMany(
            mappedBy = "person",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private Set<Phone> phones = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private PersonStatus status = PersonStatus.ACTIVE;

    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "createdBy", length = 50, nullable = false, updatable = false)
    private String createdBy;

    @Column(name = "updatedBy", length = 50)
    private String updatedBy;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
        if (createdBy == null) {
            createdBy = "SYSTEM";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (updatedBy == null) {
            updatedBy = "SYSTEM";
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Person person = (Person) o;
        return getId() != null && Objects.equals(getId(), person.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public enum Gender {
        MALE,
        FEMALE,
        OTHER
    }

    public enum PersonStatus {
        ACTIVE,
        INACTIVE,
        SUSPENDED,
        DELETED,
        LOCKED,
        CLOSED
    }
}
