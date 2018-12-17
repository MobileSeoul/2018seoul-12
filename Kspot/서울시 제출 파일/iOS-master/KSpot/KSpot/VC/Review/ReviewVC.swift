//
//  ReviewVC.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 11..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit
import MessageUI

class ReviewVC: UIViewController {
    @IBOutlet weak var ratingView: CosmosView!
    @IBOutlet weak var reviewLbl: UILabel!
    @IBOutlet weak var reviewCountLbl: UILabel!
    @IBOutlet weak var ratingLbl: UILabel!
    @IBOutlet weak var tableView: UITableView!
    var selectedIdx : Int = 0
    var rating : Double = 0.0
    var reviewData : ReviewVOData? {
        didSet {
            if let reviewData_ = reviewData{
                tableView.reloadData()
                if selectedLang == .kor {
                    reviewCountLbl.text = reviewData_.spotReview.reviewCnt.description+"개"

                } else {
                    reviewCountLbl.text = reviewData_.spotReview.reviewCnt.description
                }
                ratingLbl.text = reviewData_.spotReview.reviewScore.description
                ratingView.rating = reviewData_.spotReview.reviewScore
            }
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        getReviews(url: UrlPath.spot.getURL("\(selectedIdx)/review"))
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.delegate = self
        tableView.dataSource = self
        ratingView.settings.fillMode = .precise
        reviewLbl.text = selectedLang == .kor ? "리뷰" : "Review"
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        self.clearAllNotice()
    }
}


//MARK: - 신고
extension ReviewVC : SelectDelegate {
    func tap(selected : Int?) {
        
        if !isUserLogin() {
            goToLoginPage()
        } else {
            //신고하기
            var alertTitle : String = selectedLang == .kor ? "신고 사유를 선택해주세요" : "Please select a reason for reporting"
            var reportTitle1 : String = selectedLang == .kor ? "음란물" : "obscene"
            var reportTitle2 : String = selectedLang == .kor ? "사칭 및 사기" : "impersonation and fraud"
            var reportTitle3 : String = selectedLang == .kor ? "허위사실 유포" : "spread false information"
            var reportTitle4 : String = selectedLang == .kor ? "상업적 광고 및 판매" : "commercial advertisement and sale"
             var reportTitle5 : String = selectedLang == .kor ? "욕설 및 불쾌감을 주는 표현" : "swearing and offensive expression"
            var cancleTitle : String = selectedLang == .kor ? "취소" : "cancel"
        
            let alert = UIAlertController(title: alertTitle, message: nil, preferredStyle: .actionSheet)
            
            let report1 = UIAlertAction(title: reportTitle1, style: .default) { (re1) in
                reportAction(reportReason: re1.title!)
            }
            let report2 = UIAlertAction(title: reportTitle2, style: .default) { (re2) in
                reportAction(reportReason: re2.title!)
            }
            let report3 = UIAlertAction(title: reportTitle3, style: .default) { (re3) in
                reportAction(reportReason: re3.title!)
            }
            let report4 = UIAlertAction(title: reportTitle4, style: .default) { (re4) in
                reportAction(reportReason: re4.title!)
            }
            let report5 = UIAlertAction(title: reportTitle5, style: .default) { (re5) in
                reportAction(reportReason: re5.title!)
            }
            
            func reportAction(reportReason : String){
                guard let selectedMailId = selected else {return}
                sendMail(selectedId: selectedMailId, reason : reportReason)
            }
            
            let cancleAction = UIAlertAction(title: cancleTitle,style: .cancel)
            alert.addAction(report1)
            alert.addAction(report2)
            alert.addAction(report3)
            alert.addAction(report4)
            alert.addAction(report5)
            alert.addAction(cancleAction)
            present(alert, animated: true)
        } // else - 신고
    } //tap
}

//MARK: - 메일
extension ReviewVC : MFMailComposeViewControllerDelegate{
    func sendMail(selectedId : Int, reason : String){
        if MFMailComposeViewController.canSendMail() {
            let mail = MFMailComposeViewController()
            mail.mailComposeDelegate = self
            mail.setToRecipients(["rkdthd1234@naver.com"])
            let subjectTitle : String = selectedLang == .kor ? "신고 등록" : "report"
            let bodyTxt : String = selectedLang == .kor ?
                "<p>'ID Number. \(selectedId)'</p><p>신고 사유는 \(reason)입니다.</p>" : "<p>'ID Number. \(selectedId)'</p><p>reason for reporting is \(reason).</p>"
            mail.setSubject(subjectTitle)
            mail.setMessageBody(bodyTxt, isHTML: true)
            self.present(mail, animated: true)
        } else {
            let alertTitle = selectedLang == .kor ? "실패" : "Error"
            let alertMsg = selectedLang == .kor ? "신고 메일을 보낼 수 없습니다. 다시 시도해주세요" : "Please try again"
            self.simpleAlert(title: alertTitle, message: alertMsg)
        }
    }
    func mailComposeController(_ controller: MFMailComposeViewController, didFinishWith result: MFMailComposeResult, error: Error?) {
        controller.dismiss(animated: true)
    }
    
}

//MARK: - UITableViewDelegate,UITableViewDataSource
extension ReviewVC:UITableViewDelegate,UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if let reviewData_ = reviewData{
            return reviewData_.reviews.count
        }
        return 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell
    {
        let cell = tableView.dequeueReusableCell(withIdentifier: ReviewNoImgTVCell.reuseIdentifier) as! ReviewNoImgTVCell
        if let reviewData_ = reviewData {
            if reviewData_.reviews[indexPath.row].img == "" {
                let cell = tableView.dequeueReusableCell(withIdentifier: ReviewNoImgTVCell.reuseIdentifier) as! ReviewNoImgTVCell
                cell.delegate = self
                cell.configure(data : reviewData_.reviews[indexPath.row])
                return cell
            } else {
                let cell = tableView.dequeueReusableCell(withIdentifier: ReviewImgTVCell.reuseIdentifier) as! ReviewImgTVCell
                cell.delegate = self
                cell.configure(data : reviewData_.reviews[indexPath.row])
                return cell
            }
        }
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath)
    {
        tableView.deselectRow(at: indexPath, animated: true)
        
    }
}

//MARK: - 통신
extension ReviewVC {
    func getReviews(url : String){
        self.pleaseWait()
        ReviewService.shareInstance.getReviewData(url: url,completion: { [weak self] (result) in
            guard let `self` = self else { return }
             self.clearAllNotice()
            switch result {
            case .networkSuccess(let reviewData):
                self.reviewData = reviewData as? ReviewVOData
            case .networkFail :
                self.networkSimpleAlert()
            default :
                self.simpleAlert(title: "오류", message: "다시 시도해주세요")
                break
            }
        })
    }
}
