const service = require('../service/themeService');
const userService = require('../service/userService');
const papago = require('../lib/papago');
const s3Location = require('../../config/s3Location').location
const { respondJson, respondOnError } = require('../lib/response');

module.exports = {

    getThemeDetail : async(req, res) => {

        try{
            let themeId = req.params.theme_id;
            let flag = req.headers.flag;
    
            let result = await service.getThemeDetail(themeId, flag);
            
            respondJson("Success", result, res, 200);

        }catch(error){
            console.log(error);
            respondOnError(error.message, res, error.statusCode);
        }
    },
    addTheme : async (req, res) => {

        try{
            await userService.checkAdmin(req.user.id);

            let title = req.body.title;
            let subtitle = req.body.subtitle;
            let title_eng = await papago.koToEn(req.body.title);
            let subtitle_eng = await papago.koToEn(req.body.subtitle);
            let img = req.files.img[0].location.split(s3Location)[1];
            let main_img_kor = req.files.main_img_kor[0].location.split(s3Location)[1];
            let main_img_eng = req.files.main_img_eng[0].location.split(s3Location)[1];
            
            let result = await service.addTheme(title, subtitle, title_eng, subtitle_eng, main_img_kor, main_img_eng, img);

            respondJson("Success", result, res, 201);

        }catch(error){
            console.log(error);

            if (error.message == 'Not Admin')
                respondOnError(error.message, res, 403);    
            else
                respondOnError(error.message, res, error.statusCode);
        }
    },
    addThemeContents : async (req, res) => {

        try{
            await userService.checkAdmin(req.user.id);

            let themeId = req.body.theme_id
            let spotId = req.body.spot_id
            let title = req.body.title
            let description = req.body.description
            let title_eng = await papago.koToEn(req.body.title)
            let description_eng = await papago.koToEn(req.body.description)         
            
            await service.addThemeContents(themeId, spotId, title, description, title_eng, description_eng);

            respondJson("Success", null, res, 201);

        }catch(error){
            console.log(error);

            if (error.message == 'Not Admin')
                respondOnError(error.message, res, 403);    
            else
                respondOnError(error.message, res, error.statusCode);
        }
    }
}