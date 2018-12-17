//
//  MapCelebrityCVCell.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 2..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit

class MapCelebrityCVCell: UICollectionViewCell {
     @IBOutlet weak var myImgView: UIImageView!
    
    func configure(data : String){
        setImgWithKF(url: data, imgView: myImgView, defaultImg: #imageLiteral(resourceName: "aimg"))
    }
    override func awakeFromNib() {
        self.makeCornerRound(cornerRadius: self.layer.frame.width/2)
    }
}
