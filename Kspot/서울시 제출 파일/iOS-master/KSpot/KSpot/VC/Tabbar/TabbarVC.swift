//
//  TabbarVC.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 27..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit

class TabbarVC: UITabBarController {
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setLanguageNoti(selector: #selector(getLangInfo(_:)))
    }

    // MARK: - 언어 설정
    @objc func getLangInfo(_ notification : Notification) {
        if selectedLang == .kor {
            tabBar.items?[0].title = "홈"
            tabBar.items?[1].title = "카테고리"
            tabBar.items?[2].title = "장소"
            tabBar.items?[3].title = "마이페이지"
        } else {
            tabBar.items?[0].title = "HOME"
            tabBar.items?[1].title = "CATEGORY"
            tabBar.items?[2].title = "SPOT"
            tabBar.items?[3].title = "MYPAGE"
        }
    }
}
