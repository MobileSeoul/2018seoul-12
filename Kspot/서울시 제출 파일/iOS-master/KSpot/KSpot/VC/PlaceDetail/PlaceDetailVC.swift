//  PlaceDetailVC.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 4..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit

private let NAVBAR_COLORCHANGE_POINT:CGFloat = -80
private let IMAGE_HEIGHT:CGFloat = 232
private let SCROLL_DOWN_LIMIT:CGFloat = 100
private let LIMIT_OFFSET_Y:CGFloat = -(IMAGE_HEIGHT + SCROLL_DOWN_LIMIT)

class PlaceDetailVC: UIViewController, UIGestureRecognizerDelegate {
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var ratingLbl: UILabel!
    @IBOutlet weak var titleLbl: UILabel!
    @IBOutlet weak var descLbl: UILabel!
    @IBOutlet weak var addressLbl: UILabel!
    @IBOutlet weak var addressLbl2: UILabel!
    @IBOutlet weak var staionImgView: UIImageView!
    @IBOutlet weak var lineNumImgView: UIImageView!
    @IBOutlet weak var currentStationLbl: UILabel!
    @IBOutlet weak var prevStationLbl: UILabel!
    @IBOutlet weak var nextsStationLbl: UILabel!
    @IBOutlet weak var writeReviewBtn: UIButton!
    @IBOutlet weak var locationView : UIView!
    @IBOutlet weak var contactView: UIView!
    @IBOutlet weak var contactImgView: UIImageView!
    @IBOutlet weak var contactLbl: UILabel!
    @IBOutlet weak var searchWithGoogleLbl: UILabel!
    @IBOutlet weak var openCloseLbl: UILabel!
    @IBOutlet weak var openLbl: UILabel!
    @IBOutlet weak var closeLbl: UILabel!
    @IBOutlet weak var openTimeLbl: UILabel!
    @IBOutlet weak var closeTimeLbl: UILabel!
    var whiteScrapBarBtn : UIBarButtonItem?
    var whiteFullScrapBarBtn : UIBarButtonItem?
    var blackScrapBarBtn : UIBarButtonItem?
    var greenScrapBarBtn : UIBarButtonItem?
    var selectedIdx = 0
    var scrapCount = 0
    var isPlace = true
    var myPlace : MyPlace?
    var placeData : PlaceDetailVOData?
    var isChange : Bool? {
        didSet {
            tableView.reloadData()
            if let placeData_ = placeData {
                setcycleScrollView(placeData : placeData_)
                setBarButtons(placeData: placeData_)
                setHeaderView(placeData: placeData_)
                setFooterView(placeData: placeData_)
            }
        }
    }

    lazy var cycleScrollView:WRCycleScrollView = {
        
        let frame = CGRect(x: 0, y: -IMAGE_HEIGHT, width: CGFloat(kScreenWidth), height: IMAGE_HEIGHT)
        let cycleView = WRCycleScrollView(frame: frame, type: .LOCAL, imgs: nil, descs: nil)
        return cycleView
    }()
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        getPlaceInfo(url: UrlPath.spot.getURL("\(selectedIdx)/detail"))
    }
    
    override func viewDidLoad()
    {
        super.viewDidLoad()
        setTableView()
        setNavbar()
        currentStationLbl.adjustsFontSizeToFitWidth = true
        titleLbl.adjustsFontSizeToFitWidth = true
        descLbl.adjustsFontSizeToFitWidth = true
        addressLbl.sizeToFit()
        addressLbl2.sizeToFit()
        locationView.makeRounded(cornerRadius: 17)
        contactView.makeRounded(cornerRadius: nil)
        contactView.makeViewBorder(width: 0.5, color: #colorLiteral(red: 0.7529411765, green: 0.7529411765, blue: 0.7529411765, alpha: 1))
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        self.clearAllNotice()
    }
    
    //구글맵으로 가는 함수
    @IBAction func goToMapAction(_ sender: Any) {
        guard let myPlace = myPlace else {return}
        let title = myPlace.name
        let lat = myPlace.lat
        let long = myPlace.long
        let query = "https://www.google.com/maps/search/\(title)/@\(lat),\(long),23z/"
        guard let encodedUrl = query.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) else {
            print("invalid url")
            return
        }
        if let url = URL(string:encodedUrl) {
            UIApplication.shared.open(url)
        } else {
            simpleAlert(title: "오류", message: "구글맵 페이지로 이동할 수 없습니다")
        }
    }
    
    //리뷰 작성
    @IBAction func writeReviewAction(_ sender: Any) {
        if !isUserLogin() {
            goToLoginPage()
        } else {
            //리뷰 쓰기
            let mapStoryboard = Storyboard.shared().mapStoryboard
            if let reviewWriteVC = mapStoryboard.instantiateViewController(withIdentifier:ReviewWriteVC.reuseIdentifier) as? ReviewWriteVC {
                reviewWriteVC.selectedIdx = self.selectedIdx
                self.navigationController?.pushViewController(reviewWriteVC, animated: true)
            }
        }
    }
    
    //전화
    @IBAction func phoneViewAction(_ sender: Any) {
        guard let contact = placeData?.contact else {return}
        if isPlace {
            contact.makeACall()
        }
    }
    
    //맨위로
    @IBAction func scrollToTopAction(_ sender: Any) {
        tableView.setContentOffset(CGPoint(x: 0, y : -IMAGE_HEIGHT), animated: true)
    }
    
    //스크랩
    @objc public func scrapAction(_sender : UIBarButtonItem) {
        if !isUserLogin() {
            goToLoginPage()
        } else {
            if navigationItem.rightBarButtonItems?[0] == whiteScrapBarBtn! || navigationItem.rightBarButtonItems?[0] == blackScrapBarBtn! {
                let param : [String : Any] = ["spot_id":selectedIdx]
                scrap(url: UrlPath.spotSubscription.getURL(), params: param, sender: _sender)
            } else {
                unscrap(url: UrlPath.spotSubscription.getURL(selectedIdx.description), sender: _sender)
            }
        }
    }
}

