//
//  Extensions.swift
//  KSpot
//
//  Created by 강수진 on 2018. 8. 31..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit
import FBSDKLoginKit
import Kingfisher

/*---------------------NSObject---------------------------*/
extension NSObject {
    static var reuseIdentifier:String {
        return String(describing:self)
    }
}



extension UIDevice {
    var iPhoneX: Bool {
        return UIScreen.main.nativeBounds.height == 2436
    }
    var iPhone: Bool {
        return UIDevice.current.userInterfaceIdiom == .phone
    }
    enum ScreenType: String {
        case iPhones_4_4S = "iPhone 4 or iPhone 4S"
        case iPhones_5_5s_5c_SE = "iPhone 5, iPhone 5s, iPhone 5c or iPhone SE"
        case iPhones_6_6s_7_8 = "iPhone 6, iPhone 6S, iPhone 7 or iPhone 8"
        case iPhones_6Plus_6sPlus_7Plus_8Plus = "iPhone 6 Plus, iPhone 6S Plus, iPhone 7 Plus or iPhone 8 Plus"
        case iPhones_X_XS = "iPhone X or iPhone XS"
        case iPhone_XR = "iPhone XR"
        case iPhone_XSMax = "iPhone XS Max"
        case unknown
    }
    var screenType: ScreenType {
        switch UIScreen.main.nativeBounds.height {
        case 960:
            return .iPhones_4_4S
        case 1136:
            return .iPhones_5_5s_5c_SE
        case 1334:
            return .iPhones_6_6s_7_8
        case 1792:
            return .iPhone_XR
        case 1920, 2208:
            return .iPhones_6Plus_6sPlus_7Plus_8Plus
        case 2436:
            return .iPhones_X_XS
        case 2688:
            return .iPhone_XSMax
        default:
            return .unknown
        }
    }
}

/*---------------------UIViewController---------------------------*/


//화면 이동
extension UIViewController {
    func goToPlaceDetailVC(selectedIdx : Int, isPlace : Bool = true, title : String = ""){
        let mapStoryboard = Storyboard.shared().mapStoryboard
        if let placeDetailVC = mapStoryboard.instantiateViewController(withIdentifier:PlaceDetailVC.reuseIdentifier) as? PlaceDetailVC {
            placeDetailVC.navigationItem.title = title
            placeDetailVC.isPlace = isPlace
            placeDetailVC.selectedIdx = selectedIdx
            self.navigationController?.pushViewController(placeDetailVC, animated: true)
        }
    } //goToPlaceDetailVC
    
    func goToCelebrityDetail(selectedIdx : Int, title : String = ""){
        let categoryStoryboard = Storyboard.shared().categoryStoryboard
        if let categoryDetailVC = categoryStoryboard.instantiateViewController(withIdentifier:CategoryDetailVC.reuseIdentifier) as? CategoryDetailVC {
            categoryDetailVC.navigationItem.title = title
            categoryDetailVC.selectedIdx = selectedIdx
            self.navigationController?.pushViewController(categoryDetailVC, animated: true)
        }
    } //goToCelebrityDetail
    
    func goToSearchVC(){
        let mainStoryboard = Storyboard.shared().mainStoryboard
        if let mainSearchVC = mainStoryboard.instantiateViewController(withIdentifier:MainSearchVC.reuseIdentifier) as? MainSearchVC {
            
            self.navigationController?.pushViewController(mainSearchVC, animated: true)
        }
    } //goToSearchVC
    
    func goToLoginPage(entryPoint : Int = 0){
        let mainStoryboard = Storyboard.shared().mainStoryboard
        if let loginVC = mainStoryboard.instantiateViewController(withIdentifier:LoginVC.reuseIdentifier) as? LoginVC {
           loginVC.entryPoint = entryPoint
            self.present(loginVC, animated: true, completion: nil)
        }
    } //goToLoginPage
    
