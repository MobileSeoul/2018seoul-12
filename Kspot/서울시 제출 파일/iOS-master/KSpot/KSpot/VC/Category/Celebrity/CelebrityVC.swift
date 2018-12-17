//
//  CelebrityVC.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 2..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit

class CelebrityVC: UIViewController{
    
    @IBOutlet weak var tableView: UITableView!
    var delegate : SelectDelegate?
    var isChange : Bool? {
        didSet {
            tableView.reloadData()
        }
    }
    var celebrityList : [ChannelVODataChannelList]?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.delegate = self
        tableView.dataSource = self
        tableView.tableFooterView = UIView(frame : .zero)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        self.clearAllNotice()
    }
}

//MARK: - UITableViewDelegate, UITableViewDataSource
extension CelebrityVC : UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if let celebrityList_ = celebrityList {
            return celebrityList_.count
        }
        return 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: CelebrityTVCell.reuseIdentifier) as! CelebrityTVCell
        if let celebrityList_ = celebrityList {
           cell.delegate = self
           cell.configure(data: celebrityList_[indexPath.row], index : indexPath.row)
        }
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if let celebrityList_ = celebrityList {
            delegate?.tap(selected: celebrityList_[indexPath.row].channelID)
        }
    }
}

//MARK: - 구독하기
extension CelebrityVC : SelectSenderDelegate {
    func tap(section : Section, seledtedId : Int, sender : mySubscribeBtn){
        if !isUserLogin() {
            goToLoginPage()
        } else {
            let params = ["channel_id" : sender.contentIdx]
            if sender.isSelected {
                unsubscribe(url: UrlPath.channelSubscription.getURL(sender.contentIdx?.description), sender: sender)
            } else {
                subscribe(url: UrlPath.channelSubscription.getURL(), params: params, sender: sender)
            }
        }
    }
}

//MARK: - 통신
extension CelebrityVC {
    func subscribe(url : String, params : [String:Any], sender : mySubscribeBtn){
         self.pleaseWait()
        ChannelSubscribeService.shareInstance.subscribe(url: url, params : params, completion: { [weak self] (result) in
            guard let `self` = self else { return }
            self.clearAllNotice()
            switch result {
            case .networkSuccess(_):
                sender.isSelected = true
                self.celebrityList![sender.indexPath!].subscription = 1
            case .networkFail :
                self.networkSimpleAlert()
            default :
                self.simpleAlert(title: "오류", message: "다시 시도해주세요")
                break
            }
        })
    } //subscribe
    
    func unsubscribe(url : String, sender : mySubscribeBtn){
        self.pleaseWait()
        ChannelSubscribeService.shareInstance.unsubscribe(url: url, completion: { [weak self] (result) in
            guard let `self` = self else { return }
            self.clearAllNotice()
            switch result {
            case .networkSuccess(_):
                sender.isSelected = false
                self.celebrityList![sender.indexPath!].subscription = 0
            case .networkFail :
               self.networkSimpleAlert()
            default :
                self.simpleAlert(title: "오류", message: "다시 시도해주세요")
                break
            }
        })
    }
}
