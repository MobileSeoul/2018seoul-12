const themeDao = require('../dao/themeDao');
const spotDao = require('../dao/spotDao');
const s3Location = require('../../config/s3Location').location

module.exports = {

    getMain : async function(userId, flag) {

        let theme, mainRecommandSpot, mainBestPlace, mainBestEvent;
       
        if (flag == 0) {   
            theme = await themeDao.selectThemeListKor();
            mainRecommandSpot = await spotDao.selectMainRecommandSpotKor(userId)
            mainBestPlace = await spotDao.selectMainBestPlaceKor()
            mainBestEvent = await spotDao.selectMainBestEventKor()
        }
        else if (flag == 1) {
            theme = await themeDao.selectThemeListEng();
            mainRecommandSpot = await spotDao.selectMainRecommandSpotEng(userId)
            mainBestPlace = await spotDao.selectMainBestPlaceEng()
            mainBestEvent = await spotDao.selectMainBestEventEng()
        }

        // 이미지 url
        for (var i in theme) {
            theme[i].main_img = theme[i].main_img ? s3Location + theme[i].main_img : '';
        }
        for (var i in mainRecommandSpot) {
            mainRecommandSpot[i].img = mainRecommandSpot[i].img ? s3Location + mainRecommandSpot[i].img : '';
        }
        for (var i in mainBestPlace) {
            mainBestPlace[i].img = mainBestPlace[i].img ? s3Location + mainBestPlace[i].img : '';
        }
        for (var i in mainBestEvent) {
            mainBestEvent[i].img = mainBestEvent[i].img ? s3Location + mainBestEvent[i].img : '';
        }

        var result = {
            theme: theme,
            main_recommand_spot : mainRecommandSpot,
            main_best_place : mainBestPlace,
            main_best_event : mainBestEvent
        }

        return result;
        
    }

}
