//
//  MypageCVCell.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 9..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit
import Kingfisher

class MypageCVCell: UICollectionViewCell {
    @IBOutlet weak var myImgView: UIImageView!
    @IBOutlet weak var engNameLbl: UILabel!
    @IBOutlet weak var koreanNameLbl: UILabel!
    
    func configure(data : MypageVODataChannel){
        self.setImgWithKF(url: data.backgroundImg, imgView: myImgView, defaultImg: #imageLiteral(resourceName: "aimg"))
         engNameLbl.text = data.engName
        koreanNameLbl.text = data.korName
    }
    override func awakeFromNib() {
        self.makeRounded(cornerRadius: 17)
        self.makeViewBorder(width: 0.5, color: #colorLiteral(red: 0.7529411765, green: 0.7529411765, blue: 0.7529411765, alpha: 1))
    }
}
