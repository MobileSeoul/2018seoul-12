const db = require('../lib/db');

module.exports = {

    // 테마의 목록 보기 for main - Kor
    selectThemeListKor : async function() {
        
        let query = 
        `
        SELECT id AS theme_id, main_img_kor AS main_img
        FROM theme
        ORDER BY reg_time DESC
        LIMIT 5;
        `;
        let data = await db.query(query, []);

        // theme_id, title, subtitle, main_img
        return data;
    },
    // 테마의 목록 보기 for main - Eng
    selectThemeListEng : async function() {
        
        let query = 
        `
        SELECT id AS theme_id, main_img_eng AS main_img
        FROM theme
        ORDER BY reg_time DESC
        LIMIT 5;
        `;
        let data = await db.query(query, []);
        
        // theme_id, title, subtitle, main_img
        return data;
    },
    // 테마의 상세 정보 보기 - Kor
    selectThemeKor : async function(themeId) {
        
        let query = 
        `
        SELECT title, subtitle, img
        FROM theme
        WHERE id = ?;
        `;

        let data = await db.query(query, [themeId]);

        // title, subtitle, img
        return data;
    },
    // 테마의 상세 정보 보기 - Eng
    selectThemeEng : async function(themeId) {
        
        let query = 
        `
        SELECT title_eng AS title, subtitle_eng AS subtitle, img
        FROM theme
        WHERE id = ?;
        `;

        let data = await db.query(query, [themeId]);

        // title, subtitle, img
        return data;
    },
    // 테마의 컨텐츠 보기 - Kor
    selectThemeContentsKor : async function(themeId) {
        
        let query = 
        `
        SELECT fk_contents_spot_id AS spot_id, title, description, img
        FROM theme_contents
        WHERE fk_contents_theme_id = ?;
        `;

        let data = await db.query(query, [themeId]);

        // spot_id, title, description, img
        return data;
    },
    // 테마의 상세 정보 보기 - Eng
    selectThemeContentsEng : async function(themeId) {
        
        let query = 
        `
        SELECT fk_contents_spot_id AS spot_id, title_eng AS title, description_eng AS description, img
        FROM theme_contents
        WHERE fk_contents_theme_id = ?;
        `;

        let data = await db.query(query, [themeId]);

        // spot_id, title_eng, description_eng, img
        return data;
    },




    // 새로운 테마를 삽입
    insertTheme : async function(title, subtitle, title_eng, subtitle_eng, main_img_kor, main_img_eng, img) {
        
        let query = 
        `
        INSERT INTO theme
            (title, subtitle, title_eng, subtitle_eng, main_img_kor, main_img_eng, img)
        VALUES (?, ?, ?, ?, ?, ?, ?);
        `;

        let data = await db.query(query, [title, subtitle, title_eng, subtitle_eng, main_img_kor, main_img_eng, img]);

        return data;
    },
    // 테마의 컨텐츠 삽입
    insertThemeContents : async function(themeId, spotId, title, description, title_eng, description_eng, img) {
        
        let query = 
        `
        INSERT INTO theme_contents
            (fk_contents_theme_id, fk_contents_spot_id, title, description, title_eng, description_eng, img)
        VALUES (?, ?, ?, ?, ?, ?, ?);
        `;

        await db.query(query, [themeId, spotId, title, description, title_eng, description_eng, img]);
    }
}