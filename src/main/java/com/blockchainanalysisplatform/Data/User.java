package com.blockchainanalysisplatform.Data;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.io.Serial;
import java.io.Serializable;
import java.util.*;


@Data
@NoArgsConstructor
@Entity
@Table(name = "Users")
public class User implements UserDetails, Serializable {
    @Serial
    @Transient
    private static final long serialVersionUID = 1993558129899513236L;

    @ToString.Exclude
    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER) //TODO: cascade mb?
    @JoinTable(
            name = "User_Subscription",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "subscription_id")
    )
    private List<Subscription> subscriptions = new LinkedList<>();


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    public User(@NotBlank String name, @NotBlank String password, @NotBlank @Email String email) {
        this.username = name;
        this.password = password;
        this.email = email;
    }

    public void addNewSubscription(Subscription subscription){
            this.subscriptions.add(subscription);
            subscription.getUsers().add(this);

    }
    public void removeSubscription(Subscription subscription){
        if(subscriptions!=null&&subscriptions.contains(subscription)){
            subscriptions.remove(subscription);

            subscription.getUsers().remove(this);
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object u) {
        if (this == u) return true;
        if (!(u instanceof User)) return false;
        User user = (User) u;
        return (Objects.equals(this.getId(),user.getId())&&
                Objects.equals(this.getSubscriptions(),user.getSubscriptions()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail());
    }
}