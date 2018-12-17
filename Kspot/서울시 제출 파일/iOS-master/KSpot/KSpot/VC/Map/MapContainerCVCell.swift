//
//  MapContainerCVCell.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 2..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit

class MapContainerCVCell: UICollectionViewCell {
    @IBOutlet weak var myImgView: UIImageView!
    @IBOutlet weak var titleLbl: UILabel!
    @IBOutlet weak var ratingLbl: UILabel!
    @IBOutlet weak var descLbl: UILabel!
    @IBOutlet weak var locationLbl: UILabel!
    @IBOutlet weak var collectionView: UICollectionView!
    var channel : UserScrapVODataChannel?
    var delegate : SelectDelegate?
    func configure(data : UserScrapVOData){
        setImgWithKF(url: data.img, imgView: myImgView, defaultImg: #imageLiteral(resourceName: "aimg"))
        titleLbl.text = data.name
        ratingLbl.text = data.reviewScore.description
        descLbl.text = data.description
        locationLbl.text = "\(data.addressGu) · \(data.station)"
        channel = data.channel
    }
    
    override func awakeFromNib() {
        self.collectionView.delegate = self
        self.collectionView.dataSource = self
        self.contentView.layer.cornerRadius = 17
        self.contentView.layer.borderWidth = 0.5
        self.contentView.layer.borderColor = #colorLiteral(red: 0.8784313725, green: 0.8784313725, blue: 0.8784313725, alpha: 1)
        self.contentView.layer.masksToBounds = true
    }
    
    override func prepareForReuse() {
        super.prepareForReuse()
        collectionView.reloadData()
    }
    
}

//MARK: - UICollectionViewDataSource, UICollectionViewDelegate
extension MapContainerCVCell  : UICollectionViewDataSource, UICollectionViewDelegate {
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        if let channel_ = channel {
            return channel_.thumbnailImg.count
        }
        return 0
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        
        if let cell: MapCelebrityCVCell = collectionView.dequeueReusableCell(withReuseIdentifier: MapCelebrityCVCell.reuseIdentifier, for: indexPath) as? MapCelebrityCVCell
        {
            if let channel_ = channel {
                cell.configure(data : channel_.thumbnailImg[indexPath.row])
            }
            return cell
        }
        return UICollectionViewCell()
    }
 
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        if let channel_ = channel {
            delegate?.tap(selected: Int(channel_.channelID[indexPath.row]))
        }
    }
}

//MARK: - UICollectionViewDelegateFlowLayout
extension MapContainerCVCell : UICollectionViewDelegateFlowLayout {
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
       // return CGSize(width: self.frame.width, height: (364/375)*self.frame.width)
        return CGSize(width: 26, height: 26)
    }
    
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForSectionAt section: Int) -> UIEdgeInsets {
        
        return UIEdgeInsetsMake(11 ,16, 15 , 16)
    }
}
