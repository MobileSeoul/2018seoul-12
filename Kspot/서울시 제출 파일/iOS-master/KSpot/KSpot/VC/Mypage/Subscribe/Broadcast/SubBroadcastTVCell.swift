//
//  SubBroadcastTVCell.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 10..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit

class SubBroadcastTVCell: UITableViewCell {

    @IBOutlet weak var greenView: CustomView!
    @IBOutlet weak var profileView: UIImageView!
    @IBOutlet weak var nameLbl: UILabel!
    
    func configure(data : UserSubcriptionVOBroadcast) {
        setImgWithKF(url: data.thumbnailImg, imgView: profileView, defaultImg: #imageLiteral(resourceName: "aimg"))
        nameLbl.text = data.name
        greenView.isHidden = (data.newPostCheck == 0)
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        profileView.makeRounded(cornerRadius: profileView.frame.height/2)
         profileView.makeViewBorder(width: 0.5, color: #colorLiteral(red: 0.7529411765, green: 0.7529411765, blue: 0.7529411765, alpha: 1))
    }


}
