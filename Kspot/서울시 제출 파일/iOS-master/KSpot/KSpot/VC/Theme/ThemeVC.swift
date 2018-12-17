//
//  ThemeVC.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 10..
//  Copyright © 2018년 강수진. All rights reserved.
//


import UIKit
import SnapKit

private let IMAGE_HEIGHT:CGFloat = 284
private let NAVBAR_COLORCHANGE_POINT:CGFloat = IMAGE_HEIGHT - CGFloat(kNavBarBottom * 2)

class ThemeVC: UIViewController, UIGestureRecognizerDelegate {
    @IBOutlet weak var tableView : UITableView!
    var themeData : ThemeVOData? {
        didSet {
            setHeader(data : themeData)
            tableView.reloadData()
        }
    }
    var selectedId : Int? = 0
    lazy var topView:UIImageView = {
        let imgView = UIImageView(image: UIImage(named: "aimg"))
        imgView.frame = CGRect(x: 0, y: 0, width: kScreenWidth, height: IMAGE_HEIGHT)
        imgView.contentMode = UIViewContentMode.scaleAspectFill
        imgView.clipsToBounds = true
        return imgView
    }()
    lazy var blackView:UIView = {
        let view = UIView()
        view.backgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.26)
        return view
    }()
    lazy var titleLbl:UILabel = {
        let label = UILabel()
        label.backgroundColor = UIColor.clear
        label.textColor = UIColor.white
        label.text = "방탄소년단's PICK! \n HOT PLACE 5"
        label.numberOfLines = 2
        label.setLineSpacing(lineSpacing: 10)
        label.textAlignment = .center
        label.font = UIFont(name: NanumSquareOTF.NanumSquareOTFB.rawValue, size: 22)
        return label
    }()
    lazy var subtitleLbl:UILabel = {
        let label = UILabel()
        label.backgroundColor = UIColor.clear
        label.textColor = UIColor.white
        label.text = "#논현동 #압구정동 #신사동 #가락시장"
        label.textAlignment = .center
        label.font = UIFont(name: NanumSquareOTF.NanumSquareOTFR.rawValue, size: 15)
        return label
    }()
 
    
    override func viewDidLoad(){
        super.viewDidLoad()
        setupTableView()
        setupNavView()
        guard let selectedId_ = selectedId else {return}
        getTheme(url: UrlPath.theme.getURL(selectedId_.description))
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        self.clearAllNotice()
    }
    
    func setupTableView(){
        tableView.contentInset = UIEdgeInsetsMake(-CGFloat(kNavBarBottom), 0, 0, 0)
        tableView.delegate = self
        tableView.dataSource = self
        topView.addSubview(blackView)
        topView.addSubview(titleLbl)
        topView.addSubview(subtitleLbl)

        blackView.snp.makeConstraints { (make) in
            make.bottom.top.leading.trailing.equalToSuperview()
        }
        titleLbl.snp.makeConstraints { (make) in
            make.centerX.centerY.equalTo(topView)
        }
        subtitleLbl.snp.makeConstraints { (make) in
            make.centerX.equalTo(titleLbl)
            make.top.equalTo(titleLbl.snp.bottom).offset(24)
        }
        
        tableView.tableHeaderView = topView
    } //setupTableView
    
    func setHeader(data : ThemeVOData?){
        guard let data = data else {return}
          let count = data.theme.title.count
        if count == 2 {
            titleLbl.text = "\(data.theme.title[0])\n\(data.theme.title[1])"
        } else if count == 1 {
            titleLbl.text = "\(data.theme.title[0])"
        }
        //titleLbl.text = "\(data.theme.title[0])\n\(data.theme.title[1])"
        titleLbl.adjustsFontSizeToFitWidth = true
        subtitleLbl.text = data.theme.subtitle
        subtitleLbl.adjustsFontSizeToFitWidth = true
        setImgWithKF(url: data.theme.img, imgView: topView, defaultImg: #imageLiteral(resourceName: "aimg"))
    }
    
    func setupNavView(){
        //왼쪽 백버튼 아이템 설정
        setBackBtn(color: .white)
        self.navigationItem.title = ""
        //네비게이션바 컬러
        navBarBarTintColor = .white
        navBarBackgroundAlpha = 0
        //네비게이션 바 안의 아이템 컬러
        navBarTintColor = .white
    }
}

//MARK: -UITableViewDelegate,UITableViewDataSource
extension ThemeVC : UITableViewDelegate,UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if let themeData_ = themeData {
            return themeData_.themeContents.count
        }
        return 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: ThemeTVCell.reuseIdentifier) as! ThemeTVCell
        cell.delegate = self
        if let themeData_ = themeData {
            cell.configure(data : themeData_.themeContents[indexPath.row], row : indexPath.row)
        }
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath){
        tableView.deselectRow(at: indexPath, animated: true)
    }
}

// MARK: - 스크롤 할 때 네비게이션 색깔
extension ThemeVC {
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        let offsetY = scrollView.contentOffset.y
        //정해진 포인트보다 아래로 스크롤
        if (offsetY > NAVBAR_COLORCHANGE_POINT){
            navBarTintColor = #colorLiteral(red: 0.4392156863, green: 0.4392156863, blue: 0.4392156863, alpha: 1)
            navigationItem.leftBarButtonItem?.tintColor = #colorLiteral(red: 0.4392156863, green: 0.4392156863, blue: 0.4392156863, alpha: 1)
            let alpha = (offsetY - NAVBAR_COLORCHANGE_POINT) / CGFloat(kNavBarBottom)
            navBarBackgroundAlpha = alpha
            navBarTintColor = UIColor.black.withAlphaComponent(alpha)
            navBarTitleColor = UIColor.black.withAlphaComponent(alpha)
            statusBarStyle = .default
        }
        //정해진 포인트 보다 위로 스크롤
        else {
            navBarTintColor = .white
            navigationItem.leftBarButtonItem?.tintColor = .white
            navBarBackgroundAlpha = 0
            navBarTitleColor = .white
            statusBarStyle = .lightContent
        }
    }
}

//MARK: - SelectDelegate. 장소 상세보기 버튼
extension ThemeVC : SelectDelegate {
    func tap(selected: Int?) {
        guard let selected_ = selected else {return}
        goToPlaceDetailVC(selectedIdx: selected_)
    }
}

//MARK- : 통신
extension ThemeVC {
    func getTheme(url : String){
        self.pleaseWait()
        ThemeService.shareInstance.getThemeData(url: url,completion: { [weak self] (result) in
            guard let `self` = self else { return }
            self.clearAllNotice()
            switch result {
            case .networkSuccess(let themeData):
                self.themeData = themeData as? ThemeVOData
            case .networkFail :
                self.networkSimpleAlert()
            default :
                self.simpleAlert(title: "오류", message: "다시 시도해주세요")
                break
            }
        })
    }
}


