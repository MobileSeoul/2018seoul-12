const dao = require('../dao/spotDao');
const userDao = require('../dao/userDao')
const channelDao = require('../dao/channelDao');
const jwt = require('../lib/jwt')
const s3Location = require('../../config/s3Location').location
const papago = require('../lib/papago');
const gu = require('../lib/gu')

module.exports = {

    getSpotRecommendByGu: async (req) => {
        let type = [];

        // 맛집
        if (req.params.is_food == 1) {
            type.push(0)
        }
        // 카페
        if (req.params.is_cafe == 1) {
            type.push(1)
        }
        // 명소
        if (req.params.is_sights == 1) {
            type.push(2)
        }
        // 이벤트
        if (req.params.is_event == 1) {
            // 생일
            type.push(3)
            // 기념
            type.push(4)
            // 이벤트 기타
            type.push(5)
        }
        // 기타
        if (req.params.is_etc == 1) {
            type.push(6)
        }

        if(type.length == 0){
            type.push(-1)
        }

        let result
        if (req.headers.flag == 0) {
            result = await dao.selectSpotByGuKor(req.params.order, req.params.address_gu, type);
        }
        else if (req.headers.flag == 1) {
            result = await dao.selectSpotByGuEng(req.params.order, req.params.address_gu, type);
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

            result[i].img = result[i].img ? s3Location + result[i].img : ''

            delete result[i].channel_id;
            delete result[i].thumbnail_img;
        }

        return result;
    },
    getSpotRecommendByLocation: async (req) => {
        let type = [];

        // 맛집
        if (req.params.is_food == 1) {
            type.push(0)
        }
        // 카페
        if (req.params.is_cafe == 1) {
            type.push(1)
        }
        // 명소
        if (req.params.is_sights == 1) {
            type.push(2)
        }
        // 이벤트
        if (req.params.is_event == 1) {
            // 생일
            type.push(3)
            // 기념
            type.push(4)
            // 이벤트 기타
            type.push(5)
        }
        // 기타
        if (req.params.is_etc == 1) {
            type.push(6)
        }

        if(type.length == 0){
            type.push(-1)
        }

        let result
        if (req.headers.flag == 0) {
            result = await dao.selectSpotByLocationKor(req.params.latitude, req.params.longitude, req.params.distance, type)
        }
        else if (req.headers.flag == 1) {
            result = await dao.selectSpotByLocationEng(req.params.latitude, req.params.longitude, req.params.distance, type)
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

            result[i].img = result[i].img ? s3Location + result[i].img : ''

            delete result[i].channel_id;
            delete result[i].thumbnail_img;
        }

        return result
    },
    getSpotDetail: async (req) => {

        let result

        // spot 세부정보 
        const userId = req.headers.authorization ? jwt.verify(req.headers.authorization).id : ''
        if (req.headers.flag == 0) {
            result = await dao.selectSpotDetailKor(userId, req.params.spot_id)
        }
        else if (req.headers.flag == 1) {
            result = await dao.selectSpotDetailEng(userId, req.params.spot_id)
        }

        // scrap 여부 가져오기
        const scrapResult = await userDao.selectScrapUserWithSpot(req.params.spot_id, userId)

        if(scrapResult.length != 0){
            result[0].is_scrap = 1
        }
        else{
            result[0].is_scrap = 0
        }

        // group_concat을 통해 string으로 합쳐 받은 데이터 분리
        for (var i in result) {

            let tempImg = result[i].thumbnail_img ? result[i].thumbnail_img.split(',') : []
            for (var j in tempImg) {
                tempImg[j] = s3Location + tempImg[j]
            }

            result[i].channel = {
                channel_id: result[i].channel_id ? result[i].channel_id.split(',') : [],
                channel_name: result[i].channel_name ? result[i].channel_name.split(',') : [],
                thumbnail_img: tempImg,
                is_subscription: result[i].is_subscription ? result[i].is_subscription.split(',') : []
            }

            result[i].img = result[i].img ? result[i].img.split(',') : []
            for (var j in result[i].img) {
                result[i].img[j] = s3Location + result[i].img[j]
            }

            delete result[i].channel_id
            delete result[i].channel_name
            delete result[i].thumbnail_img
            delete result[i].is_subscription
        }

        // spot review
        if (req.headers.flag == 0) {
            result[0].reviews = await dao.selectSpotReviewPreviewKor(req.params.spot_id)
        }
        else if (req.headers.flag == 1) {
            result[0].reviews = await dao.selectSpotReviewPreviewEng(req.params.spot_id)
        }
        result[0].reviews = (result[0].reviews) ? result[0].reviews : []

        for (var i in result[0].reviews) {
            result[0].reviews[i].img = result[0].reviews[i].img ? s3Location + result[0].reviews[i].img : ''
        }

        return result
    },
    getReview: async (req) => {

        let reviewScore = await dao.selectReviewScore(req.params.spot_id)
        let review;

        if (req.headers.flag == 0) {
            review = await dao.selectReviewKor(req.params.spot_id)
        }
        else if (req.headers.flag == 1) {
            review = await dao.selectReviewEng(req.params.spot_id)
        }

        for (var i in review) {
            review[i].img = review[i].img? s3Location + review[i].img : ''
        }

        let result = {
            spot_review : reviewScore[0],
            reviews : review
        }
        
        return result
    },
    addReview: async (req) => {

        let userId = req.user.id;
        let spotId = req.body.spot_id;
        let reviewScore = req.body.review_score;
        reviewScore = reviewScore ? reviewScore : 0;

        let img = req.files;

        if(img.length == 0) {
            img = '';
        } else {
            img = img[0].location.split(s3Location)[1];
        }

        let titleKor, contentKor
        let titleEng, contentEng

        if(req.headers.flag == 0){
            titleKor = req.body.title
            contentKor = req.body.content
    
            titleEng = await papago.koToEn(req.body.title)
            contentEng = await papago.koToEn(req.body.content)
        }
        else if(req.headers.flag == 1){
            titleKor = await papago.enToKo(req.body.title)
            contentKor = await papago.enToKo(req.body.content)
    
            titleEng = req.body.title
            contentEng = req.body.content
        }
        
        await dao.insertReview(spotId, userId, titleKor, contentKor, titleEng, contentEng, reviewScore, img)
        await dao.updateReviewCount(reviewScore, spotId)
    },
    addSpot: async (req) => {

        if(!req.body.name || !req.body.description || !req.body.station || !req.body.prev_station || !req.body.next_station || !req.body.address || !req.body.address_gu || !req.body.line_number || !req.body.type || !req.body.open_time || !req.body.close_time || !req.body.contact || !req.body.latitude || !req.body.longitude){
            throw Error("insert error")
        }

        const result = await dao.insertSpot(req)

        // papago 
        const name_eng = await papago.koToEn(req.body.name)

        let description_eng = ''
        let description_temp = req.body.description.replace("\r\n","\n")
        description_temp = description_temp.split("\n")
        for(var i in description_temp){
            description_eng += description_temp[i]
        }
        description_eng = await papago.koToEn(description_eng)
    
        const station_eng = await papago.koToEn(req.body.station)
        const prev_station_eng = await papago.koToEn(req.body.prev_station)
        const next_station_eng = await papago.koToEn(req.body.next_station)
        const address_eng = await papago.koToEn(req.body.address)
        const address_gu_eng = gu[req.body.address_gu]

        await dao.insertSpotInfoKor(result.insertId, req)
        await dao.insertSpotInfoEng(result.insertId, name_eng, description_eng, station_eng, prev_station_eng, next_station_eng, address_eng, address_gu_eng)
        
        for (var i = 0; i < req.files.length; i++) {
            await dao.insertSpotImg(result.insertId, req.files[i].location.split(s3Location)[1])
        }
        
        let channel_id = req.body.channel_id;
        let channelIdList = [];

        channelIdList = channel_id ? channel_id.split(',') : [];

        console.log(channelIdList)

        for(var i in channelIdList){
            await dao.insertSpotChannelMap(result.insertId, channelIdList[i])
            await channelDao.updateSpotCount(channelIdList[i], 1)
            await channelDao.updateChannelNewPostCheck(channelIdList[i])
        } 
    },
    getSpotList: async () => {

        // 스팟 인덱스랑 제목만 가져오기
        let result = await dao.selectSpotList();

        return result;
    },
    getEventList: async (flag) => {
        let result;

        // 이벤트 리스트 가져오기
        if (flag == 0) {
            result = await dao.selectEventListKor();
        } else if (flag == 1) {
            result = await dao.selectEventListEng();
        }

        // group_concat을 통해 string으로 합쳐 받은 데이터 분리
        for (var i in result) {

            let tempImg = result[i].channel_img ? result[i].channel_img.split(',') : []
            for (var j in tempImg) {
                tempImg[j] = s3Location + tempImg[j]
            }

            result[i].channel = {
                channel_id: result[i].channel_id ? result[i].channel_id.split(',') : [],
                thumbnail_img: tempImg
            }

            result[i].img = result[i].spot_img? s3Location + result[i].spot_img : ''

            delete result[i].channel_id;
            delete result[i].channel_img;
            delete result[i].spot_img;
        }

        return result;
    },
    addScrapSpot: async function (userId, spotId) {

        // 유저의 장소 스크랩 삽입
        await dao.insertScrapSpot(userId, spotId)

        // 업데이트 스팟 스크랩 수
        await dao.updateScrapCount(spotId, 1)

    },
    removeScrapSpot: async function (userId, spotId) {

        // 유저의 장소 스크랩 삭제
        await dao.deleteScrapSpot(userId, spotId)

        // 업데이트 스팟 스크랩 수
        await dao.updateScrapCount(spotId, -1)

    },
    getSpotInfo : async (spotId) => {
        let spotInfo = await dao.selectSpotInfo(spotId)
        let spotImg = await dao.selectSpotImgById(spotId)

        spotInfo[0].img = []
        for(var i in spotImg){
            spotInfo[0].img.push(s3Location + spotImg[i].img)
        }

        return spotInfo
    },
    removeSpot : async (spotId) => {
        await dao.deleteSpot(spotId)
    },
    setSpot : async (req) => {
        let spotInfo = await dao.selectSpotInfo(req.body.spot_id)

        let kor_name
        let kor_description
        let kor_address
        let kor_address_gu

        let eng_name
        let eng_description
        let eng_address
        let eng_address_gu

        if(req.body.name != undefined){
            kor_name = req.body.name
            eng_name = papago.koToEn(kor_name)
        }
        else{
            kor_name = spotInfo[0].kor_name
            eng_name = spotInfo[0].eng_name
        }

        if(req.body.description != undefined){
            kor_description = req.body.description
            eng_description = await papago.koToEn(kor_description)
        }
        else{
            kor_description = spotInfo[0].kor_description
            eng_description = spotInfo[0].eng_description
        }

        if(req.body.address != undefined){
            kor_address = req.body.address
            eng_address = await papago.koToEn(kor_address)
        }
        else{
            kor_address = spotInfo[0].kor_address
            eng_address = spotInfo[0].eng_address
        }

        if(req.body.address_gu != undefined){
            kor_address_gu = req.body.address_gu
            eng_address_gu = gu[req.body.address_gu]
        }
        else{
            kor_address_gu = spotInfo[0].kor_address_gu
            eng_address_gu = spotInfo[0].eng_address_gu
        }

        await dao.updateSpotKor(kor_name, kor_description, kor_address, kor_address_gu, req.body.spot_id)
        await dao.updateSpotEng(eng_name, eng_description, eng_address, eng_address_gu, req.body.spot_id)
    },





    // by jiyeon
    setSpotAddressEng : async () => {

        result = await dao.selectAllSpotAddressKor();
        var address_eng;

        for (var i = 0; i < result.length; i++) {
            
            address_eng = await papago.koToEn(result[i].address);
            console.log(result[i].address, "   ==>>  ", address_eng);
            await dao.updateSpotAddressEng(result[i].spot_id, address_eng);
        }

    },
}
