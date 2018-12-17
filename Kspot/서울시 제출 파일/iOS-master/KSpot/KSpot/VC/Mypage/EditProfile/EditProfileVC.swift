//
//  EditProfileVC.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 10..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit

class EditProfileVC: UIViewController, UIGestureRecognizerDelegate {
    
    @IBOutlet weak var editBtn: UIButton!
    @IBOutlet weak var nickNameLbl: UILabel!
    @IBOutlet weak var nameTxtfield: UITextField!
    @IBOutlet weak var nameCountLbl: UILabel!
    @IBOutlet weak var profileImgView: UIImageView!
    @IBOutlet weak var doneBtn: UIBarButtonItem!
    
    var keyboardDismissGesture: UITapGestureRecognizer?
    let imagePicker : UIImagePickerController = UIImagePickerController()
    var profileImg : UIImage?
    var nameTxt : String = ""
    var imageData : Data? {
        didSet {
            if imageData != nil {
                if let imageData_ = imageData {
                    //makeImgView()
                    profileImgView.image =  UIImage(data: imageData_)
                    //isValid()
                }
            }
        }
    }

    override func viewDidLoad() {
        super.viewDidLoad()
       
        profileImgView.makeRounded(cornerRadius: nil)
        profileImgView.makeViewBorder(width: 0.5, color: #colorLiteral(red: 0.7529411765, green: 0.7529411765, blue: 0.7529411765, alpha: 1))
        nameTxtfield.addTarget(self, action: #selector(textFieldDidChange(_:)), for: .editingChanged)
        nameCountLbl.text = nameTxt.count.description
        nameTxtfield.text = nameTxt
        profileImgView.image = profileImg
        setBackBtn()
        setKeyboardSetting()
        setLanguageNoti(selector: #selector(getLangInfo(_:)))
        setLanguage()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        self.clearAllNotice()
    }
    
    @IBAction func imageTapAction(_ sender: Any) {
        openGallery()
    }
    
    @IBAction func doneAction(_ sender: Any) {
        editProfile(url: UrlPath.userEdit.getURL(), editedName: nameTxtfield.text!)
    }
    
    @objc func getLangInfo(_ notification : Notification) {
        setLanguage()
    }
    
    func setLanguage(){
        self.navigationItem.title = selectedLang == .kor ? "회원정보 수정" : "Edit Profile"
        nickNameLbl.text = selectedLang == .kor ? "닉네임" : "Nickname"
        nickNameLbl.sizeToFit()
        doneBtn.title = selectedLang == .kor ? "완료" : "complete"
    }
    
    //닉네임 변경
    @objc func textFieldDidChange(_ textField: UITextField) {
        if let text = nameTxtfield.text {
            nameCountLbl.text = text.count.description
        } else {
            nameCountLbl.text = "0"
        }
        guard let contentTxt = nameTxtfield.text else {return}
        
        if contentTxt.count < 2 {
            doneBtn.tintColor = #colorLiteral(red: 0.9254901961, green: 0.9254901961, blue: 0.9254901961, alpha: 1)
            doneBtn.isEnabled = false
        } else {
            doneBtn.tintColor = ColorChip.shared().mainColor
            doneBtn.isEnabled = true
        }
        if(contentTxt.count > 20) {
            let alertTitle = selectedLang == .kor ? "오류" : "Error"
            let alertMsg = selectedLang == .kor ? "20글자 초과" : "longer than 20 characters"
            self.simpleAlert(title: alertTitle, message: alertMsg)
            nameTxtfield.text = String(describing: contentTxt.prefix(19))
            nameCountLbl.text = nameTxtfield.text?.count.description
        }
    }
}


//MARK: - 앨범 열기 위함
extension EditProfileVC : UIImagePickerControllerDelegate,
UINavigationControllerDelegate  {
    
    // imagePickerDelegate
    func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        //사용자 취소
        self.dismiss(animated: true)
    }
    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : Any]) {
        
        //크롭한 이미지
        if let editedImage: UIImage = info[UIImagePickerControllerEditedImage] as? UIImage {
            imageData = UIImageJPEGRepresentation(editedImage, 0.1)
        } else if let originalImage: UIImage = info[UIImagePickerControllerOriginalImage] as? UIImage{
            imageData = UIImageJPEGRepresentation(originalImage, 0.1)
        }
        
        self.dismiss(animated: true)
    }
    
