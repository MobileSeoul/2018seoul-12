//
//  CategoryDetailVC.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 3..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit

private let TOPVIEW_HEIGHT:CGFloat = 269
private let NAVBAR_COLORCHANGE_POINT:CGFloat = TOPVIEW_HEIGHT - CGFloat(kNavBarBottom * 2)

class CategoryDetailVC: UIViewController, UIGestureRecognizerDelegate{

    @IBOutlet weak var tableView: UITableView!
    var selectedIdx = 0
    var initailSubCount = 0
    var recommendPlace : [ChannelDetailVODataPlaceRecommendedByChannel]?
    var relatedPlace : [ChannelDetailVODataRelatedChannel]?
    var relatedEvent : [ChannelDetailVODataRelatedChannel]?
   
    lazy var backgroundImg :UIImageView = {
        let imgView = UIImageView(image: UIImage(named: "cimg"))
        
        return imgView
    }()
    lazy var logoImg :UIImageView = {
        let imgView = UIImageView(image: UIImage(named: "aimg"))
        
        return imgView
    }()
    
    lazy var mainTitleLbl:UILabel = {
        let label = UILabel()
        label.backgroundColor = UIColor.clear
        label.textColor = .black
        label.text = ""
        label.textAlignment = .left
        label.font = UIFont(name: NanumSquareOTF.NanumSquareOTFB.rawValue, size: 20)
        return label
    }()
    lazy var subTitleLbl:UILabel = {
        let label = UILabel()
        label.backgroundColor = UIColor.clear
        label.textColor = #colorLiteral(red: 0.7529411765, green: 0.7529411765, blue: 0.7529411765, alpha: 1)
        label.text = ""
        label.textAlignment = .left
        label.font = UIFont(name: NanumSquareOTF.NanumSquareOTFR.rawValue, size: 14)
        return label
    }()
    
    lazy var subscribeLbl:UILabel = {
        let label = UILabel()
        label.backgroundColor = UIColor.clear
        label.textColor = #colorLiteral(red: 0.7529411765, green: 0.7529411765, blue: 0.7529411765, alpha: 1)
        label.text = "1,445,054"
        label.textAlignment = .left
        label.font = UIFont(name: NanumSquareOTF.NanumSquareOTFR.rawValue, size: 14)
        return label
    }()
    
