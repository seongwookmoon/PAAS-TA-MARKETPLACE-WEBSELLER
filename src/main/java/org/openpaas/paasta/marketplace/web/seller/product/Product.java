package org.openpaas.paasta.marketplace.web.seller.product;

import java.util.List;

import org.openpaas.paasta.marketplace.web.seller.category.Category;
import org.openpaas.paasta.marketplace.web.seller.common.BaseModel;
import org.openpaas.paasta.marketplace.web.seller.profile.SellerProfile;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openpaas.paasta.marketplace.web.seller.userProduct.UserProduct;

@Data
@EqualsAndHashCode(callSuper=false)
public class Product extends BaseModel {

	// 상품ID
	private Long id;

	// 카테고리ID
    private Category category;

    // 판매자ID
    private SellerProfile seller;

    // 파일경로
    private String filePath;

    private List<UserProduct> userProducts;

    // 스크린샷 파일명 목록
    private List<Screenshot> screenshots;

    // 상품명
    private String productName;
    
    // 버전정보
    private String versionInfo;

    // 상품개요
    private String simpleDescription;

    // 상품상세
    private String detailDescription;

    // 상품유형
    private String productType;

    // 아이콘 파일명
    private String iconFileName;

    // 상품 파일명
    private String productFileName;
    
    // 환경 파일명
    private String envFileName;

    // 미터링 유형
    private String meteringType;
    
    // 미터링 금액
    private int unitPrice;

    // 전시여부
    private String displayYn;

}