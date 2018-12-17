//
//  ReviewContainerVC.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 11..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit

class ReviewContainerVC: UIViewController, UIGestureRecognizerDelegate {

    @IBOutlet weak var writeReviewBtn: UIButton!
    @IBOutlet weak var containerView: UIView!
    var selectedIdx = 0
    var rating = 0.0
    private lazy var reviewVC: ReviewVC = {
        let storyboard = Storyboard.shared().mapStoryboard
        var viewController = storyboard.instantiateViewController(withIdentifier: ReviewVC.reuseIdentifier) as! ReviewVC
        viewController.rating = rating
        viewController.selectedIdx = selectedIdx
        return viewController
    }()
    
    override func viewDidLoad() {
        super.viewDidLoad()

        if selectedLang == .eng {
              writeReviewBtn.setImage(#imageLiteral(resourceName: "review_write_floating_eng"), for: .normal)
        }
        setBackBtn()
        initContainerView()
    }
    
    @IBAction func writeReviewAction(_ sender: Any) {
        if !isUserLogin() {
            goToLoginPage()
        } else {
            //리뷰 쓰기
            let mapStoryboard = Storyboard.shared().mapStoryboard
            if let reviewWriteVC = mapStoryboard.instantiateViewController(withIdentifier:ReviewWriteVC.reuseIdentifier) as? ReviewWriteVC {
                reviewWriteVC.selectedIdx = self.selectedIdx
                self.navigationController?.pushViewController(reviewWriteVC, animated: true)
            }
        }
    }
    func initContainerView(){
        addChildView(containerView: containerView, asChildViewController: reviewVC)
    }
}
