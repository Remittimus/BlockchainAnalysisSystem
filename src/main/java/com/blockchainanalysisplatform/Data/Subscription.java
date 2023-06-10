package com.blockchainanalysisplatform.Data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;

import java.util.*;


//TODO: enum type {trans,form,to}
@Data
@Entity
@Table(name = "Subscriptions")

public class Subscription {


    @ToString.Exclude
    @ManyToMany(mappedBy = "subscriptions")
    private List<User> users = new LinkedList<>();

    @Embedded
    Filter filter;

    @Id
    private String id; //id after encoding with filters

    @NonNull
    private String type; //from to hash
    @NotBlank
    private String address; //address transaction or wallet
    private String nodeName; //url to eth-node
    private String topicId; //topic in kafka, response in eventeum

    @ElementCollection
//    @CollectionCo(name = "statuses")
    @Enumerated(EnumType.STRING)
    private List<SubscriptionStatuses> statuses;

    public Subscription() {
        this.nodeName="default";
    }

    public void addNewUser(User user){
            users.add(user);
            user.getSubscriptions().add(this);
    }

    public void removeUser(User user){
        if((users!=null&&users.contains(user))){
            users.remove(user);
            user.getSubscriptions().remove(this);
        }
    }

    @Override
    public boolean equals(Object s) {
        if (this == s) return true;
        if (!(s instanceof Subscription)) return false;
        Subscription subscription = (Subscription) s;
        return (
                Objects.equals(this.getId(),subscription.getId()) &&
                Objects.equals(this.getFilter(),subscription.getFilter())&&
                Objects.equals(this.getTopicId(),subscription.getTopicId()));
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId()+getFilter()+getTopicId());
    }


}
