const db = require('../lib/db');

module.exports = {

    // 채널들 for admin
    selectChannels : async function(type) {
        
        let query = 
        `
        SELECT id AS channel_id, kor_name AS channel_name
        FROM channel
        WHERE type = ?
        `;
        let data = await db.query(query, [type]);

        // channel_id, channel_name
        return data;
    },

    // 채널의 목록 보기
    selectChannelList : async function(userId, type) {
        
        let query = 
        `
        SELECT channel.id AS channel_id, kor_name, eng_name, subscription_cnt, spot_cnt, thumbnail_img, fk_sub_user_user_id
        FROM
            channel
        LEFT JOIN
            (
                SELECT fk_sub_user_user_id, fk_sub_ch_id FROM subscription_channel
                WHERE subscription_channel.fk_sub_user_user_id = ?
                ) AS sub_ch 
        ON channel.id = sub_ch.fk_sub_ch_id 
        WHERE type = ?
        AND is_member = 0
        ORDER BY sub_ch.fk_sub_user_user_id DESC, subscription_cnt DESC;
        `;
        let data = await db.query(query, [userId, type]);

        // channel_id, kor_name, eng_name, thumbnail_img, subscription_cnt, spot_cnt, fk_sub_user_user_id
        return data;
    },
    // 채널의 상세 정보 보기
    selectChannelDetail : async function(userId, channelId) {
        
        let query = 
        `
        SELECT id, kor_name, eng_name, kor_company, eng_company, background_img, thumbnail_img, subscription_cnt, fk_sub_user_user_id
        FROM channel
        LEFT JOIN
            (
                SELECT fk_sub_user_user_id, fk_sub_ch_id
                FROM subscription_channel
                WHERE subscription_channel.fk_sub_user_user_id = ?
            ) AS sub_ch 
        ON channel.id = sub_ch.fk_sub_ch_id 
        WHERE channel.id = ?;
        `;

        let data = await db.query(query, [userId, channelId]);

        // id, kor_name, eng_name, kor_company, eng_company, background_img, thumbnail_img, subscription_cnt, fk_sub_user_user_id
        return data;
    },
    // 유저의 채널 구독정보 삽입
    insertSubscriptionChannel : async function(userId, channelId) {
 
        let query = 
        `
        INSERT INTO subscription_channel
            (fk_sub_user_user_id, fk_sub_ch_id)
        VALUES (?, ?);
        `;

        await db.query(query, [userId, channelId]);
    },
    // 유저의 채널 구독정보 삭제
    deleteSubscriptionChannel : async function(userId, channelId) {
 
        let query = 
        `
        DELETE
        FROM subscription_channel
        WHERE fk_sub_user_user_id = ?
        AND fk_sub_ch_id = ?;
        `;

        await db.query(query, [userId, channelId]);
    },
    // 채널관련 새로운 스팟 생성 시 
    updateChannelNewPostCheck : async function(channelId) {
            
        let query = 
        `
        UPDATE channel
        SET new_post_check = 1
        WHERE id = ?;
        `;

        await db.query(query, [channelId]);
    },
    updateNewPostCheck : async (channelId) => {
        const sql = 
        `
        UPDATE
        channel
        SET new_post_check = 0
        WHERE id = ?
        `

        await db.query(sql, [channelId])
    },
    updateSubscriptionCount : async (channelId, n) => {
        const sql = 
        `
        UPDATE
        channel
        SET 
        subscription_cnt = subscription_cnt + ?
        WHERE id = ?
        `

        await db.query(sql, [n, channelId])
    },
    updateSpotCount : async (channelId, n) => {
        const sql = 
        `
        UPDATE
        channel
        SET 
        spot_cnt = spot_cnt + ?
        WHERE id = ?
        `

        await db.query(sql, [n, channelId])
    },

    // for search
    // 인기있는 n개의 채널 - kor
    selectChannelKeywordKor : async (type, n) => {

        let query = 
        `
        SELECT channel.id as channel_id, concat('# ', kor_name) AS name
        FROM channel
        WHERE type = ?
        AND is_member = 0
        ORDER BY RAND()
        LIMIT ?;
        `;

        let data = await db.query(query, [type, n]);

        return data;
    },
    // 인기있는 n개의 채널 - eng
    selectChannelKeywordEng : async (type, n) => {

        let query = 
        `
        SELECT channel.id as channel_id, concat('# ', eng_name) AS name
        FROM channel
        WHERE type = ?
        AND is_member = 0
        ORDER BY RAND()
        LIMIT ?;
        `;

        let data = await db.query(query, [type, n]);

        return data;
    },




    // for admin
    // 새로운 채널(방송, 그룹, 멤버)를 삽입
    insertChannel : async function(type, isMember, korName, engName, korCompany, engCompany, backgroundImg, thumbnailImg) {
        
        let query = 
        `
        INSERT INTO channel
            (type, kor_name, eng_name, kor_company, eng_company, background_img, thumbnail_img, is_member)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?);
        `;

        let data = await db.query(query, [type, korName, engName, korCompany, engCompany, backgroundImg, thumbnailImg, isMember]);

        return data;
    },
    // 채널 삭제
    deleteChannel : async function(channelId) {
 
        let query = 
        `
        DELETE
        FROM channel
        WHERE id = ?;
        `;

        await db.query(query, [channelId]);
    },
    // 채널 수정을 위한 채널정보 보여주기
    selectChannelForEdit : async function(channelId) {
 
        let query = 
        `
        SELECT CASE is_member
                    WHEN 0 THEN kor_name 
                    ELSE (SELECT c.kor_name FROM channel AS c WHERE c.id = ch.is_member)
                    END AS belong_to,
			id, kor_name, eng_name, kor_company, eng_company, background_img, thumbnail_img
        FROM channel AS ch
        where ch.id = ?;
        `;

        let data = await db.query(query, [channelId]);

        return data;
    },
    updateChannel : async (channelId, korName, engName, korCompany, engCompany, backgroundImg, thumbnailImg) => {
        
        const query = 
        `
        UPDATE 
        channel
        SET kor_name = ?, eng_name = ?, kor_company = ?, eng_company = ?, background_img = ?, thumbnail_img = ?
        WHERE id = ?;
        `

        await db.query(query, [korName, engName, korCompany, engCompany, backgroundImg, thumbnailImg, channelId])
    },
    updateChannelMember : async (channelId, backgroundImg, thumbnailImg) => {
        
        const query = 
        `
        UPDATE 
        channel
        SET background_img = ?, thumbnail_img = ?
        WHERE is_member = ?;
        `

        await db.query(query, [backgroundImg, thumbnailImg, channelId])
    },




    // for search
    selectChannelByKeywordKor : async (userId, channelId) => {

        let query = 
        `
        SELECT channel.id AS channel_id, channel.kor_name AS name, subscription_cnt, spot_cnt, thumbnail_img, fk_sub_user_user_id
        FROM
            channel
        LEFT JOIN
            (
                SELECT fk_sub_user_user_id, fk_sub_ch_id FROM subscription_channel
                WHERE subscription_channel.fk_sub_user_user_id = ?
                ) AS sub_ch 
        ON channel.id = sub_ch.fk_sub_ch_id 
        WHERE channel.id = ?
        ORDER BY channel.kor_name
        `;

        let data = await db.query(query, [userId, channelId]);

        return data;
    },
    selectChannelByKeywordEng : async (userId, channelId) => {

        let query = 
        `
        SELECT channel.id AS channel_id, channel.eng_name AS name, subscription_cnt, spot_cnt, thumbnail_img, fk_sub_user_user_id
        FROM
            channel
        LEFT JOIN
            (
                SELECT fk_sub_user_user_id, fk_sub_ch_id FROM subscription_channel
                WHERE subscription_channel.fk_sub_user_user_id = ?
                ) AS sub_ch 
        ON channel.id = sub_ch.fk_sub_ch_id 
        WHERE channel.id = ?
        ORDER BY channel.eng_name
        `;

        let data = await db.query(query, [userId, channelId]);

        return data;
    },
    // 모든 채널의 인덱스와 이름
    selectChannelName : async function() {
        
        let query = 
        `
        SELECT channel.id AS channel_id, kor_name, eng_name, is_member
        FROM channel
        `;
        let data = await db.query(query, []);

        // channel_id, kor_name, eng_name, is_member
        return data;
    },
    // 멤버 인지 아닌지
    selectChannelForCheckIsMember : async function(channelId) {
        
        let query = 
        `
        SELECT channel.id AS channel_id, is_member
        FROM channel
        WHERE id = ?
        `;
        let data = await db.query(query, [channelId]);

        // channel_id, is_member
        return data;
    },
    
    
    // 채널의 멤버 삽입
    // 안 쓰임?!?! -> 사실 channel_member 필요없음.
    insertMemberOfChannel : async function(channelId, memberKorName, memberEngName) {
        
        let query = 
        `
        INSERT INTO channel_member
            (fk_chmem_ch_id, kor_name, eng_name)
        VALUES (?, ?, ?);
        `;

        await db.query(query, [channelId, memberKorName, memberEngName]);
    }
}