    lazy var subscribeBtn:mySubscribeBtn = {
        let button = mySubscribeBtn()
        button.addTarget(self, action: #selector(CategoryDetailVC.subscribeAction(_:)), for: .touchUpInside)
        return button
    }()
    
    
    lazy var bottomGrayView: UIView = {
        let view = UIView()
        view.backgroundColor = #colorLiteral(red: 0.4392156863, green: 0.4392156863, blue: 0.4392156863, alpha: 1)
        return view
    }()
    
    lazy var topView: UIView = {
        let view = UIView()
        view.frame = CGRect(x: 0, y: 0, width: kScreenWidth, height: TOPVIEW_HEIGHT)
        view.contentMode = UIViewContentMode.scaleAspectFill
        view.clipsToBounds = true
        return view
    }()
    
    override func viewDidLoad() {
        setupTableView()
        setupNavView()
        getChannelDetail(url : UrlPath.channelDetail.getURL(selectedIdx.description))
        setLanguageNoti(selector: #selector(getLangInfo(_:)))
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        self.clearAllNotice()
    }
    
    @IBAction func scrollToTopAction(_ sender: Any) {
        tableView.setContentOffset(.zero, animated: true)
    }
    
    @objc func getLangInfo(_ notification : Notification) {
        getChannelDetail(url : UrlPath.channelDetail.getURL(selectedIdx.description))
    }
    
    //구독
    @objc func subscribeAction(_ sender : mySubscribeBtn){
        tap(section: .first, seledtedId: sender.contentIdx!, sender: sender)
    }
    
    func setupTableView(){
        tableView.contentInset = UIEdgeInsetsMake(-CGFloat(kNavBarBottom), 0, 0, 0)
        tableView.delegate = self
        tableView.dataSource = self
        makeTopViewConstraint()
        tableView.tableHeaderView = topView
    }
    
}

//MARK: - 네비게이션 및 탑 뷰 설정
extension CategoryDetailVC {
    //네비게이션 설정
    func setupNavView(){
        setBackBtn(color: .white)
        self.navigationItem.title = ""
        //네비게이션바 컬러
        navBarBarTintColor = .white
        navBarBackgroundAlpha = 0
        //네비게이션 바 안의 아이템 컬러
        navBarTintColor = .white
    }
    //탑뷰 설정
    func makeTopViewConstraint(){
        topView.addSubview(backgroundImg)
        topView.addSubview(logoImg)
        topView.addSubview(mainTitleLbl)
        topView.addSubview(subTitleLbl)
        topView.addSubview(subscribeLbl)
        topView.addSubview(subscribeBtn)
        topView.addSubview(bottomGrayView)
        backgroundImg.snp.makeConstraints { (make) in
            make.leading.trailing.top.equalToSuperview()
            make.height.equalTo(165)
            
        }
        backgroundImg.contentMode = .scaleToFill
        logoImg.snp.makeConstraints { (make) in
            make.height.width.equalTo(74)
            make.leading.equalToSuperview().offset(16)
            make.centerY.equalTo(backgroundImg.snp.bottom) //centerVertically
        }
        logoImg.makeRounded(cornerRadius: 37)
        logoImg.makeViewBorder(width: 0.5, color: #colorLiteral(red: 0.7529411765, green: 0.7529411765, blue: 0.7529411765, alpha: 1))
        logoImg.backgroundColor = .white
        mainTitleLbl.snp.makeConstraints { (make) in
            make.leading.equalTo(logoImg.snp.trailing).offset(8)
            make.top.equalTo(backgroundImg.snp.bottom).offset(16)
            make.width.equalTo(180)
            mainTitleLbl.adjustsFontSizeToFitWidth = true
        }
        subTitleLbl.snp.makeConstraints { (make) in
            make.leading.equalTo(mainTitleLbl.snp.leading)
            make.width.equalTo(200)
            subTitleLbl.adjustsFontSizeToFitWidth = true
            make.top.equalTo(mainTitleLbl.snp.bottom).offset(9)
        }
        subscribeBtn.snp.makeConstraints { (make) in
            make.height.equalTo(24)
            make.width.equalTo(57)
            make.centerY.equalTo(mainTitleLbl.snp.centerY)
            make.trailing.equalToSuperview().offset(-16)
            
        }
        subscribeLbl.snp.makeConstraints { (make) in
            make.trailing.equalToSuperview().offset(-16)
            make.width.equalTo(50)
            subscribeLbl.textAlignment = .right
            subscribeLbl.adjustsFontSizeToFitWidth = true
            make.top.equalTo(subscribeBtn.snp.bottom).offset(9)
        }
        bottomGrayView.snp.makeConstraints { (make) in
            make.leading.trailing.bottom.equalToSuperview()
            make.height.equalTo(0.5)
        }
    } //setConstraint
}


// MARK: - 스크롤 할 때
extension CategoryDetailVC {
    func scrollViewDidScroll(_ scrollView: UIScrollView){
        let offsetY = scrollView.contentOffset.y
        if (offsetY > NAVBAR_COLORCHANGE_POINT){
            navBarTintColor = #colorLiteral(red: 0.4392156863, green: 0.4392156863, blue: 0.4392156863, alpha: 1)
            navigationItem.leftBarButtonItem?.tintColor = #colorLiteral(red: 0.4392156863, green: 0.4392156863, blue: 0.4392156863, alpha: 1)
            
            let alpha = (offsetY - NAVBAR_COLORCHANGE_POINT) / CGFloat(kNavBarBottom)
            navBarBackgroundAlpha = alpha
            navBarTintColor = UIColor.black.withAlphaComponent(alpha)
            navBarTitleColor = UIColor.black.withAlphaComponent(alpha)
            statusBarStyle = .default
        } else{
            navBarTintColor = .white
            navigationItem.leftBarButtonItem?.tintColor = .white
            navBarBackgroundAlpha = 0
            navBarTitleColor = .white
            statusBarStyle = .lightContent
        }
    }
}

//MARK: - UITableViewDelegate, UITableViewDataSource
extension CategoryDetailVC : UITableViewDelegate, UITableViewDataSource  {
    
    func numberOfSections(in tableView: UITableView) -> Int {
        if let relatedEvent_ = relatedEvent {
            if relatedEvent_.count == 0 {
                return 2
            } else {
                return 3
            }
        }
        return 0
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if section == 0 {
            return 1
        } else if section == 1{
            if let relatedPlace_ = relatedPlace {
                return relatedPlace_.count
            }
        } else {
            if let relatedEvent_ = relatedEvent {
                return relatedEvent_.count
            }
        }
        return 0
    }
    
    //headerSection View 만드는 것
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let header = tableView.dequeueReusableCell(withIdentifier: CategoryDetailSecondTVHeaderCell.reuseIdentifier) as! CategoryDetailSecondTVHeaderCell
        
        if section == 1 {
            if selectedLang == .kor {
                header.titleLbl.text = "관련된 장소"
            } else {
                header.titleLbl.text = "related place"
                header.morBtn.setTitle("MORE", for: .normal)
            }
            header.morBtn.addTarget(self, action: #selector(placeMoreAction(_:)), for: .touchUpInside)
            
            return header
        } else if section == 2{
            if selectedLang == .kor {
                header.titleLbl.text = "관련된 이벤트"
            } else {
                header.titleLbl.text = "related event"
                header.morBtn.setTitle("MORE", for: .normal)
            }
            header.morBtn.addTarget(self, action: #selector(eventMoreAction(_:)), for: .touchUpInside)
            return header
        } else {
            return nil
        }
    }
    
    //헤더 높이
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        if section == 1 {
            if let relatedPlace_ = relatedPlace {
                return heightForHeaderInSection(arr : relatedPlace_)
            }
        } else if section == 2 {
            if let relatedEvent_ = relatedEvent {
                return heightForHeaderInSection(arr : relatedEvent_)
            }
        } else {
            return 0
        }
        return 0
    }
    
    private func heightForHeaderInSection(arr : [ChannelDetailVODataRelatedChannel]) -> CGFloat {
        if (arr.count > 0){
            return 62
        }
        return 0
    }

    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if indexPath.section == 0 {
            let cell = tableView.dequeueReusableCell(withIdentifier: CategoryDetailFirstTVCell.reuseIdentifier) as! CategoryDetailFirstTVCell
            cell.delegate = self
            cell.recommendData = self.recommendPlace
            cell.configure(celebrityName: self.mainTitleLbl.text)
            return cell
            
        }  else {
            let cell = tableView.dequeueReusableCell(withIdentifier: CategoryDetailSecondTVCell.reuseIdentifier) as! CategoryDetailSecondTVCell
            if indexPath.section == 1 {
                if let relatedPlace_ = relatedPlace{
                     cell.configure(data: relatedPlace_[indexPath.row])
                }
            } else {
                if let relatedEvent_ = relatedEvent{
                   cell.configure(data: relatedEvent_[indexPath.row])
                }
            }
            return cell
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: false)
        if let relatedPlace_ = relatedPlace, let relatedEvent_ = relatedEvent {
            if indexPath.section == 1  {
                self.goToPlaceDetailVC(selectedIdx: relatedPlace_[indexPath.row].spotID)
            } else if indexPath.section == 2 {
                self.goToPlaceDetailVC(selectedIdx : relatedEvent_[indexPath.row].spotID, isPlace : false)
            }
        }
    }
}

//MARK: - 장소/이벤트 더보기에 대한 액션
extension CategoryDetailVC {
    @objc func placeMoreAction(_ sender : UIButton){
        let categoryStoryboard = Storyboard.shared().categoryStoryboard
        if let categoryDetailMorePlaceVC = categoryStoryboard.instantiateViewController(withIdentifier:CategoryDetailMorePlaceVC.reuseIdentifier) as? CategoryDetailMorePlaceVC {
            if selectedLang == .kor {
                categoryDetailMorePlaceVC.title = "장소"
            } else {
                categoryDetailMorePlaceVC.title = "PLACE"
            }
            
            categoryDetailMorePlaceVC.isPlace = true
            categoryDetailMorePlaceVC.selectedIdx = selectedIdx
            categoryDetailMorePlaceVC.mainTitle = mainTitleLbl.text
            self.navigationController?.pushViewController(categoryDetailMorePlaceVC, animated: true)
        }
    }
    
    @objc func eventMoreAction(_ sender : UIButton){
        let categoryStoryboard = Storyboard.shared().categoryStoryboard
        if let categoryDetailMoreEventVC = categoryStoryboard.instantiateViewController(withIdentifier:CategoryDetailMorePlaceVC.reuseIdentifier) as? CategoryDetailMorePlaceVC {
            if selectedLang == .kor {
                categoryDetailMoreEventVC.title = "이벤트"
            } else {
                categoryDetailMoreEventVC.title = "EVENT"
            }
            categoryDetailMoreEventVC.isPlace = false
            categoryDetailMoreEventVC.selectedIdx = selectedIdx
            categoryDetailMoreEventVC.mainTitle = mainTitleLbl.text
            self.navigationController?.pushViewController(categoryDetailMoreEventVC, animated: true)
        }
    }
}

//MARK: - 장소 상세보기, 구독
extension CategoryDetailVC : SelectSectionDelegate, SelectSenderDelegate {
    //장소 상세보기
    func tap(section: Section, seledtedId: Int) {
        if section == .first {
           self.goToPlaceDetailVC(selectedIdx: seledtedId)
        }
    }
    
    //구독
    func tap(section: Section, seledtedId: Int, sender: mySubscribeBtn) {
        if !isUserLogin() {
            goToLoginPage()
        } else {
            let params = ["channel_id" : sender.contentIdx]
            if sender.isSelected {
                unsubscribe(url: UrlPath.channelSubscription.getURL(sender.contentIdx?.description), sender: sender)
            } else {
                subscribe(url: UrlPath.channelSubscription.getURL(), params: params, sender: sender)
            }
        }
    }
}

//통신
extension CategoryDetailVC {
    func getChannelDetail(url : String){
         self.pleaseWait()
        ChannelDetailService.shareInstance.getChannelDetail(url: url,completion: { [weak self] (result) in
            guard let `self` = self else { return }
             self.clearAllNotice()
            switch result {
            case .networkSuccess(let channelDetailData):
                let detailData = channelDetailData as! ChannelDetailVOData
                let info = detailData.channelInfo
                self.mainTitleLbl.text = info.name
                self.subTitleLbl.text = info.company
                self.initailSubCount = info.subscriptionCnt
                self.subscribeLbl.text = info.subscriptionCnt.description
                self.setImgWithKF(url: info.backgroundImg, imgView: self.backgroundImg, defaultImg: #imageLiteral(resourceName: "aimg"))
                self.setImgWithKF(url: info.thumbnailImg, imgView: self.logoImg, defaultImg: #imageLiteral(resourceName: "aimg"))
                self.subscribeBtn.setSubscribeBtn(idx: info.id, isSubscribe: info.subscription)

                self.recommendPlace = detailData.placeRecommendedByChannel
                self.relatedPlace = detailData.placeRelatedChannel
                self.relatedEvent = detailData.eventRelatedChannel
                self.tableView.reloadData()
            case .networkFail :
                self.networkSimpleAlert()
            default :
                self.simpleAlert(title: "오류", message: "다시 시도해주세요")
                break
            }
        })
    }
    
    
    
    func subscribe(url : String, params : [String:Any], sender : mySubscribeBtn){
        self.pleaseWait()
        ChannelSubscribeService.shareInstance.subscribe(url: url, params : params, completion: { [weak self] (result) in
            guard let `self` = self else { return }
            self.clearAllNotice()
            switch result {
            case .networkSuccess(_):
                sender.isSelected = true
                sender.isSubscribe = 1
                var changed : Int = 0
                //Now change the text and background colour
                if self.subscribeLbl.text == self.initailSubCount.description {
                    changed = self.initailSubCount+1
                } else {
                    changed = self.initailSubCount
                }
                self.subscribeLbl.text = "\(changed)"
            case .networkFail :
               self.networkSimpleAlert()
            default :
                self.simpleAlert(title: "오류", message: "다시 시도해주세요")
                break
            }
        })
    } //subscribe
    
    func unsubscribe(url : String, sender : mySubscribeBtn){
        self.pleaseWait()
        ChannelSubscribeService.shareInstance.unsubscribe(url: url, completion: { [weak self] (result) in
            guard let `self` = self else { return }
            self.clearAllNotice()
            switch result {
            case .networkSuccess(_):
                sender.isSelected = false
                sender.isSubscribe = 0
                var changed : Int = 0
                
                //Now change the text and background colour
                if self.subscribeLbl.text == self.initailSubCount.description {
                    changed = self.initailSubCount-1
                } else {
                    changed = self.initailSubCount
                }
                self.subscribeLbl.text = "\(changed)"
            case .networkFail :
               self.networkSimpleAlert()
            default :
                self.simpleAlert(title: "오류", message: "다시 시도해주세요")
                break
            }
        })
    }
}


