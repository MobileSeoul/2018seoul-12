//
//  PlaceFilterView.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 17..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit

class PlaceFilterView: UIView {

    @IBOutlet weak var cancleBtn: UIButton!
    @IBOutlet weak var popularBtn: FilterToggleBtn!
    @IBOutlet weak var recentBtn: FilterToggleBtn!
    @IBOutlet weak var restaurantBtn: UIButton!
    @IBOutlet weak var cafeBtn: UIButton!
    @IBOutlet weak var hotplaceBtn: UIButton!
    @IBOutlet weak var etcBtn: UIButton!
    @IBOutlet weak var okBtn: UIButton!
    
    class func instanceFromNib() -> PlaceFilterView {
        let view = UINib(nibName: "PlaceFilterView", bundle: nil).instantiate(withOwner: nil, options: nil)[0] as! PlaceFilterView
        
        return view
    }

}


