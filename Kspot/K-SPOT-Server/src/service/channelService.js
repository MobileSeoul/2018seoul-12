const channelDao = require('../dao/channelDao');
const spotDao = require('../dao/spotDao');
const s3Location = require('../../config/s3Location').location

module.exports = {

    

    getChannelList: async function (userId, flag) {

        // 채널의 목록 보기
        let channelCelebrityList = await channelDao.selectChannelList(userId, 0);
        let channelBroadcastList = await channelDao.selectChannelList(userId, 1);

        for (var i in channelCelebrityList) {

            // 섬네일 이미지 url
            channelCelebrityList[i].thumbnail_img = channelCelebrityList[i].thumbnail_img? s3Location + channelCelebrityList[i].thumbnail_img : ''

            // 연예인 구독 여부 체크
            if (channelCelebrityList[i].fk_sub_user_user_id == null) {
                channelCelebrityList[i].subscription = 0;
            } else {
                channelCelebrityList[i].subscription = 1;
            }
            delete channelCelebrityList[i].fk_sub_user_user_id;

            // Kor or Eng
            if (flag == 0) {
                channelCelebrityList[i].name = channelCelebrityList[i].kor_name;
            } else if (flag == 1) {
                channelCelebrityList[i].name = channelCelebrityList[i].eng_name;
            }
            delete channelCelebrityList[i].kor_name;
            delete channelCelebrityList[i].eng_name;
        }

        for (var i in channelBroadcastList) {

            // 섬네일 이미지 url
            channelBroadcastList[i].thumbnail_img = channelBroadcastList[i].thumbnail_img? s3Location + channelBroadcastList[i].thumbnail_img : ''

            // 방송 구독 여부 체크
            if (channelBroadcastList[i].fk_sub_user_user_id == null) {
                channelBroadcastList[i].subscription = 0;
            } else {
                channelBroadcastList[i].subscription = 1;
            }
            delete channelBroadcastList[i].fk_sub_user_user_id;

            // Kor or Eng
            if (flag == 0) {
                channelBroadcastList[i].name = channelBroadcastList[i].kor_name;
            } else if (flag == 1) {
                channelBroadcastList[i].name = channelBroadcastList[i].eng_name;
            }
            delete channelBroadcastList[i].kor_name;
            delete channelBroadcastList[i].eng_name;
        }

        var result = {
            channel_celebrity_list: channelCelebrityList,
            channel_broadcast_list: channelBroadcastList
        }

        return result;
    },
    getChannelDetail: async function (userId, channelId, flag) {

        // 멤버인지 아닌지 검사
        let checkIsMember = await channelDao.selectChannelForCheckIsMember(channelId)

        if (checkIsMember.length == 0) {
            throw new Error('Wrong Input')
        }
        else if (checkIsMember[0].is_member != 0) {
            throw new Error('Wrong Input')
        }

        // 채널의 상세 정보 보기
        let channelDetail = await channelDao.selectChannelDetail(userId, channelId);

        // 이미지 url
        channelDetail[0].thumbnail_img = channelDetail[0].thumbnail_img? s3Location + channelDetail[0].thumbnail_img : ''
        channelDetail[0].background_img = channelDetail[0].background_img? s3Location + channelDetail[0].background_img : ''

        // 구독 여부 체크
        if (channelDetail[0].fk_sub_user_user_id == null) {
            channelDetail[0].subscription = 0;
        } else {
            channelDetail[0].subscription = 1;
        }

        delete channelDetail[0].fk_sub_user_user_id;
        

        // 채널과 관련된 스팟 정보 보기 (추천 스팟)
        let placeRecommendedByChannel = await spotDao.selectPlaceRecommendedByChannel(channelId);

        // 채널과 관련된 스팟 정보 보기 (최신 장소/이벤트)
        let placeRelatedChannel;
        let eventRelatedChannel;

        if (flag == 0) {
            channelDetail[0].name = channelDetail[0].kor_name
            channelDetail[0].company = channelDetail[0].kor_company

            for(i in placeRecommendedByChannel){
                placeRecommendedByChannel[i].name = placeRecommendedByChannel[i].kor_name
                delete placeRecommendedByChannel[i].kor_name;
                delete placeRecommendedByChannel[i].eng_name;
            }

            placeRelatedChannel = await spotDao.selectSpotRelatedChannelKor(channelId, 0);
            eventRelatedChannel = await spotDao.selectSpotRelatedChannelKor(channelId, 1);
        }
        else if (flag == 1) {
            channelDetail[0].name = channelDetail[0].eng_name
            channelDetail[0].company = channelDetail[0].eng_company

            for(i in placeRecommendedByChannel){
                placeRecommendedByChannel[i].name = placeRecommendedByChannel[i].eng_name
                delete placeRecommendedByChannel[i].kor_name;
                delete placeRecommendedByChannel[i].eng_name;
            }

            placeRelatedChannel = await spotDao.selectSpotRelatedChannelEng(channelId, 0);
            eventRelatedChannel = await spotDao.selectSpotRelatedChannelEng(channelId, 1);
        }
        delete channelDetail[0].kor_name;
        delete channelDetail[0].eng_name;
        delete channelDetail[0].kor_company;
        delete channelDetail[0].eng_company;

        // 이미지 url
        for (var i in placeRecommendedByChannel) {
            placeRecommendedByChannel[i].img = placeRecommendedByChannel[i].img ? s3Location + placeRecommendedByChannel[i].img : '';
        }
        for (var i in placeRelatedChannel) {
            placeRelatedChannel[i].img = placeRelatedChannel[i].img ? s3Location + placeRelatedChannel[i].img : '';
        }
        for (var i in eventRelatedChannel) {
            eventRelatedChannel[i].img = eventRelatedChannel[i].img ? s3Location + eventRelatedChannel[i].img : '';
        }

        var result = {
            channel_info: channelDetail[0],
            place_recommended_by_channel: placeRecommendedByChannel,
            place_related_channel: placeRelatedChannel,
            event_related_channel: eventRelatedChannel
        }

        return result;
    },
    addSubscriptionChannel: async function (userId, channelId) {

        // 유저의 채널 구독정보 삽입
        await channelDao.insertSubscriptionChannel(userId, channelId)

        // 업데이트 스팟 스크랩 수
        await channelDao.updateSubscriptionCount(channelId, 1)

    },
    removeSubscriptionChannel: async function (userId, channelId) {

        // 유저의 채널 구독정보 삭제
        await channelDao.deleteSubscriptionChannel(userId, channelId)

        // 업데이트 스팟 스크랩 수
        await channelDao.updateSubscriptionCount(channelId, -1)

    },
    getPlaceWithChannel: async (req) => {
        let result

        if (req.headers.flag == 0) {
            result = await spotDao.selectPlaceWithChannelKor(req.params.channel_id)
        }
        else if (req.headers.flag == 1) {
            result = await spotDao.selectPlaceWithChannelEng(req.params.channel_id)
        }

        // group_concat을 통해 string으로 합쳐 받은 데이터 분리
        for (var i in result) {

            let tempImg = result[i].thumbnail_img? result[i].thumbnail_img.split(',') : []
            for(var j in tempImg){
                tempImg[j] = s3Location + tempImg[j]
            }

            result[i].channel = {
                channel_id: result[i].channel_id ? result[i].channel_id.split(',') : [],
                thumbnail_img: tempImg
            }

            result[i].img = result[i].img? s3Location + result[i].img : ''

            delete result[i].channel_id;
            delete result[i].thumbnail_img;
        }

        return result;
    },
    getEventWithChannel: async (req) => {
        let result

        if (req.headers.flag == 0) {
            result = await spotDao.selectEventWithChannelKor(req.params.channel_id)
        }
        else if (req.headers.flag == 1) {
            result = await spotDao.selectEventWithChannelEng(req.params.channel_id)
        }

        // group_concat을 통해 string으로 합쳐 받은 데이터 분리
        for (var i in result) {

            let tempImg = result[i].thumbnail_img ? result[i].thumbnail_img.split(',') : []
            for (var j in tempImg) {
                tempImg[j] = s3Location + tempImg[j]
            }
            
            result[i].channel = {
                channel_id: result[i].channel_id ? result[i].channel_id.split(',') : [],
                thumbnail_img: tempImg
            }

            result[i].img = result[i].img? s3Location + result[i].img : ''

            delete result[i].channel_id;
            delete result[i].thumbnail_img;
        }

        return result;
    },



    // for admin
    getChannels: async function () {

        let channelCelebrity = await channelDao.selectChannels(0);
        let channelBroadcast = await channelDao.selectChannels(1);

        var result = {
            channel_celebrity: channelCelebrity,
            channel_broadcast: channelBroadcast
        }

        return result;
    },
    addChannel: async function (type, korName, engName, korCompany, engCompany, backgroundImg, thumbnailImg, memberKor, memberEng) {

        if (memberKor.length != memberEng.length) {
            throw new Error('Wrong Input')
        }

        // 새로운 채널을 삽입
        let channel = await channelDao.insertChannel(type, 0, korName, engName, korCompany, engCompany, backgroundImg, thumbnailImg);

        // 채널의 멤버 삽입
        for (var i in memberKor) {
            await channelDao.insertChannel('', channel.insertId, memberKor[i], memberEng[i], '', '', backgroundImg, thumbnailImg);
        }
    },
    getChannelForEdit : async function (channelId) {

        let result = await channelDao.selectChannelForEdit(channelId);

        if (result == undefined || result.length == 0) {
            throw new Error('Wrong Input')
        }

        for (var i in result) {
            result[i].background_img = result[i].background_img ? s3Location + result[i].background_img : '';
            result[i].thumbnail_img = result[i].thumbnail_img ? s3Location + result[i].thumbnail_img : '';
        }

        // belong_to, id, kor_name, eng_name, kor_company, eng_company, background_img, thumbnail_img
        return result;
    },
    setChannel : async function (channelId, korName, engName, korCompany, engCompany, backgroundImg, thumbnailImg) {

        // 채널 업데이트
        await channelDao.updateChannel(channelId, korName, engName, korCompany, engCompany, backgroundImg, thumbnailImg)
        await channelDao.updateChannelMember(channelId, backgroundImg, thumbnailImg)

    },
    removeChannel : async function (channelId) {

        let result = await channelDao.selectChannelForEdit(channelId);

        if (result == undefined || result.length == 0) {
            throw new Error('Wrong Input')
        }

        // 채널 삭제
        await channelDao.deleteChannel(channelId)

    },

}
