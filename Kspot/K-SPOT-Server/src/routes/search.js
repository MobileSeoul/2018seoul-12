const express = require('express')
const channel = express.Router();

const searchController = require('../controller/searchController');
const userCheck = require('../middleware/userCheck');



/* select best keyword list */
channel.get('/', searchController.getBestKeyword);

/* select channel & palce & event by keyword */
channel.get('/:keyword', searchController.getChannelAndSpotByKeyword);

/* select channel & palce & event by keyword */
channel.get('/:keyword', searchController.getChannelAndSpotByKeyword);



/* select palce filter by keyword */
channel.get('/:keyword/filter/place/:order/:is_food/:is_cafe/:is_sights/:is_etc', searchController.getFilterPlaceByKeyword);

/* select event filter by keyword */
channel.get('/:keyword/filter/event/:order/:is_birth/:is_celebrate/:is_etc', searchController.getFilterEventByKeyword);


module.exports = channel;