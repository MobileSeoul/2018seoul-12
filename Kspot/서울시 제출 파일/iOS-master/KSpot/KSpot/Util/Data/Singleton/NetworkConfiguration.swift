//
//  NetworkConfiguration.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 11..
//  Copyright © 2018년 강수진. All rights reserved.
//

import Foundation
class NetworkConfiguration {
    
    let googleMapAPIKey = "AIzaSyAjENH3Hb6jDbi_X6Qca7NhlUNsOKz12gg"
    let baseURL = "http://13.209.35.110:3000"
    
    struct StaticInstance {
        static var instance: NetworkConfiguration?
    }
    
    class func shared() -> NetworkConfiguration {
        if StaticInstance.instance == nil {
            StaticInstance.instance = NetworkConfiguration()
        }
        return StaticInstance.instance!
    }
}



