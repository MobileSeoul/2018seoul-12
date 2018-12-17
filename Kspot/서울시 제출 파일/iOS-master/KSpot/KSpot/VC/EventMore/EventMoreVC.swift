//
//  EventMoreVC.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 24..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit

class EventMoreVC: UIViewController, UIGestureRecognizerDelegate {
    
    @IBOutlet weak var collectionView: UICollectionView!
    var eventList : [UserScrapVOData]? {
        didSet {
            collectionView.reloadData()
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        if selectedLang == .kor {
            self.navigationItem.title = "최신 EVENT"
        } else {
            self.navigationItem.title = "NEW EVENT"
        }
        setBackBtn()
        getMoreEvent(url: UrlPath.spotEvent.getURL())
        collectionView.delegate = self
        collectionView.dataSource = self
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        self.clearAllNotice()
    }
}

//MARK: - UICollectionViewDataSource, UICollectionViewDelegate
extension EventMoreVC : UICollectionViewDataSource, UICollectionViewDelegate{
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        if let eventList_ = eventList{
            return eventList_.count
        }
        return 0
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        
        if let cell: MapContainerCVCell = collectionView.dequeueReusableCell(withReuseIdentifier: "EventMoreCVCell", for: indexPath) as? MapContainerCVCell {
            cell.delegate = self
            if let eventList_ = eventList {
                cell.configure(data: eventList_[indexPath.row])
            }
            return cell
        }
        return UICollectionViewCell()
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        if let eventList_ = eventList{
            self.goToPlaceDetailVC(selectedIdx: eventList_[indexPath.row].spotID)
        }
    }
}

//MARK: - UICollectionViewDelegateFlowLayout
extension EventMoreVC : UICollectionViewDelegateFlowLayout {
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
extension EventMoreVC : SelectDelegate {
    func tap(selected: Int?) {
        if let selected_ = selected {
            self.goToCelebrityDetail(selectedIdx: selected_)
        }
    }
}

//MARK: - 통신
extension EventMoreVC {
    func getMoreEvent(url : String){
        self.pleaseWait()
        UserScrapService.shareInstance.getScrapList(url: url,completion: { [weak self] (result) in
            guard let `self` = self else { return }
            self.clearAllNotice()
            switch result {
            case .networkSuccess(let eventData):
                let eventData = eventData as! [UserScrapVOData]
                self.eventList = eventData
            case .networkFail :
                self.networkSimpleAlert()
            default :
                self.simpleAlert(title: "오류", message: "다시 시도해주세요")
                break
            }
        })
    }
}
