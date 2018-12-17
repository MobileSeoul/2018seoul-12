//
//  MapFilterView.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 3..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit

class MapFilterView: UIView {

    var isGoogle : Bool = true {
        didSet {
            if isGoogle {
                //거리 레이블 - 활성화
                leftBtn.isEnabled = true
                rightBtn.isEnabled = true
                leftBtn.setTitleColor(#colorLiteral(red: 0.4392156863, green: 0.4392156863, blue: 0.4392156863, alpha: 1), for: .normal)
                rightBtn.setTitleColor(#colorLiteral(red: 0.4392156863, green: 0.4392156863, blue: 0.4392156863, alpha: 1), for: .normal)
                distanceLbl.textColor = ColorChip.shared().mainColor
                
                //소팅 버튼 - 비활성화
                popularBtn.isEnabled = false
                recentBtn.isEnabled = false
                popularBtn.setTitleColor(#colorLiteral(red: 0.9176470588, green: 0.9176470588, blue: 0.9176470588, alpha: 1), for: .normal)
                recentBtn.setTitleColor(#colorLiteral(red: 0.9176470588, green: 0.9176470588, blue: 0.9176470588, alpha: 1), for: .normal)
                
            } else {
                //거리 레이블 - 비활성화
                leftBtn.isEnabled = false
                rightBtn.isEnabled = false
                leftBtn.setTitleColor(#colorLiteral(red: 0.9176470588, green: 0.9176470588, blue: 0.9176470588, alpha: 1), for: .normal)
                rightBtn.setTitleColor(#colorLiteral(red: 0.9176470588, green: 0.9176470588, blue: 0.9176470588, alpha: 1), for: .normal)
                distanceLbl.textColor = #colorLiteral(red: 0.9176470588, green: 0.9176470588, blue: 0.9176470588, alpha: 1)
                
                //소팅 버튼 - 활성화
                popularBtn.isEnabled = true
                recentBtn.isEnabled = true
              
            }
        }
    }
    
    @IBOutlet weak var cancleBtn: UIButton!
    @IBOutlet weak var popularBtn: FilterToggleBtn!
    @IBOutlet weak var recentBtn: FilterToggleBtn!
    @IBOutlet weak var leftBtn: UIButton!
    @IBOutlet weak var rightBtn: UIButton!
    @IBOutlet weak var distanceLbl: UILabel!
    @IBOutlet weak var restaurantBtn: UIButton!
    @IBOutlet weak var cafeBtn: UIButton!
    @IBOutlet weak var hotplaceBtn: UIButton!
    @IBOutlet weak var eventBtn: UIButton!
    @IBOutlet weak var etcBtn: UIButton!
    @IBOutlet weak var okBtn: UIButton!
    
    class func instanceFromNib() -> MapFilterView {
    let view = UINib(nibName: "MapFilterView", bundle: nil).instantiate(withOwner: nil, options: nil)[0] as! MapFilterView
        return view
    }

}
