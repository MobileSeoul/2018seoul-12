//
//  SearchResultVC.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 17..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit

class SearchResultVC: UIViewController, UIGestureRecognizerDelegate {
   
    @IBOutlet weak var tableView : UITableView!
    var isChange : Bool? {
        didSet {
            tableView.reloadData()
        }
    }
    var searchResultData : SearchResultVOData?
    var searchTxt = ""
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        getSearchData(url: UrlPath.searchResult.getURL(searchTxt))
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.delegate = self
        tableView.dataSource = self
        tableView.tableFooterView = UIView(frame : .zero)
        setBackBtn()
        getSearchData(url: UrlPath.searchResult.getURL(searchTxt))
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        self.clearAllNotice()
    }
}

//MARK: - UITableViewDelegate, UITableViewDataSource
extension SearchResultVC : UITableViewDelegate, UITableViewDataSource  {
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 3
    }
    
    //섹션마다 3/4/4 이하로 테이블 뷰 뿌려줌
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if let searchResultData_ = searchResultData {
            if section == 0 {
                return searchResultData_.channel.count <= 3 ? searchResultData_.channel.count : 3
            } else if section == 1{
                return searchResultData_.place.count <= 4 ? searchResultData_.place.count :  4
            } else {
                return searchResultData_.event.count <= 4 ? searchResultData_.event.count :  4
            }
        }
        return 0
    }
    
    
    //headerSection View 만드는 것
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let header = tableView.dequeueReusableCell(withIdentifier: CategoryDetailSecondTVHeaderCell.reuseIdentifier) as! CategoryDetailSecondTVHeaderCell
        
        if selectedLang == .eng {
            header.morBtn.setTitle("MORE", for: .normal)
        }
        guard let searchResultData_ = searchResultData else {return nil}
        if section == 0  {
            if( searchResultData_.channel.count != 0) {
                if selectedLang == .kor {
                    header.titleLbl.text = "연예인 / 방송"
                } else {
                    header.titleLbl.text = "Celebrity / Broadcast"
                }
                
                header.morBtn.addTarget(self, action: #selector(goToCelebrityMore(_:)), for: .touchUpInside)
                return header
            } else {
                return nil
            }
        } else if section == 1 {
            if( searchResultData_.place.count != 0) {
                if selectedLang == .kor {
                    header.titleLbl.text = "장소"
                } else {
                    header.titleLbl.text = "Spot"
                }
                header.morBtn.addTarget(self, action: #selector(goToPlaceMore(_:)), for: .touchUpInside)
                return header
            } else {
                return nil
            }
        } else {
            if( searchResultData_.event.count != 0) {
                if selectedLang == .kor {
                    header.titleLbl.text = "이벤트"
                } else {
                    header.titleLbl.text = "Event"
                }
                header.morBtn.addTarget(self, action: #selector(goToEventMore(_:)), for: .touchUpInside)
                return header
            } else {
                return nil
            }
        }
    }
    
    //헤더뷰 높이
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        guard let searchResultData_ = searchResultData else {return 0}
        if section == 0  {
            return heightForHeaderInSection(arr: searchResultData_.channel)
        } else if section == 1 {
            return heightForHeaderInSection(arr: searchResultData_.place)
        } else {
            return heightForHeaderInSection(arr: searchResultData_.event)
        }
    }
    
    private func heightForHeaderInSection(arr : [Any]) -> CGFloat {
        if (arr.count > 0){
            return 62
        }
        return 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if indexPath.section == 0 {
            let cell = tableView.dequeueReusableCell(withIdentifier: BroadcastTVCell.reuseIdentifier) as! BroadcastTVCell
            if let channelData = searchResultData?.channel {
                cell.delegate = self
                cell.configure(data: channelData[indexPath.row], index : indexPath.row)
            }
            return cell
            
        }  else {
            let cell = tableView.dequeueReusableCell(withIdentifier: SearchResultTVCell.reuseIdentifier) as! SearchResultTVCell
            if indexPath.section == 1 {
                if let placeData = searchResultData?.place {
                    cell.configure(data: placeData[indexPath.row])
                }
            } else {
                if let eventData = searchResultData?.event {
                    cell.configure(data: eventData[indexPath.row])
                }
            }
            return cell
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let section = indexPath.section
        if section == 0 {
            if let channelData = searchResultData?.channel {
                self.goToCelebrityDetail(selectedIdx : channelData[indexPath.row].channelID)
            }
            
        } else if section == 1 {
            if let placeData = searchResultData?.place {
                self.goToPlaceDetailVC(selectedIdx : placeData[indexPath.row].spotID)
            }
        } else {
            if let eventData = searchResultData?.event {
                self.goToPlaceDetailVC(selectedIdx : eventData[indexPath.row].spotID, isPlace : false)
            }
        }
        tableView.deselectRow(at: indexPath, animated: false)
    }
    
}

//MARK: - 각 섹션의 더보기에 대한 함수들
extension SearchResultVC  {
    //연예인/방송 더보기
    @objc func goToCelebrityMore(_ sender : UIButton) {
        let mainStoryboard = Storyboard.shared().mainStoryboard
        if let searchResultMoreCelebrityVC = mainStoryboard.instantiateViewController(withIdentifier:SearchResultMoreCelebrityVC.reuseIdentifier) as? SearchResultMoreCelebrityVC {
            if selectedLang == .kor {
                searchResultMoreCelebrityVC.headerTitle = "연예인 / 방송"
            } else {
                searchResultMoreCelebrityVC.headerTitle = "Celebrity / Broadcast"
            }
            
            searchResultMoreCelebrityVC.searchString = searchTxt
            guard let navTitle = self.navigationItem.title else{return}
            if ((navTitle.count) < 10) {
                if selectedLang == .kor {
                    searchResultMoreCelebrityVC.navigationItem.title = "'\(navTitle)' 검색결과"
                } else {
                    searchResultMoreCelebrityVC.navigationItem.title = "'\(navTitle)' search result"
                }
            } else {
                if selectedLang == .kor {
                    searchResultMoreCelebrityVC.navigationItem.title = "'\(navTitle.prefix(9))...' 검색결과"
                } else {
                    searchResultMoreCelebrityVC.navigationItem.title = "'\(navTitle.prefix(9))...' search result"
                }
            }
            searchResultMoreCelebrityVC.searchData = searchResultData?.channel
            self.navigationController?.pushViewController(searchResultMoreCelebrityVC, animated: true)
        }
    }
    
    //장소 더보기
    @objc func goToPlaceMore(_ sender : UIButton){
        let mainStoryboard = Storyboard.shared().mainStoryboard
        if let searchResultMoreVC = mainStoryboard.instantiateViewController(withIdentifier:SearchResultMoreVC.reuseIdentifier) as? SearchResultMoreVC {
            
            guard let navTitle = self.navigationItem.title else{return}
            
            if ((navTitle.count) < 10) {
                if selectedLang == .kor {
                    searchResultMoreVC.navigationItem.title = "'\(navTitle)' 검색결과"
                } else {
                    searchResultMoreVC.navigationItem.title = "'\(navTitle)' search result"
                }
                
            } else {
                if selectedLang == .kor {
                    searchResultMoreVC.navigationItem.title = "'\(navTitle.prefix(9))...' 검색결과"
                } else {
                    searchResultMoreVC.navigationItem.title = "'\(navTitle.prefix(9))...' search result"
                }
                
            }
            searchResultMoreVC.searchData = searchResultData?.place
            searchResultMoreVC.searchTxt = self.searchTxt
            self.navigationController?.pushViewController(searchResultMoreVC, animated: true)
        }
    }
    
    //이벤트 더보기
    @objc func goToEventMore(_ sender : UIButton){
        let mainStoryboard = Storyboard.shared().mainStoryboard
        if let searchResultMorePlaceVC = mainStoryboard.instantiateViewController(withIdentifier:SearchResultMorePlaceVC.reuseIdentifier) as? SearchResultMorePlaceVC {
            
            if selectedLang == .kor {
                searchResultMorePlaceVC.headerTitle = "이벤트"
            } else {
                searchResultMorePlaceVC.headerTitle = "Event"
            }
            
            guard let navTitle = self.navigationItem.title else{return}
            if ((navTitle.count) < 10) {
                if selectedLang == .kor {
                    searchResultMorePlaceVC.navigationItem.title = "'\(navTitle)' 검색결과"
                } else {
                    searchResultMorePlaceVC.navigationItem.title = "'\(navTitle)' search result"
                }
                
            } else {
                if selectedLang == .kor {
                    searchResultMorePlaceVC.navigationItem.title = "'\(navTitle.prefix(9))...' 검색결과"
                } else {
                    searchResultMorePlaceVC.navigationItem.title = "'\(navTitle.prefix(9))...' search result"
                }
            }
            searchResultMorePlaceVC.searchData = searchResultData?.event
            self.navigationController?.pushViewController(searchResultMorePlaceVC, animated: true)
        }
    }
}

//MARK: - 구독
extension SearchResultVC : SelectSenderDelegate{
    func tap(section: Section, seledtedId: Int, sender: mySubscribeBtn) {
        if sender.isSelected {
            unsubscribe(url: UrlPath.channelSubscription.getURL(sender.contentIdx?.description), sender: sender)
        } else {
            let params = ["channel_id" : seledtedId]
            subscribe(url: UrlPath.channelSubscription.getURL(), params: params, sender: sender)
        }
    }
}

//MARK: - 통신
extension SearchResultVC {
    func getSearchData(url : String){
        self.pleaseWait()
        SearchResultService.shareInstance.getSearchResult(url: url,completion: { [weak self] (result) in
            guard let `self` = self else { return }
            self.clearAllNotice()
            switch result {
            case .networkSuccess(let searchResultData):
                self.searchResultData = searchResultData as? SearchResultVOData
                self.isChange = true
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
                self.searchResultData?.channel[sender.indexPath!].subscription = 1
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
                self.searchResultData?.channel[sender.indexPath!].subscription = 0
            case .networkFail :
                self.networkSimpleAlert()
            default :
                self.simpleAlert(title: "오류", message: "다시 시도해주세요")
                break
            }
        })
    }
}
