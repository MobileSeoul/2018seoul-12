//
//  MainViewController.swift
//  KSpot
//
//  Created by 강수진 on 2018. 8. 31..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit
import FBSDKLoginKit
import ImageSlideshow

//MARK: - 앱 전체에서 쓰일 전역변수
var selectedLang : Language = .kor
var loginWith : LoginType?

class MainViewController: UIViewController {
  
    @IBOutlet weak var themeIconImg: UIImageView!
    @IBOutlet weak var tableView: UITableView!
    var mainData : MainVOData? {
        didSet {
            tableView.reloadData()
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setLanguageNoti(selector: #selector(getLangInfo(_:)))
        setTranslationBtn()
        setRootViewController()
        setNavTitleImg()
        tableView.delegate = self
        tableView.dataSource = self
        tableView.tableFooterView = UIView(frame : .zero)
        getMainData(url: UrlPath.main.getURL())
        
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        self.clearAllNotice()
    }
    
    @IBAction func searchAction(_ sender: Any) {
        self.goToSearchVC()
    }
    
    
    
    @objc func getLangInfo(_ notification : Notification) {
        if selectedLang == .kor {
            themeIconImg.image = #imageLiteral(resourceName: "main_theme")
        } else {
            themeIconImg.image = #imageLiteral(resourceName: "main_theme_eng")
        }
        getMainData(url: UrlPath.main.getURL())
    }
    
    //MARK: - 로그인 안했을 때 첫 화면 띄우는 함수
    fileprivate func setRootViewController() {
        let session: KOSession = KOSession.shared()
        if FBSDKAccessToken.current() != nil{
            loginWith = .facebook
        } else if session.isOpen() {
            loginWith = .kakao
        }
        if !isUserLogin() {
            goToLoginPage(entryPoint: 1)
        }
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

//MARK: - UITableViewDelegate, UITableViewDataSource
extension MainViewController : UITableViewDelegate, UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 4
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if indexPath.row == 0 {
            let cell = tableView.dequeueReusableCell(withIdentifier: MainFirstTVCell.reuseIdentifier) as! MainFirstTVCell
            cell.delegate = self
            
            if let mainData_ = mainData{
                var imageArr : [InputSource] = mainData_.theme.flatMap({ (data) in
                    KingfisherSource(urlString: data.mainImg)
                })
                if selectedLang == .kor {
                    imageArr.insert(ImageSource(imageString: "main_theme_img_today")!, at: 0)
                } else {
                    imageArr.insert(ImageSource(imageString: "main_theme_img_today_eng")!, at: 0)
                }
                cell.imageSource = imageArr
            }
            cell.awakeFromNib()
            return cell
        } else if indexPath.row == 1 {
            let cell = tableView.dequeueReusableCell(withIdentifier: MainSecondTVCell.reuseIdentifier) as! MainSecondTVCell
            cell.recommendPlaceData = mainData?.mainRecommandSpot
            cell.delegate = self
            return cell
        } else if indexPath.row == 2 {
            let cell = tableView.dequeueReusableCell(withIdentifier: MainThirdTVCell.reuseIdentifier) as! MainThirdTVCell
            cell.popularPlaceData = mainData?.mainBestPlace
            cell.configure(section: indexPath.row)
            cell.delegate = self
            return cell
        } else {
            let cell = tableView.dequeueReusableCell(withIdentifier: "MainForthTVCell") as! MainThirdTVCell
            cell.popularPlaceData = mainData?.mainBestEvent
            cell.configure(section: indexPath.row)
            cell.delegate = self
            return cell
        }
    }
    
}

//MARK: - SelectSectionDelegate
extension MainViewController : SelectSectionDelegate {
    func tap(section: Section, seledtedId: Int) {
        //테마 뷰 클릭했을 때
        if (section == .first) {
            if seledtedId >= 0 {
                let mainStoryboard = Storyboard.shared().mainStoryboard
                if let themeVC = mainStoryboard.instantiateViewController(withIdentifier:ThemeVC.reuseIdentifier) as? ThemeVC {
                    themeVC.selectedId = (mainData?.theme[seledtedId].themeID)
                    self.navigationController?.pushViewController(themeVC, animated: true)
                }
            }
        }
        //이벤트 더보기 버튼
        else if seledtedId == -1 {
            let mainStoryboard = Storyboard.shared().mainStoryboard
            if let eventMoreVC = mainStoryboard.instantiateViewController(withIdentifier:EventMoreVC.reuseIdentifier) as? EventMoreVC {
                self.navigationController?.pushViewController(eventMoreVC, animated: true)
            }
        }
        //장소 상세보기
        else if section == .second || section == .third{
            self.goToPlaceDetailVC(selectedIdx: seledtedId)
        }
        //이벤트 상세보기
        else if section == .forth {
            self.goToPlaceDetailVC(selectedIdx: seledtedId, isPlace: false)
        }
    }
}

extension MainViewController {
    func getMainData(url : String){
        self.pleaseWait()
        MainService.shareInstance.getMainData(url: url,completion: { [weak self] (result) in
            guard let `self` = self else { return }
            self.clearAllNotice()
            switch result {
            case .networkSuccess(let mainData):
                self.mainData = mainData as? MainVOData
            case .networkFail :
                self.networkSimpleAlert()
            default :
                self.simpleAlert(title: "오류", message: "다시 시도해주세요")
                break
            }
        })
    }
}

