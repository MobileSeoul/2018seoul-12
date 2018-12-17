const multer = require('multer');
const multerS3 = require('multer-s3');
const aws = require('aws-sdk');
const moment = require('moment')

aws.config.loadFromPath('./config/s3Config.json');

const s3 = new aws.S3();

module.exports = {
    getMulter: function (package) {
        const date = {
            YYYY: moment().format('YYYY'),
            MM: moment().format('MM'),
            DD: moment().format('DD')
        }

        return multer({
            storage: multerS3({
                s3: s3,
                bucket: 'k-spot',
                acl: 'public-read',
                key: function (req, file, cb) {
                    cb(null, `${package}/${date.YYYY}/${date.MM}/${date.DD}/${file.originalname}`)
                    //cb(null, package + "/" + date.YYYY + "/" + file.originalname);
                }
            })
        })
    }
}