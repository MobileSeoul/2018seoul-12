//
//  MainThirdCVCell.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 2..
//  Copyright © 2018년 강수진. All rights reserved.
//
import UIKit

class MainThirdCVCell: UICollectionViewCell {
    
    @IBOutlet weak var titleLbl: UILabel!
    @IBOutlet weak var placeLbl: UILabel!
    @IBOutlet weak var myImgView: UIImageView!
    
    func configure(data : MainVODataMain) {
        titleLbl.text = data.description
        placeLbl.text = data.name
        setImgWithKF(url: data.img, imgView: myImgView, defaultImg: #imageLiteral(resourceName: "aimg"))
    }
    
    override func awakeFromNib() {
        titleLbl.setLineSpacing(lineSpacing: 6)
        self.makeCornerRound(cornerRadius: 17)
    }
}


