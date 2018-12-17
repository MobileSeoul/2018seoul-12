//
//  ChannelListVO.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 19..
//  Copyright © 2018년 강수진. All rights reserved.
//

import Foundation

struct ChannelVO: Codable {
    let message: String
    let data: ChannelVOData
}

struct ChannelVOData: Codable {
    let channelCelebrityList, channelBroadcastList: [ChannelVODataChannelList]
    
    enum CodingKeys: String, CodingKey {
        case channelCelebrityList = "channel_celebrity_list"
        case channelBroadcastList = "channel_broadcast_list"
    }
}

struct ChannelVODataChannelList: Codable {
    let channelID, subscriptionCnt, spotCnt: Int
    let thumbnailImg: String
    var subscription: Int
    let name: String
    
    enum CodingKeys: String, CodingKey {
        case channelID = "channel_id"
        case subscriptionCnt = "subscription_cnt"
        case spotCnt = "spot_cnt"
        case thumbnailImg = "thumbnail_img"
        case subscription, name
    }
}



