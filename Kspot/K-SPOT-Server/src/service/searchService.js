const hangul = require('hangul-js');
const channelDao = require('../dao/channelDao');
const spotDao = require('../dao/spotDao');
const s3Location = require('../../config/s3Location').location

module.exports = {

    getBestKeyword : async function(flag) {

        let celebrity, broadcast, event;

        if (flag == 0) {
            celebrity = await channelDao.selectChannelKeywordKor(0, 2);
            broadcast = await channelDao.selectChannelKeywordKor(1, 3);
            event = await spotDao.selectBestEventKor(2);
        }
        else if (flag == 1) {
            celebrity = await channelDao.selectChannelKeywordEng(0, 2);
            broadcast = await channelDao.selectChannelKeywordEng(1, 3);
            event = await spotDao.selectBestEventEng(2);
        }

        var result = {
            celebrity : celebrity,
            broadcast : broadcast,
            event : event
        }

        return result;
    },


    // 검색 결과창
    getChannelListByKeyword : async function(userId, keyword, isKorean) {

        let result = [];
        var channelSet = new Set();
        let channelList = await channelDao.selectChannelName();

        if (isKorean){

            let searcher = new hangul.Searcher(keyword);

            for (i in channelList){
                if (searcher.search(channelList[i].kor_name) >= 0){

                    var channelId = channelList[i].channel_id;

                    if (channelList[i].is_member != 0) {
                        channelId = channelList[i].is_member
                    }
                    channelSet.add(channelId)
                }
            }
        }
        else {

            for (i in channelList){
                if(((channelList[i].eng_name).toUpperCase()).search(keyword.toUpperCase()) >= 0){
    
                    var channelId = channelList[i].channel_id;
    
                    if (channelList[i].is_member != 0) {
                        channelId = channelList[i].is_member
                    }
                    channelSet.add(channelId)
                }
            }
    
        }
        
        channelList = Array.from(channelSet)
        channelList.sort()

        for(var i in channelList){
            if (isKorean){

                let channelKor = await channelDao.selectChannelByKeywordKor(userId, channelList[i]);
            
                if (channelKor[0].fk_sub_user_user_id == null) {
                    channelKor[0].subscription = 0;
                } else {
                    channelKor[0].subscription = 1;
                }
                delete channelKor[0].fk_sub_user_user_id;
    
                channelKor[0].thumbnail_img = channelKor[0].thumbnail_img? s3Location + channelKor[0].thumbnail_img : ''
                result.push(channelKor[0]) 

            } else {

                let channelEng = await channelDao.selectChannelByKeywordEng(userId, channelList[i]);

                for(j in channelEng) {
    
                    if (channelEng[j].fk_sub_user_user_id == null) {
                        channelEng[j].subscription = 0;
                    } else {
                        channelEng[j].subscription = 1;
                    }
                    delete channelEng[j].fk_sub_user_user_id;
        
                    channelEng[j].thumbnail_img = channelEng[j].thumbnail_img? s3Location + channelEng[j].thumbnail_img : ''
                    result.push(channelEng[j]) 
    
                }
            }
        }
        
        return result;
    },
    getSpotListByKeyword : async function(isEvent, keyword, isKorean) {

        let result = [];


        if (isKorean){
            
            let searcher = new hangul.Searcher(keyword);

            let spotKor = await spotDao.selectSpotListByKeywordKor(isEvent);

            for (i in spotKor){
                if(searcher.search(spotKor[i].name) >= 0){
                    spotKor[i].img = spotKor[i].img? s3Location + spotKor[i].img : ''
                    result.push(spotKor[i])
                }
            }

        }else {

            let spotEng = await spotDao.selectSpotListByKeywordEng(isEvent);

            for (i in spotEng){
                if(((spotEng[i].name).toUpperCase()).search(keyword.toUpperCase()) >= 0){
                    spotEng[i].img = spotEng[i].img? s3Location + spotEng[i].img : ''
                    result.push(spotEng[i])
                }
            }
        }

        return result;
    },

    // 검색결과창 필터
    getSearchSpotFilterByKeyword : async function(keyword, order, is_food, is_cafe, is_sights, is_birth, is_celebrate, is_event_etc, is_etc) {

        let result = [];
        let type = [];

        // 맛집
        if (is_food == 1) {
            type.push(0)
        }
        // 카페
        if (is_cafe == 1) {
            type.push(1)
        }
        // 명소
        if (is_sights == 1) {
            type.push(2)
        }
        // 생일 이벤트
        if (is_birth == 1) {
            type.push(3)
        }
        // 기념 이벤트
        if (is_celebrate == 1) {
            type.push(4)
        }
        // 기타 이벤트
        if (is_event_etc == 1) {
            type.push(5)
        }
        // 기타
        if (is_etc == 1) {
            type.push(6)
        }

        if(type.length == 0){
            type.push(-1)
        }

        // Kor
        let searcher = new hangul.Searcher(keyword);

        let spotKor = await spotDao.selectSearchSpotFilterByKeywordKor(order, type);

        for (i in spotKor){
            if(searcher.search(spotKor[i].name) >= 0){
                spotKor[i].img = spotKor[i].img? s3Location + spotKor[i].img : ''
                result.push(spotKor[i])
            }
        }

        // Eng
        let spotEng = await spotDao.selectSearchSpotFilterByKeywordEng(order, type);

        for (i in spotEng){
            if(((spotEng[i].name).toUpperCase()).search(keyword.toUpperCase()) >= 0){
                spotEng[i].img = spotEng[i].img? s3Location + spotEng[i].img : ''
                result.push(spotEng[i])
            }
        }

        return result;
    },

}
