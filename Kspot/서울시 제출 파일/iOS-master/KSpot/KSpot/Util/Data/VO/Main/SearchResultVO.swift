//
//  SearchResultVO.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 20..
//  Copyright © 2018년 강수진. All rights reserved.
//

import Foundation
struct SearchResultVO: Codable {
    let message: String
    let data: SearchResultVOData
}

struct SearchResultVOData: Codable {
    var channel: [ChannelVODataChannelList]
    let place, event : [SearchResultVODataPlace]
}



/*struct SearchResultVODataChannel: Codable {
    let channelID: Int
    let name: String
    let subscriptionCnt, spotCnt: Int
    let thumbnailImg: String
    let subscription: Int
    
    enum CodingKeys: String, CodingKey {
        case channelID = "channel_id"
        case name
        case subscriptionCnt = "subscription_cnt"
        case spotCnt = "spot_cnt"
        case thumbnailImg = "thumbnail_img"
        case subscription
    }
}*/

//ChannelDetailVODataRelatedChannel 에 비해 type 한개 더 들어가있음
struct SearchResultVODataPlace: Codable {
    let type, spotID: Int
    let name, description: String
    let img: String
    let addressGu, station: String
    let scrapCnt: Int
    
    enum CodingKeys: String, CodingKey {
        case type
        case spotID = "spot_id"
        case name, description, img
        case addressGu = "address_gu"
        case station
        case scrapCnt = "scrap_cnt"
    }
}


