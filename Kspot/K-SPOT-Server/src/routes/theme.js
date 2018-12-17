const express = require('express')
const theme = express.Router();

const themeController = require('../controller/themeController');
const userCheck = require('../middleware/userCheck');
const upload = require('../lib/s3Bucket.js').getMulter('theme');


/* select theme detail */
theme.get('/:theme_id', themeController.getThemeDetail);

/* insert new theme */
theme.post('/', userCheck, upload.fields([{name : 'main_img_kor', maxCount : 1}, {name : 'main_img_eng', maxCount : 1}, {name : 'img', maxCount : 50}]), themeController.addTheme);

/* insert new theme contents */
theme.post('/contents', userCheck, themeController.addThemeContents);


module.exports = theme