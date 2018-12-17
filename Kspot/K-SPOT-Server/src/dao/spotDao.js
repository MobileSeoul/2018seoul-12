const db = require('../lib/db');
const s3Location = require('../../config/s3Location').location

module.exports = {
    // 메인 탭에서의 추천 스팟 목록 (limit 10)
    selectMainRecommandSpotEng : async function (userId) {
        let query =`
        SELECT
        spot_info.id AS spot_id, name, description, img
        FROM
            (
                SELECT 
                spot.id AS id, grade_sum/review_cnt AS review_score, grade_sum, scrap_cnt, name, description, address_gu, station, img, review_cnt, reg_time
                FROM spot
                JOIN
                    (
                        SELECT 
                        fk_eng_spot_id AS id, name, description, address_gu, station, img
                        FROM spot_eng
                        JOIN spot_img
                        ON spot_eng.fk_eng_spot_id = spot_img.fk_spotimg_spot_id
                        GROUP BY id
                    ) AS spot_detail
                ON spot.id = spot_detail.id
                WHERE type NOT IN (3,4,5)
            ) AS spot_info
        LEFT JOIN
            (
                SELECT
                fk_map_spot_id AS spot_id, channel.id AS channel_id, is_subscription
                FROM 
                (
					SELECT channel.id, (CASE WHEN fk_sub_user_user_id = ? THEN 1 ELSE 0 END) as is_subscription, is_member FROM channel LEFT JOIN subscription_channel ON channel.id = subscription_channel.fk_sub_ch_id
				) as channel
				JOIN map_channel_spot
                ON channel.id = map_channel_spot.fk_map_ch_id
                WHERE is_member = 0
            ) AS channel_info
        ON spot_info.id = channel_info.spot_id
        GROUP BY spot_id
        ORDER BY POW(grade_sum/((TIMESTAMPDIFF(second, reg_time, NOW())/86400)+1), (CASE WHEN SUM(is_subscription) > 0 THEN 1 ELSE 0 END) +1) DESC
        LIMIT 10
        `;

        let data = await db.query(query, [userId]);

        // spot_id, name, description, img
        return data;
    },
    // 메인 탭에서의 추천 스팟 목록 (limit 10)
    selectMainRecommandSpotKor : async function (userId) {

        let query =`
        SELECT
        spot_info.id AS spot_id, name, description, img
        FROM
            (
                SELECT 
                spot.id AS id, grade_sum/review_cnt AS review_score, grade_sum, scrap_cnt, name, description, address_gu, station, img, review_cnt, reg_time
                FROM spot
                JOIN
                    (
                        SELECT 
                        fk_kor_spot_id AS id, name, description, address_gu, station, img
                        FROM spot_kor
                        JOIN spot_img
                        ON spot_kor.fk_kor_spot_id = spot_img.fk_spotimg_spot_id
                        GROUP BY id
                    ) AS spot_detail
                ON spot.id = spot_detail.id
                WHERE type NOT IN (3,4,5)
            ) AS spot_info
        LEFT JOIN
            (
                SELECT
                fk_map_spot_id AS spot_id, channel.id AS channel_id, is_subscription
                FROM 
                (
					SELECT channel.id, (CASE WHEN fk_sub_user_user_id = ? THEN 1 ELSE 0 END) as is_subscription, is_member FROM channel LEFT JOIN subscription_channel ON channel.id = subscription_channel.fk_sub_ch_id
				) as channel
				JOIN map_channel_spot
                ON channel.id = map_channel_spot.fk_map_ch_id
                WHERE is_member = 0
            ) AS channel_info
        ON spot_info.id = channel_info.spot_id
        GROUP BY spot_id
        ORDER BY POW(grade_sum/((TIMESTAMPDIFF(second, reg_time, NOW())/86400)+1), (CASE WHEN SUM(is_subscription) > 0 THEN 1 ELSE 0 END) +1) DESC
        LIMIT 10
        `;

        let data = await db.query(query, [userId]);

        // spot_id, name, description, img
        return data;
    },
    // 메인 탭에서의 인기 장소 목록 (limit 10)
    selectMainBestPlaceKor : async function () {

        let query =
        `
            SELECT spot.id AS spot_id, spot_detail.name AS name, description, img
            FROM spot LEFT JOIN 
                (
                    SELECT spot_kor.name, img, spot_kor.fk_kor_spot_id AS spot_id, spot_kor.description
                    FROM spot_kor LEFT JOIN spot_img
                    ON spot_kor.fk_kor_spot_id = spot_img.fk_spotimg_spot_id
                    GROUP BY spot_kor.fk_kor_spot_id
                ) AS spot_detail
            ON spot.id = spot_detail.spot_id
            WHERE spot.type NOT IN (3,4,5)
            ORDER BY (spot.grade_sum) + (2 * spot.scrap_cnt) DESC
            LIMIT 10
        `;

        let data = await db.query(query, []);

        // spot_id, name, description, img
        return data;
    },
    // 메인 탭에서의 인기 장소 목록 (limit 10)
    selectMainBestPlaceEng : async function () {

        let query =
        `
            SELECT spot.id AS spot_id, spot_detail.name AS name, description, img
            FROM spot LEFT JOIN 
                (
                    SELECT spot_eng.name, img, spot_eng.fk_eng_spot_id AS spot_id, spot_eng.description
                    FROM spot_eng LEFT JOIN spot_img
                    ON spot_eng.fk_eng_spot_id = spot_img.fk_spotimg_spot_id
                    GROUP BY spot_eng.fk_eng_spot_id
                ) AS spot_detail
            ON spot.id = spot_detail.spot_id
            WHERE spot.type NOT IN (3,4,5)
            ORDER BY (spot.grade_sum) + (2 * spot.scrap_cnt) DESC
            LIMIT 10
        `;

        let data = await db.query(query, []);
        
        // spot_id, name, description, img
        return data;
    },
    // 메인 탭에서의 인기 이벤트 목록 (limit 5)
    selectMainBestEventKor : async function () {

        let query =
        `
            SELECT spot.id AS spot_id, spot_detail.name AS name, description, img
            FROM spot LEFT JOIN 
                (
                    SELECT spot_kor.name, img, spot_kor.fk_kor_spot_id AS spot_id, spot_kor.description
                    FROM spot_kor LEFT JOIN spot_img
                    ON spot_kor.fk_kor_spot_id = spot_img.fk_spotimg_spot_id
                    GROUP BY spot_kor.fk_kor_spot_id
                ) AS spot_detail
            ON spot.id = spot_detail.spot_id
            WHERE spot.type IN (3,4,5)
            ORDER BY (spot.grade_sum) + (2 * spot.scrap_cnt) DESC
            LIMIT 5
        `;

        let data = await db.query(query, []);

        // spot_id, name, description, img
        return data;
    },
    // 메인 탭에서의 인기 이벤트 목록 (limit 5)
    selectMainBestEventEng : async function () {

        let query =
        `
            SELECT spot.id AS spot_id, spot_detail.name AS name, description, img
            FROM spot LEFT JOIN 
                (
                    SELECT spot_eng.name, img, spot_eng.fk_eng_spot_id AS spot_id, spot_eng.description
                    FROM spot_eng LEFT JOIN spot_img
                    ON spot_eng.fk_eng_spot_id = spot_img.fk_spotimg_spot_id
                    GROUP BY spot_eng.fk_eng_spot_id
                ) AS spot_detail
            ON spot.id = spot_detail.spot_id
            WHERE spot.type IN (3,4,5)
            ORDER BY (spot.grade_sum) + (2 * spot.scrap_cnt) DESC
            LIMIT 5
        `;

        let data = await db.query(query, []);
        
        // spot_id, name, description, img
        return data;
    },


    // for channel
    // 채널과 관련된 스팟 정보 목록 (추천장소) (limit 5)
    selectPlaceRecommendedByChannel: async function (channelId) {

        let query = 
        `
            SELECT spot.id AS spot_id, spot_detail.kor_name AS kor_name, spot_detail.eng_name AS eng_name, img
            FROM spot LEFT JOIN 
                (
                    SELECT spot_name.id AS spot_id, spot_name.kor_name AS kor_name, spot_name.eng_name AS eng_name, img
                    FROM
                        (
                            SELECT spot_kor.fk_kor_spot_id as id, spot_kor.name AS kor_name, spot_eng.name AS eng_name
                            FROM spot_kor, spot_eng
                            WHERE spot_kor.fk_kor_spot_id = spot_eng.fk_eng_spot_id
                        ) AS spot_name LEFT JOIN spot_img
                    ON spot_name.id = spot_img.fk_spotimg_spot_id
                    GROUP BY spot_name.id
                ) AS spot_detail
            ON spot.id = spot_detail.spot_id
            WHERE spot.id IN
                (
                    SELECT map_channel_spot.fk_map_spot_id AS id
                    FROM map_channel_spot JOIN channel 
                    ON map_channel_spot.fk_map_ch_id = channel.id
                    WHERE channel.id = ?
                )
            AND type NOT IN (3,4,5)
            ORDER BY (grade_sum + 2*scrap_cnt) DESC
            LIMIT 5;
        `;

        let data = await db.query(query, [channelId]);

        return data;
    },
    // 채널과 관련된 스팟 정보 목록 (최신 장소/이벤트) (limit 5)
    selectSpotRelatedChannelKor: async function (channelId, isEvent) {

        let query =
        `
            SELECT spot.id AS spot_id, spot_detail.name AS name, description, address_gu, station, img, scrap_cnt
            FROM spot LEFT JOIN 
                (
                    SELECT spot_kor.name, img, spot_kor.fk_kor_spot_id AS spot_id, spot_kor.description, spot_kor.address_gu, spot_kor.station
                    FROM spot_kor LEFT JOIN spot_img
                    ON spot_kor.fk_kor_spot_id = spot_img.fk_spotimg_spot_id
                    GROUP BY spot_kor.fk_kor_spot_id
                ) AS spot_detail
            ON spot.id = spot_detail.spot_id
            WHERE spot.id IN
                (
                    SELECT map_channel_spot.fk_map_spot_id AS id
                    FROM map_channel_spot JOIN channel 
                    ON map_channel_spot.fk_map_ch_id = channel.id
                    WHERE channel.id = ?
                )
        `;

        // 장소
        if (isEvent == 0) {
            query +=
                `
            AND type NOT IN (3,4,5)
            ORDER BY reg_time DESC
            LIMIT 5;
            `;
        }
        // 이벤트
        else if (isEvent == 1) {
            query +=
                `
            AND type IN (3,4,5)
            ORDER BY reg_time DESC
            LIMIT 5;
            `;
        }

        let data = await db.query(query, [channelId]);

        // spot_id, name, description, address_gu, station, img, scrap_cnt
        return data;
    },
    // 채널과 관련된 스팟 정보 목록 (최신 장소/이벤트) (limit 5)
    selectSpotRelatedChannelEng: async function (channelId, isEvent) {

        let query = `
            SELECT spot.id AS spot_id, spot_detail.name AS name, description, address_gu, station, img, scrap_cnt
            FROM spot LEFT JOIN 
                (
                    SELECT spot_eng.name, img, spot_eng.fk_eng_spot_id AS spot_id, spot_eng.description, spot_eng.address_gu, spot_eng.station
                    FROM spot_eng LEFT JOIN spot_img
                    ON spot_eng.fk_eng_spot_id = spot_img.fk_spotimg_spot_id
                    GROUP BY spot_eng.fk_eng_spot_id
                ) AS spot_detail
            ON spot.id = spot_detail.spot_id
            WHERE spot.id IN
                (
                    SELECT map_channel_spot.fk_map_spot_id AS id
                    FROM map_channel_spot JOIN channel 
                    ON map_channel_spot.fk_map_ch_id = channel.id
                    WHERE channel.id = ?
                )
        `;

        // 장소
        if (isEvent == 0) {
            query += `
            AND type NOT IN (3,4,5)
            ORDER BY reg_time DESC
            LIMIT 5;
            `;
        }
        // 이벤트
        else if (isEvent == 1) {
            query += `
            AND type IN (3,4,5)
            ORDER BY reg_time DESC
            LIMIT 5;
            `;
        }

        let data = await db.query(query, [channelId]);

        return data;
    },
    selectSpotByGuKor: async (order, gu, type) => {
        let sql = `
        SELECT
        spot_info.id AS spot_id, GROUP_CONCAT(channel_info.channel_id) AS channel_id, GROUP_CONCAT(thumbnail_img) AS thumbnail_img, IFNULL(ROUND(review_score,1),0) as review_score, name, description, type, address_gu, station, img
        FROM
            (
                SELECT 
                spot.id AS id, type, grade_sum/review_cnt AS review_score, grade_sum, scrap_cnt, name, description, address_gu, station, img, review_cnt, reg_time
                FROM spot
                JOIN
                    (
                        SELECT 
                        fk_kor_spot_id AS id, name, description, address_gu, station, img
                        FROM spot_kor
                        JOIN spot_img
                        ON spot_kor.fk_kor_spot_id = spot_img.fk_spotimg_spot_id
                        WHERE
                        address_gu = ?
                        GROUP BY id
                    ) AS spot_detail
                ON spot.id = spot_detail.id
                WHERE type IN (?)
            ) AS spot_info
        LEFT JOIN
            (
                SELECT
                fk_map_spot_id AS spot_id, channel.id AS channel_id, thumbnail_img 
                FROM channel
                JOIN map_channel_spot
                ON channel.id = map_channel_spot.fk_map_ch_id
                WHERE is_member = 0
            ) AS channel_info
        ON spot_info.id = channel_info.spot_id
        GROUP BY spot_id
        `;

        // 인기순
        if (order == 0) {
            sql +=
                `ORDER BY spot_info.grade_sum + 2*spot_info.scrap_cnt DESC`;
        }
        // 최신 순
        else if (order == 1) {
            sql +=
                `ORDER BY reg_time DESC`;
        }
        else if (order == 2){
            sql +=
            `ORDER BY review_score`
        }

        let result = await db.query(sql, [gu, type])

        return result;
    },
    selectSpotByGuEng: async (order, gu, type) => {
        let sql = `
        SELECT
        spot_info.id AS spot_id, GROUP_CONCAT(channel_info.channel_id) AS channel_id, GROUP_CONCAT(thumbnail_img) AS thumbnail_img, IFNULL(ROUND(review_score,1),0) as review_score, name, description, type, address_gu, station, img
        FROM
            (
                SELECT 
                spot.id AS id, type, grade_sum/review_cnt AS review_score, grade_sum, scrap_cnt, name, description, address_gu, station, img, review_cnt, reg_time
                FROM spot
                JOIN
                    (
                        SELECT 
                        fk_eng_spot_id AS id, name, description, address_gu, station, img
                        FROM spot_eng
                        JOIN spot_img
                        ON spot_eng.fk_eng_spot_id = spot_img.fk_spotimg_spot_id
                        WHERE
                        address_gu = ?
                        GROUP BY id
                    ) AS spot_detail
                ON spot.id = spot_detail.id
                WHERE type IN (?)
            ) AS spot_info
        LEFT JOIN
            (
                SELECT
                fk_map_spot_id AS spot_id, channel.id AS channel_id, thumbnail_img 
                FROM channel
                JOIN map_channel_spot
                ON channel.id = map_channel_spot.fk_map_ch_id
                WHERE is_member = 0
            ) AS channel_info
        ON spot_info.id = channel_info.spot_id
        GROUP BY spot_id
        `;

        // 인기순
        if (order == 0) {
            sql +=
                `ORDER BY spot_info.grade_sum + 2*spot_info.scrap_cnt DESC`;
        }
        // 최신 순
        else if (order == 1) {
            sql +=
                `ORDER BY reg_time DESC`;
        }
        else if (order == 2){
            sql +=
            `ORDER BY review_score`
        }

        let result = await db.query(sql, [gu, type])

        return result;
    },
    selectSpotByLocationKor: async (latitude, longitude, distance, type) => {
        const sql = `
        SELECT
        spot_info.id as spot_id, GROUP_CONCAT(channel_info.channel_id) as channel_id, GROUP_CONCAT(thumbnail_img) as thumbnail_img, IFNULL(ROUND(review_score,1),0) as review_score, name, description, type, address_gu, station, img, 
        ROUND( 6371 * acos( cos( radians(latitude) ) * cos( radians( ? ) )
          * cos( radians( ? ) - radians(longitude) )
          + sin( radians(latitude) ) * sin( radians(  ? ) ) ),1 ) AS distance
        FROM
            (
                SELECT 
                spot.id as id, type, grade_sum/review_cnt as review_score, grade_sum, scrap_cnt, name, description, address_gu, station, img, review_cnt, latitude, longitude, reg_time
                FROM spot
                JOIN
                    (
                        SELECT 
                        fk_kor_spot_id as id, name, description, address_gu, station, img
                        FROM spot_kor
                        JOIN spot_img
                        ON spot_kor.fk_kor_spot_id = spot_img.fk_spotimg_spot_id
                        GROUP BY id
                    ) as spot_detail
                ON spot.id = spot_detail.id
                WHERE type IN (?)
            ) as spot_info
        LEFT JOIN
            (
                SELECT
                fk_map_spot_id as spot_id, channel.id as channel_id, thumbnail_img 
                FROM channel
                JOIN map_channel_spot
                ON channel.id = map_channel_spot.fk_map_ch_id
                WHERE is_member = 0
            ) as channel_info
        ON spot_info.id = channel_info.spot_id
        GROUP BY spot_id 
        HAVING distance < ?
        ORDER BY distance
        `

        let result = await db.query(sql, [latitude, longitude, latitude, type, distance])

        return result;
    },
    selectSpotByLocationEng: async (latitude, longitude, distance, type) => {
        const sql = `
        SELECT
        spot_info.id as spot_id, GROUP_CONCAT(channel_info.channel_id) as channel_id, GROUP_CONCAT(thumbnail_img) as thumbnail_img, IFNULL(ROUND(review_score,1),0) as review_score, name, description, type, address_gu, station, img, 
	    ROUND( 6371 * acos( cos( radians(latitude) ) * cos( radians(?) )
          * cos( radians(?) - radians(longitude) )
          + sin( radians(latitude) ) * sin( radians(?) ) ), 1 ) AS distance
        FROM
            (
                SELECT 
                spot.id as id, type, grade_sum/review_cnt as review_score, grade_sum, scrap_cnt, name, description, address_gu, station, img, review_cnt, latitude, longitude, reg_time
                FROM spot
                JOIN
                    (
                        SELECT 
                        fk_eng_spot_id as id, name, description, address_gu, station, img
                        FROM spot_eng
                        JOIN spot_img
                        ON spot_eng.fk_eng_spot_id = spot_img.fk_spotimg_spot_id
                        GROUP BY id
                    ) as spot_detail
                ON spot.id = spot_detail.id
                WHERE type IN (?)
            ) as spot_info
        LEFT JOIN
            (
                SELECT
                fk_map_spot_id as spot_id, channel.id as channel_id, thumbnail_img 
                FROM channel
                JOIN map_channel_spot
                ON channel.id = map_channel_spot.fk_map_ch_id
                WHERE is_member = 0
            ) as channel_info
        ON spot_info.id = channel_info.spot_id
        GROUP BY spot_id
        HAVING distance < ?
        ORDER BY distance
        `

        let result = await db.query(sql, [latitude, longitude, latitude, type, distance])

        return result;
    },
    selectSpotDetailEng: async (userId, spotId) => {
        const sql = `
        SELECT
        spot_info.id AS spot_id, img, name, description, latitude, longitude, address, IFNULL(ROUND(review_score,1),0) as review_score, review_cnt, line_number, station, prev_station, next_station, GROUP_CONCAT(channel_info.channel_id) AS channel_id, GROUP_CONCAT(eng_name) as channel_name, GROUP_CONCAT(thumbnail_img) AS thumbnail_img, GROUP_CONCAT(CASE WHEN fk_sub_user_user_id = ? THEN 1 ELSE 0 END) as is_subscription, open_time, close_time, contact, scrap_cnt
        FROM
            (
                SELECT 
                spot.id AS id, grade_sum/review_cnt AS review_score, grade_sum, scrap_cnt, name, description, latitude, longitude, address, address_gu, line_number, station, prev_station, next_station, GROUP_CONCAT(img) as img, review_cnt, open_time, close_time, contact, reg_time
                FROM spot
                JOIN
                    (
                        SELECT 
                        fk_eng_spot_id AS id, name, description, address, address_gu, station, prev_station, next_station, img
                        FROM spot_eng
                        JOIN spot_img
                        ON spot_eng.fk_eng_spot_id = spot_img.fk_spotimg_spot_id
                        WHERE
						fk_eng_spot_id = ?
                    ) AS spot_detail
                ON spot.id = spot_detail.id
            ) AS spot_info
        LEFT JOIN
        (
            SELECT spot_id, channel_id, eng_name, thumbnail_img, fk_sub_user_user_id
            FROM
            (
                SELECT
                fk_map_spot_id AS spot_id, channel.id AS channel_id, eng_name, thumbnail_img 
                FROM channel
                JOIN map_channel_spot
                ON channel.id = map_channel_spot.fk_map_ch_id
                WHERE
                fk_map_spot_id = ? AND is_member = 0
            ) as spot_channel
            LEFT JOIN
            (
                SELECT fk_sub_user_user_id, fk_sub_ch_id
                FROM subscription_channel
                WHERE fk_sub_user_user_id = ?
            ) as subscription_channel
            ON
            spot_channel.channel_id = subscription_channel.fk_sub_ch_id
            GROUP BY channel_id
        ) AS channel_info
        ON spot_info.id = channel_info.spot_id
        GROUP BY spot_id
        `

        const result = await db.query(sql, [userId, spotId, spotId, userId])

        return result
    },
    selectSpotDetailKor: async (userId, spotId) => {
        const sql = `
        SELECT
        spot_info.id AS spot_id, img, name, description, latitude, longitude, address, IFNULL(ROUND(review_score,1),0) as review_score, review_cnt, line_number, station, prev_station, next_station, GROUP_CONCAT(channel_info.channel_id) AS channel_id, GROUP_CONCAT(kor_name) as channel_name, GROUP_CONCAT(thumbnail_img) AS thumbnail_img, GROUP_CONCAT(CASE WHEN fk_sub_user_user_id = ? THEN 1 ELSE 0 END) as is_subscription, open_time, close_time, contact, scrap_cnt
        FROM
            (
                SELECT 
                spot.id AS id, grade_sum/review_cnt AS review_score, grade_sum, scrap_cnt, name, description, latitude, longitude, address, address_gu, line_number, station, prev_station, next_station, GROUP_CONCAT(img) as img, review_cnt, open_time, close_time, contact, reg_time
                FROM spot
                JOIN
                    (
                        SELECT 
                        fk_kor_spot_id AS id, name, description, address, address_gu, station, prev_station, next_station, img
                        FROM spot_kor
                        JOIN spot_img
                        ON spot_kor.fk_kor_spot_id = spot_img.fk_spotimg_spot_id
                        WHERE
						fk_kor_spot_id = ?
                    ) AS spot_detail
                ON spot.id = spot_detail.id
            ) AS spot_info
        LEFT JOIN
        (
            SELECT spot_id, channel_id, kor_name, thumbnail_img, fk_sub_user_user_id
            FROM
            (
                SELECT
                fk_map_spot_id AS spot_id, channel.id AS channel_id, kor_name, thumbnail_img 
                FROM channel
                JOIN map_channel_spot
                ON channel.id = map_channel_spot.fk_map_ch_id
                WHERE
                fk_map_spot_id = ? AND is_member = 0
            ) as spot_channel
            LEFT JOIN
            (
                SELECT fk_sub_user_user_id, fk_sub_ch_id
                FROM subscription_channel
                WHERE fk_sub_user_user_id = ?
            ) as subscription_channel
            ON
            spot_channel.channel_id = subscription_channel.fk_sub_ch_id
            GROUP BY channel_id
        ) AS channel_info
        ON spot_info.id = channel_info.spot_id
        GROUP BY spot_id
        `

        const result = await db.query(sql, [userId, spotId, spotId, userId])

        return result
    },
    selectSpotReviewPreviewKor: async (spotId) => {
        const sql = 
        `
        SELECT spot_review.id as review_id, name, title_kor as title, content_kor as content, img, ROUND(score,1) as review_score, DATE_FORMAT(reg_time, '%Y-%c-%e') as reg_time
        FROM spot_review, user
        WHERE spot_review.fk_spotrev_spot_id = ?
        AND spot_review.fk_spotrev_user_user_id = user.user_id
        ORDER BY reg_time DESC
        LIMIT 5;
        `;

        const result = await db.query(sql, [spotId])

        return result
    },
    selectSpotReviewPreviewEng: async (spotId) => {
        const sql = 
        `
        SELECT spot_review.id as review_id, name, title_eng as title, content_eng as content, img, ROUND(score,1) as review_score, DATE_FORMAT(reg_time, '%Y-%c-%e') as reg_time
        FROM spot_review, user
        WHERE spot_review.fk_spotrev_spot_id = ?
        AND spot_review.fk_spotrev_user_user_id = user.user_id
        ORDER BY reg_time DESC
        LIMIT 5;
        `;

        const result = await db.query(sql, [spotId])

        return result
    },

    selectReviewScore: async (spotId) => {
        let sql = `
        SELECT ROUND(grade_sum/review_cnt, 1) as review_score, review_cnt
        FROM spot
        WHERE id = ?
        `
        const result = await db.query(sql, [spotId])

        return result
    },
    selectReviewKor: async (spotId) => {
        let sql = `
        SELECT spot_review.id as review_id, name, title_kor as title, content_kor as content, img, score as review_score, DATE_FORMAT(reg_time, '%Y-%c-%e') as reg_time
        FROM spot_review
        JOIN
        user
        ON
        fk_spotrev_user_user_id = user_id
        WHERE fk_spotrev_spot_id = ?
        ORDER BY reg_time DESC
        `
        const result = await db.query(sql, [spotId])

        return result
    },
    selectReviewEng: async (spotId) => {
        let sql = `
        SELECT spot_review.id as review_id, name, title_eng as title, content_eng as content, img, score as review_score, DATE_FORMAT(reg_time, '%Y-%c-%e') as reg_time
        FROM spot_review
        JOIN
        user
        ON
        fk_spotrev_user_user_id = user_id
        WHERE fk_spotrev_spot_id = ?
        ORDER BY reg_time DESC
        `

        const result = await db.query(sql, [spotId])

        return result
    },
    insertReview: async (spotId, userId, titleKor, contentKor, titleEng, contentEng, reviewScore, img) => {
        
        let sql = `
        INSERT INTO
        spot_review
        (fk_spotrev_spot_id,fk_spotrev_user_user_id,title_kor, content_kor, title_eng, content_eng, score, img)
        VALUES 
        (?,?,?,?,?,?,?,?)
        `

        await db.query(sql, [spotId, userId, titleKor, contentKor, titleEng, contentEng, reviewScore, img])
    },
    updateReviewCount : async (reviewScore, spotId) => {

        // spot 평점 및 review count 증가
        const sql = `
        UPDATE spot 
        SET 
        grade_sum = grade_sum + ?, review_cnt = review_cnt + 1
        WHERE id = ?
        `

        await db.query(sql, [reviewScore, spotId])
    },
    insertSpot: async (req) => {
        /*
        type
        open_time
        close_time
        contact
        link
        latitude
        longitude
        line_number

        name
        description
        station
        prev_station
        next_station
        address
        address_gu

        spot_img

        channel_id
        */

        let sql = `
        INSERT INTO
        spot
        (type, open_time, close_time, contact, latitude, longitude, line_number)
        VALUES
        (?,?,?,?,?,?,?)
        `

        const spotId = await db.query(sql, [req.body.type, req.body.open_time, req.body.close_time, req.body.contact, req.body.latitude, req.body.longitude, req.body.line_number])

        return spotId
    },
    insertSpotInfoKor : async (spotId, req) => {
        // spot_kor
        const sql = `
        INSERT INTO
        spot_kor
        (fk_kor_spot_id, name, description, address, address_gu, station, prev_station, next_station)
        VALUES
        (?,?,?,?,?,?,?,?)
        `

        await db.query(sql, [spotId, req.body.name, req.body.description, req.body.address, req.body.address_gu, req.body.station, req.body.prev_station, req.body.next_station])
    },
    insertSpotInfoEng : async (spotId, name_eng, description_eng, station_eng, prev_station_eng, next_station_eng, address_eng, address_gu_eng) => {
        // spot_eng
        const sql = `
        INSERT INTO
        spot_eng
        (fk_eng_spot_id, name, description, address, address_gu, station, prev_station, next_station)
        VALUES
        (?,?,?,?,?,?,?,?)
        `

        await db.query(sql, [spotId, name_eng, description_eng, address_eng, address_gu_eng, station_eng, prev_station_eng, next_station_eng])
    },
    insertSpotImg : async (spotId, fileLocation) => {
        // img
        const sql = `
        INSERT INTO
        spot_img
        (fk_spotimg_spot_id, img)
        VALUES
        (?,?)
        `

        await db.query(sql, [spotId, fileLocation])
    },
    insertSpotChannelMap: async(spotId, channelId) => {
        // 연관 채널 등록
        const query = `
        INSERT INTO 
        map_channel_spot 
        (fk_map_ch_id, fk_map_spot_id) 
        VALUES 
        (?, ?);
        `
        
        await db.query(query, [channelId, spotId])
    }
    ,
    // see place more in channel
    selectPlaceWithChannelKor: async (channelId) => {
        const sql = `
        SELECT
        spot_info.id AS spot_id, GROUP_CONCAT(channel_info.channel_id) AS channel_id, GROUP_CONCAT(thumbnail_img) AS thumbnail_img, IFNULL(ROUND(review_score,1),0) as review_score, name, description, address_gu, station, img
        FROM
            (
                SELECT 
                spot.id AS id, grade_sum/review_cnt AS review_score, grade_sum, scrap_cnt, name, description, address_gu, station, img, review_cnt, reg_time
                FROM 
					(
						SELECT
						spot.id, type, grade_sum, scrap_cnt, review_cnt, reg_time
						FROM
						spot
						JOIN
						map_channel_spot
						ON
						spot.id = fk_map_spot_id
						WHERE
						fk_map_ch_id = ?
					) as spot
                JOIN
                    (
                        SELECT 
                        fk_kor_spot_id AS id, name, description, address_gu, station, img
                        FROM spot_kor
                        JOIN spot_img
                        ON spot_kor.fk_kor_spot_id = spot_img.fk_spotimg_spot_id
                        GROUP BY id
                    ) AS spot_detail
                ON spot.id = spot_detail.id
                WHERE type NOT IN (3,4,5)
            ) AS spot_info
        LEFT JOIN
            (
                SELECT
                fk_map_spot_id AS spot_id, channel.id AS channel_id, thumbnail_img 
                FROM channel
                JOIN map_channel_spot
                ON channel.id = map_channel_spot.fk_map_ch_id
                WHERE is_member = 0
            ) AS channel_info
        ON spot_info.id = channel_info.spot_id
        GROUP BY spot_id
    `

    const result = await db.query(sql, [channelId])

    return result
    },
    selectPlaceWithChannelEng : async(channelId) => {
        const sql = `
        SELECT
        spot_info.id AS spot_id, GROUP_CONCAT(channel_info.channel_id) AS channel_id, GROUP_CONCAT(thumbnail_img) AS thumbnail_img, IFNULL(ROUND(review_score,1),0) as review_score, name, description, address_gu, station, img
        FROM
            (
                SELECT 
                spot.id AS id, grade_sum/review_cnt AS review_score, grade_sum, scrap_cnt, name, description, address_gu, station, img, review_cnt, reg_time
                FROM 
					(
						SELECT
						spot.id, type, grade_sum, scrap_cnt, review_cnt, reg_time
						FROM
						spot
						JOIN
						map_channel_spot
						ON
						spot.id = fk_map_spot_id
						WHERE
						fk_map_ch_id = ?
					) as spot
                JOIN
                    (
                        SELECT 
                        fk_eng_spot_id AS id, name, description, address_gu, station, img
                        FROM spot_eng
                        JOIN spot_img
                        ON spot_eng.fk_eng_spot_id = spot_img.fk_spotimg_spot_id
                        GROUP BY id
                    ) AS spot_detail
                ON spot.id = spot_detail.id
                WHERE type NOT IN (3,4,5)
            ) AS spot_info
        LEFT JOIN
            (
                SELECT
                fk_map_spot_id AS spot_id, channel.id AS channel_id, thumbnail_img 
                FROM channel
                JOIN map_channel_spot
                ON channel.id = map_channel_spot.fk_map_ch_id
                WHERE is_member = 0
            ) AS channel_info
        ON spot_info.id = channel_info.spot_id
        GROUP BY spot_id
        `

        const result = await db.query(sql, [channelId])

        return result
    },
    selectEventWithChannelKor: async (channelId) => {
        const sql = `
        SELECT
        spot_info.id AS spot_id, GROUP_CONCAT(channel_info.channel_id) AS channel_id, GROUP_CONCAT(thumbnail_img) AS thumbnail_img, IFNULL(ROUND(review_score,1),0) as review_score, name, description, address_gu, station, img
        FROM
            (
                SELECT 
                spot.id AS id, grade_sum/review_cnt AS review_score, grade_sum, scrap_cnt, name, description, address_gu, station, img, review_cnt, reg_time
                FROM 
					(
						SELECT
						spot.id, type, grade_sum, scrap_cnt, review_cnt, reg_time
						FROM
						spot
						JOIN
						map_channel_spot
						ON
						spot.id = fk_map_spot_id
						WHERE
						fk_map_ch_id = ?
					) as spot
                JOIN
                    (
                        SELECT 
                        fk_kor_spot_id AS id, name, description, address_gu, station, img
                        FROM spot_kor
                        JOIN spot_img
                        ON spot_kor.fk_kor_spot_id = spot_img.fk_spotimg_spot_id
                        GROUP BY id
                    ) AS spot_detail
                ON spot.id = spot_detail.id
                WHERE type IN (3,4,5)
            ) AS spot_info
        LEFT JOIN
            (
                SELECT
                fk_map_spot_id AS spot_id, channel.id AS channel_id, thumbnail_img 
                FROM channel
                JOIN map_channel_spot
                ON channel.id = map_channel_spot.fk_map_ch_id
                WHERE is_member = 0
            ) AS channel_info
        ON spot_info.id = channel_info.spot_id
        GROUP BY spot_id
        `

        const result = await db.query(sql, [channelId])

        return result
    },
    selectEventWithChannelEng: async (channelId) => {
        const sql = `
        SELECT
        spot_info.id AS spot_id, GROUP_CONCAT(channel_info.channel_id) AS channel_id, GROUP_CONCAT(thumbnail_img) AS thumbnail_img, IFNULL(ROUND(review_score,1),0) as review_score, name, description, address_gu, station, img
        FROM
            (
                SELECT 
                spot.id AS id, grade_sum/review_cnt AS review_score, grade_sum, scrap_cnt, name, description, address_gu, station, img, review_cnt, reg_time
                FROM 
					(
						SELECT
						spot.id, type, grade_sum, scrap_cnt, review_cnt, reg_time
						FROM
						spot
						JOIN
						map_channel_spot
						ON
						spot.id = fk_map_spot_id
						WHERE
						fk_map_ch_id = ?
					) as spot
                JOIN
                    (
                        SELECT 
                        fk_eng_spot_id AS id, name, description, address_gu, station, img
                        FROM spot_eng
                        JOIN spot_img
                        ON spot_eng.fk_eng_spot_id = spot_img.fk_spotimg_spot_id
                        GROUP BY id
                    ) AS spot_detail
                ON spot.id = spot_detail.id
                WHERE type IN (3,4,5)
            ) AS spot_info
        LEFT JOIN
            (
                SELECT
                fk_map_spot_id AS spot_id, channel.id AS channel_id, thumbnail_img 
                FROM channel
                JOIN map_channel_spot
                ON channel.id = map_channel_spot.fk_map_ch_id
                WHERE is_member = 0
            ) AS channel_info
        ON spot_info.id = channel_info.spot_id
        GROUP BY spot_id
        `
        
        const result = await db.query(sql, [channelId])

        return result
    },


    // 유저의 장소 스크랩 삽입
    insertScrapSpot : async function(userId, spotId) {
 
        let query = 
        `
        INSERT INTO scrap_spot
            (fk_scrap_user_user_id, fk_scrap_spot_id)
        VALUES (?, ?);
        `;

        await db.query(query, [userId, spotId]);
    },
    // 유저의 장소 스크랩 삭제
    deleteScrapSpot : async function(userId, spotId) {
 
        let query = 
        `
        DELETE
        FROM scrap_spot
        WHERE fk_scrap_user_user_id = ?
        AND fk_scrap_spot_id = ?;
        `;

        await db.query(query, [userId, spotId]);
    },
    updateScrapCount : async (spotId, n) => {

        // spot 스크랩 수 갱신
        const sql = 
        `
        UPDATE spot 
        SET 
        scrap_cnt = scrap_cnt + ?
        WHERE id = ?;
        `;

        await db.query(sql, [n, spotId]);
    },


    // 스팟 이미지 한장 가져오기
    selectSpotImg : async function (spotId) {

        let query =
        `
        SELECT img
        FROM spot_img
        WHERE fk_spotimg_spot_id = ?
        LIMIT 1;
        `;

        let data = await db.query(query, [spotId]);

        // img
        return data;
    },
    // 장소 인덱스랑 제목 가져오기
    selectSpotList : async function () {

        let query =
        `
        SELECT spot.id AS spot_id, spot_kor.name AS spot_name
        FROM spot, spot_kor
        WHERE spot.id = spot_kor.fk_kor_spot_id
        ORDER BY spot.id DESC
        `;

        let data = await db.query(query, []);

        // spot_id, spot_name
        return data;
    },
    selectSpotByTimeStampDiff : async () => {
        const sql = `
        SELECT spot.id as spot_id, fk_map_ch_id as channel_id
        FROM spot
        JOIN
        map_channel_spot
        ON
        spot.id = map_channel_spot.fk_map_spot_id
        WHERE TIMESTAMPDIFF(second, spot.reg_time, NOW()) > 86400 AND TIMESTAMPDIFF(second, spot.reg_time, NOW()) < 200000
        GROUP BY channel_id
        `

        const result = await db.query(sql)

        return result
    },
    // 이벤트 리스트 (채널별로 묶기) - Kor
    selectEventListKor : async () => {

        let query =
        `
        SELECT
        spot_info.id AS spot_id, GROUP_CONCAT(channel_info.channel_id) AS channel_id, GROUP_CONCAT(thumbnail_img) AS channel_img, IFNULL(ROUND(review_score,1),0) as review_score, spot_info.name, description, address_gu, station, img as spot_img
        FROM
        
            (
                SELECT 
                spot.id AS id, grade_sum/review_cnt AS review_score, grade_sum, scrap_cnt, name, description, address_gu, station, img, review_cnt, reg_time
                FROM spot
                JOIN
                    (
                        SELECT 
                        fk_kor_spot_id AS id, name, description, address_gu, station, img
                        FROM spot_kor
                        JOIN spot_img
                        ON spot_kor.fk_kor_spot_id = spot_img.fk_spotimg_spot_id
                        GROUP BY id
                    ) AS spot_detail
                ON spot.id = spot_detail.id
                WHERE type IN (3,4,5)
            ) AS spot_info
            
        LEFT JOIN
        
            (
                SELECT
                fk_map_spot_id AS spot_id, channel.id AS channel_id, channel.kor_name AS name, thumbnail_img
                FROM channel
                JOIN map_channel_spot
                ON channel.id = map_channel_spot.fk_map_ch_id
            ) AS channel_info
            
        ON spot_info.id = channel_info.spot_id
        GROUP BY spot_id
        ORDER BY channel_info.name
        `;

        let data = await db.query(query, []);

        return data;
    },
    // 이벤트 리스트 (채널별로 묶기) - Eng
    selectEventListEng : async () => {

        let query =
        `
        SELECT
        spot_info.id AS spot_id, GROUP_CONCAT(channel_info.channel_id) AS channel_id, GROUP_CONCAT(thumbnail_img) AS channel_img, IFNULL(ROUND(review_score,1),0) as review_score, spot_info.name, description, address_gu, station, img as spot_img
        FROM
            (
                SELECT 
                spot.id AS id, grade_sum/review_cnt AS review_score, grade_sum, scrap_cnt, name, description, address_gu, station, img, review_cnt, reg_time
                FROM spot
                JOIN
                    (
                        SELECT 
                        fk_eng_spot_id AS id, name, description, address_gu, station, img
                        FROM spot_eng
                        JOIN spot_img
                        ON spot_eng.fk_eng_spot_id = spot_img.fk_spotimg_spot_id
                        GROUP BY id
                    ) AS spot_detail
                ON spot.id = spot_detail.id
                WHERE type IN (3,4,5)
            ) AS spot_info
            
        LEFT JOIN
        
            (
                SELECT
                fk_map_spot_id AS spot_id, channel.id AS channel_id, channel.eng_name AS name, thumbnail_img
                FROM channel
                JOIN map_channel_spot
                ON channel.id = map_channel_spot.fk_map_ch_id
            ) AS channel_info
            
        ON spot_info.id = channel_info.spot_id
        GROUP BY spot_id
        order by channel_info.name
        `;

        let data = await db.query(query, []);

        return data;
    },

    // for search
    selectBestEventKor : async (n) => {

        let query = 
        `
        SELECT spot.id as spot_id, concat('# ', spot_kor.name) AS name
        FROM spot, spot_kor
        WHERE spot.id = spot_kor.fk_kor_spot_id
        AND type IN (3,4,5)
        ORDER BY RAND()
        LIMIT ?;
        `;

        let data = await db.query(query, [n]);

        return data;
    },
    selectBestEventEng : async (n) => {

        let query = 
        `
        SELECT spot.id as spot_id, concat('# ', spot_eng.name) AS name
        FROM spot, spot_eng
        WHERE spot.id = spot_eng.fk_eng_spot_id
        AND type IN (3,4,5)
        ORDER BY RAND()
        LIMIT ?;
        `;

        let data = await db.query(query, [n]);

        return data;
    },
    // by keyword
    selectSpotListByKeywordKor : async (isEvent) => {

        let query =
        `
        SELECT spot.type, spot.id AS spot_id, spot_detail.name AS name, description, img, address_gu, station, scrap_cnt
        FROM spot LEFT JOIN 
            (
                SELECT spot_kor.name, img, spot_kor.fk_kor_spot_id AS spot_id, spot_kor.description, spot_kor.address_gu, spot_kor.station
                FROM spot_kor LEFT JOIN spot_img
                ON spot_kor.fk_kor_spot_id = spot_img.fk_spotimg_spot_id
                GROUP BY spot_kor.fk_kor_spot_id
            ) AS spot_detail
        ON spot.id = spot_detail.spot_id
        `;

        // 장소
        if (isEvent == 0) {
            query += 
            `
            WHERE spot.type NOT IN (3,4,5)
            ORDER BY name
            `;
        }
        // 이벤트
        else if (isEvent == 1) {
            query += 
            `
            WHERE spot.type IN (3,4,5)
            ORDER BY name
            `;
        }

        let data = await db.query(query, []);

        return data;
    },
    selectSpotListByKeywordEng : async (isEvent) => {

        let query =
        `
        SELECT spot.type, spot.id AS spot_id, spot_detail.name AS name, description, img, address_gu, station, scrap_cnt
        FROM spot LEFT JOIN 
            (
                SELECT spot_eng.name, img, spot_eng.fk_eng_spot_id AS spot_id, spot_eng.description, spot_eng.address_gu, spot_eng.station
                FROM spot_eng LEFT JOIN spot_img
                ON spot_eng.fk_eng_spot_id = spot_img.fk_spotimg_spot_id
                GROUP BY spot_eng.fk_eng_spot_id
            ) AS spot_detail
        ON spot.id = spot_detail.spot_id
        `;

        // 장소
        if (isEvent == 0) {
            query += 
            `
            WHERE spot.type NOT IN (3,4,5)
            ORDER BY name
            `;
        }
        // 이벤트
        else if (isEvent == 1) {
            query += 
            `
            WHERE spot.type IN (3,4,5)
            ORDER BY name
            `;
        }

        let data = await db.query(query, []);

        return data;
    },

    
    // search filter
    selectSearchSpotFilterByKeywordKor : async (order, type) => {

        let query =
        `
        SELECT spot.type, spot.id AS spot_id, spot_detail.name AS name, description, img, address_gu, station, scrap_cnt
        FROM spot LEFT JOIN 
            (
                SELECT spot_kor.name, img, spot_kor.fk_kor_spot_id AS spot_id, spot_kor.description, spot_kor.address_gu, spot_kor.station
                FROM spot_kor LEFT JOIN spot_img
                ON spot_kor.fk_kor_spot_id = spot_img.fk_spotimg_spot_id
                GROUP BY spot_kor.fk_kor_spot_id
            ) AS spot_detail
        ON spot.id = spot_detail.spot_id
        WHERE spot.type IN (?)
        `;

        // 인기순
        if (order == 0) {
            query +=
            `
            ORDER BY spot.grade_sum + 2 * spot.scrap_cnt DESC
            `;
        }
        // 최신 순
        else if (order == 1) {
            query +=
            `
            ORDER BY reg_time DESC
            `;
        }

        let data = await db.query(query, [type]);

        return data;
    },
    selectSearchSpotFilterByKeywordEng : async (order, type) => {

        let query =
        `
        SELECT spot.type, spot.id AS spot_id, spot_detail.name AS name, description, img, address_gu, station, scrap_cnt
        FROM spot LEFT JOIN 
            (
                SELECT spot_eng.name, img, spot_eng.fk_eng_spot_id AS spot_id, spot_eng.description, spot_eng.address_gu, spot_eng.station
                FROM spot_eng LEFT JOIN spot_img
                ON spot_eng.fk_eng_spot_id = spot_img.fk_spotimg_spot_id
                GROUP BY spot_eng.fk_eng_spot_id
            ) AS spot_detail
        ON spot.id = spot_detail.spot_id
        WHERE spot.type IN (?)
        `;

        // 인기순
        if (order == 0) {
            query +=
            `
            ORDER BY spot.grade_sum + 2 * spot.scrap_cnt DESC
            `;
        }
        // 최신 순
        else if (order == 1) {
            query +=
            `
            ORDER BY reg_time DESC
            `;
        }

        let data = await db.query(query, [type]);

        return data;
    },
    selectAllSpotInfo : async() => {
        const sql = `
        SELECT 
        spot.id as spot_id, type, grade_sum, review_cnt, scrap_cnt, line_number, open_time, close_time, contact, latitude, longitude, reg_time, 
        spot_eng.name as eng_name, spot_eng.description as eng_description, spot_eng.address as eng_address, spot_eng.address_gu as eng_address_gu, spot_eng.station as eng_station, spot_eng.prev_station as eng_prev_station, spot_eng.next_station as eng_next_station, 
        spot_kor.name as kor_name, spot_kor.description as kor_description, spot_kor.address as kor_address, spot_kor.address_gu as kor_address_gu, spot_kor.station as kor_station, spot_kor.prev_station as kor_prev_station, spot_kor.next_station as kor_next_station 
        FROM spot, spot_eng, spot_kor 
        WHERE spot.id = spot_eng.fk_eng_spot_id AND spot.id = spot_kor.fk_kor_spot_id
        `

        const result = await db.query(sql)
        
        return result
    },
    selectSpotInfo : async (spotId) => {
        const sql = `
        SELECT 
        spot.id as spot_id, type, grade_sum, review_cnt, scrap_cnt, line_number, open_time, close_time, contact, latitude, longitude, reg_time, 
        spot_eng.name as eng_name, spot_eng.description as eng_description, spot_eng.address as eng_address, spot_eng.address_gu as eng_address_gu, spot_eng.station as eng_station, spot_eng.prev_station as eng_prev_station, spot_eng.next_station as eng_next_station, 
        spot_kor.name as kor_name, spot_kor.description as kor_description, spot_kor.address as kor_address, spot_kor.address_gu as kor_address_gu, spot_kor.station as kor_station, spot_kor.prev_station as kor_prev_station, spot_kor.next_station as kor_next_station 
        FROM spot, spot_eng, spot_kor 
        WHERE spot.id = spot_eng.fk_eng_spot_id AND spot.id = spot_kor.fk_kor_spot_id AND spot.id = ?
        `

        const result = await db.query(sql, [spotId])

        return result
    },
    selectAllSpotImg : async () => {
        const sql = `
        SELECT fk_spotimg_spot_id as spot_id, img FROM spot_img
        `

        const result = await db.query(sql)

        return result
    },
    selectSpotImgById : async (spotId) => {
        const sql = `
        SELECT id as img_id, fk_spotimg_spot_id as spot_id, img FROM spot_img WHERE fk_spotimg_spot_id = ?
        `

        const result = await db.query(sql, [spotId])

        return result
    },
    deleteSpot : async (spotId) => {
        const sql = `
        DELETE FROM spot WHERE id = ?
        `

        await db.query(sql, [spotId])
    },
    updateSpotKor : async (name, description, address, address_gu, spotId) => {
        const sql = `
        UPDATE spot_kor
        SET name = ?, description = ?, address = ?, address_gu = ?
        WHERE fk_kor_spot_id = ?
        `

        await db.query(sql, [name, description, address, address_gu, spotId])
    },
    updateSpotEng : async (name, description, address, address_gu, spotId) => {
        const sql = `
        UPDATE spot_eng
        SET name = ?, description = ?, address = ?, address_gu = ?
        WHERE fk_eng_spot_id = ?
        `

        await db.query(sql, [name, description, address, address_gu, spotId])
    },


    selectAllSpotAddressKor : async () => {
        const sql = `
        SELECT 
        fk_kor_spot_id as spot_id, address
        FROM spot_kor
        `

        const result = await db.query(sql, [])

        return result
    },
    updateSpotAddressEng : async (spotId, address) => {
        const sql = `
        UPDATE spot_eng
        SET address = ?
        WHERE fk_eng_spot_id = ?
        `

        await db.query(sql, [address, spotId])
    },


}
