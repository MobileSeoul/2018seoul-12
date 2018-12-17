//
//  CelebrityTVCell.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 2..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit

class mySubscribeBtn : UIButton {
    var isSubscribe : Int?
    var contentIdx : Int?
    var indexPath : Int?
    var isSelectedFisrt : Bool?
}

class CelebrityTVCell: UITableViewCell {
    
    @IBOutlet weak var mainImgView : UIImageView!
    @IBOutlet weak var titleLbl: UILabel!
    @IBOutlet weak var subtitleLbl: UILabel!
    @IBOutlet weak var countLbl: UILabel!
    @IBOutlet weak var subscribeBtn : mySubscribeBtn!
    var delegate : SelectSenderDelegate?
    
    func configure(data : ChannelVODataChannelList, index : Int){
        self.setImgWithKF(url: data.thumbnailImg, imgView: mainImgView, defaultImg: #imageLiteral(resourceName: "aimg"))
        countLbl.text = (index+1).description
        titleLbl.text = data.name
        subscribeBtn.setSubscribeBtn(idx: data.channelID, isSubscribe: data.subscription)
        subscribeBtn.indexPath = index
        subscribeBtn.addTarget(self, action: #selector(subscribeAction(_:)), for: .touchUpInside)
        if selectedLang == .kor {
            subtitleLbl.text = "구독자 \(data.subscriptionCnt.description) · 게시물 \(data.spotCnt.description)"
        } else {
            subtitleLbl.text = "sub \(data.subscriptionCnt.description) · post \(data.spotCnt.description)"
        }
    }
    
    @objc func subscribeAction(_ sender : mySubscribeBtn){
        delegate?.tap(section: .first, seledtedId: sender.tag, sender: sender)
    }
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
        self.mainImgView.makeRounded(cornerRadius: 5)
        self.mainImgView.makeViewBorder(width: 0.5, color: #colorLiteral(red: 0.7529411765, green: 0.7529411765, blue: 0.7529411765, alpha: 1))
    }
    
}
