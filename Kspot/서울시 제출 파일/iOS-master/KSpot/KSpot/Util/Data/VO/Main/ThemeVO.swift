//
//  ThemeVO.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 20..
//  Copyright © 2018년 강수진. All rights reserved.
//

import Foundation

struct ThemeVO: Codable {
    let data: ThemeVOData
    let message: String
}

struct ThemeVOData: Codable {
    let themeContents: [ThemeVODataThemeContent]
    let theme: ThemeVODataTheme
    
    enum CodingKeys: String, CodingKey {
        case themeContents = "theme_contents"
        case theme
    }
}

struct ThemeVODataTheme : Codable {
    let img: String
    let title: [String]
    let subtitle: String
    
    enum CodingKeys: String, CodingKey {
        case img, title
        case subtitle
    }
}

struct ThemeVODataThemeContent: Codable {
    let spotID: Int
    let description: [String]
    let title: String
    let img: String
    
    enum CodingKeys: String, CodingKey {
        case spotID = "spot_id"
        case description, title, img
    }
}



