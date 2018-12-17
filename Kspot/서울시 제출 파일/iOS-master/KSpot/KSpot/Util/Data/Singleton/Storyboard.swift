//
//  Storyboard.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 2..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit

class Storyboard {
    
    let mainStoryboard = UIStoryboard(name: "Main", bundle: nil)
    let categoryStoryboard = UIStoryboard(name: "Category", bundle: nil)
    let mapStoryboard = UIStoryboard(name: "Map", bundle: nil)
    let mypageStoryboard = UIStoryboard(name: "Mypage", bundle: nil)
  
    struct StaticInstance {
        static var instance: Storyboard?
    }
    
    class func shared() -> Storyboard {
        if StaticInstance.instance == nil {
            StaticInstance.instance = Storyboard()
        }
        return StaticInstance.instance!
    }
}
