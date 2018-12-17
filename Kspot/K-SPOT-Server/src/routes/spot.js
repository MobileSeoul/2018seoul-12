const express = require('express')
const spot = express.Router()

const spotController = require('../controller/spotController')
const userCheck = require('../middleware/userCheck')
const uploadReview = require('../lib/s3Bucket.js').getMulter('review');
const uploadSpot = require('../lib/s3Bucket.js').getMulter('spot');


// event list for main
spot.get('/event', spotController.getEventList)

// spot recommend by gu
spot.get('/:address_gu/:order/:is_food/:is_cafe/:is_sights/:is_event/:is_etc', spotController.getSpotRecommendByGu)

// spot recommend by location
spot.get('/:distance/:latitude/:longitude/:is_food/:is_cafe/:is_sights/:is_event/:is_etc', spotController.getSpotRecommendByLocation)

// spot detail
spot.get('/:spot_id/detail', spotController.getSpotDetail)

/* insert and delete subscription channel */
spot.post('/scrap', userCheck, spotController.addScrapSpot);
spot.delete('/scrap/:spot_id', userCheck, spotController.removeScrapSpot);

// spot review list
spot.get('/:spot_id/review', spotController.getReview)

// write review
spot.post('/review', userCheck, uploadReview.array('review_img'), spotController.addReview)

// admin permission
// write spot
spot.post('/', userCheck, uploadSpot.array('spot_img'), spotController.addSpot)

// spot title list
spot.get('/list', spotController.getSpotList)

// spot allinfo by id
spot.get('/:spot_id/AllInfo', spotController.getSpotInfo)

// modify spot
spot.post('/edit', userCheck, spotController.setSpot)
spot.post('/edit/address', userCheck, spotController.setSpotAddressEng)


// delete spot
spot.delete('/:spot_id', userCheck, spotController.removeSpot)

module.exports = spot