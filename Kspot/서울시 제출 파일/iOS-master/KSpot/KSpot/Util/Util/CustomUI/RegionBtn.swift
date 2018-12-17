//
//  RegionBtn.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 2..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit

class RegionBtn : UIButton {
    //이 토글 버튼이 초기화될 때 실행되는 구문. 여기서 setBtnClickEvent, setDefaultBtn이라는 함수를 실행 시킴
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setBtnClickEvent()
    }
    var delegate : SelectRegionDelegate?
    var region : Region?
    
    func setBtnClickEvent() {
        self.addTarget(self, action: #selector(RegionBtn.touchBtn(_:)), for: UIControlEvents.touchUpInside)
    }
    
    @objc func touchBtn(_ sender: RegionBtn){
        if let region_ = sender.region {
            delegate?.tap(region_)
        }
    }
}
