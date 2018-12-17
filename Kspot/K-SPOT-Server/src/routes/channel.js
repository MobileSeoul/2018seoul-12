const express = require('express')
const channel = express.Router();

const channelController = require('../controller/channelController');
const userCheck = require('../middleware/userCheck');
const upload = require('../lib/s3Bucket.js').getMulter('channel');


/* select channel list */
channel.get('/list', channelController.getChannelList);

/* select channel detail */
channel.get('/detail/:channel_id', channelController.getChannelDetail);

/* select spot more in channel */
channel.get('/:channel_id/spot/:is_event', channelController.getSpotWithChannel)

/* insert and delete subscription channel */
channel.post('/subscription', userCheck, channelController.addSubscriptionChannel);
channel.delete('/subscription/:channel_id', userCheck, channelController.removeSubscriptionChannel);


/* select channels for admin */
channel.get('/', channelController.getChannels);

/* insert and delete channel */
channel.post('/', userCheck, upload.fields([{name : 'background_img', maxCount : 1}, {name : 'thumbnail_img', maxCount : 1}]), channelController.addChannel);
channel.get('/:channel_id/edit', userCheck, channelController.getChannelForEdit);
channel.post('/edit', userCheck, upload.fields([{name : 'background_img', maxCount : 1}, {name : 'thumbnail_img', maxCount : 1}]), channelController.setChannel);
channel.delete('/:channel_id', userCheck, channelController.removeChannel);


module.exports = channel;