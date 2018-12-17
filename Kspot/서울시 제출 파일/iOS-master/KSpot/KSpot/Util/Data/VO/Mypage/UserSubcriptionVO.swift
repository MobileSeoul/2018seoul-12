//
//  UserSubcriptionVO.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 19..
//  Copyright © 2018년 강수진. All rights reserved.
//

import Foundation

struct UserSubcriptionVO: Codable {
    let message: String
    let data: UserSubcriptionVOData
}

struct UserSubcriptionVOData: Codable {
    let celebrity, broadcast: [UserSubcriptionVOBroadcast]
}

struct UserSubcriptionVOBroadcast: Codable {
    let name, thumbnailImg: String
    let newPostCheck: Int
    let channelId : Int
    
    enum CodingKeys: String, CodingKey {
        case channelId = "channel_id"
        case name = "name"
        case thumbnailImg = "thumbnail_img"
        case newPostCheck = "new_post_check"
    }
}
