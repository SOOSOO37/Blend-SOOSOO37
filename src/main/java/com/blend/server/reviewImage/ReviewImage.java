package com.blend.server.reviewImage;

import com.blend.server.review.Review;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
public class ReviewImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private byte[] image;

    @Column
    private String type;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;

    public void addReview(Review review){
        this.review = review;
        if(!review.getImages().contains(this)){
            this.review.addImage(this);
        }
    }
}
