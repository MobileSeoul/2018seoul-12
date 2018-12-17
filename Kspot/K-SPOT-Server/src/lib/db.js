const mysql = require('promise-mysql')
const { dbConfig} = require('../../config/dbConfig')

let DBpool
const getPool = () => {
  if (!DBpool) {
    DBpool = mysql.createPool(dbConfig)
    return DBpool
  }
  return DBpool
}

module.exports = {
  query : async (...args) => {
    const query = args[0]
    const data = args[1]

    const pool = getPool()
    let connection = await pool.getConnection()
    let result = await connection.query(query, data)

    connection.release()
    return result
  },
  // 수정 필요
  transaction : async (...args) => {
    let result = "Success"

    let connection

    try{
      const pool = getPool()
      connection = await pool.getConnection()
      await connection.beginTransaction()

      await args[0](connection, ...args)
      await connection.commit()
    }
    catch(err){
      await connection.rollback()
      console.log("mysql error : " + error)
      result = undefined
    }
    finally {
      connection.release()
      return result
    }
  }
}


// 수정 필요
/* Transaction 사용 예시

const db = require('../module/pool.js')

await db.Transaction( async (connection) => {
  await connection.query(query1, [data])
  await connection.query(query2, [data])
})


let Transaction = await db.Transaction( async (connection) => {
  var result_addvote = await connection.query(select_addvote,[vote, id])
  if(!result_addvote){
    return next("500")
  }

  let result_subcoin = await connection.query(select_subcoin,[vote, id])
  if(!result_subcoin){
    return next("500")
  }
})

if(!Transaction){
  return next("500")
}

*/
