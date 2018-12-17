//
//  PlaceDetailFirstTVCell.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 5..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit

class PlaceDetailFirstTVCell: UITableViewCell {
    
    @IBOutlet weak var relatedCelebrityLbl: UILabel!
    @IBOutlet weak var countLbl: UILabel!
    @IBOutlet weak var collectionView: UICollectionView!
  
    var delegate : SelectSectionDelegate?
    var senderDelegate : SelectSenderDelegate?
    var relatedChannel : PlaceDetailVODataChannel? {
        didSet {
            if let relatedChannel_ = self.relatedChannel {
                if selectedLang == .kor {
                    countLbl.text = relatedChannel_.channelID.count.description+"개"
                    relatedCelebrityLbl.text = "관련 연예인/방송"
                } else {
                    countLbl.text = relatedChannel_.channelID.count.description
                    relatedCelebrityLbl.text = "related Celebrity/Broadcast"
                }
               
                collectionView.reloadData()
            }
        }
    }
   
    override func awakeFromNib() {
        super.awakeFromNib()
        self.collectionView.delegate = self
        self.collectionView.dataSource = self
    }
    
}

//MARK: UICollectionViewDataSource, UICollectionViewDelegate
extension PlaceDetailFirstTVCell : UICollectionViewDataSource, UICollectionViewDelegate {
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        if let relatedChannel_ = relatedChannel{
            return relatedChannel_.channelID.count
        }
        return 0
    }
    
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        
        if let cell: PlaceDetailFirstCVCell = collectionView.dequeueReusableCell(withReuseIdentifier: PlaceDetailFirstCVCell.reuseIdentifier, for: indexPath) as? PlaceDetailFirstCVCell
        {
            cell.delegate = self.senderDelegate
            if let relatedChannel_ = relatedChannel{
                //id, name, img, issubscription
                cell.configure(id : relatedChannel_.channelID[indexPath.row],
                               name : relatedChannel_.channelName[indexPath.row],
                               img : relatedChannel_.thumbnailImg[indexPath.row],
                               isSubscribe : relatedChannel_.isSubscription[indexPath.row],
                               indexPath : indexPath.row
                               )
                
            }
            return cell
        }
        return UICollectionViewCell()
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        if let relatedChannel_ = relatedChannel {
            delegate?.tap(section: .first, seledtedId: Int(relatedChannel_.channelID[indexPath.row])!)
        }
        
    }
}

//MARK: - UICollectionViewDelegateFlowLayout
extension PlaceDetailFirstTVCell: UICollectionViewDelegateFlowLayout {
    //section내의
    //-간격 위아래
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
        return 10
    }
    //-간격 왼쪽오른쪽
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumInteritemSpacingForSectionAt section: Int) -> CGFloat {
        return 0
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CGSize(width: (100/375)*self.frame.width, height: 134)
        //return CGSize(width: 375, height: 375)
    }
}

