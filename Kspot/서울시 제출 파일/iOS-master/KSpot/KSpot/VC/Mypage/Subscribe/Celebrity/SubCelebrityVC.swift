//
//  SubCelebrityVC.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 10..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit

class SubCelebrityVC: UIViewController {

    @IBOutlet weak var tableView: UITableView!
    var celebritySubscriptionList : [UserSubcriptionVOBroadcast]? {
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
extension SubCelebrityVC : UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if let celebritySubscriptionList_ = celebritySubscriptionList {
            return celebritySubscriptionList_.count
        }
        return 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: SubCelebrityTVCell.reuseIdentifier) as! SubCelebrityTVCell
        if let celebritySubscriptionList_ = celebritySubscriptionList {
            cell.configure(data: celebritySubscriptionList_[indexPath.row])
        }
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        if let celebritySubscriptionList_ = celebritySubscriptionList{
             self.goToCelebrityDetail(selectedIdx: celebritySubscriptionList_[indexPath.row].channelId)
        }
       
    }
}
