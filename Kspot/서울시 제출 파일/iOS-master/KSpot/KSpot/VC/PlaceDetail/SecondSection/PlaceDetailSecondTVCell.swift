//
//  PlaceDetailSecondTVCell.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 4..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit

class PlaceDetailSecondTVCell: UITableViewCell {

    @IBOutlet weak var reviewLbl: UILabel!
    @IBOutlet weak var countLbl: UILabel!
    @IBOutlet weak var ratingLbl: UILabel!
    @IBOutlet weak var moreBtn: UIButton!
    @IBOutlet weak var ratingView: CosmosView!
    @IBOutlet weak var collectionView: UICollectionView!
    var finalOffset : CGFloat = 0
    var startOffset  : CGFloat = 0
    var currentIdx = 0
    var delegate : SelectSectionDelegate?
    var reviewData : [PlaceDetailVODataReview]? {
        didSet {
            if let reviewData_ = reviewData {
                if selectedLang == .kor {
                   countLbl.text = reviewData_.count.description+"개"
                    reviewLbl.text = "리뷰"
                    moreBtn.setTitle("모두보기", for: .normal)
                } else {
                     countLbl.text = reviewData_.count.description
                     reviewLbl.text = "Review"
                    moreBtn.setTitle("view all", for: .normal)
                }
               
            }
            collectionView.reloadData()
        }
    }
  
    
    @IBAction func showAllAction(_ sender: Any) {
        delegate?.tap(section: .second, seledtedId: 1)
    }
    
   
    func configure(reviewScore : Double){
        ratingLbl.text = reviewScore.description
        ratingView.rating = reviewScore
    }
  
    override func awakeFromNib() {
        super.awakeFromNib()
        self.collectionView.delegate = self
        self.collectionView.dataSource = self
        ratingView.settings.fillMode = .precise
        
        let ratingLblInt = Double(ratingLbl.text!)
        
        if let ratingLblInt_ = ratingLblInt {
            ratingView.rating = ratingLblInt_
        }
    }    
   
}

//MARK: -UICollectionViewDataSource, UICollectionViewDelegate
extension PlaceDetailSecondTVCell : UICollectionViewDataSource, UICollectionViewDelegate {
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        if let reviewData_ = reviewData {
            return reviewData_.count
        }
        return 0
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        
        if let cell: PlaceDetailSecondCVCell = collectionView.dequeueReusableCell(withReuseIdentifier: PlaceDetailSecondCVCell.reuseIdentifier, for: indexPath) as? PlaceDetailSecondCVCell
        {
            if let reviewData_ = reviewData {
                cell.configure(data : reviewData_[indexPath.row])
            }
            return cell
        }
        return UICollectionViewCell()
    }

}

//MARK: - UICollectionViewDelegateFlowLayout
extension PlaceDetailSecondTVCell: UICollectionViewDelegateFlowLayout {
    //section내의
    //-간격 위아래
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
        return 10
    }
    //-간격 왼쪽오른쪽
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumInteritemSpacingForSectionAt section: Int) -> CGFloat {
        return 0
    }
    
    //cell size
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CGSize(width: (340/375)*window!.frame.width, height: 206)
    }
}

//MARK: - 컬렉션뷰 드래깅
extension PlaceDetailSecondTVCell : UIScrollViewDelegate {
    private func indexOfMajorCell(direction : Direction) -> Int {
        var index = 0
        switch direction {
        case .right :
            index = currentIdx + 1
        case .left :
            index = currentIdx - 1
        }
        let numberOfItems = collectionView.numberOfItems(inSection: 0)
        let safeIndex = max(0, min(numberOfItems - 1, index))
        currentIdx = safeIndex
        return safeIndex
    }
    
    func scrollViewWillBeginDragging(_ scrollView: UIScrollView) {
        startOffset = collectionView.contentOffset.x
    }
    
    func scrollViewWillEndDragging(_ scrollView: UIScrollView, withVelocity velocity: CGPoint, targetContentOffset: UnsafeMutablePointer<CGPoint>) {
        
        finalOffset = collectionView.contentOffset.x
        targetContentOffset.pointee = scrollView.contentOffset
        if finalOffset > startOffset {
            //뒤로 넘기기
            let majorIdx = indexOfMajorCell(direction: .right)
            let indexPath = IndexPath(row: majorIdx, section: 0)
            collectionView.scrollToItem(at: indexPath, at: .centeredHorizontally, animated: true)
        } else if finalOffset < startOffset {
            //앞으로 가기
            let majorIdx = indexOfMajorCell(direction: .left)
            let indexPath = IndexPath(row: majorIdx, section: 0)
            collectionView.scrollToItem(at: indexPath, at: .centeredHorizontally, animated: true)
        } else {
            print("둘다 아님")
        }
    }
    
}

