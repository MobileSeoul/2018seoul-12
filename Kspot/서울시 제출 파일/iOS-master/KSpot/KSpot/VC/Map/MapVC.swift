//
//  MapVC.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 2..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit
import CoreLocation
import SwiftyJSON
class MapVC: UIViewController {
    
    @IBOutlet weak var filterBtn: UIButton!
    @IBOutlet weak var containerView: UIView!
    var locationManager = CLLocationManager()
    var currentLocation : CLLocation?
    var currentDevice = UIDevice.current.screenType
    var currentSelectedLang = selectedLang
    var filterView = MapFilterView.instanceFromNib()
    var selectedFirstFilter : FilterToggleBtn?
    var selectedSecondFilter : Int?
    var selectedThirdFilter : Set<UIButton>?
    var selectedThirdFilter_ = Set<UIButton>()
    var entryPoint : EntryPoint = .local
    var chosenPlace : MyPlace?
    var defaultSpot : [UserScrapVOData]?{
        didSet {
            mapContainerVC.defaultSpot = defaultSpot
        }
    }
    private lazy var mapContainerVC: MapContainerVC = {
        let storyboard = Storyboard.shared().mapStoryboard
        var viewController = storyboard.instantiateViewController(withIdentifier: MapContainerVC.reuseIdentifier) as! MapContainerVC
        return viewController
    }()