    // Method
    func openGallery(){
        let selectAlert = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
        let libraryTxt = selectedLang == .kor ? "앨범에서 사진 선택" : "Select image from album"
        let defualtTxt = selectedLang == .kor ? "기본 이미지 선택" : "Default image"
        let libraryAction = UIAlertAction(title: libraryTxt, style: .default) { _ in if UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.photoLibrary){
            let imagePicker = UIImagePickerController()
            imagePicker.delegate = self
            imagePicker.sourceType = UIImagePickerControllerSourceType.photoLibrary
            imagePicker.allowsEditing = true
            self.present(imagePicker, animated: true, completion: nil)
            }
        }
        
        let defualtAction = UIAlertAction(title: defualtTxt, style: .default) {
            _ in
            self.imageData = UIImageJPEGRepresentation(#imageLiteral(resourceName: "mypage_membership_edit_default_img"), 0.1)
        }
        let cancelAction = UIAlertAction(title: "취소", style: .cancel)
        selectAlert.addAction(libraryAction)
        selectAlert.addAction(defualtAction)
        selectAlert.addAction(cancelAction)
        self.present(selectAlert, animated: true, completion: nil)
    }
    
}


//MARK: - 키보드 대응
extension EditProfileVC{
    func setKeyboardSetting() {
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow(_:)), name: .UIKeyboardWillShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide(_:)), name: .UIKeyboardWillHide, object: nil)
    }
    
    @objc func keyboardWillShow(_ notification: Notification) {
        adjustKeyboardDismissGesture(isKeyboardVisible: true)
        self.editBtn.isHidden = true
    }
    
    @objc func keyboardWillHide(_ notification: Notification) {
        adjustKeyboardDismissGesture(isKeyboardVisible: false)
        self.editBtn.isHidden = false
    }
    
    //화면 바깥 터치했을때 키보드 없어지는 코드
    func adjustKeyboardDismissGesture(isKeyboardVisible: Bool) {
        if isKeyboardVisible {
            if keyboardDismissGesture == nil {
                keyboardDismissGesture = UITapGestureRecognizer(target: self, action: #selector(tapBackground))
                view.addGestureRecognizer(keyboardDismissGesture!)
            }
        } else {
            if keyboardDismissGesture != nil {
                view.removeGestureRecognizer(keyboardDismissGesture!)
                keyboardDismissGesture = nil
            }
        }
    }
    
    @objc func tapBackground() {
        self.view.endEditing(true)
    }
    
}

//MARK: - 통신
extension EditProfileVC {
    func editProfile(url : String, editedName : String ){
        self.pleaseWait()
        
        let params : [String : Any] = [
            "name" : editedName
        ]
        
        var images : [String : Data]?
        if let image = imageData {
            images = [
                "profile_img" : image
            ]
        }
        
        UserEditService.shareInstance.editProfile(url: url, params: params, image: images, completion: { [weak self] (result) in
            guard let `self` = self else { return }
            self.clearAllNotice()
            switch result {
            case .networkSuccess(_):
                self.noticeSuccess("수정 완료!", autoClear: true, autoClearTime: 1)
                 self.pop()
            case .duplicated :
                let alertTitle = selectedLang == .kor ? "오류" : "Error"
                let alertMsg = selectedLang == .kor ? "이미 사용중인 닉네임입니다" : "This nickname is already using"
                self.simpleAlert(title: alertTitle, message: alertMsg)
            case .networkFail :
                self.networkSimpleAlert()
            default :
                self.simpleAlert(title: "오류", message: "다시 시도해주세요")
                break
            }
        })
    }
}
