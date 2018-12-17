//
//  MainFirstTVCell.swift
//  KSpot
//
//  Created by 강수진 on 2018. 8. 31..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit
import ImageSlideshow

class MainFirstTVCell: UITableViewCell {

    @IBOutlet var slideshow: ImageSlideshow!
    var imageSource : [InputSource] = []
    var delegate : SelectSectionDelegate?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        slideshow.slideshowInterval = 5.0
        slideshow.pageIndicatorPosition = .init(horizontal: .center, vertical: .customUnder(padding: 30))
        slideshow.contentScaleMode = UIViewContentMode.scaleAspectFill
        //slideshow.contentScaleMode = UIViewContentMode.scaleAspectFit
        
        let pageControl = UIPageControl()
        pageControl.currentPageIndicatorTintColor = ColorChip.shared().mainColor
        pageControl.pageIndicatorTintColor = #colorLiteral(red: 0.8784313725, green: 0.8784313725, blue: 0.8784313725, alpha: 1)
        slideshow.pageIndicator = pageControl
        
        // optional way to show activity indicator during image load (skipping the line will show no activity indicator)
        slideshow.activityIndicator = DefaultActivityIndicator()
        slideshow.currentPageChanged = { page in
            //print("current page:", page)
        }
        
        // can be used with other sample sources as `afNetworkingSource`, `alamofireSource` or `sdWebImageSource` or `kingfisherSource`
        slideshow.setImageInputs(imageSource)
        
        let recognizer = UITapGestureRecognizer(target: self, action: #selector(MainFirstTVCell.didTap))
        slideshow.addGestureRecognizer(recognizer)
    }
    
    //delegate
    @objc func didTap() {
        delegate?.tap(section: .first, seledtedId: slideshow.currentPage-1)
    }
}


