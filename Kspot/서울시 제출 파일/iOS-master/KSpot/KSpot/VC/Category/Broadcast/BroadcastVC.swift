//
//  BroadcastVC.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 2..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit

class BroadcastVC: UIViewController {

    @IBOutlet weak var tableView: UITableView!
    var delegate : SelectDelegate?
    var broadcastList : [ChannelVODataChannelList]? 
    var isChange : Bool? {
        didSet {
            tableView.reloadData()
        }
    }
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
extension BroadcastVC : UITableViewDelegate, UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if let broadcastList_ = broadcastList {
            return broadcastList_.count
        }
        return 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: BroadcastTVCell.reuseIdentifier) as! BroadcastTVCell
        if let broadcastList_ = broadcastList {
            cell.configure(data: broadcastList_[indexPath.row], index : indexPath.row)
             //cell.delegate = parent as? SelectSenderDelegate
            cell.delegate = self
        }
        return cell
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if let broadcastList_ = broadcastList {
            delegate?.tap(selected: broadcastList_[indexPath.row].channelID)
        }
    }
}

//MARK: - 구독
extension BroadcastVC : SelectSenderDelegate {
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
extension BroadcastVC {
    func subscribe(url : String, params : [String:Any], sender : mySubscribeBtn){
         self.pleaseWait()
        ChannelSubscribeService.shareInstance.subscribe(url: url, params : params, completion: { [weak self] (result) in
            guard let `self` = self else { return }
             self.clearAllNotice()
            switch result {
            case .networkSuccess(_):
               
                sender.isSelected = true
                self.broadcastList![sender.indexPath!].subscription = 1
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
                self.broadcastList![sender.indexPath!].subscription = 0
            case .networkFail :
                self.networkSimpleAlert()
            default :
                self.simpleAlert(title: "오류", message: "다시 시도해주세요")
                break
            }
        })
    }
}