//MARK: - 네비게이션 바, 테이블뷰, 스크롤뷰 설정
extension PlaceDetailVC {
    
    func setNavbar(){
        //오른쪽 바버튼 아이템 설정
        whiteScrapBarBtn?.tag = 0
        blackScrapBarBtn?.tag = 1
        whiteFullScrapBarBtn?.tag = 2
        greenScrapBarBtn?.tag = 3
        whiteScrapBarBtn = UIBarButtonItem.itemWith(colorfulImage: #imageLiteral(resourceName: "place_detail_unscrap"), target: self, action: #selector(PlaceDetailVC.scrapAction(_sender:)))
        blackScrapBarBtn = UIBarButtonItem.itemWith(colorfulImage: #imageLiteral(resourceName: "place_detail_unscrap_black"), target: self, action: #selector(PlaceDetailVC.scrapAction(_sender:)))
        whiteFullScrapBarBtn = UIBarButtonItem.itemWith(colorfulImage: #imageLiteral(resourceName: "place_detail_scrap"), target: self, action: #selector(PlaceDetailVC.scrapAction(_sender:)))
        greenScrapBarBtn = UIBarButtonItem.itemWith(colorfulImage: #imageLiteral(resourceName: "place_detail_scrap_green"), target: self, action: #selector(PlaceDetailVC.scrapAction(_sender:)))
        
        let titleBarBtn = UIBarButtonItem.titleBarbutton(title: "", red: 255, green: 255, blue: 255, fontSize: 14, fontName: NanumSquareOTF.NanumSquareOTFR.rawValue, selector: nil, target: self)
        titleBarBtn.isEnabled = false
        self.navigationItem.rightBarButtonItems = [whiteScrapBarBtn!, titleBarBtn]
        //왼쪽 백버튼 아이템 설정
        setBackBtn(color: .white)
        
        self.navigationItem.title = ""
        
        //네비게이션바 컬러
        navBarBarTintColor = .white
        navBarBackgroundAlpha = 0
        //네비게이션 바 안의 아이템 컬러
        navBarTintColor = .white
        
    }
    
    func setTableView(){
        tableView.contentInset = UIEdgeInsetsMake(IMAGE_HEIGHT-CGFloat(kNavBarBottom), 0, 0, 0);
        tableView.showsVerticalScrollIndicator = false
        tableView.delegate = self
        tableView.dataSource = self
        tableView.addSubview(cycleScrollView)
        
    }
    
    func setcycleScrollView(placeData : PlaceDetailVOData){
        let serverImages = placeData.img
        var descs : [String]? = []
        for i in 0..<serverImages.count {
            descs?.append("\(i+1)/\(serverImages.count)")
        }
        cycleScrollView.serverImgArray = serverImages
        cycleScrollView.descTextArray = descs
        cycleScrollView.showPageControl = false
        
    } //setcycleScrollView
    
    func setBarButtons(placeData : PlaceDetailVOData){
        let titleBarBtn = UIBarButtonItem.titleBarbutton(title: placeData.scrapCnt.description, red: 255, green: 255, blue: 255, fontSize: 14, fontName: NanumSquareOTF.NanumSquareOTFR.rawValue, selector: nil, target: self)
        titleBarBtn.isEnabled = false
        if placeData.isScrap == 0{
            self.navigationItem.rightBarButtonItems = [whiteScrapBarBtn!, titleBarBtn]
        } else {
            self.navigationItem.rightBarButtonItems = [whiteFullScrapBarBtn!, titleBarBtn]
        }
    } //setBarButtons
    
    func setHeaderView(placeData : PlaceDetailVOData){
        
        scrapCount = placeData.scrapCnt
        ratingLbl.text = placeData.reviewScore.description
        titleLbl.text = placeData.name
        //
        let desc = placeData.description
       
        if let range = desc.range(of: "\r\n") {
            let prefix = desc[..<range.lowerBound] // or str[str.startIndex..<range.lowerBound]
            let suffix = desc[range.upperBound..<desc.endIndex]
            let totalString = prefix+" "+suffix
            descLbl.text = totalString.description
        }
        else if let range = desc.range(of: "\n"){
            let prefix = desc[..<range.lowerBound] // or str[str.startIndex..<range.lowerBound]
            let suffix = desc[range.upperBound..<desc.endIndex]
            let totalString = prefix+" "+suffix
            descLbl.text = totalString.description
        }
        else {
             descLbl.text = placeData.description
        }
        addressLbl.text = placeData.address
        addressLbl2.text = placeData.address
        addressLbl2.adjustsFontSizeToFitWidth = true
        currentStationLbl.text = placeData.station
        prevStationLbl.text = placeData.prevStation
        prevStationLbl.adjustsFontSizeToFitWidth = true
        nextsStationLbl.text = placeData.nextStation
        nextsStationLbl.adjustsFontSizeToFitWidth = true
        if let lineNum = LineNumber(rawValue : placeData.lineNumber) {
            staionImgView.image = UIImage(named: lineNum.lineImgName)
            lineNumImgView.image = UIImage(named: lineNum.dotImgName)
        }
    } //setHeaderView
    
    func setFooterView(placeData : PlaceDetailVOData){
        myPlace = MyPlace(name: placeData.name, lat: placeData.latitude, long: placeData.longitude)
        
        if selectedLang == .eng  {
            writeReviewBtn.setImage(#imageLiteral(resourceName: "place_detail_review_write_eng"), for: .normal)
        }
        searchWithGoogleLbl.text = selectedLang == .kor ? "구글맵으로 길찾기" : "Search with google map"
        
        if isPlace {
            if selectedLang == .kor  {
                openCloseLbl.text = "오픈/마감 시간"
                openLbl.text = "오픈"
                closeLbl.text = "마감"
            } else {
                openCloseLbl.text = "open / close time"
                openLbl.text = "open"
                closeLbl.text = "close"
            }
            
            contactImgView.image = #imageLiteral(resourceName: "place_detail_phone")
        } else {
            if selectedLang == .kor  {
                openCloseLbl.text = "기간"
                openLbl.text = "시작"
                closeLbl.text = "종료"
            } else {
                openCloseLbl.text = "period"
                openLbl.text = "start"
                closeLbl.text = "end"
            }
            contactImgView.image = #imageLiteral(resourceName: "place_detail_user")
        }
        
        openTimeLbl.text = placeData.openTime
        closeTimeLbl.text = placeData.closeTime
        contactLbl.text = placeData.contact
        contactLbl.adjustsFontSizeToFitWidth = true
    } //setFooterView
}

// MARK: - ScrollViewDidScroll
extension PlaceDetailVC {
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        let offsetY = scrollView.contentOffset.y
        
        if (offsetY > NAVBAR_COLORCHANGE_POINT) {
            navBarTintColor = #colorLiteral(red: 0.4392156863, green: 0.4392156863, blue: 0.4392156863, alpha: 1)
            navigationItem.leftBarButtonItem?.tintColor = #colorLiteral(red: 0.4392156863, green: 0.4392156863, blue: 0.4392156863, alpha: 1)
            navigationItem.rightBarButtonItems?[1].tintColor = #colorLiteral(red: 0.4392156863, green: 0.4392156863, blue: 0.4392156863, alpha: 1)
            if navigationItem.rightBarButtonItems?[0] == whiteScrapBarBtn {
                navigationItem.rightBarButtonItems?[0] = blackScrapBarBtn!
            } else if navigationItem.rightBarButtonItems?[0] == whiteFullScrapBarBtn{
                 navigationItem.rightBarButtonItems?[0] = greenScrapBarBtn!
            }
            
            let alpha = (offsetY - NAVBAR_COLORCHANGE_POINT) / CGFloat(kNavBarBottom)
            navBarBackgroundAlpha = alpha
            
        } else {
            navBarTintColor = .white
            navigationItem.leftBarButtonItem?.tintColor = .white
            navigationItem.rightBarButtonItems?[1].tintColor = .white
            if navigationItem.rightBarButtonItems?[0] == blackScrapBarBtn {
                navigationItem.rightBarButtonItems?[0] = whiteScrapBarBtn!
            } else if navigationItem.rightBarButtonItems?[0] == greenScrapBarBtn{
                navigationItem.rightBarButtonItems?[0] = whiteFullScrapBarBtn!
            }
            navBarBackgroundAlpha = 0
        }
        
        // 풀 다운 한계
        if (offsetY < LIMIT_OFFSET_Y) {
            scrollView.contentOffset = CGPoint.init(x: 0, y: LIMIT_OFFSET_Y)
        }
        
        // 사진의 크기 변경 (위로올릴때는 변하지 않음)
        // 여기서는 offsetY 사용 불가.（offsetY < LIMIT_OFFSET_Y） 일때, y = LIMIT_OFFSET_Y 는 offsetY 대응되지 않기 때문
        let newOffsetY = scrollView.contentOffset.y
        if (newOffsetY < -IMAGE_HEIGHT) {
            cycleScrollView.frame = CGRect(x: 0, y: newOffsetY, width: CGFloat(kScreenWidth), height: -newOffsetY)
        }
    }
    
    // private
   /* fileprivate func imageScaledToSize(image:UIImage, newSize:CGSize) -> UIImage {
        UIGraphicsBeginImageContext(CGSize(width: newSize.width * 2.0, height: newSize.height * 2.0))
        image.draw(in: CGRect(x: 0, y: 0, width: newSize.width * 2.0, height: newSize.height * 2.0))
        let newImage:UIImage = UIGraphicsGetImageFromCurrentImageContext() ?? image
        UIGraphicsEndImageContext()
        return newImage
    }*/
}

//MARK: - UITableViewDelegate,UITableViewDataSource
extension PlaceDetailVC: UITableViewDelegate,UITableViewDataSource{
    func numberOfSections(in tableView: UITableView) -> Int {
        return 2
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if let placeData_ = placeData{
            if section == 0 && placeData_.channel.channelID.count > 0{
                return 1
            } else if section == 1 && placeData_.reviews.count > 0{
                return 1
            }
        }
        return 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if indexPath.section == 0 {
            let cell = tableView.dequeueReusableCell(withIdentifier: PlaceDetailFirstTVCell.reuseIdentifier) as! PlaceDetailFirstTVCell
            cell.relatedChannel = placeData?.channel
            cell.delegate = self
            cell.senderDelegate = self
            return cell
        } else {
            let cell = tableView.dequeueReusableCell(withIdentifier: PlaceDetailSecondTVCell.reuseIdentifier) as! PlaceDetailSecondTVCell
            if let reviewScore = placeData?.reviewScore {
                cell.configure(reviewScore : reviewScore)
            }
            cell.reviewData = placeData?.reviews
            cell.delegate = self
            return cell
        }
    }
    
}

//MARK: - 관련 연예인/방송 구독 / 연예인 디테일 / 리뷰 보기
extension PlaceDetailVC : SelectSectionDelegate, SelectSenderDelegate {
    //SelectSenderDelegate -  관련 연예인/방송 구독
    func tap(section: Section, seledtedId: Int, sender: mySubscribeBtn) {
        if !isUserLogin() {
            goToLoginPage()
        } else {
            let params = ["channel_id" : sender.contentIdx]
            if sender.isSelected {
                self.unsubscribe(url: UrlPath.channelSubscription.getURL(sender.contentIdx?.description), sender: sender)
            } else {
                self.subscribe(url: UrlPath.channelSubscription.getURL(), params: params, sender: sender)
            }
        }
    }
    //SelectSectionDelegate
    func tap(section: Section, seledtedId: Int) {
        //관련/연예인 방송
        if section == .first {
            self.goToCelebrityDetail(selectedIdx : seledtedId)
        } else {
            //리뷰 보기
            let mapStoryboard = Storyboard.shared().mapStoryboard
            if let reviewContainerVC = mapStoryboard.instantiateViewController(withIdentifier:ReviewContainerVC.reuseIdentifier) as? ReviewContainerVC {
                
                reviewContainerVC.selectedIdx = self.selectedIdx
                
                if let reviewScore = placeData?.reviewScore {
                    reviewContainerVC.rating = reviewScore
                }
                self.navigationController?.pushViewController(reviewContainerVC, animated: true)
            }
        }
    }
}


//MARK: - 통신
extension PlaceDetailVC {
    func getPlaceInfo(url : String){
         self.pleaseWait()
        PlaceDetailService.shareInstance.getPlaceData(url: url,completion: { [weak self] (result) in
            guard let `self` = self else { return }
             self.clearAllNotice()
            switch result {
            case .networkSuccess(let placeData):
                let placeData_ = placeData as! [PlaceDetailVOData]
                self.placeData = placeData_[0]
                self.isChange = true
            case .networkFail :
                self.networkSimpleAlert()
            default :
                self.simpleAlert(title: "오류", message: "다시 시도해주세요")
                break
            }
        })
    }
    
    func scrap(url : String, params : [String:Any], sender : UIBarButtonItem){
         self.pleaseWait()
        ChannelSubscribeService.shareInstance.subscribe(url: url, params : params, completion: { [weak self] (result) in
            guard let `self` = self else { return }
             self.clearAllNotice()
            switch result {
            case .networkSuccess(_):
                if self.navigationItem.rightBarButtonItems?[0] == self.whiteScrapBarBtn! {
                    self.navigationItem.rightBarButtonItems?[0] = self.whiteFullScrapBarBtn!
                } else {
                    self.navigationItem.rightBarButtonItems?[0] = self.greenScrapBarBtn!
                }
                var changed : Int = 0
                if self.navigationItem.rightBarButtonItems?[1].title == self.scrapCount.description {
                    changed = self.scrapCount+1
                } else {
                    changed = self.scrapCount
                }
                self.navigationItem.rightBarButtonItems?[1].title =  changed.description
            case .networkFail :
                self.networkSimpleAlert()
            default :
                self.simpleAlert(title: "오류", message: "다시 시도해주세요")
                break
            }
        })
    } //subscribe
    
    func unscrap(url : String, sender : UIBarButtonItem){
         self.pleaseWait()
        ChannelSubscribeService.shareInstance.unsubscribe(url: url, completion: { [weak self] (result) in
            guard let `self` = self else { return }
            self.clearAllNotice()
            switch result {
            case .networkSuccess(_):
                if self.navigationItem.rightBarButtonItems?[0] == self.whiteFullScrapBarBtn! {
                    self.navigationItem.rightBarButtonItems?[0] = self.whiteScrapBarBtn!
                } else {
                    self.navigationItem.rightBarButtonItems?[0] = self.blackScrapBarBtn!
                }
                var changed : Int = 0
                if self.navigationItem.rightBarButtonItems?[1].title == self.scrapCount.description {
                    changed = self.scrapCount-1
                } else {
                    changed = self.scrapCount
                }
                self.navigationItem.rightBarButtonItems?[1].title =  changed.description
            case .networkFail :
                self.networkSimpleAlert()
            default :
                self.simpleAlert(title: "오류", message: "다시 시도해주세요")
                break
            }
        })
    }
    
    func subscribe(url : String, params : [String:Any], sender : mySubscribeBtn){
        ChannelSubscribeService.shareInstance.subscribe(url: url, params : params, completion: { [weak self] (result) in
            guard let `self` = self else { return }
            switch result {
            case .networkSuccess(_):
                sender.isSelected = true
                self.placeData?.channel.isSubscription[sender.indexPath!] = "1"
                /*let indexPath = IndexPath(item: sender.indexPath!, section: 0)
                 self.tableView.reloadRows(at: [indexPath], with: .top)*/
            case .networkFail :
                self.networkSimpleAlert()
            default :
                self.simpleAlert(title: "오류", message: "다시 시도해주세요")
                break
            }
        })
    } //subscribe
    
    func unsubscribe(url : String, sender : mySubscribeBtn){
        ChannelSubscribeService.shareInstance.unsubscribe(url: url, completion: { [weak self] (result) in
            guard let `self` = self else { return }
            switch result {
            case .networkSuccess(_):
                sender.isSelected = false
                self.placeData?.channel.isSubscription[sender.indexPath!] = "0"
            case .networkFail :
                self.networkSimpleAlert()
            default :
                self.simpleAlert(title: "오류", message: "다시 시도해주세요")
                break
            }
        })
    }
    
}



