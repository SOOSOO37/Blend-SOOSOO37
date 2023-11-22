package com.blend.server.review;

import com.blend.server.global.audit.Auditable;
import com.blend.server.product.Product;
import com.blend.server.reviewImage.ReviewImage;
import com.blend.server.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@Setter
@Getter
@Entity
public class Review extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String title;

    @Lob
    private String content;

    @Column(nullable = false)
    private int score;

    @Enumerated(value = EnumType.STRING)
    private ReviewStatus reviewStatus = ReviewStatus.REVIEW_ACTIVE;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "review",cascade = CascadeType.ALL)
    private List<ReviewImage> images = new ArrayList<>();

    public enum ReviewStatus{

        REVIEW_ACTIVE(1, "작성된 리뷰"),
        REVIEW_DELETE(2, "삭제된 리뷰");

        @Getter
        private int number;
        @Getter
        private String description;

        ReviewStatus(int number, String description) {
            this.number = number;
            this.description = description;
        }
    }

    public void addImage(ReviewImage image){
        this.images.add(image);
        if(image.getReview() != this){
            image.addReview(this);
        }
    }
}
