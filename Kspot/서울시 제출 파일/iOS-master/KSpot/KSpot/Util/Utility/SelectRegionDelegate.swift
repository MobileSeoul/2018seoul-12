//
//  SelectRegionDelegate.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 2..
//  Copyright © 2018년 강수진. All rights reserved.
//

import Foundation
import UIKit

protocol SelectRegionDelegate {
    func tap(_ tag : Region)
}

protocol SelectDelegate {
    func tap(selected : Int?)
}

protocol SelectGoogleDelegate {
    func tap(selectedGoogle : MyPlace?)
}

protocol SelectSectionDelegate {
    func tap(section : Section, seledtedId : Int)
}


protocol SelectSenderDelegate {
    func tap(section : Section, seledtedId : Int, sender : mySubscribeBtn)

}



enum Section {
    case first
    case second
    case third
    case forth
}
