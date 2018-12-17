//
//  MainThirdTVCell.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 2..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit

class MainThirdTVCell: UITableViewCell {
    
    @IBOutlet weak var collectionView: UICollectionView!
    @IBOutlet weak var moreBtn: UIButton!
    @IBOutlet weak var regularLbl: UILabel!
    @IBOutlet weak var boldLbl: UILabel!
    var isPlace : Bool = true
    var finalOffset : CGFloat = 0
    var startOffset  : CGFloat = 0
    var currentIdx = 0
    var delegate : SelectSectionDelegate?
    var popularPlaceData : [MainVODataMain]? {
        didSet {
            collectionView.reloadData()
        }
    }
    
    func configure(section : Int){
        //장소 셀 설정
        if section == 2 {
            isPlace = true
            if selectedLang == .kor {
                regularLbl.text = "인기 장소"
                boldLbl.text = "BEST 10"
                
            } else {
                regularLbl.text = "Hot place"
                boldLbl.text = "BEST 10"
            }
        }
        //이벤트 셀 설정
        else {
            isPlace = false
            if selectedLang == .kor {
                regularLbl.text = "이번주"
                boldLbl.text = "NEW EVENT 5"
                moreBtn.setTitle("더보기", for: .normal)
            } else {
                regularLbl.text = ""
                boldLbl.text = "NEW EVENT 5"
                moreBtn.setTitle("MORE", for: .normal)
            }
        }
    }
    
   
    //더보기 버튼에 대한 액션 -> selectedId : -1
    @objc func tap(_ sender : UIButton){
        delegate?.tap(section: .forth, seledtedId: -1)
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        self.collectionView.delegate = self
        self.collectionView.dataSource = self
        moreBtn.addTarget(self, action: #selector(tap(_:)), for: .touchUpInside)
    }
}

//MARK: - UICollectionViewDataSource, UICollectionViewDelegate
extension MainThirdTVCell : UICollectionViewDataSource, UICollectionViewDelegate {
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        if let popularPlaceData_ = popularPlaceData{
            return popularPlaceData_.count
        }
        return 0
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        
        if let cell: MainThirdCVCell = collectionView.dequeueReusableCell(withReuseIdentifier: MainThirdCVCell.reuseIdentifier, for: indexPath) as? MainThirdCVCell
        {
            if let popularPlaceData_ = popularPlaceData {
                cell.configure(data: popularPlaceData_[indexPath.row])
            }
            return cell
        }
        return UICollectionViewCell()
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        if let popularPlaceData_ = popularPlaceData {
            if isPlace == true {
                delegate?.tap(section: .third, seledtedId: popularPlaceData_[indexPath.row].spotID)
            } else {
                delegate?.tap(section: .forth, seledtedId: popularPlaceData_[indexPath.row].spotID)
            }
        }
    }
}

//MARK: - UICollectionViewDelegateFlowLayout
extension MainThirdTVCell: UICollectionViewDelegateFlowLayout {
    //section내의
    //-간격 위아래
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
        return 16
    }
    //-간격 왼쪽오른쪽
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumInteritemSpacingForSectionAt section: Int) -> CGFloat {
        return 0
    }
    
    //cell size
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CGSize(width: (252/375)*window!.frame.width, height: 262)
    }
    
    //collectionView inset
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForSectionAt section: Int) -> UIEdgeInsets {
        return UIEdgeInsetsMake(0, 33, 0, 33)
    }
}

//MARK: - 컬렉션뷰 드래깅
extension MainThirdTVCell : UIScrollViewDelegate{
    /**
     현재 메인셀의 인덱스를 구하는 함수
     */
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

