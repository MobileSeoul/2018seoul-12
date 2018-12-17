//
//  CategoryVC.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 2..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit

class CategoryVC: UIViewController {
    
    @IBOutlet weak var containerView: UIView!
    @IBOutlet weak var celebrityBtn: CategoryToggleBtn!
    @IBOutlet weak var celebrityGreenView: UIView!
    @IBOutlet weak var broadcastBtn: CategoryToggleBtn!
    @IBOutlet weak var broadcastGreenView: UIView!
    var currentSelectedLang = selectedLang
    var celebrityList : [ChannelVODataChannelList]? {
        didSet {
            celebrityVC.celebrityList = celebrityList
            celebrityVC.isChange = true
        }
    }
    var broadcastList : [ChannelVODataChannelList]? {
        didSet {
            broadcastVC.broadcastList = broadcastList
            broadcastVC.isChange = true
        }
    }
    

    
    private lazy var celebrityVC: CelebrityVC = {
        let storyboard = Storyboard.shared().categoryStoryboard
        var viewController = storyboard.instantiateViewController(withIdentifier: CelebrityVC.reuseIdentifier) as! CelebrityVC
        viewController.delegate = self
        
        return viewController
    }()
    
    private lazy var broadcastVC: BroadcastVC = {
        let storyboard = Storyboard.shared().categoryStoryboard
        var viewController = storyboard.instantiateViewController(withIdentifier: BroadcastVC.reuseIdentifier) as! BroadcastVC
        viewController.delegate = self
        return viewController
    }()
    
    
    override func viewWillAppear(_ animated: Bool) {
        self.getMyChannel(url: UrlPath.channelList.getURL())
    }
    

    override func viewDidLoad() {
        super.viewDidLoad()
        setLanguageNoti(selector: #selector(getLangInfo(_:)))
        celebrityBtn.setBtn(another: broadcastBtn, bottomLine: celebrityGreenView)
        broadcastBtn.setBtn(another: celebrityBtn, bottomLine: broadcastGreenView)
        self.getMyChannel(url: UrlPath.channelList.getURL())
        updateView(selected: 0)
        setTranslationBtn()
        setNavTitleImg()
    }
    
    
    @IBAction func searchAction(_ sender: Any) {
        self.goToSearchVC()
    }
    
    //0 - 연예인, 1 - 방송
    @IBAction func switchView(_ sender: CategoryToggleBtn) {
        updateView(selected: sender.tag)
    }

    @objc func getLangInfo(_ notification : Notification) {
        if selectedLang == .kor {
            celebrityBtn.setTitle("연예인", for: .normal)
            broadcastBtn.setTitle("방송", for: .normal)
            
        } else {
            celebrityBtn.setTitle("Celebrity", for: .normal)
            broadcastBtn.setTitle("Broadcast", for: .normal)
        }
        self.getMyChannel(url: UrlPath.channelList.getURL())
    }
    
    //네비게이션 타이틀 이미지 설정
    func setNavTitleImg(){
        //setupTitleNavImg
        let titleImageView = UIImageView(image: #imageLiteral(resourceName: "main_logo"))
        titleImageView.contentMode = .scaleAspectFit
        titleImageView.snp.makeConstraints { (make) in
            make.height.equalTo(19)
            make.width.equalTo(71)
        }
        navigationItem.titleView = titleImageView
    }
}

//MARK: - 업데이트 컨테이너 뷰
extension CategoryVC{
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

//MARK: - Celebrity Detail로 이동
extension CategoryVC : SelectDelegate {
    func tap(selected: Int?) {
        goToCelebrityDetail(selectedIdx: selected!)
    }
}

//MARK: -통신
extension CategoryVC  {
    
    func getMyChannel(url : String){
        //self.pleaseWait()
        ChannelService.shareInstance.getChannelList(url: url,completion: { [weak self] (result) in
            guard let `self` = self else { return }
            //self.clearAllNotice()
            switch result {
            case .networkSuccess(let channelList):
                let channelList = channelList as! ChannelVOData
                self.broadcastList = channelList.channelBroadcastList
                self.celebrityList = channelList.channelCelebrityList
            case .networkFail :
                self.networkSimpleAlert()
            default :
                self.simpleAlert(title: "오류", message: "다시 시도해주세요")
                break
            }
        })
    }
}
