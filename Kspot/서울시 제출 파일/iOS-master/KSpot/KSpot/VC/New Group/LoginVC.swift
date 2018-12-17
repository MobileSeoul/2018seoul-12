//
//  LoginVC.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 17..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit
import FBSDKCoreKit
import FBSDKLoginKit
import FacebookCore

//MARK: - 임시로그인을 위한 유저아이디
let tempUserId = "2163555827048248"

class LoginVC: UIViewController {

    @IBOutlet weak var regularLbl: UILabel!
    @IBOutlet weak var boldLbl: UILabel!
    @IBOutlet weak var kakaoBtn: UIButton!
    @IBOutlet weak var facebookBtn: UIButton!
    
    @IBOutlet weak var tempBtn: UIButton!
    @IBOutlet weak var skipBtn: UIButton!
    @IBOutlet weak var xBtn: UIButton!
    // 0이면 중간에 들어온 것, 1이면 처음으로 들어온 것
    var entryPoint = 0
    
    override func viewDidLoad() {
        super.viewDidLoad()
        //언어 설정
        if selectedLang == .kor {
            regularLbl.text = "안녕하세요!"
            boldLbl.text = "로그인을 해주세요 :)"
            skipBtn.setImage(#imageLiteral(resourceName: "login_skip"), for: .normal)
            tempBtn.setImage(#imageLiteral(resourceName: "login_button_temporary"), for: .normal)
            facebookBtn.setImage(#imageLiteral(resourceName: "login__facebook_button"), for: .normal)
            kakaoBtn.setImage(#imageLiteral(resourceName: "login_kakao_button"), for: .normal)
        } else {
            regularLbl.text = "Hi"
            boldLbl.text = "Please Login :)"
            skipBtn.setImage(#imageLiteral(resourceName: "login_skip_eng"), for: .normal)
            tempBtn.setImage(#imageLiteral(resourceName: "login_button_temporary_eng"), for: .normal)
            facebookBtn.setImage(#imageLiteral(resourceName: "login__facebook_eng"), for: .normal)
            kakaoBtn.setImage(#imageLiteral(resourceName: "login_kakao_eng"), for: .normal)
        }
      
        skipBtn.addTarget(self, action: #selector(self.dismiss(_:)), for: .touchUpInside)
        xBtn.addTarget(self, action: #selector(self.dismiss(_:)), for: .touchUpInside)
        
        //처음으로 들어온 것
        if entryPoint == 1 {
            skipBtn.isHidden = false
            xBtn.isHidden = true
        } else {
            skipBtn.isHidden = true
            xBtn.isHidden = false
        }
    } //viewDidLoad
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        self.clearAllNotice()
    }
    
    @objc func dismiss(_ sender : UIButton){
        self.dismiss(animated: true, completion: nil)
    }

    // MARK: - 페이스북 로그인
    @IBAction func facebookLoginAction(_ sender: UIButton) {
        simpleAlert(title: "알림", message: "안드로이드와 동일한 테스트 환경을 위해 페이스북 로그인은 현재 잠시 중단되어있습니다")
       /* //카카오톡 세션 열려있으면 닫기
        let session: KOSession = KOSession.shared();
        
        if session.isOpen() {
            session.close()
        }
        
        // Facebook SDK 사용
        let fbLoginManager = FBSDKLoginManager()
        fbLoginManager.loginBehavior = .native // .web, .brower, .systemAccount

        fbLoginManager.logIn(withReadPermissions: ["public_profile", "email"], from: self) { (result, error) in
            if (error == nil){
                let fbloginresult : FBSDKLoginManagerLoginResult = result!
                if fbloginresult.isCancelled {
                    //로그인 취소
                    print("취소됨")
                } else {
                    //로그인 성공
                    let currentToken = AccessToken.current?.authenticationToken ?? ""
                    let param = ["access_token" : currentToken]
                    self.facebookLogin(url: UrlPath.facebookLogin.getURL(), params: param)
                }
            }
        }*/
    } //fbLogin
    
    // MARK: - 카카오톡 로그인
    @IBAction func loginWithKakao(_ sender: Any) {
        simpleAlert(title: "알림", message: "안드로이드와 동일한 테스트 환경을 위해 카카오톡 로그인은 현재 잠시 중단되어있습니다")
        /*//페이스북 열려있으면 닫기
        if FBSDKAccessToken.current() != nil{
            let fbLoginManager = FBSDKLoginManager()
            fbLoginManager.logOut()
        }
        
        //이전 카카오톡 세션 열려있으면 닫기
        let session: KOSession = KOSession.shared();
        if session.isOpen() {
            session.close()
        }
        
        session.open(completionHandler: { (error) -> Void in
            if error == nil{
                if session.isOpen(){
                    //로그인 성공
                    let params : [String : Any] = ["access_token" : session.token.accessToken]
                    self.kakaoLogin(url: UrlPath.kakaoLogin.getURL(), params: params)
                } else {
                    print("Login failed")
                }
            }else{
                print("Login error : \(String(describing: error))")
            }
            if !session.isOpen() {
                if let error = error as NSError? {
                    switch error.code {
                    case Int(KOErrorCancelled.rawValue):
                        break
                    default:
                        //간편 로그인 취소
                        print("error : \(error.description)")
                        
                    }
                }
            }
        })*/
    } //kakao login
    
    //temp login
    @IBAction func loginWithTemp(_ sender: Any) {
        let params : [String : Any] = ["user_id" : tempUserId]
        self.tempLogin(url: UrlPath.tempLogin.getURL(), params: params)
    }
}

// MARK: - 통신
extension LoginVC {
    
    //임시 로그인
    func tempLogin(url : String, params : [String:Any]){
        self.pleaseWait()
        FacebookLoginService.shareInstance.login(url: url, params : params, completion: { [weak self] (result) in
            guard let `self` = self else { return }
            self.clearAllNotice()
            switch result {
            case .networkSuccess(let loginData):
                //유저 값 설정
                let userData = loginData as? FacebookLoginVOData
                UserDefaults.standard.set(userData?.authorization, forKey : "userAuth")
                self.dismiss(animated: false, completion: nil)
            case .networkFail :
                self.networkSimpleAlert()
            default :
                self.simpleAlert(title: "오류", message: "다시 시도해주세요")
                break
            }
        })
    } //tempLogin

    //페이스북
    func facebookLogin(url : String, params : [String:Any]){
        self.pleaseWait()
        FacebookLoginService.shareInstance.login(url: url, params : params, completion: { [weak self] (result) in
            guard let `self` = self else { return }
            self.clearAllNotice()
            switch result {
            case .networkSuccess(let loginData):
                //유저 값 설정
                let userData = loginData as? FacebookLoginVOData
                UserDefaults.standard.set(userData?.authorization, forKey : "userAuth")
                loginWith = .facebook
                self.dismiss(animated: false, completion: nil)
            case .networkFail :
               self.networkSimpleAlert()
            default :
                self.simpleAlert(title: "오류", message: "다시 시도해주세요")
                break
            }
        })
    } //fb login
    
    //카카오톡
    func kakaoLogin(url : String, params : [String:Any]){
        self.pleaseWait()
        FacebookLoginService.shareInstance.login(url: url, params : params, completion: { [weak self] (result) in
            guard let `self` = self else { return }
            self.clearAllNotice()
            switch result {
            case .networkSuccess(let loginData):
                //유저 값 설정
                let userData = loginData as? FacebookLoginVOData
                UserDefaults.standard.set(userData?.authorization, forKey : "userAuth")
                self.dismiss(animated: false, completion: nil)
                loginWith = .kakao
            case .networkFail :
                self.networkSimpleAlert()
            default :
                self.simpleAlert(title: "오류", message: "다시 시도해주세요")
                break
            }
        })
    } //ko login
}
