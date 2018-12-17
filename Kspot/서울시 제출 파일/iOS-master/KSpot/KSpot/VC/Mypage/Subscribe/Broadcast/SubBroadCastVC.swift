//
//  SubBroadCastVC.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 10..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit

class SubBroadCastVC: UIViewController {

    @IBOutlet weak var tableView: UITableView!
    var broadcastSubscriptionList : [UserSubcriptionVOBroadcast]?{
        didSet {
            tableView.reloadData()
        }
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.delegate = self
        tableView.dataSource = self
    }

}

//MARK: - UITableViewDelegate, UITableViewDataSource
extension SubBroadCastVC : UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if let broadcastSubscriptionList_ = broadcastSubscriptionList {
            return broadcastSubscriptionList_.count
        }
        return 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: SubBroadcastTVCell.reuseIdentifier) as! SubBroadcastTVCell
        if let broadcastSubscriptionList_ = broadcastSubscriptionList {
           cell.configure(data: broadcastSubscriptionList_[indexPath.row])
        }
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        if let broadcastSubscriptionList_ = broadcastSubscriptionList{
            self.goToCelebrityDetail(selectedIdx: broadcastSubscriptionList_[indexPath.row].channelId)
        }
        
    }
}
