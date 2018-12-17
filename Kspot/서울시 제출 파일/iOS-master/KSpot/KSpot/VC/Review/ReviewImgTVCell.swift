//
//  ReviewImgTVCell.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 11..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit

class ReviewImgTVCell: UITableViewCell {
    
    @IBOutlet weak var titleLbl: UILabel!
    @IBOutlet weak var contentLbl: UILabel!
    @IBOutlet weak var myImgView: UIImageView!
    @IBOutlet weak var ratingView: CosmosView!
    @IBOutlet weak var writingInfoLbl: UILabel!
    var delegate : SelectDelegate?
   
    @IBAction func moreAction(_ sender: Any) {
        delegate?.tap(selected: self.tag)
    }
    func configure(data : PlaceDetailVODataReview){
        titleLbl.text = data.title
        contentLbl.text = data.content
        setImgWithKF(url: data.img, imgView: myImgView, defaultImg: #imageLiteral(resourceName: "aimg"))
        ratingView.rating = data.reviewScore
        writingInfoLbl.text = "\(data.name) · \(data.regTime)"
        self.tag = data.reviewId
    } //configure
    
    override func awakeFromNib() {
        super.awakeFromNib()
        contentLbl.setLineSpacing(lineSpacing: 6)
        ratingView.settings.fillMode = .half
        ratingView.rating = 3.5
    }

}
