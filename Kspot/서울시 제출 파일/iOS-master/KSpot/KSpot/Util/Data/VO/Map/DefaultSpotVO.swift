//
//  DefaultSpotVO.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 21..
//  Copyright © 2018년 강수진. All rights reserved.
//

import Foundation
struct DefaultSpotVO: Codable {
    let message: String
    let data: [UserScrapVOData]
}

/*struct DefaultSpotVOData: Codable {
    let spotID: Int
    let reviewScore: Double
    let name, description, addressGu, station: String
    let img: String
    let channel: DefaultSpotVODataChannel
    
    enum CodingKeys: String, CodingKey {
        case spotID = "spot_id"
        case reviewScore = "review_score"
        case name, description
        case addressGu = "address_gu"
        case station, img, channel
    }
}

struct DefaultSpotVODataChannel: Codable {
    let channelID, thumbnailImg: [String]
    
    enum CodingKeys: String, CodingKey {
        case channelID = "channel_id"
        case thumbnailImg = "thumbnail_img"
    }
}*/
