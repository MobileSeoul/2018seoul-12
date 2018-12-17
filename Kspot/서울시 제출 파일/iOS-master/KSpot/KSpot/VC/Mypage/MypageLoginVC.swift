//
//  MypageLoginVC.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 24..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit

class MypageLoginVC: UIViewController {
    
    @IBOutlet weak var boldLbl: UILabel!
    @IBOutlet weak var regularLbl: UILabel!
    @IBOutlet weak var loginBtn: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setLanguageNoti(selector: #selector(getLangInfo(_:)))
    }
    
    @IBAction func loginAction(_ sender: Any) {
        goToLoginPage()
    }
    
    @objc func getLangInfo(_ notification : Notification) {
        boldLbl.text = selectedLang == .kor ? "로그인이 필요한 서비스입니다 :)" : "This is a service that requires login :)"
         regularLbl.text = selectedLang == .kor ? "구독채널과 스크랩을 하려면 로그인해주세요" : "Please login to subscribe and scrap"
        if selectedLang == .kor {
            loginBtn.setImage(#imageLiteral(resourceName: "mypage_login_button"), for: .normal)
        } else {
            loginBtn.setImage(#imageLiteral(resourceName: "mypage_login_button_eng"), for: .normal)
        }
    }


}
