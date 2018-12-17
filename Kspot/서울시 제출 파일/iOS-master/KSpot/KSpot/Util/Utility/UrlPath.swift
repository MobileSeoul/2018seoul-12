//
//  UrlPath.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 19..
//  Copyright © 2018년 강수진. All rights reserved.
//
import Foundation

import Foundation

enum UrlPath : String {
    //로그인
    case facebookLogin = "/user/facebook/signin"
    case kakaoLogin = "/user/kakao/signin"
    case tempLogin = "/user/temp/signin"
    
    //메인
    case main = "/main"
    case mainSearch = "/search"
    case theme = "/theme/"
    case searchResult = "/search/"
    case spotEvent = "/spot/event"
    
    //마이페이지
    case mypage = "/user/mypage"
    case userSubscription = "/user/subscription/"
    case userScrap = "/user/scrap"
    case userEdit = "/user/edit"
    

    //맵
    case spot = "/spot/"
    case spotSubscription = "/spot/scrap/"

    //리뷰
    case reviewWrite = "/spot/review"


    
    //카테고리
    case channelList = "/channel/list" //get
    case channelSubscription = "/channel/subscription/"
    case channelDetail = "/channel/detail/"
    case channelSpotMore
    
    
    func getSpotMoreURL(channelId : Int, isEvent : Int)->String{
        return "\(NetworkConfiguration.shared().baseURL)/channel/\(channelId)/spot/\(isEvent)"
    }
    
    func getURL(_ parameter : String? = nil) -> String{
        return "\(NetworkConfiguration.shared().baseURL)\(self.rawValue)\(parameter ?? "")"
    }
}



