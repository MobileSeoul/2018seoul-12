const themeDao = require('../dao/themeDao');
const spotDao = require('../dao/spotDao');
const s3Location = require('../../config/s3Location').location

module.exports = {

    getThemeDetail : async function(themeId, flag) {

        // 테마 정보, 컨텐츠 가져오기
        let theme;
        let themeContents;

        if (flag == 0) {   
            theme = await themeDao.selectThemeKor(themeId);
            themeContents = await themeDao.selectThemeContentsKor(themeId);
        }
        else if (flag == 1) {
            theme = await themeDao.selectThemeEng(themeId);
            themeContents = await themeDao.selectThemeContentsEng(themeId);
        }

        // 테마 이미지 url
        theme[0].img = theme[0].img? s3Location + theme[0].img : ''
        theme[0].title = theme[0].title? theme[0].title.split("\n") : []

        // 테마 컨텐츠 이미지 url
        for (var i in themeContents) {
            themeContents[i].img = themeContents[i].img? s3Location + themeContents[i].img : ''
            themeContents[i].description = themeContents[i].description? themeContents[i].description.split("\n") : []
        }
        
        var result = {
            theme: theme[0],
            theme_contents: themeContents
        }

        return result;
    },
    addTheme : async function(title, subtitle, title_eng, subtitle_eng, main_img_kor, main_img_eng, img) {

        // 새로운 테마를 삽입
        let theme = await themeDao.insertTheme(title, subtitle, title_eng, subtitle_eng, main_img_kor, main_img_eng, img);

        let themeId = theme.insertId;

        var result = {
            theme_id: themeId
        }

        return result;
    },
    addThemeContents : async function(themeId, spotId, title, description, title_eng, description_eng) {

        let img
        let spotImg = await spotDao.selectSpotImg(spotId);
        
        if(spotImg.length == 0) {
            img = ''
        } else {
            img = spotImg[0].img
        }
        
        await themeDao.insertThemeContents(themeId, spotId, title, description, title_eng, description_eng, img);

    }

}
