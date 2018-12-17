//
//  ReviewVO.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 21..
//  Copyright © 2018년 강수진. All rights reserved.
//

import Foundation

struct ReviewVO: Codable {
    let message: String
    let data: ReviewVOData
}

struct ReviewVOData: Codable {
    let spotReview: ReviewVODataSpotReview
    let reviews: [PlaceDetailVODataReview]
    enum CodingKeys: String, CodingKey {
        case spotReview = "spot_review"
        case reviews
    }
}

struct ReviewVODataSpotReview: Codable {
    let reviewScore: Double
    let reviewCnt: Int
    
    enum CodingKeys: String, CodingKey {
        case reviewScore = "review_score"
        case reviewCnt = "review_cnt"
    }
}
