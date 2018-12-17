//
//  MypageVC.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 9..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit
import FBSDKLoginKit
import Kingfisher

class MypageVC: UIViewController {
    
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var welcomLbl: UILabel!
    @IBOutlet weak var logoutBtn: UIButton!
    @IBOutlet weak var nameLbl: UILabel!
    @IBOutlet weak var profileImgView: UIImageView!
    
    var numberofRows = 0
    var userName = "" {
        didSet {
            nameLbl.text = selectedLang == .kor ? userName+", 고객님" : userName
        }
    }
    var channelArr : [MypageVODataChannel]? {
        didSet {
            tableView.reloadData()
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.getMyInfo(url: UrlPath.mypage.getURL())
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setLanguageNoti(selector: #selector(getLangInfo(_:)))
        tableView.delegate = self
        tableView.dataSource = self
        profileImgView.makeRounded(cornerRadius: profileImgView.frame.height/2)
        profileImgView.makeViewBorder(width: 0.5, color: #colorLiteral(red: 0.7529411765, green: 0.7529411765, blue: 0.7529411765, alpha: 1))
        
    }
    
    //로그아웃
    @IBAction func logoutAction(_ sender: Any) {
        let logoutTitle = selectedLang == .kor ? "로그아웃 하시겠습니까?" : "Do you want to logout?"
        self.simpleAlertwithHandler(title: logoutTitle, message: "") { (_) in
            UserDefaults.standard.set(nil, forKey: "userAuth")
            let parentVC = self.parent as? MyPageContainerVC
            parentVC?.viewWillAppear(false)
        }
        /*if loginWith == .facebook {
            self.simpleAlertwithHandler(title: logoutTitle, message: "") { (_) in
                //faceBookLogout
                let fbLoginManager = FBSDKLoginManager()
                fbLoginManager.logOut()
                loginWith = nil
                UserDefaults.standard.set(nil, forKey: "userAuth")
                let parentVC = self.parent as? MyPageContainerVC
                parentVC?.viewWillAppear(false)
            }
        } else {
            self.simpleAlertwithHandler(title: logoutTitle, message: "") { (_) in
                //kakaoLogout
                KOSession.shared().logoutAndClose(completionHandler: { (success, err) in
                    UserDefaults.standard.set(nil, forKey: "userAuth")
                    loginWith = nil
                    let parentVC = self.parent as? MyPageContainerVC
                    parentVC?.viewWillAppear(false)
                })
            }
        }*/
    } //logout
    
    @objc func getLangInfo(_ notification : Notification) {
        
        if selectedLang == .kor {
             self.navigationItem.title = "마이페이지"
            nameLbl.text = userName+" 고객님,"
            welcomLbl.text = "안녕하세요!"
            logoutBtn.setImage(#imageLiteral(resourceName: "mypage_logout"), for: .normal)
            
        } else {
            self.navigationItem.title = "MY PAGE"
            nameLbl.text = userName+","
            welcomLbl.text = "Welcome!"
            logoutBtn.setImage(#imageLiteral(resourceName: "mypage_logout_eng"), for: .normal)
        }
        tableView.reloadData()
    }
    
}

//MARK: - tableView delegate, datasource
extension MypageVC : UITableViewDelegate, UITableViewDataSource{
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if channelArr?.count != 0 {
            numberofRows = 3
        } else {
            numberofRows = 2
        }
        return numberofRows
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if numberofRows == 2 {
            let cell = tableView.dequeueReusableCell(withIdentifier: MypageTVCell.reuseIdentifier) as! MypageTVCell
            if indexPath.row == 0 {
                cell.myLbl.text = selectedLang == .kor ? "스크랩" : "Scrap"
            } else {
                cell.myLbl.text = selectedLang == .kor ? "회원정보 수정" : "Edit Profile"
            }
            return cell
        } else {
            
            if indexPath.row == 0 {
                let cell = tableView.dequeueReusableCell(withIdentifier: MypageFisrtTVCell.reuseIdentifier) as! MypageFisrtTVCell
                cell.channelArr = self.channelArr
                cell.delegate = self
                cell.mySubsLbl.text = selectedLang == .kor ? "내 구독" : "My Subscribe"
                if selectedLang == .kor {
                    cell.moreBtn.setTitle("더보기", for: .normal)
                } else {
                    cell.moreBtn.setTitle("MORE", for: .normal)
                }
                return cell
            } else {
                let cell = tableView.dequeueReusableCell(withIdentifier: MypageTVCell.reuseIdentifier) as! MypageTVCell
                if indexPath.row == 1 {
                    cell.myLbl.text = selectedLang == .kor ? "스크랩" : "Scrap"
                } else {
                    cell.myLbl.text = selectedLang == .kor ? "회원정보 수정" : "Edit Profile"
                }
                return cell
            }
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        let mypageStoryboard = Storyboard.shared().mypageStoryboard
        if (indexPath.row == 0 && numberofRows == 2) || (indexPath.row == 1 && numberofRows == 3){
            
            if let scrapVC = mypageStoryboard.instantiateViewController(withIdentifier:ScrapVC.reuseIdentifier) as? ScrapVC {
                
                self.navigationController?.pushViewController(scrapVC, animated: true)
            }
        } else if (indexPath.row == 1 && numberofRows == 2) || (indexPath.row == 2 && numberofRows == 3) {
            if let editProfileVC = mypageStoryboard.instantiateViewController(withIdentifier:EditProfileVC.reuseIdentifier) as? EditProfileVC {
                editProfileVC.profileImg = self.profileImgView.image
                editProfileVC.nameTxt = userName
                self.navigationController?.pushViewController(editProfileVC, animated: true)
            }
        }
        tableView.deselectRow(at: indexPath, animated: false)
    }
}

//MARK: - 더보기, 연예인 상세 보기
extension MypageVC : SelectSectionDelegate{
    func tap(section: Section, seledtedId: Int) {
        //더보기
        if (seledtedId == -1){
            let mypageStoryboard = Storyboard.shared().mypageStoryboard
            if let subscribeVC = mypageStoryboard.instantiateViewController(withIdentifier:SubscribeVC.reuseIdentifier) as? SubscribeVC {
                
                self.navigationController?.pushViewController(subscribeVC, animated: true)
            }
        } else {
            self.goToCelebrityDetail(selectedIdx : seledtedId)
        }
    }
}



//MARK: - 통신
extension MypageVC {
    func getMyInfo(url : String){
        //self.pleaseWait()
        MypageService.shareInstance.getMypageInfo(url: url,completion: { [weak self] (result) in
            guard let `self` = self else { return }
            // self.clearAllNotice()
            switch result {
            case .networkSuccess(let mypageData):
                let userData = mypageData as! MypageVOData
                self.userName = userData.user.name
                self.setImgWithKF(url: self.gsno(userData.user.profileImg), imgView: self.profileImgView, defaultImg: #imageLiteral(resourceName: "mypage_membership_edit_default_img"))
                self.channelArr = userData.channel
            case .networkFail :
                self.networkSimpleAlert()
            default :
                self.simpleAlert(title: "오류", message: "다시 시도해주세요")
                break
            }
        })
    } //getmyinfo
}



