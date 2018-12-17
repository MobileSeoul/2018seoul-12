//
//  SearchResultMoreCelebrityVC.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 17..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit

class SearchResultMoreCelebrityVC: UIViewController, UIGestureRecognizerDelegate {
    
    @IBOutlet weak var tableView : UITableView!
    var searchData : [ChannelVODataChannelList]?
    var headerTitle = ""
    var searchString = ""
    
    
    override func viewWillAppear(_ animated: Bool) {
        getSearchData(url: UrlPath.searchResult.getURL(searchString))
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.delegate = self
        tableView.dataSource = self
        tableView.tableFooterView = UIView(frame : .zero)
        setBackBtn()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        self.clearAllNotice()
    }
 
}


//MARK: - UITableViewDelegate, UITableViewDataSource
extension SearchResultMoreCelebrityVC : UITableViewDelegate, UITableViewDataSource  {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if let searchData_ = searchData {
            return searchData_.count
        }
        return 0
    }

    //headerSection View 만드는 것
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let header = tableView.dequeueReusableCell(withIdentifier: "SearchResultHeaderCell2") as! CategoryDetailSecondTVHeaderCell
        header.titleLbl.text = headerTitle
        return header
    }
    
    //헤더뷰 높이
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 62
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "BroadcastTVCell2") as! BroadcastTVCell
        if let searchData_ = searchData {
            cell.delegate = self
            cell.configure(data: searchData_[indexPath.row], index: indexPath.row)
        }
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if let searchData_ = searchData{
            self.goToCelebrityDetail(selectedIdx: searchData_[indexPath.row].channelID)
        }
        tableView.deselectRow(at: indexPath, animated: false)
    }
    
}

//구독
extension SearchResultMoreCelebrityVC : SelectSenderDelegate {
    func tap(section: Section, seledtedId: Int, sender: mySubscribeBtn) {
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
extension SearchResultMoreCelebrityVC {
    func getSearchData(url : String){
        self.pleaseWait()
        SearchResultService.shareInstance.getSearchResult(url: url,completion: { [weak self] (result) in
            guard let `self` = self else { return }
            self.clearAllNotice()
            switch result {
            case .networkSuccess(let searchResultData):
                let searchResultData_ = searchResultData as? SearchResultVOData
                self.searchData = searchResultData_?.channel
                self.tableView.reloadData()
            case .networkFail :
                self.networkSimpleAlert()
            default :
                self.simpleAlert(title: "오류", message: "다시 시도해주세요")
                break
            }
        })
    }
    
    func subscribe(url : String, params : [String:Any], sender : mySubscribeBtn){
        self.pleaseWait()
        ChannelSubscribeService.shareInstance.subscribe(url: url, params : params, completion: { [weak self] (result) in
            guard let `self` = self else { return }
            self.clearAllNotice()
            switch result {
            case .networkSuccess(_):
                sender.isSelected = true
                self.searchData![sender.indexPath!].subscription = 1
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
                self.searchData![sender.indexPath!].subscription = 0
            case .networkFail :
                self.networkSimpleAlert()
            default :
                self.simpleAlert(title: "오류", message: "다시 시도해주세요")
                break
            }
        })
    }
}

