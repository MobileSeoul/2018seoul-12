const db = require('../lib/db')

module.exports = {
    selectUser : async (userId) => {
        const sql = `
        SELECT user_id, name, profile_img  
        FROM user 
        WHERE user_id = ?
        `

        const result = await db.query(sql, userId)

        return result
    },
    insertUser : async (userId, name, profileImg) => {
        const sql = `
        INSERT INTO user
            (user_id, name, profile_img) 
        VALUES (?, ?, ?)
        `

        await db.query(sql, [userId, name, profileImg])
    },
    selectSubscriptionPreview : async (userId, count) => {
        const sql = `
        SELECT 
        channel.id as channel_id, kor_name, eng_name, background_img 
        FROM channel 
        JOIN subscription_channel as sub_ch 
        ON channel.id = sub_ch.fk_sub_ch_id 
        WHERE sub_ch.fk_sub_user_user_id = ? 
        ORDER BY reg_time
        LIMIT ?
        `

        const result = await db.query(sql, [userId, count])

        return result
    },
    selectSubscriptionBroadcastKor : async (userId) => {
        const sql = `
        SELECT 
        channel.id as channel_id, kor_name as name, thumbnail_img, new_post_check
        FROM channel 
        JOIN subscription_channel as sub_ch 
        ON channel.id = sub_ch.fk_sub_ch_id 
        WHERE sub_ch.fk_sub_user_user_id = ?
        AND type = 1
        ORDER BY reg_time
        `

        const result = await db.query(sql, [userId])

        return result
    },
    selectSubscriptionCelebrityKor : async (userId) => {
        const sql = `
        SELECT 
        channel.id as channel_id, kor_name as name, thumbnail_img, new_post_check
        FROM channel 
        JOIN subscription_channel as sub_ch 
        ON channel.id = sub_ch.fk_sub_ch_id 
        WHERE sub_ch.fk_sub_user_user_id = ?
        AND type = 0
        ORDER BY reg_time
        `

        const result = await db.query(sql, [userId])

        return result
    },
    selectSubscriptionBroadcastEng : async (userId) => {
        const sql = `
        SELECT 
        channel.id as channel_id, eng_name as name, thumbnail_img, new_post_check
        FROM channel 
        JOIN subscription_channel as sub_ch 
        ON channel.id = sub_ch.fk_sub_ch_id 
        WHERE sub_ch.fk_sub_user_user_id = ?
        AND type = 1
        ORDER BY reg_time
        `

        const result = await db.query(sql, [userId])

        return result
    },
    selectSubscriptionCelebrityEng : async (userId) => {
        const sql = `
        SELECT 
        channel.id as channel_id, eng_name as name, thumbnail_img, new_post_check
        FROM channel 
        JOIN subscription_channel as sub_ch 
        ON channel.id = sub_ch.fk_sub_ch_id 
        WHERE sub_ch.fk_sub_user_user_id = ? AND type = 0
        ORDER BY reg_time
        `

        const result = await db.query(sql, [userId])

        return result
    },
    updateUser : async (userId, name, profileImg) => {
        const sql = `
        UPDATE 
        user
        SET name = ?, profile_img = ?
        WHERE user_id = ?
        `

        await db.query(sql, [name, profileImg, userId])
    },
    selectScrapKor : async (userId) => {
        const sql = `
        SELECT
        spot_info.id AS spot_id, group_concat(channel_info.channel_id) AS channel_id, group_concat(thumbnail_img) AS thumbnail_img, IFNULL(ROUND(review_score,1),0) as review_score, name, description, address_gu, station, img
        FROM
        (
            SELECT user_spot. id as id, review_score, grade_sum, scrap_cnt, review_cnt, name, description, address_gu, station, img, reg_time 
            FROM
                (
                    SELECT spot.id AS id, grade_sum/review_cnt AS review_score, type, grade_sum, scrap_cnt, review_cnt, reg_time 
                    FROM spot 
                    JOIN scrap_spot 
                    ON spot.id = scrap_spot.fk_scrap_spot_id 
                    WHERE scrap_spot.fk_scrap_user_user_id = ?
                ) as user_spot
            JOIN
                (
                    SELECT 
                    fk_kor_spot_id AS id, name, description, address_gu, station, img
                    FROM spot_kor
                    JOIN spot_img
                    ON spot_kor.fk_kor_spot_id = spot_img.fk_spotimg_spot_id
                    GROUP BY id
                ) AS spot_detail
            ON user_spot.id = spot_detail.id
		) as spot_info
        LEFT JOIN
            (
                SELECT
                fk_map_spot_id AS spot_id, channel.id AS channel_id, thumbnail_img 
                FROM channel
                JOIN map_channel_spot
                ON channel.id = map_channel_spot.fk_map_ch_id
            ) AS channel_info
        ON spot_info.id = channel_info.spot_id
        GROUP BY spot_id
        `

        const result = await db.query(sql, [userId])

        return result
    },
    selectScrapEng : async (userId) => {
        const sql = `
        SELECT
        spot_info.id AS spot_id, group_concat(channel_info.channel_id) AS channel_id, group_concat(thumbnail_img) AS thumbnail_img, IFNULL(ROUND(review_score,1),0) as review_score, name, description, address_gu, station, img
        FROM
        (
            SELECT user_spot. id as id, review_score, grade_sum, scrap_cnt, review_cnt, name, description, address_gu, station, img, reg_time 
            FROM
                (
                    SELECT spot.id AS id, grade_sum/review_cnt AS review_score, type, grade_sum, scrap_cnt, review_cnt, reg_time 
                    FROM spot 
                    JOIN scrap_spot 
                    ON spot.id = scrap_spot.fk_scrap_spot_id 
                    WHERE scrap_spot.fk_scrap_user_user_id = ?
                ) as user_spot
            JOIN
                (
                    SELECT 
                    fk_eng_spot_id AS id, name, description, address_gu, station, img
                    FROM spot_eng
                    JOIN spot_img
                    ON spot_eng.fk_eng_spot_id = spot_img.fk_spotimg_spot_id
                    GROUP BY id
                ) AS spot_detail
            ON user_spot.id = spot_detail.id
		) as spot_info
        LEFT JOIN
            (
                SELECT
                fk_map_spot_id AS spot_id, channel.id AS channel_id, thumbnail_img 
                FROM channel
                JOIN map_channel_spot
                ON channel.id = map_channel_spot.fk_map_ch_id
            ) AS channel_info
        ON spot_info.id = channel_info.spot_id
        GROUP BY spot_id
        `

        const result = await db.query(sql, [userId])

        return result
    },
    selectUserName : async (name, user_id) => {
        const sql = 
        `
        SELECT name
        FROM user
        WHERE name = ? AND user_id not like ?
        `

        const result = await db.query(sql, [name, user_id])

        return result
    },
    selectUserAdmin : async (userId) => {
        const sql = 
        `
        SELECT admin
        FROM user
        WHERE user_id = ?
        `

        const result = await db.query(sql, [userId])

        return result
    },
    selectScrapUserWithSpot : async (spotId, userId) => {
        const sql = 
        `
        SELECT fk_scrap_user_user_id
        FROM scrap_spot
        WHERE fk_scrap_spot_id = ?
        AND fk_scrap_user_user_id = ?
        `;

        const result = await db.query(sql, [spotId, userId])
        
        return result
    }
}