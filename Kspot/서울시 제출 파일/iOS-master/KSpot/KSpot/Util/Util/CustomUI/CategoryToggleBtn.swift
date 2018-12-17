//
//  CategoryToggleBtn.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 3..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit

class CategoryToggleBtn : UIButton {
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setBtnClickEvent()
        setDefaultBtn()
    }
    
    var anotherBtn : CategoryToggleBtn?
    var bottomLineView : UIView?
    
    func setBtnClickEvent() {
        self.addTarget(self, action: #selector(CategoryToggleBtn.touchBtn(_:)), for: UIControlEvents.touchUpInside)
    }
    
    @objc func touchBtn(_ sender: CategoryToggleBtn){
        sender.selected()
        sender.anotherBtn?.unSelected()
    }
    
    func setDefaultBtn(){
        self.isSelected = false
    }
    
    func setBtn(another : CategoryToggleBtn, bottomLine : UIView){
        self.anotherBtn = another
        self.bottomLineView = bottomLine
    }
}

extension CategoryToggleBtn{
    func selected(){
        self.setTitleColor(ColorChip.shared().mainColor, for: .normal)
        self.bottomLineView?.isHidden = false
        //self.isSelected = true
    }
    func unSelected(){
        self.setTitleColor(#colorLiteral(red: 0.1960784314, green: 0.1960784314, blue: 0.1960784314, alpha: 1), for: .normal)
        self.bottomLineView?.isHidden = true
        self.isSelected = false
    }
    
   
}
