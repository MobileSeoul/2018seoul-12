const spotDao = require('../dao/spotDao')
const channelDao = require('../dao/channelDao')

module.exports = async() => {
    // spotDao에서 select spot 하루지난애들(timestampdiff)
    const result = await spotDao.selectSpotByTimeStampDiff()

    // spot_id로 channel, map join해서 isnewpost가 1인애들 0으로 set
    for(var i in result){
        await channelDao.updateNewPostCheck(result[i].channel_id)
    }
}