    //백버튼
    func setBackBtn(color : UIColor? = ColorChip.shared().barbuttonColor){
        let backBTN = UIBarButtonItem(image: UIImage(named: "category_detail_left_arrow"),
                                      style: .plain,
                                      target: self,
                                      action: #selector(self.pop))
        navigationItem.leftBarButtonItem = backBTN
        navigationItem.leftBarButtonItem?.tintColor = color
        navigationController?.interactivePopGestureRecognizer?.delegate = self as? UIGestureRecognizerDelegate
    }
    
    func setTranslationBtn(){
        let transBTN = UIBarButtonItem(image: UIImage(named: "navigationbar_translation"),
                                      style: .plain,
                                      target: self,
                                      action: #selector(self.translate))
        navigationItem.leftBarButtonItem = transBTN
        navigationItem.leftBarButtonItem?.tintColor = ColorChip.shared().barbuttonColor
    }
    
    func setLanguageNoti(selector : Selector){
       
        NotificationCenter.default.addObserver(self, selector: selector, name: NSNotification.Name("GetLanguageValue"), object: nil)
    
    }
    
    @objc func translate(){
        selectedLang = selectedLang == .kor ? .eng : .kor
        let langInfo : [String : Language] = ["selectedLanguage" : selectedLang]
        NotificationCenter.default.post(name: NSNotification.Name("GetLanguageValue"), object: nil, userInfo: langInfo)
       // self.viewDidLoad()
    }
    
    @objc func pop(){
        self.navigationController?.popViewController(animated: true)
    }
}

extension UIViewController {
    
    func setImgWithKF(url : String, imgView : UIImageView, defaultImg : UIImage){
        if let url = URL(string: url){
            imgView.kf.setImage(with: url)
           
        } else {
            imgView.image = defaultImg
        }
    }
    func gsno(_ value : String?) -> String{
        return value ?? ""
    }
    
    func gino(_ value : Int?) -> Int {
        return value ?? 0
    }
    

    
    func isUserLogin() -> Bool {
        
       /* if loginWith == .facebook {
            if FBSDKAccessToken.current() != nil{
                return true
            } else {
               return false
            }
        } else if loginWith == .kakao {
            let session: KOSession = KOSession.shared();
            if session.isOpen() {
                return true
            } else {
                return false
            }
        } else {
            return false
        }*/
        let userAuth = UserDefaults.standard.string(forKey: "userAuth") ?? "-1"
        if userAuth == "-1"{
            return false
        } else {
            return true
        }
    }
    
    func simpleAlert(title: String, message: String){
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let okTitle = selectedLang == .kor ? "확인" : "Check"
        let okAction = UIAlertAction(title: okTitle,style: .default)
        alert.addAction(okAction)
        present(alert, animated: true)
    }
    
    func networkSimpleAlert(){
        let title = selectedLang == .kor ? "오류" : "Error"
         let message = selectedLang == .kor ? "네트워크 연결상태를 확인해주세요" : "Please check network"
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let okTitle = selectedLang == .kor ? "확인" : "Check"
        let okAction = UIAlertAction(title: okTitle,style: .default)
        alert.addAction(okAction)
        present(alert, animated: true)
    }
    
    func simpleAlertwithHandler(title: String, message: String, okHandler : ((UIAlertAction) -> Void)?){
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let okTitle = selectedLang == .kor ? "확인" : "Check"
        let cancelTitle = selectedLang == .kor ? "취소" : "Cancel"
        let okAction = UIAlertAction(title: okTitle,style: .default, handler: okHandler)
        let cancelAction = UIAlertAction(title: cancelTitle,style: .cancel, handler: nil)
        alert.addAction(okAction)
        alert.addAction(cancelAction)
        present(alert, animated: true, completion: nil)
    }
    
    func addChildView(containerView : UIView, asChildViewController viewController: UIViewController) {
        addChildViewController(viewController)
        containerView.addSubview(viewController.view)
        viewController.view.frame = containerView.bounds
        viewController.view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        viewController.didMove(toParentViewController: self)
    }
    
    func removeChildView(containerView : UIView, asChildViewController viewController: UIViewController) {
        viewController.willMove(toParentViewController: nil)
        viewController.view.removeFromSuperview()
        viewController.removeFromParentViewController()
    }
    
    func customBarbuttonItem(title : String, red : Double, green : Double, blue : Double, fontSize : Int, selector : Selector?)->UIBarButtonItem{
        let customBarbuttonItem = UIBarButtonItem(title: title, style: .plain, target: self, action: selector)
        let fontSize = UIFont.systemFont(ofSize: CGFloat(fontSize))
        customBarbuttonItem.setTitleTextAttributes([
            NSAttributedStringKey(rawValue: NSAttributedStringKey.font.rawValue): fontSize,
            NSAttributedStringKey(rawValue: NSAttributedStringKey.foregroundColor.rawValue) : UIColor(red: CGFloat(red/255.0), green: CGFloat(green/255.0), blue: CGFloat(blue/255.0), alpha: CGFloat(1.0) )
            ], for: UIControlState.normal)
        return customBarbuttonItem
    }
    
}

/*---------------------UIView---------------------------*/

extension UIView {
    func makeRounded(cornerRadius : CGFloat?){
        if let cornerRadius_ = cornerRadius {
            self.layer.cornerRadius = cornerRadius_
        }  else {
             self.layer.cornerRadius = self.layer.frame.height/2
        }
        self.layer.masksToBounds = true
    }
    
    
    
    func makeViewBorder(width : Double, color : UIColor){
        self.layer.borderWidth = CGFloat(width)
        self.layer.borderColor = color.cgColor
    }
}

extension UIImage {
    func getCropRatio() -> CGFloat {
        let widthRatio = CGFloat(self.size.width / self.size.height)
        return widthRatio
    }
}

/*---------------------UIImageView---------------------------*/
/*extension UIImageView {
    func makeImageRound(){
        self.layer.cornerRadius = self.layer.frame.width/2
        self.layer.masksToBounds = true
    }
    
    func makeImgBorder(width : Double, color : UIColor){
        self.layer.borderWidth = CGFloat(width)
        self.layer.borderColor = color.cgColor
    }
}*/


/*---------------------UIButton---------------------------*/
extension UIButton{
    func setImage(selected : UIImage, unselected : UIImage){
        self.setImage(selected, for: .selected)
        self.setImage(unselected, for: .normal)
    }
}

/*---------------------UIBarButtonItem---------------------------*/
extension UIBarButtonItem {
    class func itemWith(colorfulImage: UIImage?, target: AnyObject, action: Selector) -> UIBarButtonItem {
        let button = UIButton(type: .custom)
        button.setImage(colorfulImage, for: .normal)
        button.translatesAutoresizingMaskIntoConstraints = false
        button.widthAnchor.constraint(equalToConstant: 48).isActive = true
        button.heightAnchor.constraint(equalToConstant: 48).isActive = true
        button.addTarget(target, action: action, for: .touchUpInside)
        
        let barButtonItem = UIBarButtonItem(customView: button)
        return barButtonItem
    }
    
    class func titleBarbutton(title : String, red : Double, green : Double, blue : Double, fontSize : CGFloat, fontName : String, selector : Selector?, target: AnyObject)->UIBarButtonItem{
        let customBarbuttonItem = UIBarButtonItem(title: title, style: .plain, target: target, action: selector)
        let customFont = UIFont(name: fontName, size: fontSize)!
        customBarbuttonItem.setTitleTextAttributes([
            NSAttributedStringKey(rawValue: NSAttributedStringKey.font.rawValue): customFont,
            NSAttributedStringKey(rawValue: NSAttributedStringKey.foregroundColor.rawValue) : UIColor(red: CGFloat(red/255.0), green: CGFloat(green/255.0), blue: CGFloat(blue/255.0), alpha: CGFloat(1.0) )
            ], for: UIControlState.normal)
        customBarbuttonItem.setTitleTextAttributes([
            NSAttributedStringKey(rawValue: NSAttributedStringKey.font.rawValue): customFont,
            NSAttributedStringKey(rawValue: NSAttributedStringKey.foregroundColor.rawValue) : UIColor(red: CGFloat(red/255.0), green: CGFloat(green/255.0), blue: CGFloat(blue/255.0), alpha: CGFloat(1.0) )
            ], for: UIControlState.selected)
        
        return customBarbuttonItem
    }
}

/*---------------------UICollectionViewCell---------------------------*/
extension UICollectionViewCell {
    func makeCornerRound(cornerRadius : CGFloat){
        self.contentView.layer.cornerRadius = cornerRadius
        self.contentView.layer.borderWidth = 0.5
        self.contentView.layer.borderColor = #colorLiteral(red: 0.8784313725, green: 0.8784313725, blue: 0.8784313725, alpha: 1)
        self.contentView.layer.masksToBounds = true
        self.contentView.clipsToBounds = true
    }
    
    
    func setImgWithKF(url : String, imgView : UIImageView, defaultImg : UIImage){
        if let url = URL(string: url){
            imgView.kf.setImage(with: url)
        } else {
            imgView.image = defaultImg
        }
    }
 
}

extension UITableViewCell {
    func setImgWithKF(url : String, imgView : UIImageView, defaultImg : UIImage){
        if let url = URL(string: url){
            imgView.kf.setImage(with: url)
        } else {
            imgView.image = defaultImg
        }
    }
  
}

extension mySubscribeBtn {
    func setSubscribeBtn(idx : Int, isSubscribe : Int){
        if selectedLang == .kor {
            self.setImage(UIImage(named: "category_subscription_white"), for: .normal)
            self.setImage(
                UIImage(named: "category_subscription_green"), for: .selected)
        } else {
            self.setImage(UIImage(named: "category_subscription_white_eng"), for: .normal)
            self.setImage(
                UIImage(named: "category_subscription_green_eng"), for: .selected)
        }
      
        self.contentIdx = idx
        if isSubscribe == 0 {
            self.isSelected = false
        } else {
            self.isSelected = true
        }
        
    }
}

extension UILabel {
    
    func setLineSpacing(lineSpacing: CGFloat = 0.0, lineHeightMultiple: CGFloat = 0.0) {
        
        guard let labelText = self.text else { return }
        
        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.lineSpacing = lineSpacing
        paragraphStyle.lineHeightMultiple = lineHeightMultiple
        
        let attributedString:NSMutableAttributedString
        if let labelattributedText = self.attributedText {
            attributedString = NSMutableAttributedString(attributedString: labelattributedText)
        } else {
            attributedString = NSMutableAttributedString(string: labelText)
        }
        // (Swift 4.1 and 4.0) Line spacing attribute
        attributedString.addAttribute(NSAttributedStringKey.paragraphStyle, value:paragraphStyle, range:NSMakeRange(0, attributedString.length))
        
        self.attributedText = attributedString
    }
}

/*---------------------String---------------------------*/
extension String {
    
    enum RegularExpressions: String {
        case phone = "^\\s*(?:\\+?(\\d{1,3}))?([-. (]*(\\d{3})[-. )]*)?((\\d{3})[-. ]*(\\d{2,4})(?:[-.x ]*(\\d+))?)\\s*$"
    }
    
    func isValid(regex: RegularExpressions) -> Bool {
        return isValid(regex: regex.rawValue)
    }
    
    func isValid(regex: String) -> Bool {
        let matches = range(of: regex, options: .regularExpression)
        return matches != nil
    }
    
    func onlyDigits() -> String {
        let filtredUnicodeScalars = unicodeScalars.filter{CharacterSet.decimalDigits.contains($0)}
        return String(String.UnicodeScalarView(filtredUnicodeScalars))
    }
    
    func makeACall() {
        if isValid(regex: .phone) {
            if let url = URL(string: "tel://\(self.onlyDigits())"), UIApplication.shared.canOpenURL(url) {
                if #available(iOS 10, *) {
                    UIApplication.shared.open(url)
                } else {
                    UIApplication.shared.openURL(url)
                }
            }
        }
    }
}



