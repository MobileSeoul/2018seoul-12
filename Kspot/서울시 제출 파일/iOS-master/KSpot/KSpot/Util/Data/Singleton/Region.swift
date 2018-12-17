//
//  Region.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 2..
//  Copyright © 2018년 강수진. All rights reserved.
//

import Foundation

enum Language : Int {
    case kor = 0
    case eng = 1
}

enum LoginType {
    case kakao
    case facebook
}

enum Direction {
    case right
    case left
}

enum LineNumber : String {
    case one = "1"
    case two = "2"
    case three = "3"
    case four = "4"
    case five = "5"
    case six = "6"
    case seven = "7"
    case eight = "8"
    case nine = "9"
    case gyeonguijungang  = "경의중앙"
    case shinbundang = "신분당"
    case bundang = "분당"
    
    var lineImgName : String  {
        return "place_detail_line_\(self)"
    }
    var dotImgName : String  {
        if selectedLang == .kor {
            return "place_detail_dot_\(self)"
        } else {
            return "place_detail_dot_\(self)_eng"
        }
        
    }
}

enum Distance : String {
    case oneHM = "100m"
    case threeHM = "300m"
    case fiveHM = "500m"
    case oneKM = "1km"
    case threeKM = "3km"
}
enum Region : String {
    //25개
    case gangseogu = "강서구"
    case yangcheongu = "양천구"
    case gurogu = "구로구"
    case yeongdeungpogu = "영등포구"
    case geumcheongu = "금천구"
    case gwanakgu = "관악구"
    case dongjakgu = "동작구"
    case yongsangu = "용산구"
    case mapogu = "마포구"
    case seodaemungu = "서대문구"
    case gangnamgu = "강남구"
    case seochogu = "서초구"
    case junggu = "중구"
    case eunpyeonggu = "은평구"
    case jongrogu = "종로구"
    case seongbukgu = "성북구"
    case gangbukgu = "강북구"
    case dobonggu = "도봉구"
    case nowongu = "노원구"
    case jungnanggu = "중랑구"
    case dongdaemungu = "동대문구"
    case seongdonggu = "성동구"
    case gwangjingu = "광진구"
    case songpagu = "송파구"
    case gangdonggu = "강동구"

}
