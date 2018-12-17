//
//  GoogleMapVC.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 11..
//  Copyright © 2018년 강수진. All rights reserved.


import UIKit
import GoogleMaps
import GooglePlaces
import SwiftyJSON

struct MyPlace {
    var name: String
    var lat: Double
    var long: Double
}


class GoogleMapVC: UIViewController, UIGestureRecognizerDelegate, GMSMapViewDelegate {
    typealias latLong = (lat : Double, long : Double)
    
    @IBOutlet var myMapView: GMSMapView!
    let defualtLat = 36.3504
    let defualtLong = 127.3845
    let currentLocationMarker = GMSMarker()
    var locationManager = CLLocationManager()
    var currentLocation : CLLocation?
    var chosenPlace: MyPlace?
    var delegate : SelectGoogleDelegate?
    let txtFieldSearch: UITextField = {
        let tf=UITextField()
        tf.borderStyle = .roundedRect
        tf.backgroundColor = .white
        tf.layer.borderColor = UIColor.darkGray.cgColor
        if selectedLang == .kor {
            tf.placeholder="찾고 싶은 장소를 입력해주세요"
        } else {
            tf.placeholder="Enter the place"
            
        }
        
        tf.translatesAutoresizingMaskIntoConstraints=false
        return tf
    }()
    
