//
//  MainSearchVO.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 20..
//  Copyright © 2018년 강수진. All rights reserved.
//

import Foundation


struct MainSearchVO: Codable {
    let message: String
    let data: MainSearchVOData
}

struct MainSearchVOData: Codable {
    let celebrity, broadcast : [MainSearchVODataCelebrity]
    let event : [MainSearchVODataEvent]
}

struct MainSearchVODataCelebrity: Codable {
    let name: String
    let channelId : Int
    
    enum CodingKeys: String, CodingKey {
        case name
        case channelId = "channel_id"
    }
    
}

struct MainSearchVODataEvent: Codable {
    let spotID: Int
    let name: String
    
    enum CodingKeys: String, CodingKey {
        case spotID = "spot_id"
        case name
    }
}