    var isGoogleMapLocation : Bool = true {
        didSet {
            filterView.isGoogle = isGoogleMapLocation
            if isGoogleMapLocation {
                //구글 맵에서 선택했을 때는 거리 인덱스 활성화
                setDistanceIdx(index: selectedSecondFilter ?? 3)
            } else {
                if selectedLang == .kor {
                    filterView.distanceLbl.text = "1km 까지 설정"
                } else {
                    filterView.distanceLbl.text = "Set up to 1km"
                }
                
                //지도에서 클릭해서 선택했을 때는 버튼 활성화
                if let selectedBtn_ = selectedFirstFilter {
                    selectedBtn_.selected()
                } else {
                    filterView.popularBtn.selected()
                    selectedFirstFilter = filterView.popularBtn
                }
            }
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setLanguageNoti(selector: #selector(getLangInfo(_:)))
        initContainerView()
        locationInit()
        setFilterView(filterView)
        setTranslationBtn()
        setNavTitleImg()
        //네비게이션 타이틀
        //self.navigationItem.title = "K-Spot"
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        self.clearAllNotice()
    }
   
    //필터 버튼 클릭
    @IBAction func filterAction(_ sender: Any) {
        UIApplication.shared.keyWindow!.addSubview(filterView)
        filterView.snp.makeConstraints { (make) in
            make.top.bottom.leading.trailing.equalToSuperview()
        }
    }
    
    //지도 마커 클릭
    @IBAction func locationAction(_ sender: Any) {
        isGoogleMapLocation = true
        let mapStoryboard = Storyboard.shared().mapStoryboard
        if let googleMapVC = mapStoryboard.instantiateViewController(withIdentifier:GoogleMapVC.reuseIdentifier) as? GoogleMapVC {
            googleMapVC.delegate = self
            self.navigationController?.pushViewController(googleMapVC, animated: true)
        }
    }
    
    //검색 버튼 클릭
    @IBAction func searchAction(_ sender: Any) {
        goToSearchVC()
    }
    
    @objc func getLangInfo(_ notification : Notification) {
        setFilterView(filterView)
        if selectedLang == .kor {
            filterBtn.setImage(#imageLiteral(resourceName: "map_filter"), for: .normal)
            mapContainerVC.mapView?.mapImgView.image = #imageLiteral(resourceName: "map_illustration_img")
        } else {
            filterBtn.setImage(#imageLiteral(resourceName: "map_filter_eng"), for: .normal)
            mapContainerVC.mapView?.mapImgView.image = #imageLiteral(resourceName: "map_illustration_eng_img")
        }
        //통신도 다시해야함. 구글일때/로컬일때
        if entryPoint == .google {
            getMapInfo()
        } else {
            mapContainerVC.getDefualtMapData()
        }
    }
    
    func initContainerView(){
        addChildView(containerView: containerView, asChildViewController: mapContainerVC)
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

//MARK: - 구글맵에서 들어왔을 때
extension MapVC : SelectGoogleDelegate {
    func tap(selectedGoogle: MyPlace?) {
        isGoogleMapLocation = true
        if selectedLang == .kor {
            mapContainerVC.mapView?.selectedRegionLbl.text = "내 주변"
        } else {
            mapContainerVC.mapView?.selectedRegionLbl.text = "Around me"
        }
        
        entryPoint = .google
        chosenPlace = selectedGoogle
        getMapInfo()
    }
    
    //필터 정보 받아서 통신
    func getMapInfo(){
        var isFood : Int = 1
        var isCafe : Int = 1
        var isSights : Int = 1
        var isEvent : Int = 1
        var isEtc : Int = 1
        if let selectedThirdFilter_ = selectedThirdFilter {
            
            let buttonTagArr = selectedThirdFilter_.map({ (button) in
                return button.tag
            })
            isFood =  buttonTagArr.contains(0) ? 1: 0
            isCafe = buttonTagArr.contains(1) ? 1 : 0
            isSights = buttonTagArr.contains(2) ? 1 : 0
            isEvent = buttonTagArr.contains(3) ? 1 : 0
            isEtc = buttonTagArr.contains(4) ? 1 : 0
        }
        var distance : Double = 1.0
        if let selectedSecondFilter_ = selectedSecondFilter{
            switch selectedSecondFilter_ {
            case 0 :
                distance = 0.1
            case 1 :
                distance = 0.3
            case 2 :
                distance = 0.5
            case 3 :
                distance = 1
            case 4 :
                distance = 3
            default :
                break
            }
        }
        
        var lat : Double = 0
        var long : Double = 0
        if let chosenPlace_ = chosenPlace {
            lat = chosenPlace_.lat
            long = chosenPlace_.long
        }
        getGoogleSpot(url: UrlPath.spot.getURL("\(distance)/\(lat)/\(long)/\(isFood)/\(isCafe)/\(isSights)/\(isEvent)/\(isEtc)/"))
        
    }
}



//MARK: - 필터 뷰 버튼에 대한 액션 적용
extension MapVC {
    
    private typealias btnNum = (UIButton, Int)
   
    //필터 뷰 레이아웃
    func setFilterView(_ filterView : MapFilterView){
        
        filterView.cancleBtn.addTarget(self, action: #selector(MapVC.cancleAction(_sender:)), for: .touchUpInside)
        filterView.okBtn.addTarget(self, action: #selector(MapVC.okAction(_sender:)), for: .touchUpInside)
        let buttons : [btnNum] = [ (filterView.popularBtn, 0), (filterView.recentBtn, 0), (filterView.leftBtn, 1), (filterView.rightBtn, 1), (filterView.restaurantBtn, 2), (filterView.cafeBtn, 2), (filterView.hotplaceBtn, 2), (filterView.eventBtn, 2), (filterView.etcBtn, 2)]
        
        addtarget(inputs: buttons)
        
        filterView.isGoogle = isGoogleMapLocation
        
        //첫번째 섹션
        let popularBtn = filterView.popularBtn!
        let recentBtn = filterView.recentBtn!
        if selectedLang == .eng {
            popularBtn.setTitle("popularity", for: .normal)
            recentBtn.setTitle("recent", for: .normal)
            filterView.okBtn.setTitle("Check", for: .normal)
        } else {
            popularBtn.setTitle("인기순", for: .normal)
            recentBtn.setTitle("최신순", for: .normal)
            filterView.okBtn.setTitle("확인", for: .normal)
        }
        
        popularBtn.setOtherBtn(another: recentBtn)
        recentBtn.setOtherBtn(another: popularBtn)
        
        //두번째 섹션 - default 는 1km
        setDistanceIdx(index: selectedSecondFilter ?? 3)
        
        //세번째 섹션
        if selectedLang == .kor {
            filterView.restaurantBtn!.setImage(selected: #imageLiteral(resourceName: "map_filter_restaurant_green"), unselected: #imageLiteral(resourceName: "map_filter_restaurant_gray"))
            filterView.hotplaceBtn!.setImage(selected: #imageLiteral(resourceName: "map_filter_hotplace_green"), unselected: #imageLiteral(resourceName: "map_filter_hotplace_gray"))
            filterView.etcBtn!.setImage(selected: #imageLiteral(resourceName: "map_filter_etc_green"), unselected: #imageLiteral(resourceName: "map_filter_etc_gray"))
            filterView.cafeBtn!.setImage(selected: #imageLiteral(resourceName: "map_filter_cafe_green"), unselected: #imageLiteral(resourceName: "map_filter_cafe_gray"))
            filterView.eventBtn!.setImage(selected: #imageLiteral(resourceName: "map_filter_event_green"), unselected: #imageLiteral(resourceName: "map_filter_event_gray"))
        } else {
            filterView.restaurantBtn!.setImage(selected: #imageLiteral(resourceName: "map_filter_restaurant_green_new_eng"), unselected: #imageLiteral(resourceName: "map_filter_restaurant_gray_new_eng"))
            filterView.hotplaceBtn!.setImage(selected: #imageLiteral(resourceName: "map_filter_hotplace_green_new_eng"), unselected: #imageLiteral(resourceName: "map_filter_hotplace_gray_new_eng"))
            filterView.etcBtn!.setImage(selected: #imageLiteral(resourceName: "map_filter_etc_green_new_eng"), unselected:#imageLiteral(resourceName: "map_filter_etc_gray_new_eng"))
            filterView.cafeBtn!.setImage(selected: #imageLiteral(resourceName: "map_filter_cafe_green_new_eng"), unselected: #imageLiteral(resourceName: "map_filter_cafe_gray_new_eng"))
            filterView.eventBtn!.setImage(selected: #imageLiteral(resourceName: "map_filter_event_green_new_eng"), unselected: #imageLiteral(resourceName: "map_filter_event_gray_new_eng"))
        }
    } //setFilterView
    
    
    //필터 뷰 버튼에 대한 액션적용
    private func addtarget(inputs : [btnNum]){
        inputs.forEach { (button, actionNum) in
            switch actionNum {
            case 0 :
                button.addTarget(self, action: #selector(MapVC.firstFilterAction(_sender:)), for: .touchUpInside)
            case 1 :
                button.addTarget(self, action: #selector(MapVC.secondFilterAction(_sender:)), for: .touchUpInside)
            case 2 :
                button.addTarget(self, action: #selector(MapVC.thirdFilterAction(_sender:)), for: .touchUpInside)
            default :
                break
            }
        }
    }
    
    //두번째 섹션 인덱스에 따라 레이블 적용
    func setDistanceIdx(index : Int){
        let descArr : [String] = ["100m", "300m", "500m", "1km", "3km"]
        let numberOfItems = descArr.count
        let safeIndex = max(0, min(numberOfItems - 1, index))
        
        if (safeIndex == 0){
            filterView.leftBtn.isHidden = true
        } else if (safeIndex == descArr.count - 1){
            filterView.rightBtn.isHidden = true
        } else {
            filterView.leftBtn.isHidden = false
            filterView.rightBtn.isHidden = false
        }
        
        selectedSecondFilter = safeIndex
        if selectedLang == .kor {
            filterView.distanceLbl.text = "\(descArr[safeIndex]) 까지 설정"
        } else {
            filterView.distanceLbl.text = "Set up to \(descArr[safeIndex])"
        }
        
    }
    
    //필터 첫번째 섹션 클릭 했을 때
    @objc public func firstFilterAction(_sender: FilterToggleBtn) {
        selectedFirstFilter = _sender
    }
    
    //필터 두번째 섹션 클릭 했을 때
    @objc public func secondFilterAction(_sender: UIButton) {
        var index = selectedSecondFilter ?? 3
        if _sender.tag == 0 {
            index -= 1
        } else {
            index += 1
        }
        setDistanceIdx(index: index)
    }
    
    //필터 세번째 섹션 클릭 했을 때
    @objc public func thirdFilterAction(_sender: UIButton) {
        if(!_sender.isSelected) {
            _sender.isSelected = true
            selectedThirdFilter_.insert(_sender)
        } else {
            _sender.isSelected = false
            selectedThirdFilter_.remove(_sender)
        }
        selectedThirdFilter = selectedThirdFilter_
    }
    
    //취소 버튼
    @objc public func cancleAction(_sender: UIButton) {
        self.filterView.removeFromSuperview()
    }
    
    //확인 버튼
    @objc public func okAction(_sender: UIButton) {
        mapContainerVC.selectedFirstFilter = self.selectedFirstFilter
        mapContainerVC.selectedSecondFilter = self.selectedSecondFilter
        mapContainerVC.selectedThirdFilter = selectedThirdFilter
        
        //엔트리 포인트 넘겨줘서 로컬일때 통신하게 함
        mapContainerVC.entryPoint = self.entryPoint
        //구글이면 현재위치 기반으로 한 통신 적용
        if entryPoint == .google {
            getMapInfo()
        }
        self.filterView.removeFromSuperview()
    }
    
    
}


//MARK: - 위치 정보
extension MapVC : CLLocationManagerDelegate{
    
    func locationInit(){
        locationManager.delegate = self
        locationManager.requestWhenInUseAuthorization()
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.startUpdatingLocation()
        //허용 됐을 때 - 현재위치 받아옴
        if CLLocationManager.authorizationStatus() == .authorizedWhenInUse {
            currentLocation = locationManager.location
            
            guard let latitude = currentLocation?.coordinate.latitude,
                let longitude = currentLocation?.coordinate.longitude else {return}
            chosenPlace = MyPlace(name: "", lat: latitude, long: longitude)
            entryPoint = .google
            getMapInfo()
        }
        //허용 안됐을 때 - 강남구 클릭했을 때 정보
        else {
            entryPoint = .local
            mapContainerVC.tap(.gangnamgu)
        }
    }
    
    
    //location 허용 안했을 때
    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        if status == .denied {
            showLocationDisableAlert()
            //이때 디폴트 세팅
            entryPoint = .local
            mapContainerVC.tap(.gangnamgu)
        } else if status == .authorizedAlways || status == .authorizedWhenInUse{
            currentLocation = locationManager.location
            
            guard let latitude = currentLocation?.coordinate.latitude,
                let longitude = currentLocation?.coordinate.longitude else {return}
            chosenPlace = MyPlace(name: "", lat: latitude, long: longitude)
            entryPoint = .google
            getMapInfo()
            
        }
    }
    
    func showLocationDisableAlert() {
        let alertController = UIAlertController(title: "위치 접근이 제한되었습니다.", message: "위치 정보가 필요합니다.", preferredStyle: .alert)
        let openAction = UIAlertAction(title: "설정으로 가기", style: .default) { (action) in
            if let url = URL(string: UIApplicationOpenSettingsURLString) {
                UIApplication.shared.open(url, options: [:], completionHandler: nil)
            }
        }
        
        alertController.addAction(openAction)
        
        self.present(alertController, animated: true, completion: nil)
    }
}

//MARK: - 통신
extension MapVC {
    func getGoogleSpot(url : String){
        self.pleaseWait()
        GoogleSpotService.shareInstance.getGoogleSpot(url: url,completion: { [weak self] (result) in
            guard let `self` = self else { return }
            self.clearAllNotice()
            switch result {
            case .networkSuccess(let defaultSpot):
                if selectedLang == .kor {
                    self.mapContainerVC.mapView?.selectedRegionLbl.text = "내 주변"
                } else {
                    self.mapContainerVC.mapView?.selectedRegionLbl.text = "Around me"
                }
                
                self.defaultSpot = defaultSpot as? [UserScrapVOData]
            case .networkFail :
                self.networkSimpleAlert()
            default :
                self.simpleAlert(title: "오류", message: "다시 시도해주세요")
                break
            }
        })
    }
}