    let btnMyLocation: UIButton = {
        let btn=UIButton()
        btn.backgroundColor = UIColor.white
        btn.setImage(#imageLiteral(resourceName: "map_gps_gps"), for: .normal)
        btn.addTarget(self, action: #selector(btnMyLocationAction), for: .touchUpInside)
        btn.translatesAutoresizingMaskIntoConstraints=false
        return btn
    }()
    
    let okBtn: UIButton = {
        let btn=UIButton()
        btn.backgroundColor = ColorChip.shared().mainColor
        if selectedLang == .kor {
            btn.setTitle("이 위치로 장소 설정",for: .normal)
        } else {
            btn.setTitle("Set my location to here",for: .normal)
        }
        btn.setTitleColor(.white, for: .normal)
        btn.titleLabel?.font = UIFont(name: NanumSquareOTF.NanumSquareOTFR.rawValue, size: 20)
        btn.addTarget(self, action: #selector(okAction), for: .touchUpInside)
        btn.translatesAutoresizingMaskIntoConstraints=false
        return btn
    }()
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.view.backgroundColor = UIColor.white
        navBarBackgroundAlpha = 0
        myMapView.delegate=self
        txtFieldSearch.delegate=self
        locationManager.delegate = self
        locationManager.requestWhenInUseAuthorization()
        //locationManager.startUpdatingLocation()
        locationManager.startMonitoringSignificantLocationChanges()
        setBackBtn()
        setupViews()
        initGoogleMaps()
        setInitPlace()
    }
    
    //초기 구글맵 설정
    func initGoogleMaps() {
        let camera = GMSCameraPosition.camera(withLatitude: defualtLat, longitude: defualtLong, zoom: 17.0)
        self.myMapView.camera = camera
        self.myMapView.delegate = self
        self.myMapView.isMyLocationEnabled = true
    }
    
    //초기 장소 설정
    func setInitPlace(){
      
            locationManager.startUpdatingLocation()
            if CLLocationManager.authorizationStatus() == .authorizedWhenInUse {
                //위치 허용됐을 때 현재 위치
                currentLocation = locationManager.location
                guard let latitude = currentLocation?.coordinate.latitude,
                    let longitude = currentLocation?.coordinate.longitude else {return}
                chosenPlace = MyPlace(name: "", lat: latitude, long: longitude)
            } else {
                //위치 허용 안됐을때 대전으로
                if chosenPlace == nil {
                    chosenPlace = MyPlace(name: "대전", lat: defualtLat, long: defualtLong)
                }
            }
    }
    
   
    
    //현재 위치 받아오는 함수
    func getMyLatLong() -> latLong {
        let location: CLLocation? = myMapView.myLocation
        if let location_ = location {
            let lat = location_.coordinate.latitude
            let long = location_.coordinate.longitude
            return (lat, long)
        }
        return (defualtLat, defualtLong)
    }
    
    //현재 위치 허용 안됐을 때 띄우는 경고창
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

//MARK: - 버튼들에 대한 액션
extension GoogleMapVC {
    
    //현재 위치 버튼 눌렀을 때
    @objc func btnMyLocationAction() {
        if CLLocationManager.authorizationStatus() == .authorizedWhenInUse {
            //허용 되면 현재 위치 받아와서 설정
            let myLocation = getMyLatLong()
            chosenPlace = MyPlace(name: "", lat: myLocation.lat, long: myLocation.long)
            let camera = GMSCameraPosition.camera(withLatitude: myLocation.lat, longitude: myLocation.long, zoom: 17.0)
            self.myMapView.animate(to: camera)
        } else {
            //허용 안됐을때 경고
            showLocationDisableAlert()
        }
    }
    
    //확인 버튼
    @objc func okAction() {
        delegate?.tap(selectedGoogle: chosenPlace)
        self.pop()
    }
}

//MARK: - GOOGLE AUTO COMPLETE DELEGATE / 검색 시 지역 클릭, 취소에 대한 액션
extension GoogleMapVC : GMSAutocompleteViewControllerDelegate {

    func viewController(_ viewController: GMSAutocompleteViewController, didAutocompleteWith place: GMSPlace) {
        let lat = place.coordinate.latitude
        let long = place.coordinate.longitude
        let camera = GMSCameraPosition.camera(withLatitude: lat, longitude: long, zoom: 17.0)
        myMapView.camera = camera
        txtFieldSearch.text=place.formattedAddress
        chosenPlace = MyPlace(name: place.formattedAddress!, lat: lat, long: long)
        let marker=GMSMarker()
        marker.position = CLLocationCoordinate2D(latitude: lat, longitude: long)
        marker.icon = #imageLiteral(resourceName: "map_gps_dot")
        marker.title = "\(place.name)"
        marker.snippet = "\(place.formattedAddress!)"
        marker.map = myMapView
        
        self.dismiss(animated: true, completion: nil) // dismiss after place selected
    }
    
    func viewController(_ viewController: GMSAutocompleteViewController, didFailAutocompleteWithError error: Error) {
        print("ERROR AUTO COMPLETE \(error)")
    }
    
    func wasCancelled(_ viewController: GMSAutocompleteViewController) {
        self.dismiss(animated: true, completion: nil)
    }
}

//MARK: - CLLocationManagerDelegate
extension GoogleMapVC : CLLocationManagerDelegate {
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("Error while getting location \(error)")
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        locationManager.delegate = nil
        locationManager.stopUpdatingLocation()
        let location = locations.last
        let lat = (location?.coordinate.latitude)!
        let long = (location?.coordinate.longitude)!
        let camera = GMSCameraPosition.camera(withLatitude: lat, longitude: long, zoom: 17.0)
        
        self.myMapView.animate(to: camera)
        
    }
}

//MARK: - UITextFieldDelegate / 텍필 클릭할 때 검색 창 뜨는 것
extension GoogleMapVC : UITextFieldDelegate {
    func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool {
        let autoCompleteController = GMSAutocompleteViewController()
        autoCompleteController.delegate = self
        
        let filter = GMSAutocompleteFilter()
        autoCompleteController.autocompleteFilter = filter
        
        self.locationManager.startUpdatingLocation()
        
        self.present(autoCompleteController, animated: true, completion: nil)
        return false
    }
}

//MARK: - 텍필, 버튼들에 대한 컨스트레인
extension GoogleMapVC {
    func setupViews() {
        myMapView.snp.makeConstraints { (make) in
            make.top.equalToSuperview()
        }
        
        self.view.addSubview(txtFieldSearch)
        txtFieldSearch.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 10).isActive=true
        txtFieldSearch.snp.makeConstraints { (make) in
            make.leading.equalToSuperview().offset(10)
            make.trailing.equalToSuperview().offset(-10)
            make.height.equalTo(35)
        }
        txtFieldSearch.adjustsFontSizeToFitWidth = true
        setupTextField(textField: txtFieldSearch, img: #imageLiteral(resourceName: "main_search"))
        
        
        self.view.addSubview(btnMyLocation)
        btnMyLocation.snp.makeConstraints { (make) in
            make.width.height.equalTo(51)
            make.top.equalTo(txtFieldSearch.snp.bottom).offset(10)
            make.trailing.equalTo(txtFieldSearch.snp.trailing)
        }
        
        
        self.view.addSubview(okBtn)
        okBtn.snp.makeConstraints { (make) in
            make.trailing.bottom.equalToSuperview().offset(-16)
            make.leading.equalToSuperview().offset(16)
            make.height.equalTo(58)
        }
    }
    
    func setupTextField(textField: UITextField, img: UIImage){
        textField.leftViewMode = UITextFieldViewMode.always
        let imageView = UIImageView(frame: CGRect(x: 10, y: 7, width: 15, height: 15))
        imageView.image = img
        let paddingView = UIView(frame:CGRect(x: 0, y: 0, width: 30, height: 30))
        paddingView.addSubview(imageView)
        textField.leftView = paddingView
    }
}


