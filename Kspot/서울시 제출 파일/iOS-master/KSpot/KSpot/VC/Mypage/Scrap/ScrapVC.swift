//
//  ScrapVC.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 10..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit

class ScrapVC: UIViewController, UIGestureRecognizerDelegate {

    @IBOutlet weak var collectionView: UICollectionView!
  
    var userScrapList : [UserScrapVOData]? {
        didSet {
            collectionView.reloadData()
        }
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        setBackBtn()
        getUserScrap(url: UrlPath.userScrap.getURL())
        self.navigationItem.title = selectedLang == .kor ? "스크랩" : "Scrap"
        collectionView.delegate = self
        collectionView.dataSource = self
        setLanguageNoti(selector: #selector(getLangInfo(_:)))
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        self.clearAllNotice()
    }
    
    @objc func getLangInfo(_ notification : Notification) {
        self.navigationItem.title = selectedLang == .kor ? "스크랩" : "Scrap"
        getUserScrap(url: UrlPath.userScrap.getURL())
    }
}

//MARK: - UICollectionViewDataSource, UICollectionViewDelegate
extension ScrapVC : UICollectionViewDataSource, UICollectionViewDelegate{
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        if let userScrapList_ = userScrapList{
            return userScrapList_.count
        }
        return 0
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        
        if let cell: MapContainerCVCell = collectionView.dequeueReusableCell(withReuseIdentifier: "ScrapCVCell", for: indexPath) as? MapContainerCVCell
        {
            cell.delegate = self
            if let userScrapList_ = userScrapList {
                cell.configure(data: userScrapList_[indexPath.row])
            }
            
            return cell
        }
        return UICollectionViewCell()
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        if let userScrapList_ = userScrapList{
              self.goToPlaceDetailVC(selectedIdx: userScrapList_[indexPath.row].spotID)
        }
      
    }
}

//MARK: - UICollectionViewDelegateFlowLayout
extension ScrapVC: UICollectionViewDelegateFlowLayout {
    //section내의
    //-간격 위아래
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
        return 11
    }
    //-간격 왼쪽오른쪽
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumInteritemSpacingForSectionAt section: Int) -> CGFloat {
        return 0
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CGSize(width: (343/375)*view.frame.width, height: 371)
        //return CGSize(width: 375, height: 375)
    }
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForSectionAt section: Int) -> UIEdgeInsets {
        return UIEdgeInsetsMake(0, 0, 16, 0)
    }
}

//MARK: - 연예인 상세 페이지
extension ScrapVC : SelectDelegate {
    func tap(selected: Int?) {
        if let selected_ = selected {
            self.goToCelebrityDetail(selectedIdx: selected_)
        }
    }
}

//MARK: - 통신
extension ScrapVC {
    func getUserScrap(url : String){
        self.pleaseWait()
        UserScrapService.shareInstance.getScrapList(url: url,completion: { [weak self] (result) in
            guard let `self` = self else { return }
             self.clearAllNotice()
            switch result {
            case .networkSuccess(let scrapData):
                let scrapData = scrapData as! [UserScrapVOData]
                self.userScrapList = scrapData
            case .networkFail :
               self.networkSimpleAlert()
            default :
                self.simpleAlert(title: "오류", message: "다시 시도해주세요")
                break
            }
        })
    }
}

