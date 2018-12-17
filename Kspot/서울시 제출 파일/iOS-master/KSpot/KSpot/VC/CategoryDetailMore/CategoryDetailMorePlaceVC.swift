//
//  CategoryDetailMorePlaceVC.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 17..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit

class CategoryDetailMorePlaceVC: UIViewController, UIGestureRecognizerDelegate {
    
    @IBOutlet weak var collectionView: UICollectionView!
    var isPlace = true
    var selectedIdx = 0
    var mainTitle : String? = ""
    var channelMoreData : [UserScrapVOData]? {
        didSet {
            collectionView.reloadData()
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setBackBtn()
        collectionView.delegate = self
        collectionView.dataSource = self
        let isEvent = isPlace ? 0 : 1
        getChannelSpotMore(url: UrlPath.channelSpotMore.getSpotMoreURL(channelId: selectedIdx, isEvent: isEvent))
        setLanguageNoti(selector: #selector(getLangInfo(_:)))
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        self.clearAllNotice()
    }
    
    @objc func getLangInfo(_ notification : Notification) {
        let isEvent = isPlace ? 0 : 1
        getChannelSpotMore(url: UrlPath.channelSpotMore.getSpotMoreURL(channelId: selectedIdx, isEvent: isEvent))
    }
}

//MARK: - UICollectionViewDataSource, UICollectionViewDelegate
extension CategoryDetailMorePlaceVC : UICollectionViewDataSource, UICollectionViewDelegate{
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        if let channelMoreData_ = channelMoreData{
            return channelMoreData_.count
        }
        return 0
    }
    
    //헤더 뷰
    func collectionView(_ collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, at indexPath: IndexPath) -> UICollectionReusableView {
        //1
        let headerView = collectionView.dequeueReusableSupplementaryView(ofKind: kind,
                                                                         withReuseIdentifier: "CategoryDetailMorePlaceHeaderView",
                                                                         for: indexPath) as! CategoryDetailMorePlaceHeaderView
        if let mainTitle = mainTitle {
            headerView.titleLbl.text = mainTitle+" K-Spot"
        }
        
        return headerView
    }

    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        
        if let cell: MapContainerCVCell = collectionView.dequeueReusableCell(withReuseIdentifier: "PlaceMoreCVCell", for: indexPath) as? MapContainerCVCell
        {
            cell.delegate = self
            if let channelMoreData_ = channelMoreData{
               cell.configure(data: channelMoreData_[indexPath.row])
            }
            return cell
        }
        return UICollectionViewCell()
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        if let channelMoreData_ = channelMoreData{
            if self.title == "이벤트" {
                self.goToPlaceDetailVC(selectedIdx: channelMoreData_[indexPath.row].spotID, isPlace: false)
            } else {
              self.goToPlaceDetailVC(selectedIdx: channelMoreData_[indexPath.row].spotID)
            }
        }
    }
}

//MARK: - UICollectionViewDelegateFlowLayout
extension CategoryDetailMorePlaceVC: UICollectionViewDelegateFlowLayout {
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
extension CategoryDetailMorePlaceVC : SelectDelegate {
    func tap(selected: Int?) {
        if let selected_ = selected {
            self.goToCelebrityDetail(selectedIdx: selected_)
        }
    }
}

//MARK: - 통신
extension CategoryDetailMorePlaceVC {
    func getChannelSpotMore(url : String){
        self.pleaseWait()
        UserScrapService.shareInstance.getScrapList(url: url,completion: { [weak self] (result) in
            guard let `self` = self else { return }
            self.clearAllNotice()
            switch result {
            case .networkSuccess(let channelMoreData):
                self.channelMoreData = channelMoreData as? [UserScrapVOData]
            case .networkFail :
                self.networkSimpleAlert()
            default :
                self.simpleAlert(title: "오류", message: "다시 시도해주세요")
                break
            }
        })
    }
}

