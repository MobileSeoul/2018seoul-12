//
//  FacebookLoginVO.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 19..
//  Copyright © 2018년 강수진. All rights reserved.
//

import Foundation

struct FacebookLoginVO: Codable {
    let message: String
    let data: FacebookLoginVOData
}

struct FacebookLoginVOData: Codable {
  //  let id: String
    let authorization: String
}

