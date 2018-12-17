//
//  SubscribeVC.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 10..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit

class SubscribeVC: UIViewController, UIGestureRecognizerDelegate {

    @IBOutlet weak var containerView: UIView!
    @IBOutlet weak var celebrityBtn: CategoryToggleBtn!
    @IBOutlet weak var celebrityGreenView: UIView!
    @IBOutlet weak var broadcastBtn: CategoryToggleBtn!
    @IBOutlet weak var broadcastGreenView: UIView!
    
    var celebritySubscriptionList : [UserSubcriptionVOBroadcast]? {
        didSet {
            celebrityVC.celebritySubscriptionList = celebritySubscriptionList
        }
    }
    var broadcastSubscriptionList : [UserSubcriptionVOBroadcast]? {
        didSet {
            broadcastVC.broadcastSubscriptionList = broadcastSubscriptionList
        }
    }
    
    private lazy var celebrityVC: SubCelebrityVC = {
        let storyboard = Storyboard.shared().mypageStoryboard
        var viewController = storyboard.instantiateViewController(withIdentifier: SubCelebrityVC.reuseIdentifier) as! SubCelebrityVC
        return viewController
    }()
    
    private lazy var broadcastVC: SubBroadCastVC = {
        let storyboard = Storyboard.shared().mypageStoryboard
        var viewController = storyboard.instantiateViewController(withIdentifier: SubBroadCastVC.reuseIdentifier) as! SubBroadCastVC
        return viewController
    }()


    override func viewDidLoad() {
        super.viewDidLoad()
        celebrityBtn.setBtn(another: broadcastBtn, bottomLine: celebrityGreenView)
        broadcastBtn.setBtn(another: celebrityBtn, bottomLine: broadcastGreenView)
        getUserSubcription(url: UrlPath.userSubscription.getURL())
        updateView(selected: 0)
        setBackBtn()
        setLanguage()
         self.navigationItem.title = selectedLang == .kor ? "구독" : "Subscribe"
        setLanguageNoti(selector: #selector(getLangInfo(_:)))
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        self.clearAllNotice()
    }
    
    @IBAction func switchView(_ sender: CategoryToggleBtn) {
        updateView(selected: sender.tag)
    }
    
    @objc func getLangInfo(_ notification : Notification) {
        self.navigationItem.title = selectedLang == .kor ? "구독" : "Subscribe"
        getUserSubcription(url: UrlPath.userSubscription.getURL())
        setLanguage()
    }
    func setLanguage(){
        if selectedLang == .kor {
            celebrityBtn.setTitle("연예인", for: .normal)
            broadcastBtn.setTitle("방송", for: .normal)
        } else {
            celebrityBtn.setTitle("Celebrity", for: .normal)
            broadcastBtn.setTitle("Broadcast", for: .normal)
        }
    }
}


//MARK: - 컨테이너뷰 업데이트
extension SubscribeVC{
    private func updateView(selected : Int) {
        if selected == 0 {
            
            removeChildView(containerView: containerView, asChildViewController: broadcastVC)
            
            addChildView(containerView: containerView, asChildViewController: celebrityVC)
        } else {
            removeChildView(containerView: containerView, asChildViewController: celebrityVC)
            
            addChildView(containerView: containerView, asChildViewController: broadcastVC)
        }
    }
    
}

//MARK: - 통신
extension SubscribeVC{
    func getUserSubcription(url : String){
        self.pleaseWait()
        UserSubcriptionService.shareInstance.getSubscriptionList(url: url,completion: { [weak self] (result) in
            guard let `self` = self else { return }
             self.clearAllNotice()
            switch result {
            case .networkSuccess(let subsData):
                let subsData = subsData as! UserSubcriptionVOData
                self.broadcastSubscriptionList = subsData.broadcast
                self.celebritySubscriptionList = subsData.celebrity
               
            case .networkFail :
               self.networkSimpleAlert()
            default :
                self.simpleAlert(title: "오류", message: "다시 시도해주세요")
                break
            }
        })
    }
    
}


